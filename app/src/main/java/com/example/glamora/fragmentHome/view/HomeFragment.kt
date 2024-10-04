package com.example.glamora.fragmentHome.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloClient
import com.example.glamora.R
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.network.ApolloClientInterceptor
import com.example.glamora.data.repository.RepositoryImpl
import com.example.glamora.databinding.FragmentHomeBinding
import com.example.glamora.fragmentHome.viewModel.HomeViewModel
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import jakarta.inject.Inject
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: SharedViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var brandsAdapter: BrandsAdapter
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        setupRandomItemsRecyclerView()
        setupBrandsRecyclerView()
        setupCardViews()

        observeRandomProducts()
        observeBrands()


    }

    private fun setupRandomItemsRecyclerView() {
        productsAdapter = ProductsAdapter(emptyList())
        binding.homeRvItem.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = productsAdapter
        }
    }

    private fun setupBrandsRecyclerView() {
        brandsAdapter = BrandsAdapter(emptyList()) { selectedBrand ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductListFragment(
                selectedBrand.title,
                selectedBrand.image.url
            )
            navController.navigate(action)
        }
        binding.homeRvBrand.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = brandsAdapter
        }
    }


    private fun observeRandomProducts() {
        lifecycleScope.launch {
            homeViewModel.randomProductList.collect { randomProducts ->
                productsAdapter.updateData(randomProducts)
                productsAdapter = ProductsAdapter(randomProducts)
                binding.homeRvItem.adapter = productsAdapter
            }

        }
    }

    private fun observeBrands() {
        lifecycleScope.launch {
            homeViewModel.brandsList.collect { brandsList ->
                brandsAdapter.updateData(brandsList)
            }
        }
    }

    private fun setupCardViews() {
//        binding.apply {
//
//            cvMen.setOnClickListener {
//                homeViewModel.filterProductsByBrand(Constants.PRODUCT_BY_MEN)
//                navController.navigate(R.id.action_homeFragment_to_productListFragment)
//            }
//            cvWomen.setOnClickListener {
//                homeViewModel.filterProductsByBrand(Constants.PRODUCT_BY_WOMEN)
//                navController.navigate(R.id.action_homeFragment_to_productListFragment)
//            }
//
//            cvKids.setOnClickListener {
//                homeViewModel.filterProductsByBrand(Constants.PRODUCT_BY_KIDS)
//                navController.navigate(R.id.action_homeFragment_to_productListFragment)
//
//            }
//            cvSale.setOnClickListener {
//                homeViewModel.filterProductsByBrand(Constants.PRODUCT_BY_SALE)
//                navController.navigate(R.id.action_homeFragment_to_productListFragment)
//
//            }
//        }
//
    }
}