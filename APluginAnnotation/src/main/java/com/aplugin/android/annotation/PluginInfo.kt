package com.aplugin.android.annotation

data class PluginInfo(
    val clazz: Class<*>,
    val methods: List<MethodInfo>,
    val initTime: InitTime = InitTime.LazyByUse
) {
    companion object {
        fun defaultPluginInfo(): PluginInfo = PluginInfo(Any::class.java, emptyList())
    }

    class Builder {
        private val methods = mutableListOf<MethodInfo>()
        private lateinit var clazz: Class<*>
        private var initTime: InitTime = InitTime.LazyByUse

        fun setClass(clazz: Class<*>): Builder {
            this.clazz = clazz
            return this
        }

        fun setInitTime(time: String): Builder {
            this.initTime = InitTime.valueOf(time)
            return this
        }

        fun addMethodInfo(method: String, paramsKeys: List<String>): Builder {
            methods.add(MethodInfo(method, paramsKeys))
            return this
        }

        fun build(): PluginInfo = PluginInfo(clazz, methods, initTime)
    }
}

data class MethodInfo(val method: String, val paramsKeys: List<String>)

class PluginMap() : LinkedHashMap<String, PluginInfo>()
