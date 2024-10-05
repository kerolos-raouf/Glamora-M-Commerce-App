package com.example.glamora.fragmentProductList.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.glamora.R
import com.example.glamora.databinding.DialogFilterBinding
import com.example.glamora.databinding.FragmentProductListBinding
import com.example.glamora.fragmentProductList.viewModel.ProductListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {

    private lateinit var binding: FragmentProductListBinding
    private val productListViewModel: ProductListViewModel by activityViewModels()
    private lateinit var productRecycleAdapter: ProductListAdapterr
    private lateinit var navController: NavController
    private lateinit var filterDialog: Dialog
    private lateinit var filterBinding: DialogFilterBinding
    private var type: String = ""
    private var fromPrice: Double = 0.0
    private var toPrice: Double = 10000.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.listOfProductBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val titleOrCategory = arguments?.getString("titleOrCategory")
        val brandImageUrl = arguments?.getString("imageUrl")

        if (titleOrCategory != null) {
            when (titleOrCategory.uppercase()) {
                "SHOES", "T-SHIRTS", "ACCESSORIES" -> {
                    productListViewModel.filterProductsByCategory(titleOrCategory)
                    binding.listofProductFilterbutton.visibility = View.GONE
                }
                else -> {
                    // Treat it as a brand, filter by brand
                    productListViewModel.filterProductsByBrand(titleOrCategory)
                }
            }
        }



        setupProduct()
        observeOnFilterdProduct()


        if (brandImageUrl != null) {
            Glide.with(requireContext())
                .load(brandImageUrl)
                .apply(RequestOptions())
                .error(R.drawable.product)
                .into(binding.imageItem)
        } else {
            Log.e("ProductListFragment", "brandImageUrl is null")
        }

//        if (title != null) {
//            productListViewModel.filterProductsByBrand(title)
//        }
        binding.listofProductFilterbutton.setOnClickListener {
            filterDialog.show()
        }
        filterDialog = Dialog(requireContext())
        filterDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        filterBinding = DialogFilterBinding.inflate(layoutInflater)
        filterDialog.setContentView(filterBinding.root)

//        filterBinding.rgType.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                filterBinding.rbtnShoes.id -> {
//                    type = "SHOES"
//                }
//
//                filterBinding.rbtnShirt.id -> {
//                    type = "T-SHIRTS"
//                }
//
//                filterBinding.rbtnAccessories.id -> {
//                    type = "ACCESSORIES"
//                }
//            }
//        }

//        filterBinding.btnRemoveFilter.setOnClickListener {
//            type = ""
//            fromPrice = 0.0
//            toPrice = 1000.0
//            lifecycleScope.launch {
//                productListViewModel.productsByIdStateFlow.collectLatest {
//                    when (it) {
//                        is ApiState.Success -> {
//                            val productsResponse = it.data as ProductsResponse
//                            productRecycleAdapter.submitList(productsResponse.products)
//                        }
//
//                        else -> {}
//                    }
//                }
//            }


        filterBinding.apply {
            rbtnShoes.isChecked = false
            rbtnAccessories.isChecked = false
            rbtnShirt.isChecked = false
            etFromPrice.text.clear()
            etToPrice.text.clear()
        }

        filterDialog.dismiss()

//        filterBinding.btnSaveFilter.setOnClickListener {
//            lifecycleScope.launch {
//                homeViewModel.productsByIdStateFlow.collectLatest {
//                    when (it) {
//                        is ApiState.Success -> {
//                            val productsResponse = it.data as ProductsResponse
//                            val products = productsResponse.products.filter { product ->
//                                if (type != "") {
//                                    product.product_type == type
//                                } else {
//                                    true
//                                }
//                            }
//
//                            val filteredProducts = products.filter { product ->
//                                val fromPriceText = filterBinding.etFromPrice.text.toString()
//                                val toPriceText = filterBinding.etToPrice.text.toString()
//                                if (fromPriceText.isNotEmpty() && toPriceText.isNotEmpty()) {
//                                    if (currency == Constants.USD) {
//                                        fromPrice =
//                                            (fromPriceText.toDouble() / usdAmount.toDouble())
//                                        toPrice = (toPriceText.toDouble() / usdAmount.toDouble())
//                                    } else {
//                                        fromPrice = fromPriceText.toDouble()
//                                        toPrice = toPriceText.toDouble()
//                                    }
//                                    product.variants[0].price.toDouble() in fromPrice..toPrice
//                                } else {
//                                    true
//                                }
//                            }
//                            if (fromPrice < toPrice) {
//                                productRecycleAdapter.submitList(filteredProducts)
//                            } else {
//                                Toast.makeText(
//                                    requireContext(), "!Unable Price Filter", Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//
//                        else -> {}
//                    }
//                }
//            }
//            filterDialog.dismiss()
//        }
    }





    private fun setupProduct() {
        productRecycleAdapter = ProductListAdapterr(emptyList())
        binding.rvListOfProduct.apply {
             layoutManager = GridLayoutManager(context, 2)
            adapter = productRecycleAdapter
        }

    }

    private fun observeOnFilterdProduct() {
        lifecycleScope.launch {
            productListViewModel.filteredProducts.collect{ products ->
                productRecycleAdapter.updateData(products)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::productRecycleAdapter.isInitialized) productRecycleAdapter.updateData(emptyList())
    }
}