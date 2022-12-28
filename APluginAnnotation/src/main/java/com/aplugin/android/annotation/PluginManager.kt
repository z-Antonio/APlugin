package com.aplugin.android.annotation

class PluginManager {
    private val map: PluginMap = PluginMap()

    fun registerPluginInfo(key: String, builder: PluginInfo.Builder) {
        map[key] = builder.build()
    }

    fun getPlugins(): List<PluginInfo> = map.values.map { it }

    fun findPluginInfo(name: String): PluginInfo = map[name] ?: PluginInfo.defaultPluginInfo()
}