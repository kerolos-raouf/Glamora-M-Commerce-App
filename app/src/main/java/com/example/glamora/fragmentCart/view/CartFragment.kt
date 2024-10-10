package com.example.glamora.fragmentCart.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.data.model.AddressModel
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.databinding.CartBottomSheetBinding
import com.example.glamora.databinding.FragmentCartBinding
import com.example.glamora.databinding.OperationDoneBottomSheetBinding
import com.example.glamora.fragmentCart.viewModel.CartViewModel
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import com.example.glamora.util.customAlertDialog.CustomAlertDialog
import com.example.glamora.util.showGuestDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CartFragment : Fragment(),CartItemInterface {


    private val cartViewModel: CartViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var binding : FragmentCartBinding

    //bottom sheet
    private lateinit var bottomSheet : BottomSheetDialog
    private lateinit var bottomSheetBinding : CartBottomSheetBinding

    //mAdapter
    private lateinit var mAdapter : CartRecyclerViewAdapter

    //custom alert dialog
    private lateinit var customAlertDialog : CustomAlertDialog

    //cart order values
    private var discountValue = 0.0
    private var discountCode = Constants.UNKNOWN
    private var address : AddressModel = AddressModel()



    private val communicator: Communicator by lazy {
        requireActivity() as Communicator
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = cartViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initViews()
        initObservers()
    }

    private fun initViews() {

        //fetch cart items
        if(sharedViewModel.currentCustomerInfo.value.userId != Constants.UNKNOWN)
        {
            cartViewModel.fetchCartItems(sharedViewModel.currentCustomerInfo.value.userIdAsNumber)
        }

        //alert dialog
        customAlertDialog = CustomAlertDialog(requireActivity())

        //adapter
        mAdapter = CartRecyclerViewAdapter(this)
        binding.cartRecyclerView.adapter = mAdapter

        //refresh layout
        binding.cartSwipeRefreshLayout.setOnRefreshListener {
            if(sharedViewModel.currentCustomerInfo.value.userId != Constants.UNKNOWN)
            {
                cartViewModel.fetchCartItems(sharedViewModel.currentCustomerInfo.value.userIdAsNumber)
            }
            binding.cartSwipeRefreshLayout.isRefreshing = false
        }


        //apply discount code
        binding.cartApplyButton.setOnClickListener {
            if(sharedViewModel.getSharedPrefString(Constants.CUSTOMER_EMAIL,Constants.UNKNOWN) != Constants.UNKNOWN){
                var found = false
                for (discount in sharedViewModel.discountCodes.value)
                {
                    if(discount.code == binding.cartCuponCodeEditText.text.toString())
                    {
                        actionAfterGettingFoundDiscountCode(discount)
                        found = true
                        break
                    }
                }
                if (!found)
                {
                    Toast.makeText(requireContext(), "Invalid code", Toast.LENGTH_SHORT).show()
                }
            }else{
                showGuestDialog(requireContext())
            }

        }



        bottomSheet = BottomSheetDialog(requireContext())
        bottomSheetBinding = CartBottomSheetBinding.inflate(layoutInflater)
        binding.cartCheckOutButton.setOnClickListener {
            if(!communicator.isInternetAvailable()){
                Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
            }
            else if(sharedViewModel.getSharedPrefString(Constants.CUSTOMER_EMAIL,Constants.UNKNOWN) != Constants.UNKNOWN){
                showBottomSheet()
            }else{
                showGuestDialog(requireContext())
            }
        }
    }


    private fun actionAfterGettingFoundDiscountCode(discount: DiscountCodeDTO)
    {
        if (!sharedViewModel.getSharedPrefBoolean(discount.code,false))
        {
            discountValue = discount.percentage / 100
            applyPriceChangeOnUI(0.0)
            binding.cartCuponCodeEditText.setText("")
            binding.cartCuponCodeEditText.hint = discount.code
            binding.cartCuponCodeEditText.clearFocus()
            discountCode = discount.code
            Toast.makeText(requireContext(), "Discount code applied", Toast.LENGTH_SHORT).show()
        }else
        {
            Toast.makeText(requireContext(), "This code is already used", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initObservers(){
        cartViewModel.cartItems.observe(viewLifecycleOwner){
            if(it.isEmpty())
            {
                binding.cartEmptyImageView.visibility = View.VISIBLE
            }else
            {
                binding.cartEmptyImageView.visibility = View.GONE
            }
            mAdapter.submitList(it)
            calculateTotalPrice(it)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                cartViewModel.message.collect{
                    if (it.isNotEmpty()){
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                cartViewModel.showDoneBottomSheet.collect{
                    if (it){
                        showDoneBottomSheet()
                    }
                }
            }
        }

        //paypal
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                cartViewModel.openApprovalUrlState.collect{url->
                    if (url.isNotEmpty()){
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        cartViewModel.openApprovalUrlState.value = ""
                    }
                }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                sharedViewModel.operationDoneWithPayPal.collect{state->
                    if (state){
                        finishDraftOrder()
                        sharedViewModel.operationDoneWithPayPal.value = false
                    }
                }
            }
        }

    }



    private fun showDoneBottomSheet() {
        val doneBottomSheet = BottomSheetDialog(requireContext())
        val doneBottomSheetBinding = OperationDoneBottomSheetBinding.inflate(layoutInflater)
        doneBottomSheet.setContentView(doneBottomSheetBinding.root)
        doneBottomSheet.show()
        doneBottomSheet.setCancelable(true)
    }

    private fun calculateTotalPrice(cartItems: List<CartItemDTO>)
    {
        var itemsPrice = 0.0
        val shippingPrice = 0.0
        cartItems.forEach {
            itemsPrice += it.price.toDouble() * it.quantity
        }
        val totalPrice = itemsPrice + shippingPrice

        showPricesBasedOnCurrency(itemsPrice, shippingPrice, totalPrice)
    }

    private fun showPricesBasedOnCurrency(itemsPrice: Double, shippingPrice: Double, totalPrice: Double) {
        val currencyUnit = sharedViewModel.getSharedPrefString(Constants.CURRENCY_KEY,Constants.EGP)
        val multiplier = sharedViewModel.getSharedPrefString(Constants.CURRENCY_MULTIPLIER_KEY,1.toString())
        val newTotal = (totalPrice * multiplier.toDouble()) - (discountValue * totalPrice * multiplier.toDouble())
        binding.apply {
            cartItemsNumber.text =
                "${String.format("%.2f", itemsPrice * multiplier.toDouble())} $currencyUnit"
            cartShippingNumber.text =
                "${String.format("%.2f", shippingPrice * multiplier.toDouble())} $currencyUnit"
            cartDiscountNumber.text =
                "${String.format("%.2f", discountValue * totalPrice)} $currencyUnit"
            cartTotalPriceNumber.text =
                "${String.format("%.2f", newTotal)} $currencyUnit"
        }
    }



    private fun showBottomSheet() {
        bottomSheet.setCancelable(true)
        bottomSheet.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.bottomSheetPaymentMethodsPayWithCardRadio.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
            {
                bottomSheetBinding.bottomSheetPaypalButton.visibility = View.VISIBLE
                bottomSheetBinding.bottomSheetPayNowButton.visibility = View.GONE
            }
        }

        bottomSheetBinding.bottomSheetPaymentMethodsCashOnDeliveryRadio.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
            {
                bottomSheetBinding.bottomSheetPaypalButton.visibility = View.GONE
                bottomSheetBinding.bottomSheetPayNowButton.visibility = View.VISIBLE
            }
        }

        //set default address
        if(sharedViewModel.currentCustomerInfo.value.addresses.isNotEmpty())
        {
            bottomSheetBinding.bottomSheetUserAddress.text =
                "Address : ${sharedViewModel.currentCustomerInfo.value.addresses[0].country}, ${sharedViewModel.currentCustomerInfo.value.addresses[0].city}, ${sharedViewModel.currentCustomerInfo.value.addresses[0].street}"

            bottomSheetBinding.bottomSheetUserName.text = "Name : ${sharedViewModel.currentCustomerInfo.value.addresses[0].firstName}"
            address = sharedViewModel.currentCustomerInfo.value.addresses[0]
        }


        bottomSheetBinding.bottomSheetSetNewAddressButton.setOnClickListener {
            showPopUpAddresses(bottomSheetBinding.bottomSheetDetailsLinearLayout)
        }

        bottomSheetBinding.bottomSheetPayNowButton.setOnClickListener {
            if(!communicator.isInternetAvailable()){
                Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
            }
            else if(address.city != Constants.UNKNOWN)
            {
                finishDraftOrder()
                bottomSheet.dismiss()
            }else
            {
                Toast.makeText(requireContext(), "Please add your address", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetBinding.bottomSheetPaypalButton.setOnClickListener {
            if(!communicator.isInternetAvailable()){
                Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
            }
            else if(cartViewModel.cartItems.value?.isEmpty() == true)
            {
                Toast.makeText(requireContext(), "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
            else if(address.city != Constants.UNKNOWN)
            {
                payWithCard()
                bottomSheet.dismiss()
            }else
            {
                Toast.makeText(requireContext(), "Please add your address", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheet.show()
    }


    private fun showPopUpAddresses(view : View) {
        val popupMenu = PopupMenu(requireContext(),view)
        val addresses = sharedViewModel.currentCustomerInfo.value.addresses
        if(addresses.isNotEmpty())
        {
            popupMenu.menu.add(0,0,0,"+Add new address")
            addresses.forEachIndexed { index, addressModel ->
                popupMenu.menu.add(0,index+1,0,"${addressModel.country}, ${addressModel.city}, ${addressModel.street}")
            }

            popupMenu.setOnMenuItemClickListener {menuItem ->
                if(menuItem.itemId == 0)
                {
                    findNavController().navigate(R.id.action_cartFragment_to_mapFragment)
                    bottomSheet.dismiss()
                }else
                {
                    address = sharedViewModel.currentCustomerInfo.value.addresses[menuItem.itemId-1]
                    bottomSheetBinding.bottomSheetUserName.text = "Name : ${address.firstName}"
                    bottomSheetBinding.bottomSheetUserAddress.text = "Address : ${address.country}-${address.city}-${address.street}"
                }

                true
            }
        }else
        {
            popupMenu.menu.add(0,0,0,"<There is no addresses>")
        }
        popupMenu.show()
    }


    private fun finishDraftOrder(){
        //create order from drat order
        cartViewModel.createFinalDraftOrder(
            customerId = sharedViewModel.currentCustomerInfo.value.userId,
            customerEmail = sharedViewModel.currentCustomerInfo.value.email,
            discountAmount = discountValue * 100,
            address = address
        )
        discountValue = 0.0
        if(discountCode != Constants.UNKNOWN)
        {
            sharedViewModel.setSharedPrefBoolean(discountCode,true)
            discountCode = Constants.UNKNOWN
        }
    }


    private fun payWithCard(){
        cartViewModel.startOrder()
    }

    override fun onItemPlusClicked(item: CartItemDTO) {
        if(!communicator.isInternetAvailable()){
            Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
        }else{
            cartViewModel.updateCartItemQuantity(item.draftOrderId, item.id, item.quantity)
            applyPriceChangeOnUI(-item.price.toDouble())
        }
    }

    override fun onItemMinusClicked(item: CartItemDTO) {
        if(!communicator.isInternetAvailable()){
            Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
        }else{
            cartViewModel.updateCartItemQuantity(item.draftOrderId, item.id, item.quantity)
            applyPriceChangeOnUI(item.price.toDouble())
        }
    }

    override fun onItemDeleteClicked(item: CartItemDTO) {
        if(!communicator.isInternetAvailable()){
            Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
        }else{
            customAlertDialog.showAlertDialog(
                message = "Are about deleting this item?",
                actionText = "Delete"
            ){
                cartViewModel.deleteCartItemFromDraftOrder(item,sharedViewModel.currentCustomerInfo.value.userIdAsNumber)
                applyPriceChangeOnUI(item.price.toDouble() * item.quantity)
            }
        }
    }

    override fun onAddToFavoriteClicked(item: CartItemDTO) {

    }

    override fun onItemClicked(item: CartItemDTO) {
        if (communicator.isInternetAvailable())
        {
            val action = CartFragmentDirections.actionCartFragmentToProductDetailsFragment(item.productId)
            findNavController().navigate(action)
        }else{
            Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onReachedMaxQuantity(item: CartItemDTO) {
        Toast.makeText(requireContext(), "You reached to the maximum quantity", Toast.LENGTH_SHORT).show()
    }

    private fun applyPriceChangeOnUI(newItemsPrice : Double)
    {
        val oldItemsPrice = binding.cartItemsNumber.text.toString().split(" ")[0].toDouble()
        val aftersDiscount = (oldItemsPrice - newItemsPrice) - ((oldItemsPrice - newItemsPrice) * discountValue)
        val newDiscount = ((oldItemsPrice - newItemsPrice) * discountValue)
        binding.cartItemsNumber.text = "${String.format("%.2f", oldItemsPrice - newItemsPrice)} ${sharedViewModel.getSharedPrefString(Constants.CURRENCY_KEY,Constants.EGP)}"
        binding.cartTotalPriceNumber.text = "${String.format("%.2f", aftersDiscount)} ${sharedViewModel.getSharedPrefString(Constants.CURRENCY_KEY,Constants.EGP)}"
        binding.cartDiscountNumber.text = "${String.format("%.2f", newDiscount)} ${sharedViewModel.getSharedPrefString(Constants.CURRENCY_KEY,Constants.EGP)}"
    }

    override fun onStart() {
        super.onStart()
        communicator.showBottomNav()
    }

}