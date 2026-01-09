package com.example.test1;

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

public class DataParser {

    public fun getJson(inputStream: InputStream): JSONObject? {
        try {


            val reader = BufferedReader(InputStreamReader(inputStream))

            var line = reader.readLine()
            val buffer = StringBuffer();
            while (line != null) {
                buffer.append(line + "\n")
                line = reader.readLine()
            }
            reader.close()

            val jsonObj = JSONObject(buffer.toString());

            return jsonObj;
        } catch (e: Exception) {
            return null
        }
    }
}
