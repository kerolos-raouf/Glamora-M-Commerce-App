package com.example.glamora.fragmentOrderDetails.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glamora.R
import com.example.glamora.data.model.ordersModel.OrderDTO
import com.example.glamora.databinding.FragmentOrderDetailsBinding
import com.example.glamora.fragmentOrderDetails.viewModel.OrderDetailsViewModel
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {
    private lateinit var orderDetailsBinding: FragmentOrderDetailsBinding
    private lateinit var orderDetailsAdapter: OrderLineItemsAdapter
    private val orderDetailsViewModel: OrderDetailsViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var order: OrderDTO? = null
    lateinit var orderId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        orderDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_details, container, false)
        orderDetailsBinding.lifecycleOwner = viewLifecycleOwner
        orderDetailsBinding.viewModel = orderDetailsViewModel
        return orderDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderId = arguments?.let { OrderDetailsFragmentArgs.fromBundle(it).orderId } ?: ""
        Log.d("OrderDetailsFragment", "Order ID received: $orderId")



        orderDetailsBinding.ordersDetailsBackButton.setOnClickListener {
            findNavController().popBackStack()
        }


        setupRecyclerView()

        val customerEmail= sharedViewModel.currentCustomerInfo.value.email
        orderDetailsViewModel.fetchOrderDetailsById(customerEmail, orderId)

        observeOrderDetails()

    }

    private fun setupRecyclerView() {
        orderDetailsAdapter = OrderLineItemsAdapter(emptyList()) { productId ->
            try {
                val action = OrderDetailsFragmentDirections.actionOrderDetailsFragmentToProductDetailsFragment(productId)
                findNavController().navigate(action)
            }catch (e : Exception) {
                Log.d("Kerolos", "setupCardViews: $e")
            }
        }

        orderDetailsBinding.orderDetailsProductRV.apply {
            adapter = orderDetailsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }



    private fun observeOrderDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderDetailsViewModel.loading.collect { isLoading ->
                    if (isLoading) {
                        orderDetailsBinding.orderDetailsProductRV.visibility = View.GONE
                        orderDetailsBinding.ordersDetailsProductContainer.visibility= View.GONE
                        orderDetailsBinding.ordersDetailsShippingContainer.visibility= View.GONE
                        orderDetailsBinding.ordersDetailsPaymentContainer.visibility= View.GONE
                        orderDetailsBinding.addressLoadingAnimation.visibility = View.VISIBLE
                    } else {
                        orderDetailsBinding.orderDetailsProductRV.visibility = View.VISIBLE
                        orderDetailsBinding.ordersDetailsProductContainer.visibility= View.VISIBLE
                        orderDetailsBinding.ordersDetailsShippingContainer.visibility= View.VISIBLE
                        orderDetailsBinding.ordersDetailsPaymentContainer.visibility= View.VISIBLE
                        orderDetailsBinding.addressLoadingAnimation.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            orderDetailsViewModel.orderDetailsList.collect { orderDTOs ->
                order = orderDTOs.firstOrNull()
                orderDetailsBinding.order = order

                val lineItems = order?.lineItems ?: emptyList()
                orderDetailsAdapter.updateData(lineItems)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderDetailsViewModel.message.collect {
                    if (it.isNotEmpty()) {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
