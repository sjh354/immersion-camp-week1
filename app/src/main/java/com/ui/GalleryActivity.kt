package com.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.data.remote.dto.Menu
import com.data.remote.dto.MenuListDto
import com.data.repository.UserRepository
import com.example.test1.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleryActivity : Activity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }
    private lateinit var adapter: MenuAdapter
    private val repo = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery)
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: return


        val titleView = findViewById<TextView>(R.id.TitleOnGallery)
        titleView.text = category

//        val inputStream = assets.open("test.json")
//        val dp = DataParser()
//        val filtered = dp.parseMenusInSpecificCategory(inputStream, category)

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

        recyclerView.adapter = adapter
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

