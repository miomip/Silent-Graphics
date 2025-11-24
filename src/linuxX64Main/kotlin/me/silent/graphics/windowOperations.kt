@file:Suppress("Unused", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package me.silent.graphics

import kotlinx.cinterop.*
import xcb.*
import x11.*
import kotlin.experimental.ExperimentalNativeApi

data class MonitorInfo(val height: Int, val width: Int)

fun getMonitorInfo(): MonitorInfo {
    val display = XOpenDisplay(null) ?: throw Error("No XOpenDisplay found")
    val screen = XDefaultScreen(display)

    val height = XDisplayHeight(display, screen)
    val width = XDisplayWidth(display, screen)
    XCloseDisplay(display)
    return MonitorInfo(height, width)
}

actual fun getMonitorWidth(): Int = getMonitorInfo().width
actual fun getMonitorHeight(): Int = getMonitorInfo().width

actual fun createWindowWin(name: String, x: Int, y: Int,
                           allowMultipleInstances: Boolean,
                           fullScreen: Boolean, bpp: Int,
                           iconPath: String?): WindowHandle {
    val display = XOpenDisplay(null) ?: throw Error("Couldn't open window")

    val screen = XDefaultScreen(display)

    val window = XCreateSimpleWindow(display, XRootWindow(display, screen),
        10, 10, 160u, 160u, 1u, XBlackPixel(display, screen),
        XWhitePixel(display, screen))

    XSelectInput(display, window, ExposureMask or KeyPressMask)
    XMapWindow(display, window)
    val event = nativeHeap.alloc<XEvent>()
    return WindowHandle(x, y, WindowData(window, display))
}

actual fun configureMonitorInfo(windowHandle: WindowHandle, dwFlags: Int) {

}

private var windowShouldCloseVar = false

actual fun windowShouldClose(): Boolean = windowShouldCloseVar

actual fun setWindowTitle(windowHandle: WindowHandle, text: String) {
}

actual fun sgTerminate(windowHandle: WindowHandle) {
    XCloseDisplay(windowHandle.win32Handle.display)
}