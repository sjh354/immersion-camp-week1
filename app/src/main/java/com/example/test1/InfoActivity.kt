package com.example.test1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.test1.DataParser
import com.example.test1.Menu
import com.example.test1.R
import android.widget.Button


class InfoActivity : Activity() {

    companion object {
        const val EXTRA_MENU = "extra_menu"
    }
    private lateinit var adapter: InfoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info)

        val menu = intent.getParcelableExtra<Menu>(EXTRA_MENU) ?: return

        findViewById<TextView>(R.id.TitleOnInfo).text = menu.store
        findViewById<TextView>(R.id.InfoTitle_1).text = menu.name
        findViewById<TextView>(R.id.infoTitle_2).text = menu.price

        val imgView = findViewById<ImageView>(R.id.imgOnInfo)
        Glide.with(this)
            .load(menu.imgURL)
            .centerCrop()
            .into(imgView)

        findViewById<Button>(R.id.button).setOnClickListener {
            finish() // 이전(갤러리)로 돌아가기. 보통 이게 "GALLERY" 버튼 동작이면 충분
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            // 예: 검색(메인)으로 이동하고 싶으면
             startActivity(Intent(this, MainActivity::class.java))
             finish()
        }
    }
}

class InfoAdapter(
    private val menus: List<Menu>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {
    class InfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val iv = itemView.findViewById<ImageView>(R.id.ivPhoto)

        fun bind(item: Menu) {
            Glide.with(itemView)
                .load(item.imgURL)
                .centerCrop()
                .into(iv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return InfoViewHolder(v)
    }


    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.bind(menus[position])
    }

    override fun getItemCount() = menus.size

}

