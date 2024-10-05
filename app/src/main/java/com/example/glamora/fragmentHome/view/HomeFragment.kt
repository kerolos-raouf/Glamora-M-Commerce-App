package com.example.glamora.fragmentHome.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloClient
import com.example.glamora.R
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.network.ApolloClientInterceptor
import com.example.glamora.data.repository.RepositoryImpl
import com.example.glamora.databinding.FragmentHomeBinding
import com.example.glamora.fragmentHome.viewModel.HomeViewModel
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var navController: NavController
    private lateinit var brandsAdapter: BrandsAdapter
    private lateinit var mAdapter: DiscountCodesAdapter

    //communicator
    private val communicator: Communicator by lazy {
        (requireContext() as Communicator)
    }

    companion object
    {
        private var scrollJob : Job?= null
    }

    private val clipboardManager : ClipboardManager by lazy {
        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private var currentIndex = 0;
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
        setupDiscountCodesRecyclerView()
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
            sharedViewModel.productList.collect { randomProducts ->
                val filteredProducts = randomProducts.shuffled().take(10)
                productsAdapter.updateData(filteredProducts)
                productsAdapter = ProductsAdapter(filteredProducts)
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
        binding.apply {

            // Men category
            cvMen.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToProductListFragment(
                    Constants.PRODUCT_BY_MEN)
                navController.navigate(action)
            }

            // Women category
            cvWomen.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToProductListFragment(
                    Constants.PRODUCT_BY_WOMEN)
                navController.navigate(action)
            }

            // Kids category
            cvKids.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToProductListFragment(
                    Constants.PRODUCT_BY_KIDS)
                navController.navigate(action)
            }

            // Sale category
            cvSale.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToProductListFragment(
                    Constants.PRODUCT_BY_SALE)
                navController.navigate(action)
            }
        }
    }


    private fun setupDiscountCodesRecyclerView() {

        val imagesList = listOf(
            R.drawable.promotion,
            R.drawable.promotion2,
            R.drawable.promotion3,
            R.drawable.promotion4,
            R.drawable.promotion5
        )
        mAdapter = DiscountCodesAdapter(
            imagesList,
            object : DiscountCodeListener {
                override fun onDiscountCodeClicked(discountCode: DiscountCodeDTO) {
                    val clipData = ClipData.newPlainText("Promotion Code", discountCode.code)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(context, "Promotion Code Copied ${discountCode.code}", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.homeRvOffers.apply {
            adapter = mAdapter
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                sharedViewModel.discountCodes.collect{
                    mAdapter.submitList(it)
                    if(it.isNotEmpty()){
                        autoScrollForRecyclerView()
                    }
                }
            }

        }
    }

    private fun autoScrollForRecyclerView()
    {
        scrollJob?.cancel()
        scrollJob = lifecycleScope.launch(Dispatchers.Default) {
            currentIndex = 0
            while (isActive)
            {
                delay(3000)
                binding.homeRvOffers.smoothScrollToPosition((++currentIndex) % mAdapter.itemCount)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        scrollJob?.start()
        communicator.showBottomNav()
    }
    override fun onStop() {
        super.onStop()
        scrollJob?.cancel()
    }


}