@file:Suppress("Unused", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package me.silent.graphics

expect fun getMonitorWidth(): Int
expect fun getMonitorHeight(): Int


expect fun createWindowWin(name: String, x: Int, y: Int,
                           allowMultipleInstances: Boolean = false,
                           fullScreen: Boolean = false, bpp: Int = 32,
                           iconPath: String? = null): WindowHandle


expect fun configureMonitorInfo(windowHandle: WindowHandle, dwFlags: Int = 0x00000001)

expect fun windowShouldClose(): Boolean

expect fun setWindowTitle(windowHandle: WindowHandle, text: String)

expect fun sgTerminate(windowHandle: WindowHandle)
