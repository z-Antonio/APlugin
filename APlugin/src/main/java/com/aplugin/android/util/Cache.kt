package com.aplugin.android.util

class Cache {
    private val cache = mutableMapOf<Class<*>, Any?>()

    @PublishedApi
    internal fun <T> getCache(clazz: Class<T>): T? = synchronized(this) { cache[clazz] as? T }

    @PublishedApi
    internal fun <T> getCacheOrCreate(clazz: Class<T>, create: () -> T): T =
        getCache(clazz) ?: create().also {
            synchronized(this) { cache[clazz] = it }
        }

    inline fun <reified T> getInstance(): T = getCacheOrCreate(T::class.java) {
        return@getCacheOrCreate T::class.java.newInstance()
    }
}