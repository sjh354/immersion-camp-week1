package com.example.test1

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide

class GalleryActivity : Activity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }
    private lateinit var adapter: MenuAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery)
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: return

        val TitleView = findViewById<TextView>(R.id.TitleOnGallery)
        TitleView.text = category

        val inputStream = assets.open("test.json")
        val dp = DataParser()
        val filtered = dp.parseMenusInSpecificCategory(inputStream, category)

        val recyclerView = findViewById<RecyclerView>(R.id.GalleryList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        adapter = MenuAdapter(filtered) { menu ->
            Log.d(menu, "HELLO")
        }
        recyclerView.adapter = adapter
    }
}

class MenuAdapter(
    private val menus: List<Menu>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val iv = itemView.findViewById<ImageView>(R.id.ivPhoto)

        fun bind(item: Menu) {
            Glide.with(itemView)
                .load(item.imgURL)
                .centerCrop()
                .into(iv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return MenuViewHolder(v)
    }


    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menus[position])
    }

    override fun getItemCount() = menus.size

}

