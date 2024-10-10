package com.example.glamora.fragmentFavorites.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.glamora.R
import com.example.glamora.data.model.FavoriteItemDTO
import com.example.glamora.databinding.FragmentFavoritesBinding
import com.example.glamora.fragmentFavorites.viewModel.FavoritesViewModel
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.State
import com.example.glamora.util.customAlertDialog.CustomAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var favoritesBinding: FragmentFavoritesBinding
    private lateinit var fvAdapter: FavoritesAdapter


    private lateinit var customAlertDialog: CustomAlertDialog

    private val communicator by lazy { requireActivity() as Communicator }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favoritesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)
        return favoritesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customAlertDialog = CustomAlertDialog(requireActivity())

        sharedViewModel.fetchFavoriteItems()

        fvAdapter = FavoritesAdapter(object : FavoritesClickListener{
            override fun onDeleteClick(product: FavoriteItemDTO) {
                customAlertDialog.showAlertDialog("Are you sure you want to delete this item?", "Delete"){
                    sharedViewModel.deleteFromFavorites(product)
                }
            }

            override fun onItemClick(product: FavoriteItemDTO) {
                val action = FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailsFragment(product.productId)
                findNavController().navigate(action)
            }

        })

        favoritesBinding.recFavorites.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = fvAdapter
        }

        favoritesBinding.favoritesBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }


        lifecycleScope.launch {
            sharedViewModel.favoriteItemsState.collect{state ->
                when (state) {
                    is State.Loading -> {
                        favoritesBinding.progressBar.visibility = View.VISIBLE
                    }
                    is State.Success -> {
                        favoritesBinding.progressBar.visibility = View.GONE
                        val favoriteItems = state.data
                        Log.d("Abanob", "onViewCreated: ${favoriteItems.size}")
                        fvAdapter.submitList(favoriteItems)
                    }
                    is State.Error -> {
                        favoritesBinding.progressBar.visibility = View.GONE
                        val errorMessage = state.message
                        Toast.makeText(requireContext(),state.message,Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
        communicator.hideBottomNav()
    }

}