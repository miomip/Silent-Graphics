@file:Suppress("Unused")
package me.silent.graphics

import kotlinx.cinterop.ByteVarOf
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.coroutines.coroutineScope
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen
import org.intellij.lang.annotations.*

/**
 * resourcePath is used to set the path to where IntelliJ expects resources and source code to be.
 * */

expect var resourcePath: String

@OptIn(ExperimentalForeignApi::class)
class File(@param:Language("file-reference") val filepath: String) {
    var file: String
    init {
        file = try {
            if (!filepath.startsWith("C:"))
                "${resourcePath
                    .removePrefix("/")
                    .removePrefix("\\")
                    .removeSuffix("\\")
                    .removeSuffix("/")
                }/$filepath"
            else
                filepath
        } catch (e: RuntimeException) {
            throw Error("Please select starting path, " +
                    "to do that use the resourcePath variable. " +
                    "You can set it to an empty string", e)
        }
    }



    suspend fun readFile(): String {
        return coroutineScope {
            val filePtr = fopen(file, "r") ?: return@coroutineScope "Could not open file"
            val string = nativeHeap.alloc<ByteVarOf<Byte>>()
            var temp = ""
            var i = 1
            while (fgets(string.ptr, i, filePtr) != null) {
                i++
                temp += string.ptr.toKString()
            }
            nativeHeap.free(string.ptr)

            fclose(filePtr)
            return@coroutineScope temp
        }
    }


    suspend fun readLines(): List<String> {
        return coroutineScope {
            val filePtr = fopen(file, "r") ?: return@coroutineScope listOf("Could not open file")
            val string = nativeHeap.alloc<ByteVarOf<Byte>>()
            var temp = ""
            val returnList = mutableListOf<String>()
            var i = 1
            while (fgets(string.ptr, i, filePtr) != null) {
                i++
                temp += string.ptr.toKString()
                if (temp.endsWith('\n') || temp.endsWith('\r')) {
                    returnList.add(temp.removeSuffix("\n"))
                    temp = ""
                }
            }
            nativeHeap.free(string.ptr)

            returnList.add(temp)
            fclose(filePtr)
            return@coroutineScope returnList
        }
    }

    suspend fun readLine(): String {
        return coroutineScope {
            val filePtr = fopen(file, "r") ?: return@coroutineScope "Could not open file"
            val string = nativeHeap.alloc<ByteVarOf<Byte>>()
            var temp = ""
            var i = 1

            while (fgets(string.ptr, i, filePtr) != null && !temp.endsWith('\n')) {
                i++
                temp += string.ptr.toKString()
            }
            nativeHeap.free(string.ptr)

            fclose(filePtr)
            return@coroutineScope temp
        }
    }

    suspend fun exists(): Boolean {
        try {
            readFile()
            return true
        } catch (e: RuntimeException) {
            return false
        }
    }
}

