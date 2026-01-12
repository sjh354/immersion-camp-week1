package com.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.data.remote.dto.Menu
import com.data.remote.dto.MenuListDto
import com.data.remote.dto.requestMenuListByGpsDto
import com.data.repository.UserRepository
import com.example.test1.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class gpsInfo(
    val latitude: Double,
    val longitude: Double
)

class GalleryActivity : Activity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }
    private lateinit var adapter: MenuAdapter
    private val repo = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery)
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: return


        val titleView = findViewById<TextView>(R.id.TitleOnGallery)
        titleView.text = category

        requestLocationPermission()
        val curgps = getLocation()
        val reqBody = requestMenuListByGpsDto(
            sort="gps",
            latitude = curgps.latitude,
            longitude = curgps.longitude
        )

        repo.getMenusByGPS(category, reqBody).enqueue(object : Callback<MenuListDto> {
            override fun onResponse(call: Call<MenuListDto>, response: Response<MenuListDto>) {
                if (response.isSuccessful) {
                    val resp = response.body()
                    val menus = resp?.data ?: emptyList<Menu>()
                    adapter.setData(menus)
                } else {
                    Log.e("API", "server error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<MenuListDto>, t: Throwable) {
                Log.e("API", "network error", t)
            }
        })
//        이건 걍 리스트 불러오는거
//        repo.getMenus(category).enqueue(object : Callback<MenuListDto> {
//            override fun onResponse(call: Call<MenuListDto>, response: Response<MenuListDto>) {
//                if (response.isSuccessful) {
//                    val resp = response.body()
//                    Log.d("API", "server response: ${resp}")
//
//                    val menus = resp?.data ?: emptyList<Menu>()
//                    adapter.setData(menus)
//
//                } else {
//                    Log.e("API", "server error: ${response.code()}")
//                }
//            }
//            override fun onFailure(call: Call<MenuListDto>, t: Throwable) {
//                Log.e("API", "network error", t)
//            }
//        })


        val recyclerView = findViewById<RecyclerView>(R.id.GalleryList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        adapter = MenuAdapter(emptyList<Menu>()) { menu ->
            intent = Intent(this, InfoActivity::class.java)
            intent.putExtra(InfoActivity.Companion.EXTRA_MENU, menu)
            startActivity(intent)
        }

        val btnNext = findViewById<Button>(R.id.BackToSearch)

        btnNext.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        recyclerView.adapter = adapter


        val spinner = findViewById<Spinner>(R.id.Spinner)
        val button = findViewById<ImageButton>(R.id.SpinnerButton)

        val items = listOf("All", "Price ↑", "Price ↓")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        button.setOnClickListener {
            spinner.performClick()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 이곳에 정렬 함수 실행
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없을 경우, 사용자에게 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            // 권한이 이미 있을 경우, 위치 정보를 사용할 수 있음
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // 권한이 부여되면 위치 정보를 사용할 수 있음
                    getLocation()
                } else {
                    // 권한이 거부되면, 기능 사용 불가
                    Log.d("GPS Service", "GPS denied")
                }
                return
            }
        }
    }
    private fun getLocation():gpsInfo {
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location: Location? = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            location?.let {
                val latitude = location.latitude
                val longitude = location.longitude
                val accuracy = location.accuracy
                val time = location.time
                Log.d("map_test", "$latitude, $longitude, $accuracy, $time")
                return gpsInfo(
                    latitude = latitude,
                    longitude = longitude
                )
            }
        }
        return gpsInfo(
            latitude = 0.0,
            longitude = 0.0
        )
    }
}

class MenuAdapter(
    private val menus: List<Menu>,
    private val onItemClick: (Menu) -> Unit

) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private var filteredMenus: List<Menu> = menus
    private var lastAnimatedPosition = -1

    fun setData(newMenus: List<Menu>) {
        filteredMenus = newMenus
        lastAnimatedPosition = -1
        notifyDataSetChanged()
    }

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val iv = itemView.findViewById<ImageView>(R.id.ivPhoto);

        private val tv = itemView.findViewById<TextView>(R.id.tvPrice)

        fun bind(item: Menu) {
            Glide.with(itemView)
                .load(item.img)
                .centerCrop()
                .into(iv)

            tv.text = item.price.toString()   // or "₩${item.price}"
            tv.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return MenuViewHolder(v)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(filteredMenus[position])


        val adapterPosition = holder.bindingAdapterPosition
        if (adapterPosition == RecyclerView.NO_POSITION) return


        if (adapterPosition > lastAnimatedPosition) {
            val anim = AnimationUtils.loadAnimation(
                holder.itemView.context,
                if (adapterPosition % 2 == 0)
                    R.anim.slide_fade_left
                else
                    R.anim.slide_fade_right
            )
            holder.itemView.startAnimation(anim)
            lastAnimatedPosition = adapterPosition
        }

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onItemClick(filteredMenus[pos])
            }
        }
    }

    override fun getItemCount() = filteredMenus.size
}

