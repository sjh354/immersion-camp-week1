package com.example.test1

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GalleryActivity : Activity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery)

        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: return

        // json 다시 읽어서 전체 메뉴 파싱 (가장 단순/안전)
        val inputStream = assets.open("test.json")
        val dp = DataParser()
        val filtered = dp.parseMenusInSpecificCategory(inputStream, category)


        val rv = findViewById<RecyclerView>(R.id.GalleryList)
        rv.layoutManager = GridLayoutManager(this, 2) // 2열 갤러리
        rv.adapter = MenuAdapter(filtered)
    }
}
