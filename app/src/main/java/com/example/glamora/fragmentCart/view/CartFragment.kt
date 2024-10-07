package com.example.glamora.fragmentCart.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.glamora.R
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.databinding.CartBottomSheetBinding
import com.example.glamora.databinding.FragmentCartBinding
import com.example.glamora.databinding.OperationDoneBottomSheetBinding
import com.example.glamora.fragmentCart.viewModel.CartViewModel
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import com.example.glamora.util.customAlertDialog.CustomAlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paypal.android.cardpayments.CardClient
import com.paypal.android.corepayments.CoreConfig
import com.paypal.android.corepayments.Environment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.truncate


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

    //discount value
    private var discountValue = 0.0


    //paypal
    private val clientId = "AQto284OoB8DVcUW4pE4CBMOAQ-LnVV-P88g00FpO7nSCF3ruUWb0KMWe64diUwMWFzDYT3_qdanNCG6"
    private val secretId = "ECGkdlpIhYXe0rPYNZ4tvMcrNInFrM4J636j7H5n-M_DXiC2x6gykyjDm7XOIrC4PcNZ0dmqbsQRTa2I"
    private val returnUrl = "com.example.glamora://demo"



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
            Log.d("Kerolos", "initViews: ${sharedViewModel.currentCustomerInfo.value.userIdAsNumber}")
            cartViewModel.fetchCartItems(sharedViewModel.currentCustomerInfo.value.userIdAsNumber)
        }

        //alert dialog
        customAlertDialog = CustomAlertDialog(requireActivity())

        //adapter
        mAdapter = CartRecyclerViewAdapter(this)
        binding.cartRecyclerView.adapter = mAdapter

        //refresh layout
        binding.cartSwipeRefreshLayout.setOnRefreshListener {
            cartViewModel.fetchCartItems()
            binding.cartSwipeRefreshLayout.isRefreshing = false
        }

        //apply discount code
        binding.cartApplyButton.setOnClickListener {
            var found = false
            for (discount in sharedViewModel.discountCodes.value)
            {
                if(discount.code == binding.cartCuponCodeEditText.text.toString())
                {
                    discountValue = discount.percentage / 100
                    applyPriceChangeOnUI(0.0)
                    binding.cartCuponCodeEditText.setText("")
                    binding.cartCuponCodeEditText.hint = discount.code
                    binding.cartCuponCodeEditText.clearFocus()
                    found = true
                    Toast.makeText(requireContext(), "Discount code applied", Toast.LENGTH_SHORT).show()
                    break
                }
            }
            if (!found)
            {
                Toast.makeText(requireContext(), "Invalid code", Toast.LENGTH_SHORT).show()
            }
        }




        initPayPal()

        bottomSheet = BottomSheetDialog(requireContext())
        bottomSheetBinding = CartBottomSheetBinding.inflate(layoutInflater)
        binding.cartCheckOutButton.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun initObservers(){
        cartViewModel.cartItems.observe(viewLifecycleOwner){
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

    private fun initPayPal(){
        val config = CoreConfig(clientId, environment = Environment.SANDBOX)
        val cardClient = CardClient(requireActivity(),config)


    }


    private fun showBottomSheet() {
        bottomSheet.setCancelable(true)
        bottomSheet.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.bottomSheetPayNowButton.setOnClickListener {
            bottomSheet.dismiss()
            cartViewModel.createFinalDraftOrder(
                discountAmount = discountValue * 100
            )
            if(bottomSheetBinding.bottomSheetPaymentMethodsPayWithCardRadio.isChecked){
                payWithCard()
            }else{

            }
        }

        bottomSheet.show()
    }


    private fun payWithCard(){

    }

    override fun onItemPlusClicked(item: CartItemDTO) {
        cartViewModel.updateCartItemQuantity(item.draftOrderId, item.id, item.quantity)
        applyPriceChangeOnUI(-item.price.toDouble())
    }

    override fun onItemMinusClicked(item: CartItemDTO) {
        cartViewModel.updateCartItemQuantity(item.draftOrderId, item.id, item.quantity)
        applyPriceChangeOnUI(item.price.toDouble())
    }

    override fun onItemDeleteClicked(item: CartItemDTO) {
        customAlertDialog.showAlertDialog(
            message = "Are about deleting this item?",
            actionText = "Delete"
        ){
            cartViewModel.deleteCartItemFromDraftOrder(item)
            applyPriceChangeOnUI(item.price.toDouble() * item.quantity)
        }
    }

    override fun onAddToFavoriteClicked(item: CartItemDTO) {

    }

    override fun onItemClicked(item: CartItemDTO) {

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

}