package me.silent.graphics

actual fun getMonitorWidth(): Int {
    TODO("Not yet implemented")
}

actual fun getMonitorHeight(): Int {
    TODO("Not yet implemented")
}

actual fun createWindowWin(
    name: String,
    x: Int,
    y: Int,
    allowMultipleInstances: Boolean,
    fullScreen: Boolean,
    bpp: Int,
    iconPath: String?
): WindowHandle {
    TODO("Not yet implemented")
}

actual fun configureMonitorInfo(windowHandle: WindowHandle, dwFlags: Int) {
}

actual fun windowShouldClose(): Boolean {
    TODO("Not yet implemented")
}

actual fun setWindowTitle(windowHandle: WindowHandle, text: String) {
}

actual fun sgTerminate(windowHandle: WindowHandle) {
}