package com.example.test1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
            intent.putExtra(InfoActivity.EXTRA_MENU, menu)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }
}

class MenuAdapter(
    private val menus: List<Menu>,
    private val onItemClick: (Menu) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private var lastAnimatedPosition = -1

    // ✅ ViewHolder must extend RecyclerView.ViewHolder
    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv: ImageView = itemView.findViewById(R.id.ivPhoto)
        private val tv: TextView = itemView.findViewById(R.id.tvPrice)

        fun bind(item: Menu) {
            Glide.with(itemView)
                .load(item.imgURL)
                .centerCrop()
                .into(iv)

            tv.text = item.price.toString()
        }
    }

    // ✅ Return MenuViewHolder (not RecyclerView.ViewHolder)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menus[position])

        val adapterPosition = holder.bindingAdapterPosition
        if (adapterPosition == RecyclerView.NO_POSITION) return

        if (adapterPosition > lastAnimatedPosition) {
            val animation = AnimationUtils.loadAnimation(
                holder.itemView.context,
                if (adapterPosition % 2 == 0)
                    R.anim.slide_fade_left
                else
                    R.anim.slide_fade_right
            )
            holder.itemView.startAnimation(animation)
            lastAnimatedPosition = adapterPosition
        }

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onItemClick(menus[pos])
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: MenuViewHolder) {
        super.onViewDetachedFromWindow(holder)
        // 숨겨진 주석
        holder.itemView.clearAnimation()
    }

    override fun getItemCount(): Int = menus.size
}

