package com.example.glamora.fragmentOrders.view

import android.os.Bundle
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
import com.example.glamora.databinding.FragmentOrdersBinding
import com.example.glamora.fragmentOrders.viewModel.OrderViewModel
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment : Fragment() {
    private lateinit var ordersFragmentBinding: FragmentOrdersBinding
    private lateinit var navController: NavController
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
        return ordersFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        ordersFragmentBinding.ordersBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        setupOrderRecyclerView()

        val email = sharedViewModel.currentCustomerInfo.value.email

        orderViewModel.getOrdersByCustomer(email)

        lifecycleScope.launchWhenStarted {
            orderViewModel.ordersList.collect { orders ->
                ordersAdapter.updateData(orders)            }
        }



        //observeOnOrder()
    }

    private fun setupOrderRecyclerView(){
        ordersAdapter = OrdersAdapter(emptyList())
        ordersFragmentBinding.ordersRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ordersAdapter
        }

    }

    private fun observeOnOrder(){
        lifecycleScope.launch {
            val customerEmail= sharedViewModel.currentCustomerInfo.value.email
            orderViewModel.getOrdersByCustomer(customerEmail)
            orderViewModel.ordersList.collectLatest { orders->
                ordersAdapter.updateData(orders)
            }
        }
    }

}