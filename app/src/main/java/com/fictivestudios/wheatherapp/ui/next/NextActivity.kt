package com.fictivestudios.wheatherapp.ui.next

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fictivestudios.wheatherapp.R
import com.fictivestudios.wheatherapp.base.activity.BaseActivity
import com.fictivestudios.wheatherapp.base.adapter.GenericListAdapter
import com.fictivestudios.wheatherapp.base.adapter.OnItemClickListener
import com.fictivestudios.wheatherapp.base.adapter.ViewType
import com.fictivestudios.wheatherapp.base.response.Resource
import com.fictivestudios.wheatherapp.databinding.ActivityNextBinding
import com.fictivestudios.wheatherapp.ui.home.HomeActivity
import com.fictivestudios.wheatherapp.ui.next.itemView.RowItemNextFiveDayWeather
import com.fictivestudios.wheatherapp.utils.checkIfDateIsToday
import com.kennyc.view.MultiStateView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NextActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: ActivityNextBinding? = null
    val binding
        get() = _binding!!

    private val viewModel: NextFiveDayWeatherViewModel by viewModels()

    private var viewTypeArray = ArrayList<ViewType<*>>()

    private val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        binding.recyclerView.adapter = adapter

        binding.multiStateView.viewState =
            MultiStateView.ViewState.LOADING

        lifecycleScope.launch {
            viewModel.getFiveDataWeatherData(
                viewModel.fetchLocationData()?.currentLatitude ?: 0.0,
                viewModel.fetchLocationData()?.currentLongitude ?: 0.0
            )
        }
    }

    override fun setObserver() {
        viewModel.getFiveDayWeatherResponse.observe(this) {
            binding.swipeRefreshLayout.isRefreshing = false
            when (it) {
                is Resource.Success -> {
                    viewTypeArray.clear()

                    it.value.list.let { data ->
                        if (data.isNotEmpty()) {
                            for (weatherData in data) {
                                val isToday = checkIfDateIsToday(weatherData.dt_txt)
                                if (!isToday) {
                                    viewTypeArray.add(RowItemNextFiveDayWeather(weatherData))
                                }
                            }
                            adapter.items = viewTypeArray
                        }

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
    }

    override fun setOnClickListener() {
        binding.imageViewBack.setOnClickListener {
            startActivity(Intent(this@NextActivity, HomeActivity::class.java))
            finish()
        }
        binding.swipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        lifecycleScope.launch {
            viewModel.getFiveDataWeatherData(
                viewModel.fetchLocationData()?.currentLatitude ?: 0.0,
                viewModel.fetchLocationData()?.currentLongitude ?: 0.0
            )
        }
        binding.multiStateView.viewState =
            MultiStateView.ViewState.LOADING
    }
}