package com.fictivestudios.wheatherapp.ui.home.itemView

import androidx.databinding.ViewDataBinding
import com.fictivestudios.wheatherapp.R
import com.fictivestudios.wheatherapp.base.adapter.OnItemClickListener
import com.fictivestudios.wheatherapp.base.adapter.ViewType
import com.fictivestudios.wheatherapp.data.responses.WeatherData
import com.fictivestudios.wheatherapp.databinding.RowItemCurrentWeatherBinding

class RowItemCurrentWeather(private val data: WeatherData) : ViewType<WeatherData> {

    override fun layoutId(): Int {
        return R.layout.row_item_current_weather
    }

    override fun data(): WeatherData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemCurrentWeatherBinding).also { binding ->
            binding.imageView.setImageResource(data.image)
            binding.textViewTitle.text = data.title
            binding.textViewMeasurement.text = data.measurement
        }
    }
}