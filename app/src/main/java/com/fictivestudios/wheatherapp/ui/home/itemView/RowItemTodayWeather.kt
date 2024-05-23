package com.fictivestudios.wheatherapp.ui.home.itemView

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.fictivestudios.wheatherapp.R
import com.fictivestudios.wheatherapp.base.adapter.OnItemClickListener
import com.fictivestudios.wheatherapp.base.adapter.ViewType
import com.fictivestudios.wheatherapp.data.responses.WeatherList
import com.fictivestudios.wheatherapp.databinding.RowItemTodayWeatherBinding
import com.fictivestudios.wheatherapp.utils.extractTime
import com.fictivestudios.wheatherapp.utils.kelvinToCelsius

class RowItemTodayWeather(private val data: WeatherList) : ViewType<WeatherList> {

    override fun layoutId(): Int {
        return R.layout.row_item_today_weather
    }

    override fun data(): WeatherList {
        return data
    }

    @SuppressLint("SetTextI18n")
    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemTodayWeatherBinding).also { binding ->
          binding.imageView.setImageResource(R.drawable.ic_sun_white_cloud)

            binding.textViewTime.text = extractTime(data.dt_txt)
            val temperatureCelsius = kelvinToCelsius(data.main.temp.toInt())
            binding.textViewCelcious.text = "$temperatureCelsius °C"

            if(position == 1){
                binding.cardView.setCardBackgroundColor(ContextCompat.getColor(binding.cardView.context,R.color.white))
                binding.textViewTime.setTextColor(ContextCompat.getColor(binding.cardView.context,R.color.text_color_gray2))
            }else{
                binding.cardView.setCardBackgroundColor(ContextCompat.getColor(binding.cardView.context,R.color.white_half_opacity))
                binding.textViewTime.setTextColor(ContextCompat.getColor(binding.cardView.context,R.color.text_color_gray))
            }
        }
    }
}