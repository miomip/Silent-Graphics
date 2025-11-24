@file:Suppress("unused", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package me.silent.graphics

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ULongVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlin.math.pow


expect class WindowHandle


expect suspend fun sgUpdate(windowHandle: WindowHandle): Int

expect fun messageBox(message: String, name: String, mb: Int, windowHandle: WindowHandle? = null): Int

expect fun programIsAlreadyRunning(name: String): Boolean

expect fun paintPixels(windowHandle: WindowHandle)


expect class Bitmap

@OptIn(ExperimentalForeignApi::class)
private var gameDrawingMemorySize = nativeHeap.alloc<ULongVar>()

inline fun <reified T> safeCast(input: Any?): T {
    return input as T
}


expect fun sgCreateBitmap(width: Int, height: Int, bpp: Int = 32): Bitmap


data class Pixel32(val b: UByte, val g: UByte, val r: UByte, val a: UByte)

fun rgbToUINT(r: Int, g: Int, b: Int): UInt {
    return ((b shl 16) or (g shl 8) or r).toUInt()
}

fun rgbaToUINT(r: Int, g: Int, b: Int, a: Int): UInt {
    return ((a shl 32) or (b shl 16) or (g shl 8) or r).toUInt()
}


fun getScreenRatio(width: Int, height: Int): Array<Int>{
    var temp = 0
    var x: Int
    var y: Int
    var width = width
    var height = height
    fun gcd(a: Int, b: Int): Int{
        return if (b == 0) { a } else gcd(b, a % b)
    }

    if (width == height){
        x = 1
        y = 1
        return arrayOf(x, y)
    }

    if (width < height){
        temp = width
        width = height
        height = temp
    }

    val divisor = gcd(width, height)

    x = if (temp == 0) width/divisor else height / divisor
    y = if (temp == 0) height / divisor else width / divisor

    return arrayOf(x, y)
}

lateinit var backBuffer: Bitmap

expect fun sgConfigureBuffer(x: Int, y: Int, bpp: Int = 32)

expect fun swapBuffers(windowHandle: WindowHandle)

expect suspend fun pollEvents(windowHandle: WindowHandle)

private var first = true
private var itt = 0uL


fun Int.pow(x: Int): Int = (this.toDouble().pow(x)).toInt()

expect suspend fun makeCurrentContent(windowHandle: WindowHandle)

expect fun sgReconfigureBuffer(height: Int)