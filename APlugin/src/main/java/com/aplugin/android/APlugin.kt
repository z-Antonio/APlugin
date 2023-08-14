package com.aplugin.android

import android.content.Context
import android.os.Bundle
import com.aplugin.android.annotation.*
import com.aplugin.android.util.Cache

class APlugin(private val context: Context) {

    companion object {
        private inline fun <reified T> injectInstance(): T {
            val clazz =
                Class.forName("${T::class.java.`package`.name}.impl.${T::class.java.simpleName}_Impl")
            return clazz.newInstance() as T
        }
    }

    private val pluginManager = PluginManager()
    private val cache = Cache()

    fun invokePlugin(plugin: String, method: String, bundle: Bundle?): Any? {
        val pluginInfo = pluginManager.findPluginInfo(plugin)
        val obj = fetchPlugin(pluginInfo.clazz)
        val paramsKeys = pluginInfo.methods.find { it.method == method }?.paramsKeys
        pluginInfo.clazz.methods.find { it.name == method }?.let { method ->
            return if (paramsKeys.isNullOrEmpty()) {
                method.invoke(obj)
            } else {
                val args: Array<Any?> = Array(paramsKeys.size) { index ->
                    val key = paramsKeys[index]
                    bundle?.get(key).convert()
                }
                method.invoke(obj, *args)
            }
        }
        return null
    }

    fun onCreate() {
        injectInstance<PluginDepository>().fill(pluginManager)
        pluginManager.getPlugins().forEach { plugin ->
            if (plugin.initTime == InitTime.ServiceOnCreate) {
                fetchPlugin(plugin.clazz)
            }
        }
    }

    fun onDestroy() {
        pluginManager.getPlugins().forEach { plugin ->
            try {
                cache.getCache(plugin.clazz)?.let {
                    if (it is IRelease) {
                        it.release()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun registerPlugin(clazz: Class<*>) {
        clazz.getAnnotation(Plugin::class.java)?.let { annotation ->
            val builder = PluginInfo.Builder()
                .setClass(clazz)
                .setInitTime(annotation.initTime.name)
            clazz.methods.forEach { method ->
                method.getAnnotation(Method::class.java)?.let { mm ->
                    builder.addMethodInfo(method.name, mm.paramsKeys.asList())
                }
            }
            pluginManager.registerPluginInfo(annotation.name, builder)
        }
    }

    fun <T> fetchPlugin(clazz: Class<T>): T = cache.getCacheOrCreate(clazz) {
        val constructor = clazz.constructors[0]
        return@getCacheOrCreate if (constructor.parameterCount == 1) {
            constructor.newInstance(context) as T
        } else {
            constructor.newInstance() as T
        }
    }

    private fun Any?.convert(): Any? {
        if (this == null) {
            return null
        }
        return when (this) {
            is Byte -> this
            is java.lang.Byte -> this.toByte()
            is Char -> this
            is Character -> this.charValue()
            is Short -> this
            is java.lang.Short -> this.toShort()
            is Int -> this
            is Integer -> this.toInt()
            is Long -> this
            is java.lang.Long -> this.toLong()
            is Float -> this
            is java.lang.Float -> this.toFloat()
            is Double -> this
            is java.lang.Double -> this.toDouble()
            is Boolean -> this
            is java.lang.Boolean -> this.booleanValue()
            is String -> this
            is java.lang.String -> this.toString()
            else -> this
        }
    }
}