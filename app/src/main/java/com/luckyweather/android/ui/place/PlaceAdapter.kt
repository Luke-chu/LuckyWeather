package com.luckyweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.luckyweather.android.R
import com.luckyweather.android.logic.model.Place
import com.luckyweather.android.ui.weather.WeatherActivity

//class PlaceAdapter(private val fragment: Fragment, private val placeList: List<Place>) :
//记录选中的城市所更改
class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item,parent,false)
        //添加点击事件——从城市界面跳转到显示天气界面
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            //在选中某个城市后跳转到WeatherActivity，由于可能本来就在WeatherActivity中，此时并不需要跳转
            //只要去请求新选择的城市的天气信息就可以了
            val activity = fragment.activity
            if (activity is WeatherActivity){
                //如果在WeatherActivity中就关闭滑动菜单，给ViewModel赋新的经纬度和地区名的值,然后刷新天气信息
                activity.weatherBinding.drawerLayout.closeDrawers()
                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name
                activity.refreshWeather()
            }else{
                //如果在MainActivity中，就保持之前的处理逻辑
                val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                    putExtra("location_lng", place.location.lng)
                    putExtra("location_lat", place.location.lat)
                    putExtra("place_name",place.name)
                }
                fragment.startActivity(intent)
                activity?.finish()
            }
            //当点击任何子项布局时，在跳转到WeatherActivity之前，先调用PlaceViewModel的savePlace()方法来进行存储
            fragment.viewModel.savePlace(place) //记录选中的城市
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    override fun getItemCount(): Int {
        return placeList.size
    }
}