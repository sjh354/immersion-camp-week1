package com.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.data.DataParser
import com.data.remote.dto.Menu
import com.example.test1.R

class GalleryActivity : Activity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }
    private lateinit var adapter: MenuAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery)
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: return

        val titleView = findViewById<TextView>(R.id.TitleOnGallery)
        titleView.text = category

        val inputStream = assets.open("test.json")
        val dp = DataParser()
        val filtered = dp.parseMenusInSpecificCategory(inputStream, category)

        val recyclerView = findViewById<RecyclerView>(R.id.GalleryList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

//        adapter = MenuAdapter(filtered) { menu ->
//            Log.d(menu, "HELLO")
//        }

        adapter = MenuAdapter(filtered) { menu ->
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
    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val iv = itemView.findViewById<ImageView>(R.id.ivPhoto);

        private val tv = itemView.findViewById<TextView>(R.id.tvPrice)

        fun bind(item: Menu) {
            Glide.with(itemView)
                .load(item.imgURL)
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
        holder.bind(menus[position])
        holder.itemView.setOnClickListener {
            onItemClick(menus[position])
        }
    }

    override fun getItemCount() = menus.size

}

