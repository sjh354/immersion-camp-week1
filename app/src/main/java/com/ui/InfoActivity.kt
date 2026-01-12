package com.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.data.remote.dto.Menu
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
            .load(menu.img)
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

        findViewById<Button>(R.id.button3).setOnClickListener {
//            val intent = packageManager.getLaunchIntentForPackage("com.fineapp.yogiyo")
//            if (intent != null) {
//                startActivity(intent)  // 요기요 앱 실행
//            } else {
//                // 설치 안 돼 있으면 Play 스토어로 이동
//                val playIntent = Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse("https://play.google.com/store/apps/details?id=com.fineapp.yogiyo")
//                )
//                startActivity(playIntent)
//            }
            val url = "https://yogiyo.onelink.me/BlI7?pid=kakaochat&af_web_dp=https%3A%2F%2Fwww.yogiyo.co.kr%2F&af_click_lookback=1d&is_retargeting=true&c=mkt_cp&af_adset=2408_kakaochat_menu2&af_ad=chicken08&af_sub1=sns&deep_link_value=yogiyolink%3A%2F%2Fmove.page%2Fglobal_home%2Fdelivery_home%3Fcategory%3Dchicken%26has_discount%3Dtrue&af_dp=yogiyolink%3A%2F%2Fmove.page%2Fglobal_home"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
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
                .load(item.img)
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

