package com.aplugin.android.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.aplugin.android.APlugin

class MainActivity : AppCompatActivity() {

    private val plugin = APlugin(this)

    private var index = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plugin.onCreate()
        setContentView(R.layout.activity_main)
        val tv = findViewById<TextView>(R.id.id_text)
        tv.setOnClickListener {
            plugin.invokePlugin("test", "bbb", Bundle().apply {
                putString("b1", "text:$index")
                putInt("b2", index)
            })?.let {
                tv.text = it.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        plugin.onDestroy()
    }
}