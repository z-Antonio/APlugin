package com.aplugin.android.app.plugin

import android.util.Log
import com.aplugin.android.annotation.InitTime
import com.aplugin.android.annotation.Method
import com.aplugin.android.annotation.Plugin

@Plugin("test", InitTime.ServiceOnCreate)
class TestPlugin {

    @Method(["a1", "a2"])
    fun aaa(a1: Int, a2: String) {
        Log.d("TestPlugin", "#aaa $a1, $a2")
    }

    @Method(["b1", "b2"])
    fun bbb(a1: String, a2: Int): String {
        Log.d("TestPlugin", "#bbb $a1, $a2")
        return "bbb $a1, ${a2.inc()}"
    }
}