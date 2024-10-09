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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloClient
import com.example.glamora.R
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.network.ApolloClientInterceptor
import com.example.glamora.data.repository.RepositoryImpl
import com.example.glamora.data.sharedPref.SharedPrefHandler
import com.example.glamora.databinding.FragmentHomeBinding
import com.example.glamora.fragmentHome.viewModel.HomeViewModel
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import com.example.glamora.util.State
import com.google.android.material.carousel.CarouselLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var navController: NavController
    private lateinit var brandsAdapter: BrandsAdapter
    private lateinit var mAdapter: DiscountCodesAdapter



    private val communicator: Communicator by lazy {
        (requireActivity() as Communicator)
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
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeFavoriteButton.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
        }

        navController = Navigation.findNavController(view)

        initHome()
        setUpRecyclerViews()
        callObservables()

    }

    private fun callObservables(){
        observeRandomProducts()
        observeBrands()
        observeFavoriteItemsCount()
    }

    private fun setUpRecyclerViews(){
        setupRandomItemsRecyclerView()
        setupBrandsRecyclerView()
        setupDiscountCodesRecyclerView()
        setupCardViews()
    }

    private fun initHome() {
        val userEmail = sharedViewModel.getSharedPrefString(Constants.CUSTOMER_EMAIL, Constants.UNKNOWN)
        if (userEmail != Constants.UNKNOWN) {
            sharedViewModel.getCustomerInfo(userEmail)
            sharedViewModel.fetchFavoriteItems()
        }
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
                selectedBrand.title
            )
            navController.navigate(action)
        }
        binding.homeRvBrand.apply {
            layoutManager = CarouselLayoutManager()
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

            homeShoescv.setOnClickListener{
                val action= HomeFragmentDirections.actionHomeFragmentToProductListFragment(Constants.SHOES)
                navController.navigate(action)

            }
            homeTshirtcv.setOnClickListener{
                val action= HomeFragmentDirections.actionHomeFragmentToProductListFragment(Constants.T_SHIRT)
                navController.navigate(action)

            }

            homeAccssCV.setOnClickListener{
                val action= HomeFragmentDirections.actionHomeFragmentToProductListFragment(Constants.ACCESSEORIES)
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
    private fun observeFavoriteItemsCount() {
        lifecycleScope.launch {
                sharedViewModel.favoriteItemsState.collect { state ->
                    when (state) {
                        is State.Success -> {
                            Log.d("FAV","${state.data.size}")
                            val favoriteItemsCount = state.data.size
                            binding.favoriteCounter.text = favoriteItemsCount.toString()
                            binding.favoriteCounter.visibility = View.VISIBLE
                        }
                        is State.Error -> {
                            Log.d("FAV","error")
                            binding.favoriteCounter.visibility = View.GONE
                        }
                        is State.Loading -> {
                            Log.d("FAV","loading")
                            binding.favoriteCounter.visibility = View.GONE
                        }
                    }
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
