package com.example.test1

import android.util.Log
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Menu(
    val name: String,
    val price: String,
    val imgURL: String,
    val category: String,
    val store: String
):Parcelable

class DataParser {

    fun parseCategories(inputStream: InputStream): List<String> {
        val categorySet = mutableSetOf<String>()

        try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val buffer = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
            reader.close()

            val jsonArray = JSONArray(buffer.toString())

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                if (obj.has("category")) {
                    categorySet.add(obj.getString("category"))
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return categorySet.toList()
    }

    fun parseMenusInSpecificCategory(inputStream: InputStream, category: String): List<Menu>{
        val ret = mutableListOf<Menu>()

        try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val buffer = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
            reader.close()

            val jsonArray = JSONArray(buffer.toString())

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                if (obj.has("category")) {
                    if (obj.getString("category") == category) {
                        ret.add(Menu(
                            name = obj.getString("name"),
                            price = obj.getString("price"),
                            imgURL = obj.getString("img"),
                            category = obj.getString("category"),
                            store = obj.getString("store"),
                        ))
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ret.toList()
    }
}
