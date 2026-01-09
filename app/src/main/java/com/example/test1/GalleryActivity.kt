package com.example.test1

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test1.MenuAdapter

class GalleryActivity : Activity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }

    fun onSelectMenu() {
//        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery)

        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: return

        val inputStream = assets.open("test.json")
        val dp = DataParser()
        val filtered = dp.parseMenusInSpecificCategory(inputStream, category)

        val rv = findViewById<RecyclerView>(R.id.GalleryList)
        rv.layoutManager = GridLayoutManager(this, 2) // 2열 갤러리
        rv.adapter = MenuAdapter(filtered){}
    }
}

class MenuAdapter(
    private val menus: List<Menu>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuAdapter>() {

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

