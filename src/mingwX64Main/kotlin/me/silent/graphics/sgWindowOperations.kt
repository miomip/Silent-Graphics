@file:Suppress("unused", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package me.silent.graphics

import kotlinx.cinterop.*
import kotlinx.coroutines.coroutineScope
import platform.posix.memcpy
import platform.windows.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.random.Random

actual data class WindowHandle(val x: Int, val y: Int, val win32Handle: HWND)

actual suspend fun sgUpdate(windowHandle: WindowHandle) = coroutineScope {
    winPeekMessage(windowHandle.win32Handle)
}

actual fun messageBox(message: String, name: String, mb: Int, windowHandle: WindowHandle?): Int {
    memScoped {
        return MessageBoxA(windowHandle?.win32Handle, message, name, (mb).toUInt())
    }
}

actual fun programIsAlreadyRunning(name: String): Boolean {
    memScoped {
        CreateMutexA(
            null,
            bInitialOwner = 0,
            lpName = "${name}_ProgrammeMutex"
        )
    }
    return GetLastError().toInt() == ERROR_ALREADY_EXISTS
}

actual fun paintPixels(windowHandle: WindowHandle) {
    val windowHandle = windowHandle.win32Handle

    val ps = nativeHeap.alloc<PAINTSTRUCT>()
    val r = nativeHeap.alloc<RECT>()

    GetClientRect(windowHandle, r.ptr)

    if (r.bottom == 0) {
        return
    }
    val hdc = BeginPaint(windowHandle, ps.ptr)

    (0..2000).forEach { _ ->
        val x = Random.nextInt(1920)
        val y = Random.nextInt(1200)
        SetPixel(hdc, x, y, rgbToUINT(Random.nextInt(10, 255), Random.nextInt(10, 255), Random.nextInt(10, 255)))
    }

    EndPaint(windowHandle, ps.ptr)


}


actual data class Bitmap(val bitmapInfo: BITMAPINFO, val memory: CPointer<ByteVar>)

private var gameDrawingMemorySize = nativeHeap.alloc<ULongVar>()

actual fun sgCreateBitmap(width: Int, height: Int, bpp: Int): Bitmap {
    gameDrawingMemorySize.value = (width * height * (bpp / 8)).toULong()
    val va = safeCast<CPointer<ByteVar>>(VirtualAlloc(null, gameDrawingMemorySize.value,
        (MEM_RESERVE or MEM_COMMIT or SWP_NOZORDER).toUInt(),
        PAGE_READWRITE.toUInt()))

    val conf = Bitmap(nativeHeap.alloc<BITMAPINFO>(),  va)

    conf.bitmapInfo.bmiHeader.biSize = sizeOf<BITMAPINFO>().toUInt()

    conf.bitmapInfo.bmiHeader.biWidth = width

    conf.bitmapInfo.bmiHeader.biHeight = height

    conf.bitmapInfo.bmiHeader.biBitCount = bpp.toUShort()

    conf.bitmapInfo.bmiHeader.biCompression = BI_RGB.toUInt()

    conf.bitmapInfo.bmiHeader.biPlanes = 1u

    return conf
}

actual fun sgConfigureBuffer(x: Int, y: Int, bpp: Int) {
    backBuffer = sgCreateBitmap(x, y, bpp)
}

actual fun sgReconfigureBuffer(height: Int) {
    backBuffer.bitmapInfo.bmiHeader.biHeight = height
}


actual fun swapBuffers(windowHandle: WindowHandle) {

}

actual suspend fun pollEvents(windowHandle: WindowHandle) {
    sgUpdate(windowHandle)
}


actual suspend fun makeCurrentContent(windowHandle: WindowHandle): Unit = coroutineScope {
//     Allocate memory for a tagBITMAPINFO struct
    val bmi: tagBITMAPINFO = backBuffer.bitmapInfo
}
