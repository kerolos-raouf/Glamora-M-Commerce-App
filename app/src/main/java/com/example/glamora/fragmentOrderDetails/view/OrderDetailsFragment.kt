package com.example.glamora.fragmentOrderDetails.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glamora.R
import com.example.glamora.data.model.ordersModel.LineItemDTO
import com.example.glamora.data.model.ordersModel.OrderDTO
import com.example.glamora.databinding.FragmentOrderDetailsBinding
import com.example.glamora.databinding.FragmentOrdersBinding
import com.example.glamora.fragmentOrderDetails.viewModel.OrderDetailsViewModel
import com.example.glamora.fragmentOrders.view.OrdersAdapter
import com.example.glamora.fragmentOrders.viewModel.OrderViewModel
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {
    private lateinit var orderDetailsBinding: FragmentOrderDetailsBinding
    private lateinit var navController: NavController
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
        return orderDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderId = arguments?.let { OrderDetailsFragmentArgs.fromBundle(it).orderId } ?: ""
        Log.d("OrderDetailsFragment", "Order ID received: $orderId")


        navController = Navigation.findNavController(view)

        orderDetailsBinding.ordersDetailsBackButton.setOnClickListener {
            findNavController().popBackStack()
        }


        setupRecyclerView()

        val customerEmail= sharedViewModel.currentCustomerInfo.value.email
        orderDetailsViewModel.fetchOrderDetailsById(customerEmail, orderId)

        observeOrderDetails()

    }

    private fun setupRecyclerView() {
        orderDetailsAdapter = OrderLineItemsAdapter(emptyList())
        orderDetailsBinding.orderDetailsProductRV.apply {
            adapter = orderDetailsAdapter
            layoutManager = LinearLayoutManager(context)
        }
        Log.d("OrderDetailsFragment", "RecyclerView setup complete")
    }


    private fun observeOrderDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            orderDetailsViewModel.orderDetailsList.collect { orderDTOs ->
                Log.d("OrderDetailsFragment", "Order details collected: $orderDTOs")

                // Assuming you want to take the first order for simplicity
                order = orderDTOs.firstOrNull()
                orderDetailsBinding.order = order  // Bind the order after it is fetched

                val lineItems = order?.lineItems ?: emptyList()
                Log.d("OrderDetailsFragment", "Flattened line items: $lineItems")

                if (lineItems.isEmpty()) {
                    Log.d("OrderDetailsFragment", "No line items found")
                }

                orderDetailsAdapter.updateData(lineItems)
            }
        }
    }
}