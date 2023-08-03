package com.adsama.weatherapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.adsama.model.Alert
import com.adsama.model.ForecastDay
import com.adsama.model.Hour
import com.adsama.weatherapp.databinding.FragmentWeatherDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherDetailFragment : Fragment() {

    private lateinit var mWeatherDetailBinding: FragmentWeatherDetailBinding
    private val mWeatherDetailViewModel: WeatherDetailViewModel by viewModels()
    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter
    private lateinit var fiveDayForecastAdapter: FiveDayForecastAdapter
    private lateinit var alertForecastAdapter: AlertForecastAdapter
    private lateinit var mCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mWeatherDetailBinding = FragmentWeatherDetailBinding.inflate(inflater, container, false)
        mWeatherDetailBinding.viewModel = mWeatherDetailViewModel
        mWeatherDetailBinding.lifecycleOwner = this
        mWeatherDetailViewModel.getForecastData(
            WeatherDetailFragmentArgs.fromBundle(
                requireArguments()
            ).locationName
        )
        mWeatherDetailBinding.ivDetailBack.setOnClickListener {
            mCallback.handleOnBackPressed()
        }
        observeData()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, mCallback)
        return mWeatherDetailBinding.root
    }

    private fun observeData() {
        mCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
                mWeatherDetailViewModel.clearVm()
            }
        }
        mWeatherDetailViewModel.hourlyResponse.observe(viewLifecycleOwner) { hoursList ->
            val currentTimeEpoch = System.currentTimeMillis() / 1000
            val filteredList = hoursList.filter { it.time_epoch >= currentTimeEpoch }
            setupHourlyAdapter(filteredList)
        }
        mWeatherDetailViewModel.fiveDayForecastResponse.observe(viewLifecycleOwner) {
            setupFiveDayForecastAdapter(it)
        }
        mWeatherDetailViewModel.alertsResponse.observe(viewLifecycleOwner) {
            setupAlertForecastAdapter(it)
        }
        mWeatherDetailViewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupHourlyAdapter(hourlyList: List<Hour>) {
        hourlyForecastAdapter = HourlyForecastAdapter()
        mWeatherDetailBinding.clHourly.rvHourly.adapter = hourlyForecastAdapter
        hourlyForecastAdapter.submitList(hourlyList)
    }

    private fun setupFiveDayForecastAdapter(forecastList: List<ForecastDay>) {
        fiveDayForecastAdapter = FiveDayForecastAdapter()
        mWeatherDetailBinding.clForecast.rvFiveDayForecast.adapter = fiveDayForecastAdapter
        fiveDayForecastAdapter.submitList(forecastList)
    }

    private fun setupAlertForecastAdapter(alertList: List<Alert>) {
        alertForecastAdapter = AlertForecastAdapter()
        mWeatherDetailBinding.clAlerts.rvAlerts.adapter = alertForecastAdapter
        alertForecastAdapter.submitList(alertList)
    }

}