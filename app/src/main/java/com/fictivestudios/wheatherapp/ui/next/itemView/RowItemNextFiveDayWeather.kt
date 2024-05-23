package com.fictivestudios.wheatherapp.ui.next.itemView

import androidx.databinding.ViewDataBinding
import com.fictivestudios.wheatherapp.R
import com.fictivestudios.wheatherapp.base.adapter.OnItemClickListener
import com.fictivestudios.wheatherapp.base.adapter.ViewType
import com.fictivestudios.wheatherapp.data.responses.WeatherList
import com.fictivestudios.wheatherapp.databinding.RowItemNext5DayBinding
import com.fictivestudios.wheatherapp.utils.getDayName
import com.fictivestudios.wheatherapp.utils.kelvinToCelsius

class RowItemNextFiveDayWeather(private val data: WeatherList) : ViewType<WeatherList> {

    override fun layoutId(): Int {
        return R.layout.row_item_next_5_day
    }

    override fun data(): WeatherList {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemNext5DayBinding).also { binding ->
            binding.imageView.setImageResource(R.drawable.ic_sun_white_cloud)
            binding.textViewDaysTitle.text = getDayName(data.dt_txt)
            val temperatureCelsius = kelvinToCelsius(data.main.temp.toInt())
            binding.textViewCelcious.text = "$temperatureCelsius °C"

        }
    }
}