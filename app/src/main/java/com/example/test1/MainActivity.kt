package com.example.test1

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.InputBinding

class MainActivity : Activity() {
    private lateinit var binding: InputBinding;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        val inputStream = getAssets().open("test.json")

        val dp = DataParser();
        val data = dp.getJson(inputStream)


    }

}