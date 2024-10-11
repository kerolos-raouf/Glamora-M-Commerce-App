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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.glamora.R
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.databinding.FragmentHomeBinding
import com.example.glamora.fragmentHome.viewModel.HomeViewModel
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import com.example.glamora.util.State
import com.example.glamora.util.showGuestDialog
import com.google.android.material.carousel.CarouselLayoutManager
import dagger.hilt.android.AndroidEntryPoint
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
            if(!communicator.isInternetAvailable())
            {
                Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
            }
            else if (sharedViewModel.currentCustomerInfo.value.email != Constants.UNKNOWN) {
                findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
            }else {
                showGuestDialog(requireContext())
            }
        }

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

    private fun actionOnInternetAvailable() {
        sharedViewModel.fetchProducts()
        homeViewModel.getALlBrands()
        sharedViewModel.fetchDiscountCodes()
    }

    private fun initHome() {
        val userEmail = sharedViewModel.getSharedPrefString(Constants.CUSTOMER_EMAIL, Constants.UNKNOWN)
        if (userEmail != Constants.UNKNOWN) {
            sharedViewModel.getCustomerInfo(userEmail)
            sharedViewModel.fetchFavoriteItems()
        }

        binding.homeSwiperefreshlayout.setOnRefreshListener {
            if(communicator.isInternetAvailable()) {
                actionOnInternetAvailable()
            }else {
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
            binding.homeSwiperefreshlayout.isRefreshing = false
        }

    }

    private fun setupRandomItemsRecyclerView() {
        productsAdapter = ProductsAdapter(emptyList()) { productId ->
            if(!communicator.isInternetAvailable())
            {
                Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
            }else{
                val action = HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(productId)
                findNavController().navigate(action)
                Log.d("MAI","$action")
            }
        }

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
            findNavController().navigate(action)
        }
        binding.homeRvBrand.apply {
            layoutManager = CarouselLayoutManager()
            adapter = brandsAdapter
        }
    }

    private fun observeRandomProducts() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.productList.collect { randomProducts ->
                    val filteredProducts = randomProducts.shuffled().take(10)
                    productsAdapter.updateData(filteredProducts)
                }
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
                if(!communicator.isInternetAvailable())
                {
                    Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
                }else
                {
                    val action= HomeFragmentDirections.actionHomeFragmentToProductListFragment(Constants.SHOES)
                    findNavController().navigate(action)
                }

            }
            homeTshirtcv.setOnClickListener{
                if(!communicator.isInternetAvailable())
                {
                    Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
                }else{
                    val action= HomeFragmentDirections.actionHomeFragmentToProductListFragment(Constants.T_SHIRT)
                    findNavController().navigate(action)
                }

            }

            homeAccssCV.setOnClickListener{
                if(!communicator.isInternetAvailable())
                {
                    Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
                }else {
                    val action= HomeFragmentDirections.actionHomeFragmentToProductListFragment(Constants.ACCESSEORIES)
                    findNavController().navigate(action)
                }
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
                    if(sharedViewModel.getSharedPrefString(Constants.CUSTOMER_EMAIL,Constants.UNKNOWN) != Constants.UNKNOWN){
                        val clipData = ClipData.newPlainText("Promotion Code", discountCode.code)
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(context, "Promotion Code Copied ${discountCode.code}", Toast.LENGTH_SHORT).show()
                    }else{
                        showGuestDialog(requireContext())
                    }

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
                        val favoriteItemsCount = state.data.size
                        binding.favoriteCounter.text = favoriteItemsCount.toString()
                        if (favoriteItemsCount == 0) {
                            binding.favoriteCounter.visibility = View.GONE
                        } else {
                            binding.favoriteCounter.visibility = View.VISIBLE
                        }
                    }
                    is State.Error -> {
                        binding.favoriteCounter.visibility = View.GONE
                    }
                    is State.Loading -> {
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
        findNavController().graph.setStartDestination(R.id.homeFragment)
    }
    override fun onStop() {
        super.onStop()
        scrollJob?.cancel()
    }
}
