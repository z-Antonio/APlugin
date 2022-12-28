package com.aplugin.android.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Plugin(val name: String, val initTime: InitTime = InitTime.LazyByUse)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Method(val paramsKeys: Array<String>)