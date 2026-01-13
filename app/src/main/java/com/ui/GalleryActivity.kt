package com.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.data.remote.dto.Menu
import com.data.remote.dto.MenuListDto
import com.data.remote.dto.requestMenuListBySortingDto
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
    private val repo = UserRepository(this)
    private var saved_category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery)
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: return
        saved_category = category
        val titleView = findViewById<TextView>(R.id.TitleOnGallery)
        titleView.text = category

        requestLocationPermission()

        repo.getMenus(category).enqueue(object : Callback<MenuListDto> {
            override fun onResponse(call: Call<MenuListDto>, response: Response<MenuListDto>) {
                if (response.isSuccessful) {
                    val resp = response.body()
                    Log.d("API", "server response: ${resp}")

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

        button.setOnClickListener {
            spinner.performClick()
        }
        val spinner_adapter = CustomSpinnerAdapter(this, listOf("가격 오름차순", "가격 내림차순", "거리 가까운순"))
        spinner.adapter = spinner_adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinner_adapter.setSelectedPosition(position)

                val selectedItem = parent.getItemAtPosition(position) as String
                when (selectedItem) {
                    "가격 오름차순" -> callMenuListAPI(category, "price_asc")
                    "가격 내림차순" -> callMenuListAPI(category, "price_desc")
                    "거리 가까운순" -> callMenuListAPI(category, "gps")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


    }

    override fun onResume() {
        super.onResume()
        val titleView = findViewById<TextView>(R.id.TitleOnGallery)
        val category = titleView.text ?: return
        callMenuListAPI(category.toString(), "price_asc")
    }

    private fun callMenuListAPI(category: String, pivot: String) {
        var reqBody = requestMenuListBySortingDto(
            sort=pivot,
            latitude = 0.0,
            longitude = 0.0
        )
        if (pivot == "gps") {
            val curgps = getLocation()
            reqBody = requestMenuListBySortingDto(
                sort="gps",
                latitude = curgps.latitude,
                longitude = curgps.longitude
            )
        }
        repo.getMenusBySorting(category, reqBody).enqueue(object : Callback<MenuListDto> {
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
        private val screen = itemView.findViewById<FrameLayout>(R.id.screen)
        private val rating = itemView.findViewById<RatingBar>(R.id.rating)
        private val tv = itemView.findViewById<TextView>(R.id.tvPrice)

        fun bind(item: Menu) {
            Glide.with(itemView)
                .load(
                if (item.img != "no-image") {
                    item.img
                } else {
                    R.drawable.no_image
                })
                .centerCrop()
                .into(iv)

            tv.text = item.price.toString()   // or "₩${item.price}"
            tv.visibility = View.VISIBLE
            rating.rating = item.starpoint
            if(item.isFavorite) {
                screen.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.color_1))
            }
            else{
                screen.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            }
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

class CustomSpinnerAdapter(
    context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, 0, items) {

    private var selectedPosition = -1

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item, parent, false)

        view.findViewById<TextView>(R.id.tvItem).text = items[position]
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_dropdown_item, parent, false)

        val textView = view.findViewById<TextView>(R.id.tvDropdown)
        textView.text = items[position]

        if (position == selectedPosition) {
            textView.setBackgroundResource(R.drawable.spinner_selected_bg)
            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
        } else {
            textView.setBackgroundColor(Color.TRANSPARENT)
            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        return view
    }
}


