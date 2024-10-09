package com.example.glamora.fragmentOrderDetails.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glamora.R
import com.example.glamora.databinding.FragmentOrderDetailsBinding
import com.example.glamora.databinding.FragmentOrdersBinding
import com.example.glamora.fragmentOrders.view.OrdersAdapter
import com.example.glamora.fragmentOrders.viewModel.OrderViewModel
import com.example.glamora.mainActivity.viewModel.SharedViewModel


class OrderDetailsFragment : Fragment() {
    private lateinit var orderDetailsBinding: FragmentOrderDetailsBinding
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var ordersAdapter: OrdersAdapter
    lateinit var orderId:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderId = it.getString("orderId") ?: "" // Default to an empty string if null
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        orderDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_details, container, false)
        return orderDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        orderDetailsBinding.ordersDetailsBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        setupOrderDetailRecyclerView()

    }

    private fun setupOrderDetailRecyclerView(){
//        ordersAdapter = OrdersAdapter(emptyList())
//        orderDetailsBinding.orderDetailsProductRV.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = ordersAdapter
//        }

    }

}