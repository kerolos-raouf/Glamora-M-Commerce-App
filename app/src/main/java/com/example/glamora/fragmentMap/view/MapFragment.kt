package com.example.glamora.fragmentMap.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.glamora.R
import com.example.glamora.databinding.FragmentMapBinding
import com.example.glamora.fragmentMap.viewModel.MapViewModel
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@AndroidEntryPoint
class MapFragment : Fragment() {



    private val mapViewModel : MapViewModel by viewModels()

    private lateinit var binding: FragmentMapBinding

    //communicator
    private val communicator by lazy {
        (requireActivity() as Communicator)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        autoCompleteEditTextSetup()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(FlowPreview::class)
    private fun autoCompleteEditTextSetup()
    {
        //auto complete edit text setup
        lifecycleScope.launch {
            val sharedFlow = MutableSharedFlow<String>()
            binding.mapAutoCompleteEditText.doOnTextChanged{ text,_,_,_ ->
                launch {
                    sharedFlow.emit(text.toString())
                }
            }

            sharedFlow
                .debounce(1000)
                .distinctUntilChanged()
                .collect{cityName->
                    mapViewModel.getCitiesForSearch(cityName)
                }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                mapViewModel.citiesForSearch.collect{newCitiesList->
                    if(newCitiesList.isNotEmpty())
                    {
                        val citiesList = newCitiesList.map { city->
                            "${city.name}, ${city.country}"
                        }
                        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,citiesList)
                        binding.mapAutoCompleteEditText.apply {
                            setAdapter(adapter)
                            threshold = 1
                            refreshAutoCompleteResults()

                            setOnItemClickListener { parent, view, position, id ->
                                val selectedCity = newCitiesList[position]
                                val geoPoint = GeoPoint(selectedCity.latitude,selectedCity.longitude)
                                zoomToLocation(geoPoint)
                                addMarker(geoPoint)
                            }
                        }
                    }
                }
            }
        }

    }

    private fun initMap() {
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences(
            Constants.MAP_SHARED_PREFERENCE_NAME, MODE_PRIVATE))

        binding.mapView.apply {
            setMultiTouchControls(true)
            controller.setZoom(5.0)

            //set max and min zoom
            maxZoomLevel = 30.0
            minZoomLevel = 3.0


            val mapEventReceiver = object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?) = false

                override fun longPressHelper(p: GeoPoint?): Boolean {
                    p?.let {
                        onMapLongPressActions(p)
                    }
                    return true
                }

            }

            overlays.add(MapEventsOverlay(mapEventReceiver))
        }
    }

    private fun onMapLongPressActions(geoPoint: GeoPoint)
    {
        if(communicator.isInternetAvailable())
        {
            zoomToLocation(geoPoint)
            addMarker(geoPoint)
        }else
        {
            Toast.makeText(requireContext(),"No internet connection.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun zoomToLocation(geoPoint : GeoPoint)
    {
        binding.mapView.controller.apply {
            animateTo(geoPoint,17.0,2000)
        }
    }

    private fun addMarker(geoPoint : GeoPoint)
    {
        val marker = Marker(binding.mapView)

        marker.apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = requireContext().getDrawable(R.drawable.icon_favorite_location)
        }
        binding.mapView.apply {
            overlays.add(marker)
            invalidate()
        }
    }




}