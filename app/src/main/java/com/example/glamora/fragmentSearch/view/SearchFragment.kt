package com.example.glamora.fragmentSearch.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glamora.R
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.databinding.FragmentSearchBinding
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var searchBinding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter

    private val communicator: Communicator by lazy {
        (requireActivity() as Communicator)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return searchBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchView()
        setupObserver()
        setupRecyclerView()
    }

    private fun setupSearchView() {
        searchBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search submit action if necessary
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(communicator.isInternetAvailable())
                {
                    sharedViewModel.filterList(newText.orEmpty())
                    updateSearchViewAppearance(newText)

                    return true
                }else{
                    return false
                }
            }
        })
    }

    private fun updateSearchViewAppearance(query: String?) {
        if (!query.isNullOrEmpty()) {
            searchBinding.searchView.setBackgroundResource(R.drawable.button_background_focused)
            communicator.hideBottomNav()
        } else {
            searchBinding.searchView.setBackgroundResource(R.drawable.button_background)
            communicator.showBottomNav()

        }
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter(object : SearchClickListener {
            override fun onItemClick(product: ProductDTO) {

                if(!communicator.isInternetAvailable())
                {
                    Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
                }else{
                    val bundle = Bundle().apply {
                        putString("productId", product.id)
                    }
                    findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment, bundle)
                }
            }
        })

        searchBinding.recSearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    private fun setupObserver() {
        lifecycleScope.launch {
            sharedViewModel.filteredResults.collectLatest { results ->
                searchAdapter.submitList(results)
            }
        }
    }
}
