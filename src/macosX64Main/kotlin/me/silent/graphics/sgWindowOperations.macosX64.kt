@file:Suppress("Unused", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package me.silent.graphics

actual class WindowHandle

actual suspend fun sgUpdate(windowHandle: WindowHandle): Int {
    TODO("Not yet implemented")
}

actual fun messageBox(
    message: String,
    name: String,
    mb: Int,
    windowHandle: WindowHandle?
): Int {
    TODO("Not yet implemented")
}

actual fun programIsAlreadyRunning(name: String): Boolean {
    TODO("Not yet implemented")
}

actual fun paintPixels(windowHandle: WindowHandle) {
}

actual class Bitmap

actual fun sgCreateBitmap(width: Int, height: Int, bpp: Int): Bitmap {
    TODO("Not yet implemented")
}

actual fun sgConfigureBuffer(x: Int, y: Int, bpp: Int) {
}

actual fun sgReconfigureBuffer(height: Int) {
}

actual fun swapBuffers(windowHandle: WindowHandle) {
}

actual suspend fun pollEvents(windowHandle: WindowHandle) {
}

actual suspend fun makeCurrentContent(windowHandle: WindowHandle) {
}