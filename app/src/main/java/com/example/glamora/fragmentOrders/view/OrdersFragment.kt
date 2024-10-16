package com.example.glamora.fragmentOrders.view

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
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glamora.R
import com.example.glamora.databinding.FragmentOrdersBinding
import com.example.glamora.fragmentOrders.viewModel.OrderViewModel
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment : Fragment() {
    private lateinit var ordersFragmentBinding: FragmentOrdersBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ordersFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false)
        ordersFragmentBinding.lifecycleOwner = viewLifecycleOwner
        ordersFragmentBinding.viewModel = orderViewModel
        return ordersFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        ordersFragmentBinding.ordersBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        setupOrderRecyclerView()
        observeOnOrder()
    }

    private fun setupOrderRecyclerView() {
        ordersAdapter = OrdersAdapter(emptyList()) { orderId ->
            navigateToOrderDetails(orderId)
        }
        ordersFragmentBinding.ordersRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ordersAdapter
        }
    }

    private fun observeOnOrder() {
        lifecycleScope.launch {
            val customerEmail = sharedViewModel.currentCustomerInfo.value.email

            ordersFragmentBinding.orderEmptyImageView.visibility = View.GONE

            orderViewModel.getOrdersByCustomer(customerEmail)

            orderViewModel.ordersList.collectLatest { orders ->

                ordersFragmentBinding.orderEmptyImageView.visibility =
                    if (orders.isEmpty()) View.VISIBLE else View.GONE

                ordersAdapter.updateData(orders)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.message.collect { message ->
                    if (message.isNotEmpty()) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    private fun navigateToOrderDetails(orderId: String) {
        try {
            val action = OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(orderId)
            findNavController().navigate(action)
        }catch (e : Exception) {
            Log.d("Kerolos", "setupCardViews: $e")
        }
    }
}