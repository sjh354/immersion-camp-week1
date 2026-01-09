package com.example.test1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import kotlin.jvm.java

class MainActivity : Activity() {
    private lateinit var adapter: CategoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        val inputStream = assets.open("test.json")

        val dp = DataParser()
        val categories = dp.parseCategories(inputStream)

        val recyclerView = findViewById<RecyclerView>(R.id.MenuList)
        val searchView = findViewById<SearchView>(R.id.Search)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText)
                return true
            }
        })


        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = CategoryAdapter(categories)
        recyclerView.adapter = CategoryAdapter(categories) { category ->
            val intent = Intent(this, GalleryActivity::class.java).apply {
                putExtra(GalleryActivity.EXTRA_CATEGORY, category)
            }
            startActivity(intent)
        }

    }
}

class CategoryAdapter(
    private val originalCategories: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // 🔹 This list changes when searching
    private var filteredCategories: List<String> = originalCategories

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = filteredCategories[position]
        holder.textView.text = category

        holder.itemView.setOnClickListener {
            onItemClick(category)
        }
    }

    override fun getItemCount() = filteredCategories.size

    // 🔍 SEARCH FUNCTION
    fun filter(query: String?) {
        filteredCategories =
            if (query.isNullOrBlank()) {
                originalCategories
            } else {
                originalCategories.filter {
                    it.contains(query, ignoreCase = true)
                }
            }
        notifyDataSetChanged()
    }
}

