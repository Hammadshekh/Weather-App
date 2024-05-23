package com.fictivestudios.wheatherapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.fictivestudios.wheatherapp.R
import com.fictivestudios.wheatherapp.base.activity.BaseActivity
import com.fictivestudios.wheatherapp.base.adapter.GenericListAdapter
import com.fictivestudios.wheatherapp.base.adapter.OnItemClickListener
import com.fictivestudios.wheatherapp.base.adapter.ViewType
import com.fictivestudios.wheatherapp.base.response.Resource
import com.fictivestudios.wheatherapp.data.responses.LocationData
import com.fictivestudios.wheatherapp.data.responses.WeatherData
import com.fictivestudios.wheatherapp.data.responses.WeatherList
import com.fictivestudios.wheatherapp.databinding.ActivityHomeBinding
import com.fictivestudios.wheatherapp.databinding.DialogExitBinding
import com.fictivestudios.wheatherapp.ui.home.itemView.RowItemCurrentWeather
import com.fictivestudios.wheatherapp.ui.home.itemView.RowItemTodayWeather
import com.fictivestudios.wheatherapp.ui.next.NextActivity
import com.fictivestudios.wheatherapp.utils.GooglePlaceHelper
import com.fictivestudios.wheatherapp.utils.checkIfDateIsToday
import com.fictivestudios.wheatherapp.utils.checkIfDateIsTomorrow
import com.fictivestudios.wheatherapp.utils.getFormattedDate
import com.fictivestudios.wheatherapp.utils.kelvinToCelsius
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kennyc.view.MultiStateView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class HomeActivity : BaseActivity(), View.OnClickListener,
    GooglePlaceHelper.GooglePlaceDataInterface {

    private var _binding: ActivityHomeBinding? = null
    val binding
        get() = _binding!!

    private val currentWeatherList = arrayListOf<WeatherData>()
    private var todayHourlyWeatherList = arrayListOf<WeatherList>()
    private val tomorrowHourlyWeatherList = arrayListOf<WeatherList>()

    private var viewTypeArrayCurrentWeather = ArrayList<ViewType<*>>()
    private var viewTypeArrayHourlyWeather = ArrayList<ViewType<*>>()

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googlePlaceHelper: GooglePlaceHelper? = null

    private var dialogExitBinding: DialogExitBinding? = null
    private var dialog: AlertDialog? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app.
                getCurrentLocation()
            } else {
                // Explain to the user that the feature is unavailable because the feature requires a permission that the user has denied.
                showToast("Permission denied. Cannot get location.")
            }
        }

    private val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }

    private val adapter2 by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        setOnBackPressedListener()

        binding.textViewDate.text = getFormattedDate()
        binding.recyclerView.adapter = adapter
        binding.recyclerView2.adapter = adapter2

        lifecycleScope.launch {
            if (viewModel.fetchLocationData()?.currentLatitude == 0.0 || viewModel.fetchLocationData()?.currentLatitude == null) {
                fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this@HomeActivity)
                checkLocationPermissionAndGetLocation()
            } else {
                binding.multiStateView.viewState =
                    MultiStateView.ViewState.LOADING
                getCityName(
                    viewModel.fetchLocationData()?.currentLatitude ?: 0.0,
                    viewModel.fetchLocationData()?.currentLongitude ?: 0.0
                )
                binding.textViewCityName.text = viewModel.fetchLocationData()?.city.toString()
            }
        }
    }

    override fun setObserver() {
        viewModel.getTodayWeatherData.observe(this) {
            when (it) {
                is Resource.Success -> {

                    val temperatureCelsius = kelvinToCelsius(it.value.main.feels_like.toInt())
                    binding.textViewDegree.text = temperatureCelsius.toString()
                    binding.textViewWeatherType.text = it.value.weather[0].main

                    currentWeatherList.add(
                        WeatherData(
                            R.drawable.ic_wind,
                            "Wind",
                            "${it.value.wind.speed.toInt()}km/h"
                        )
                    )

                    currentWeatherList.add(
                        WeatherData(
                            R.drawable.ic_humidity,
                            "Humidity",
                            "${it.value.main.humidity}%"
                        )
                    )
                    viewTypeArrayCurrentWeather.clear()

                    for (data in currentWeatherList) {
                        viewTypeArrayCurrentWeather.add(
                            RowItemCurrentWeather(
                                data
                            )
                        )
                    }

                    adapter.items = viewTypeArrayCurrentWeather

                    lifecycleScope.launch {
                        delay(500)
                        _binding?.let { binding ->
                            if (adapter.items.size == 0) {
                                binding.multiStateView.viewState =
                                    MultiStateView.ViewState.EMPTY
                            } else {
                                binding.multiStateView.viewState =
                                    MultiStateView.ViewState.CONTENT
                            }
                        }
                    }
                }

                is Resource.Failure -> {
                    showToast(it.message.toString())

                    val imageView = binding.multiStateView.getView(
                        MultiStateView.ViewState.ERROR
                    )?.findViewById<AppCompatImageView>(R.id.imageViewError)
                    if (it.isNetworkError) {
                        imageView?.setImageResource(R.drawable.ic_error)
                    } else {
                        imageView?.setImageResource(R.drawable.ic_no_data)
                    }
                    binding.multiStateView.viewState = MultiStateView.ViewState.ERROR

                }

                is Resource.Loading -> {
                    binding.multiStateView.viewState =
                        MultiStateView.ViewState.LOADING
                }

                else -> {}
            }
        }

        viewModel.getFiveDayWeatherResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.value.list.let { data ->
                        for (weatherData in data) {

                            val isToday = checkIfDateIsToday(weatherData.dt_txt)
                            val isTomorrow = checkIfDateIsTomorrow(weatherData.dt_txt)

                            if (isToday) {
                                todayHourlyWeatherList.add(weatherData)
                                todayWeatherHourlyDataSetRecyclerView()
                            }

                            if (isTomorrow) {
                                tomorrowHourlyWeatherList.add(weatherData)
                            }
                        }
                    }
                }

                is Resource.Failure -> {
                    showToast(it.message.toString())

                    val imageView = binding.multiStateView2.getView(
                        MultiStateView.ViewState.ERROR
                    )?.findViewById<AppCompatImageView>(R.id.imageViewError)
                    if (it.isNetworkError) {
                        imageView?.setImageResource(R.drawable.ic_error)
                    } else {
                        imageView?.setImageResource(R.drawable.ic_no_data)
                    }
                    binding.multiStateView2.viewState = MultiStateView.ViewState.ERROR

                }

                is Resource.Loading -> {
                    binding.multiStateView.viewState =
                        MultiStateView.ViewState.LOADING
                }

                else -> {}
            }
        }
    }

    override fun setOnClickListener() {
        binding.scrollView.smoothScrollTo(0, binding.container2.top)
        binding.textViewToday.setOnClickListener(this)
        binding.textViewTomorrow.setOnClickListener(this)
        binding.textViewNext5Days.setOnClickListener(this)
        binding.imageViewSearch.setOnClickListener(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(v: View?) {
        when (v?.id) {
            binding.textViewToday.id -> {
                binding.imageViewTodayIndicator.show()
                binding.imageViewTomorrowIndicator.gone()

                binding.textViewToday.setTextColor(
                    ContextCompat.getColor(
                        this@HomeActivity,
                        R.color.black
                    )
                )

                binding.textViewTomorrow.setTextColor(
                    ContextCompat.getColor(
                        this@HomeActivity,
                        R.color.black_half_opacity
                    )
                )

                todayWeatherHourlyDataSetRecyclerView()
                adapter2.notifyDataSetChanged()
                binding.recyclerView2.scrollToPosition(0)
            }

            binding.textViewTomorrow.id -> {
                binding.imageViewTodayIndicator.gone()
                binding.imageViewTomorrowIndicator.show()

                binding.textViewToday.setTextColor(
                    ContextCompat.getColor(
                        this@HomeActivity,
                        R.color.black_half_opacity
                    )
                )

                binding.textViewTomorrow.setTextColor(
                    ContextCompat.getColor(
                        this@HomeActivity,
                        R.color.black
                    )
                )

                tomorrowWeatherHourlyDataSetRecyclerView()
                adapter2.notifyDataSetChanged()
                binding.recyclerView2.scrollToPosition(0)
            }

            binding.textViewNext5Days.id -> {
                startActivity(Intent(this@HomeActivity, NextActivity::class.java))
                finish()
            }

            binding.imageViewSearch.id -> {
                googlePlaceHelper =
                    GooglePlaceHelper(
                        this,
                        this,
                    )
                googlePlaceHelper?.openAutocompleteActivity()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog = null
        dialogExitBinding = null
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != RESULT_CANCELED) {
            if (requestCode == GooglePlaceHelper.REQUEST_CODE_PLACE_HELPER) {
                super.onActivityResult(requestCode, resultCode, data)
                googlePlaceHelper?.onActivityResultAutoCompleted(requestCode, resultCode, data)
            }
        }

    }

    override fun onPlaceActivityResult(
        longitude: Double,
        latitude: Double,
        locationName: String?
    ) {
        getCityName(latitude, longitude)
    }

    override fun onError(error: String?) {
        showToast(error.toString())
    }

    private fun setOnBackPressedListener() {
        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitMessageBox()
                }
            })
    }

    private fun checkLocationPermissionAndGetLocation() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                getCurrentLocation()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this permission.
                showToast("Location permission is needed to get current location")

            }

            else -> {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

            }
        }
    }

    private fun getCityName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: MutableList<Address>? =
                geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val cityName = addresses[0].locality
                val countryName = addresses[0].countryName
                val city = "${cityName},\n$countryName"
                lifecycleScope.launch {
                    viewModel.saveLocationData(LocationData(city, latitude, longitude))
                }
                runOnUiThread {
                    binding.textViewCityName.text = city
                }

                viewModel.getTodayWeather(latitude, longitude)
                viewModel.getFiveDataWeatherData(latitude, longitude)

                binding.multiStateView.viewState =
                    MultiStateView.ViewState.LOADING
                binding.multiStateView2.viewState =
                    MultiStateView.ViewState.LOADING


            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    getCityName(location.latitude, location.longitude)
                }
            }
        }

    }

    private fun todayWeatherHourlyDataSetRecyclerView() {
        viewTypeArrayHourlyWeather.clear()

        for (data in todayHourlyWeatherList) {
            viewTypeArrayHourlyWeather.add(
                RowItemTodayWeather(
                    data
                )
            )
        }
        adapter2.items = viewTypeArrayHourlyWeather

        lifecycleScope.launch {
            delay(500)
            _binding?.let { binding ->
                if (adapter2.items.size == 0) {
                    binding.multiStateView2.viewState =
                        MultiStateView.ViewState.EMPTY
                } else {
                    binding.multiStateView2.viewState =
                        MultiStateView.ViewState.CONTENT
                }
            }
        }
    }

    private fun tomorrowWeatherHourlyDataSetRecyclerView() {
        viewTypeArrayHourlyWeather.clear()

        for (data in tomorrowHourlyWeatherList) {
            viewTypeArrayHourlyWeather.add(
                RowItemTodayWeather(
                    data
                )
            )
        }
        adapter2.items = viewTypeArrayHourlyWeather

        lifecycleScope.launch {
            delay(500)
            _binding?.let { binding ->
                if (adapter2.items.size == 0) {
                    binding.multiStateView2.viewState =
                        MultiStateView.ViewState.EMPTY
                } else {
                    binding.multiStateView2.viewState =
                        MultiStateView.ViewState.CONTENT
                }
            }
        }
    }

    private fun showExitMessageBox() {
        // Build and show the alert dialog
        dialog = AlertDialog.Builder(this).create()

        dialogExitBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_exit,
            null,
            false
        )

        dialog?.let { dialog ->
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setView(dialogExitBinding!!.root)
            dialog.setCancelable(false)
            dialogExitBinding?.buttonCancel?.setOnClickListener {
                dialog.dismiss()
            }

            dialogExitBinding?.imageViewCancel?.setOnClickListener {
                dialog.dismiss()
            }
            dialogExitBinding?.buttonExit?.setOnClickListener {
                dialog.dismiss()
                finish()
            }
            dialog.show()
        }
    }
}