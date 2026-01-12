package com.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import com.data.remote.dto.Menu

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
                            id = obj.getInt("id"),
                            name = obj.getString("name"),
                            price = obj.getString("price"),
                            img = obj.getString("img"),
                            category = obj.getString("category"),
                            store = obj.getString("store"),
                            isFavorite = obj.getBoolean("isFavorite"),
                            starpoint = obj.getDouble("starpoint").toFloat(),
                            deeplink = obj.getString("deeplink"),
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
