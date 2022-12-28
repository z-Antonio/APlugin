package com.aplugin.android.processor

import com.aplugin.android.annotation.Method
import com.aplugin.android.annotation.Plugin
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedSourceVersion(SourceVersion.RELEASE_8)
class PluginProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val PACKAGE_NAME = "com.aplugin.android.annotation"
    }

    private val infoClass = ClassName(PACKAGE_NAME, "PluginInfo")

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        mutableSetOf(Plugin::class.java.canonicalName)

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        if (annotations == null || annotations.isEmpty()) return false
        val kaptKotlinGeneratedDir =
            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: return false

        println("PluginProcessor ====> start ")
        val codeBuilder = CodeBlock.Builder()
        roundEnv?.getElementsAnnotatedWith(Plugin::class.java)
            ?.forEach { element ->
                println("PluginProcessor ====> class: $element")
                val annotation = element.getAnnotation(Plugin::class.java)
                if (element is TypeElement) {
                    val sb =
                        StringBuilder("%L.Builder().setClass(%L::class.java).setInitTime(%S)")
                    val i = element.qualifiedName.indexOfLast { it == '.' }
                    val args = mutableListOf<Any?>().apply {
                        add(annotation.name)
                        add(infoClass)
                        add(
                            ClassName(
                                element.qualifiedName.substring(0, i),
                                element.simpleName.toString()
                            )
                        )
                        add(annotation.initTime.name)
                    }
                    element.enclosedElements.forEach {
                        println("PluginProcessor ====> method: $it")
                        it.getAnnotation(Method::class.java)?.let { method ->
                            println("PluginProcessor ====> params: ${method.paramsKeys}")
                            val out = method.paramsKeys.joinTo(StringBuilder(), ",") { "%S" }
                            sb.append(".addMethodInfo(%S, listOf($out))")
                            args.add(it.simpleName)
                            args.addAll(method.paramsKeys)
                        }
                    }
                    codeBuilder.addStatement(
                        "manager.registerPluginInfo(%S, $sb)",
                        *args.toTypedArray()
                    )
                }
            }
        CodeBuildHelper(kaptKotlinGeneratedDir, codeBuilder.build()).buildFile()
        return true
    }
}
