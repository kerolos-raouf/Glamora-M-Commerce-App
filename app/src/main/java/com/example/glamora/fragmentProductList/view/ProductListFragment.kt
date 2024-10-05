package com.example.glamora.fragmentProductList.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
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
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.util.Constants
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


    private val communicator: Communicator by lazy {
        (requireContext() as Communicator)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onStart() {
        super.onStart()
        communicator.hideBottomNav()
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
        binding.listOfProductIdentity.text = titleOrCategory


        if (titleOrCategory != null) {
            when (titleOrCategory.uppercase()) {
                "SHOES", "T-SHIRTS", "ACCESSORIES" -> {
                    productListViewModel.filterProductsByCategory(titleOrCategory)
                    binding.listofProductFilterbutton.visibility = View.GONE
                }

                else -> {
                    productListViewModel.filterProductsByBrand(titleOrCategory)
                }
            }
        }



        setupProduct()
        observeOnFilterdProduct()




        filterDialog = Dialog(requireContext())
        filterDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        filterBinding = DialogFilterBinding.inflate(layoutInflater)
        filterDialog.setContentView(filterBinding.root)

        binding.listofProductFilterbutton.setOnClickListener {
            filterDialog.show()
        }

        filterBinding.rgType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                filterBinding.rbtnShoes.id -> {
                    type = Constants.SHOES
                }

                filterBinding.rbtnShirt.id -> {
                    type = Constants.T_SHIRT
                }

                filterBinding.rbtnAccessories.id -> {
                    type = Constants.ACCESSEORIES
                }
            }
        }

        filterBinding.btnRemoveFilter.setOnClickListener {
            filterBinding.apply {
                rbtnShoes.isChecked = false
                rbtnAccessories.isChecked = false
                rbtnShirt.isChecked = false
                observeOnFilterdProduct()
                filterDialog.dismiss()
            }
        }

        filterBinding.btnSaveFilter.setOnClickListener {
            lifecycleScope.launch {
                productListViewModel.filteredProducts.collect { productList ->
                    val filteredList = when (type) {
                        Constants.SHOES -> productList.filter { it.category == Constants.SHOES }
                        Constants.T_SHIRT -> productList.filter { it.category == Constants.T_SHIRT }
                        Constants.ACCESSEORIES -> productList.filter { it.category == Constants.ACCESSEORIES }
                        else -> productList
                    }


                    productRecycleAdapter.updateData(filteredList)

                    filterDialog.dismiss()
                }
            }
        }
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
            //binding.listOfProductLoading.visibility = View.VISIBLE
            productListViewModel.filteredProducts.collect { products ->
                //binding.listOfProductIdentity.visibility = View.GONE
                productRecycleAdapter.updateData(products)
            }

        }
    }

}