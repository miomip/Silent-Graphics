package me.silent.graphics

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.getcwd

actual var resourcePath: String = ""
    get() = if (field == "") "${getCwd()}/src/commonMain/resources" else field
    set(value) {
        field = if(value.startsWith('/')){
            "${getCwd()}$value"
        }else
            value
    }

@OptIn(ExperimentalForeignApi::class)
fun getCwd(): String {
    memScoped {
        val bufferLength = 1024
        val buffer = allocArray<ByteVar>(bufferLength)
        val cwdPointer = getcwd(buffer, bufferLength) ?: return "Failed to get cwd"
        return cwdPointer.toKString()
    }
}