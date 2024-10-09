package com.example.glamora.fragmentProductDetails.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.glamora.R
import com.example.glamora.data.model.AvailableProductsModel
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.model.FavoriteItemDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.databinding.FragmentProductDetailsBinding
import com.example.glamora.fragmentProductDetails.view.adapters.ColorsAdapter
import com.example.glamora.fragmentProductDetails.view.adapters.SizesAdapter
import com.example.glamora.fragmentProductDetails.view.adapters.ViewPagerAdapter
import com.example.glamora.fragmentProductDetails.viewModel.ProductDetailsViewModel
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val productDetailsViewModel: ProductDetailsViewModel by viewModels()
    private lateinit var productDetailsBinding: FragmentProductDetailsBinding

    private lateinit var sizesAdapter: SizesAdapter

    private lateinit var colorsAdapter: ColorsAdapter

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator

    private var productID = "8309560443018"
    private var productDTO: ProductDTO? = null
    private var isFavorite = false
    private lateinit var variante: AvailableProductsModel


    private val communicator: Communicator by lazy {
        (requireActivity() as Communicator)
    }

    override fun onStart() {
        super.onStart()
        communicator.hideBottomNav()
        productDetailsViewModel.fetchCartItems(sharedViewModel.currentCustomerInfo.value.userId.split("/")[4])
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_details, container, false)

        sharedViewModel.fetchFavoriteItems()
        sharedViewModel.fetchProducts()

        return productDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments?.isEmpty == false){
            productID = arguments?.getString("productId").toString()
        }


        lifecycleScope.launch {
            sharedViewModel.productList.collect { _ ->
                productDTO = sharedViewModel.getProductByID(productID)

                if(productDTO != null)
                {
                    val productDetails = getProductDetails(productDTO!!)
                    variante = productDTO!!.availableProducts[0]
                    updateUI(productDetails)

                    isFavorite = sharedViewModel.isFavorite(productID = productDTO!!.availableProducts[0].id)
                    if(isFavorite){
                        productDetailsBinding.favBtn.setImageResource(R.drawable.ic_favorite)
                    }
                }
            }
        }


        productDetailsBinding.favBtn.setOnClickListener{
            if(isFavorite){
                productDetailsBinding.favBtn.setImageResource(R.drawable.ic_favorite_border)

                if(productDTO != null){
                    sharedViewModel.deleteFromFavorites(FavoriteItemDTO(variante.id,"",
                        productDTO!!.title,
                        variante.price,
                        productDTO!!.mainImage))
                }

                isFavorite = false

            }
            else{
                productDetailsBinding.favBtn.setImageResource(R.drawable.ic_favorite)

                if (productDTO != null){
                    sharedViewModel.addToFavorites(FavoriteItemDTO(variante.id,"",
                        productDTO!!.title,
                        variante.price,
                        productDTO!!.mainImage))
                }

                isFavorite = true
            }
        }

        productDetailsBinding.reviewsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_productDetailsFragment_to_reviewsFragment)
        }

        productDetailsBinding.addCardBtn.setOnClickListener{

            productDetailsViewModel.addToCard(CartItemDTO(
                id = variante.id,
                draftOrderId = "",
                title = "",
                quantity = 1,
                inventoryQuantity = 0,
                price = variante.price,
                image = "",
                isFavorite = isFavorite
            ), sharedViewModel.currentCustomerInfo.value.userId.split("/")[4] ,sharedViewModel.currentCustomerInfo.value.email)
        }

    }

    private fun getProductDetails(productDTO: ProductDTO): Map<String, Any> {
        val productTitle = productDTO.title
        val productDescription = productDTO.description
        val productMainImage = productDTO.mainImage
        val productImages =  productDTO.images + productMainImage
        val productColors = productDTO.availableColors
        val productSizes = productDTO.availableSizes
        val availableProductsList = productDTO.availableProducts

        return mapOf(
            "title" to productTitle,
            "description" to productDescription,
            "images" to productImages,
            "colors" to productColors,
            "sizes" to productSizes,
            "availableProducts" to availableProductsList
        )
    }

    private fun updateUI(productDetails: Map<String, Any> ){

        val imagesList = productDetails["images"] as List<String>

        val titleText = productDetails["title"] as String

        val desText = productDetails["description"] as String

        val availableProducts = productDetails["availableProducts"] as List<AvailableProductsModel>

        val colors = productDetails["colors"] as List<String>



        updateMainUI(titleText , desText , variante.price)

        updateImageList(imagesList)

        updateColors(colors)

        updateSizes(availableProducts)

    }

    private fun updateMainUI(titleText: String, desText: String, price: String){

        val titleList = titleText.split("|")
        productDetailsBinding.title.text = "${titleList[1]}"

        productDetailsBinding.desTxt.text = desText

        productDetailsBinding.price.text = "$$price"

    }

    private fun updateImageList(imagesList: List<String>) {
        viewPager = productDetailsBinding.changeImageView
        dotsIndicator = productDetailsBinding.dotsIndicator
        viewPagerAdapter = ViewPagerAdapter(imagesList)
        viewPager.adapter = viewPagerAdapter
        dotsIndicator.setViewPager2(viewPager)
    }

    private fun updateSizes(availableProducts: List<AvailableProductsModel>){
        sizesAdapter = SizesAdapter(availableProducts){ v ->
            variante  = v
            productDetailsBinding.price.text = "$${variante.price}"
        }
        productDetailsBinding.recSizes.adapter = sizesAdapter
    }

    private fun updateColors(colors: List<String>) {
        val colorMap = mapOf(
            "WHITE" to 0,
            "BLACK" to 1,
            "BURGANDY" to 2,
            "YELLOW" to 3,
            "Blue" to 4,
            "RED" to 5,
            "PURPLE" to 6,
        )

        val selectedColor = colors[0].uppercase()
        val colorNumber = colorMap[selectedColor] ?: -1

        val colorsList = listOf(
            "#FFFFFF",
            "#000000",
            "#FB7181",
            "#FFC833",
            "#223263",
            "#EE4040",
            "#5C61F4"
        )

        colorsAdapter = ColorsAdapter(colorsList, colorNumber)

        productDetailsBinding.recColors.apply {
            adapter = colorsAdapter
        }
    }

}