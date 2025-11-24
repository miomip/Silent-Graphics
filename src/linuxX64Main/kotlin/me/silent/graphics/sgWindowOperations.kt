@file:Suppress("unused", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
package me.silent.graphics

import kotlinx.cinterop.*
import kotlinx.coroutines.coroutineScope
import platform.posix.sleep
import platform.posix.valloc
import kotlin.experimental.ExperimentalNativeApi
import kotlin.properties.Delegates
import xcb.*
import x11.*

data class WindowData(val window: Window, val display: CPointer<Display>)

actual data class WindowHandle(val x: Int, val y: Int, val win32Handle: WindowData)

actual suspend fun sgUpdate(windowHandle: WindowHandle): Int = coroutineScope {
    // winPeekMessage(windowHandle.win32Handle)
    return@coroutineScope 1
}

actual fun messageBox(message: String, name: String, mb: Int, windowHandle: WindowHandle?): Int {
    return 0
}

actual fun programIsAlreadyRunning(name: String): Boolean {
    return true
}

actual fun paintPixels(windowHandle: WindowHandle) {
}


object BmiHeader {
    var biSize by Delegates.notNull<UInt>()
    var biWidth by Delegates.notNull<Int>()
    var biHeight by Delegates.notNull<Int>()
    var biBitCount by Delegates.notNull<UShort>()
    var biCompression by Delegates.notNull<UInt>()
    var biPlanes by Delegates.notNull<UInt>()
}

@Suppress("SpellCheckingInspection")
data class BITMAPINFO(val bmiHeader: BmiHeader, val bmiColors: CPointer<UIntVar>)


actual data class Bitmap(val bitmapInfo: BITMAPINFO, val memory: CPointer<ByteVar>)

private var gameDrawingMemorySize = nativeHeap.alloc<ULongVar>()

actual fun sgCreateBitmap(width: Int, height: Int, bpp: Int): Bitmap {
    gameDrawingMemorySize.value = (width * height * (bpp / 8)).toULong()
    val va = safeCast<CPointer<ByteVar>>(valloc(gameDrawingMemorySize.value))

    val temp = nativeHeap.alloc<UIntVar>() /* TODO: FIX */
    val conf = Bitmap(BITMAPINFO(BmiHeader, temp.ptr),  va)

    conf.bitmapInfo.bmiHeader.biWidth = width

    conf.bitmapInfo.bmiHeader.biHeight = height

    conf.bitmapInfo.bmiHeader.biBitCount = bpp.toUShort()

//    conf.bitmapInfo.bmiHeader.biCompression = BI_RGB.toUInt()

    conf.bitmapInfo.bmiHeader.biPlanes = 1u

    // if (conf.memory == null) throw OutOfMemoryError("No memory to allocate bitmap: ${conf.identityHashCode()}")

    return conf
}



actual fun sgConfigureBuffer(x: Int, y: Int, bpp: Int) {
    backBuffer = sgCreateBitmap(x, y, bpp)
}

actual fun sgReconfigureBuffer(height: Int) {
    backBuffer.bitmapInfo.bmiHeader.biHeight = getMonitorHeight()
}


actual fun swapBuffers(windowHandle: WindowHandle) {

}

actual suspend fun pollEvents(windowHandle: WindowHandle) {
    sgUpdate(windowHandle)
    sleep(0u)
}


actual suspend fun makeCurrentContent(windowHandle: WindowHandle) {
}


