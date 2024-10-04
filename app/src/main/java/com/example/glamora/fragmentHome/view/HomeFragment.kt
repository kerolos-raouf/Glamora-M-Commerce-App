package com.example.glamora.fragmentHome.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloClient
import com.example.glamora.R
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.network.ApolloClientInterceptor
import com.example.glamora.data.repository.RepositoryImpl
import com.example.glamora.databinding.FragmentHomeBinding
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        observeProducts()

    }

    private fun setupRecyclerView() {
        productsAdapter = ProductsAdapter(emptyList())
        binding.homeRvItem.apply {
            layoutManager = GridLayoutManager(context,2)
            adapter = productsAdapter
        }
    }

    private fun observeProducts() {
        lifecycleScope.launch {
            viewModel.productList.collect { productList ->
                val randomProducts = productList.shuffled().take(10)
                productsAdapter.updateData(randomProducts)
                productsAdapter = ProductsAdapter(randomProducts)
                binding.homeRvItem.adapter = productsAdapter
            }

        }
    }

}