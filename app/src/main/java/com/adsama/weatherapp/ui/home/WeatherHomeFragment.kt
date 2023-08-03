package com.adsama.weatherapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.adsama.database.PersistedWeatherModel
import com.adsama.model.SearchResponse
import com.adsama.weatherapp.databinding.FragmentWeatherHomeBinding
import com.adsama.weatherapp.ui.WeatherLocationService
import com.adsama.weatherapp.utils.DebouncedTextWatcher
import com.adsama.weatherapp.utils.ItemClickInterface
import com.adsama.weatherapp.utils.LocationCallbacks
import com.adsama.weatherapp.utils.SwipeToDeleteCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherHomeFragment : Fragment(), LocationCallbacks,
    ItemClickInterface.SelectedLocationInterface, ItemClickInterface.SearchSuggestionInterface {

    private lateinit var mFragmentWeatherHomeBinding: FragmentWeatherHomeBinding
    private lateinit var mRequestPermission: ActivityResultLauncher<String>
    private val mWeatherHomeViewModel: WeatherHomeViewModel by viewModels()
    private lateinit var savedLocationsResultAdapter: SavedLocationResultsAdapter
    private lateinit var searchSuggestionsAdapter: SearchSuggestionsAdapter

    private var isServiceBound = false
    private lateinit var locationService: WeatherLocationService
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as WeatherLocationService.LocalBinder
            locationService = binder.getService()
            locationService.setCallback(this@WeatherHomeFragment)
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mFragmentWeatherHomeBinding = FragmentWeatherHomeBinding.inflate(inflater, container, false)
        mFragmentWeatherHomeBinding.viewModel = mWeatherHomeViewModel
        mFragmentWeatherHomeBinding.lifecycleOwner = this
        mWeatherHomeViewModel.getAllSavedLocations()
        setupEditText()
        observeData()
        registerPermissionResult()
        return mFragmentWeatherHomeBinding.root
    }

    private fun observeData() {
        mWeatherHomeViewModel.savedLocationResults.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setupAdapter(it)
            }
        }
        mWeatherHomeViewModel.searchSuggestionsResult.observe(viewLifecycleOwner) {
            setupSearchSuggestionsAdapter(it as ArrayList<SearchResponse>)
            mFragmentWeatherHomeBinding.rvSearchSuggestions.visibility = View.VISIBLE
        }
        mWeatherHomeViewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupEditText() {
        mFragmentWeatherHomeBinding.etvSearch.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                if (!isLocationPermissionGranted()) {
                    // requestLocationPermission()
                } else {
                    extractLocation()
                }
            }
            false
        }
        val debouncedTextWatcher = DebouncedTextWatcher(500) {
            if (it.isNotEmpty()) {
                mWeatherHomeViewModel.searchLocation(it)
            } else {
                mFragmentWeatherHomeBinding.rvSearchSuggestions.visibility = View.GONE
                if (this::searchSuggestionsAdapter.isInitialized) {
                    searchSuggestionsAdapter.submitList(arrayListOf())
                }
            }
        }
        mFragmentWeatherHomeBinding.etvSearch.addTextChangedListener(debouncedTextWatcher)
    }

    private fun setupAdapter(persistedResult: ArrayList<PersistedWeatherModel>) {
        savedLocationsResultAdapter = SavedLocationResultsAdapter(this)
        mFragmentWeatherHomeBinding.rvSavedResults.adapter = savedLocationsResultAdapter
        savedLocationsResultAdapter.submitList(persistedResult)
        enableSwipeToDeleteAndUndo()
    }

    private fun setupSearchSuggestionsAdapter(searchSuggestionList: ArrayList<SearchResponse>) {
        searchSuggestionsAdapter = SearchSuggestionsAdapter(this)
        mFragmentWeatherHomeBinding.rvSearchSuggestions.adapter = searchSuggestionsAdapter
        searchSuggestionsAdapter.submitList(searchSuggestionList)
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    mWeatherHomeViewModel.removeLocationFromSaved(viewHolder.adapterPosition)
                }
            }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(mFragmentWeatherHomeBinding.rvSavedResults)
    }

    private fun navigateToDetails(locationName: String) {
        val navDirections =
            WeatherHomeFragmentDirections.actionWeatherHomeFragmentToWeatherDetailFragment(
                locationName
            )
        findNavController().navigate(navDirections)
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        mRequestPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun registerPermissionResult() {
        mRequestPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted)
                    extractLocation()
                else {
                    Toast.makeText(
                        requireContext(),
                        "Location Permission Denied!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun extractLocation() {
        val locationServiceIntent = Intent(requireActivity(), WeatherLocationService::class.java)
        locationServiceIntent.action = WeatherLocationService.ACTION_GET_LOCATION_ONCE
        requireContext().bindService(
            locationServiceIntent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
        startForegroundService(requireActivity(), locationServiceIntent)
    }

    override fun getLocation(latitude: Double, longitude: Double) {
        Toast.makeText(requireContext(), "LAT is $latitude, LON is $longitude", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            requireContext().unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    override fun getSelectedLocationClick(
        persistedWeatherModel: PersistedWeatherModel,
        position: Int
    ) {
        navigateToDetails(persistedWeatherModel.name)
        mWeatherHomeViewModel.clearVm()
    }

    override fun getSelectedSuggestionClick(searchResponse: SearchResponse, position: Int) {
        navigateToDetails(searchResponse.name)
        mWeatherHomeViewModel.clearVm()
    }

}