package com.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.data.remote.dto.CategoryListDto
import com.data.repository.UserRepository
import com.example.test1.R
import com.ui.GalleryActivity.Companion.EXTRA_CATEGORY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : Activity() {
    private lateinit var adapter: CategoryAdapter
    private val repo = UserRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        repo.getCategoreis().enqueue(object : Callback<CategoryListDto> {
            override fun onResponse(call: Call<CategoryListDto>, response: Response<CategoryListDto>) {
                if (response.isSuccessful) {
                    val resp = response.body()
                    val categories = resp?.data ?: emptyList<String>()
                    adapter.setData(categories)

                } else {
                    Log.e("API", "server error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<CategoryListDto>, t: Throwable) {
                Log.e("API", "network error", t)
            }
        })

        val recyclerView = findViewById<RecyclerView>(R.id.MenuList)
        val searchView = findViewById<SearchView>(R.id.Search)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CategoryAdapter(emptyList<String>()) { category ->
            val intent = Intent(this, GalleryActivity::class.java).apply {
                putExtra(EXTRA_CATEGORY, category)
                startActivity(this)
            }
        }

        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText)
                return true
            }
        })

    }
}

class CategoryAdapter(
    onItemClick1: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var allCategories: List<String> = emptyList()
    private var filteredCategories: List<String> = emptyList()

    fun setData(newList: List<String>) {
        allCategories = newList
        filteredCategories = newList
        notifyDataSetChanged()
    }

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
        holder.itemView.setOnClickListener { onItemClick(category) }
    }

    override fun getItemCount() = filteredCategories.size

    fun filter(query: String?) {
        filteredCategories =
            if (query.isNullOrBlank()) {
                allCategories
            } else {
                allCategories.filter {
                    it.contains(query, ignoreCase = true)
                }
            }
        notifyDataSetChanged()
    }
}
