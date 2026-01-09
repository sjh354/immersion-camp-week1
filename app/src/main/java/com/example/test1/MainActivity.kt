package com.example.test1

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        val inputStream = assets.open("test.json")

        val dp = DataParser()
        val categories = dp.parseCategories(inputStream)

        val recyclerView = findViewById<RecyclerView>(R.id.MenuList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CategoryAdapter(categories)
    }
}
