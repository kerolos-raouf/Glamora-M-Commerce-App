package com.example.glamora.fragmentCart.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.glamora.R
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.databinding.CartBottomSheetBinding
import com.example.glamora.databinding.FragmentCartBinding
import com.example.glamora.fragmentCart.viewModel.CartViewModel
import com.example.glamora.util.customAlertDialog.CustomAlertDialog
import com.example.glamora.util.customAlertDialog.ICustomAlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paypal.android.cardpayments.CardClient
import com.paypal.android.corepayments.CoreConfig
import com.paypal.android.corepayments.Environment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CartFragment : Fragment(),CartItemInterface {


    private val cartViewModel: CartViewModel by viewModels()

    private lateinit var binding : FragmentCartBinding

    //bottom sheet

    private lateinit var bottomSheet : BottomSheetDialog
    private lateinit var bottomSheetBinding : CartBottomSheetBinding

    //mAdapter
    private lateinit var mAdapter : CartRecyclerViewAdapter

    //custom alert dialog
    private lateinit var customAlertDialog : CustomAlertDialog


    //paypal
    private val clientId = "AQto284OoB8DVcUW4pE4CBMOAQ-LnVV-P88g00FpO7nSCF3ruUWb0KMWe64diUwMWFzDYT3_qdanNCG6"
    private val secretId = "ECGkdlpIhYXe0rPYNZ4tvMcrNInFrM4J636j7H5n-M_DXiC2x6gykyjDm7XOIrC4PcNZ0dmqbsQRTa2I"
    private val returnUrl = "com.example.glamora://demo"



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartViewModel.fetchCartItems()

        initViews()
        initObservers()
    }

    private fun initViews() {

        //alert dialog
        customAlertDialog = CustomAlertDialog(requireActivity())

        //adapter
        mAdapter = CartRecyclerViewAdapter(this)
        binding.cartRecyclerView.adapter = mAdapter

        initPayPal()

        bottomSheet = BottomSheetDialog(requireContext())
        bottomSheetBinding = CartBottomSheetBinding.inflate(layoutInflater)
        binding.cartCheckOutButton.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun initObservers(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                cartViewModel.cartItems.collect{
                    mAdapter.submitList(it)
                }
            }
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

    }

    override fun onItemMinusClicked(item: CartItemDTO) {

    }

    override fun onItemDeleteClicked(item: CartItemDTO) {
        customAlertDialog.showAlertDialog(
            message = "Are about deleting this item?",
            actionText = "Delete"
        ){
            cartViewModel.deleteDraftOrder(item.draftOrderId)
        }
    }

    override fun onAddToFavoriteClicked(item: CartItemDTO) {

    }

    override fun onItemClicked(item: CartItemDTO) {

    }

}