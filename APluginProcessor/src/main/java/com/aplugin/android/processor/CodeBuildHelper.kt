package com.aplugin.android.processor

import com.aplugin.android.annotation.PluginDepository
import com.aplugin.android.annotation.PluginManager
import com.aplugin.android.processor.PluginProcessor.Companion.PACKAGE_NAME
import com.squareup.kotlinpoet.*
import java.io.File

class CodeBuildHelper(
    private val kaptKotlinGeneratedDir: String,
    private val codeBlock: CodeBlock
) {
    private val superClass = PluginDepository::class
    private val generatedPackage = "$PACKAGE_NAME.impl"
    private val fileName = "${superClass.simpleName}_Impl"

    fun buildFile() =
        FileSpec.builder(generatedPackage, fileName)
            .addInitClass()
            .build()
            .writeTo(File(kaptKotlinGeneratedDir))

    private fun FileSpec.Builder.addInitClass() = apply {
        addType(
            TypeSpec.classBuilder(fileName)
                .addSuperinterface(superClass)
                .addMethod()
                .build()
        )
    }

    private fun TypeSpec.Builder.addMethod() = apply {
        addFunction(
            FunSpec.builder("fill")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("manager", PluginManager::class)
                .returns(Unit::class.java)
                .addCode(codeBlock)
                .build()
        )
    }
}
