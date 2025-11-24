@file:Suppress("Unused")
@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package me.silent.graphics

import kotlinx.cinterop.*
import kotlinx.cinterop.invoke
import platform.windows.*
import kotlin.experimental.ExperimentalNativeApi


private val msg = nativeHeap.alloc<MSG>()

private var monitorInfo = nativeHeap.alloc<MONITORINFO>()

private var windowShouldCloseVar = false

private data class WindowInfo(val width: Int, val height: Int)

actual fun getMonitorWidth(): Int = GetSystemMetrics(SM_CXSCREEN)
actual fun getMonitorHeight(): Int = GetSystemMetrics(SM_CYSCREEN)


/**
 * @see Error when trying to run multiple instances //TODO FIX
 **/
@Suppress("duplicatedCode")
actual fun createWindowWin(name: String, x: Int, y: Int,
                           allowMultipleInstances: Boolean,
                           fullScreen: Boolean, bpp: Int,
                           iconPath: String?): WindowHandle {
    val name = name
    if (programIsAlreadyRunning(name) and !allowMultipleInstances){
        messageBox("Sorry an instance is already running", "Error!", MB_ICONEXCLAMATION or MB_OK)
        throw Error("Instance running already")
    }

    val instance = GetModuleHandleA(null)
    val windowClass = nativeHeap.alloc<WNDCLASSEX>()

    windowClass.cbSize        = sizeOf<WNDCLASSEX>().toUInt()
    windowClass.style         = 0u
    windowClass.lpfnWndProc   = staticCFunction(::mainWindowsProc)
    windowClass.cbClsExtra    = 0
    windowClass.cbWndExtra    = 0
    windowClass.hInstance     = instance

    memScoped {
        windowClass.hIcon = safeCast<HICON?>(
            LoadImageA(
                windowClass.hInstance, iconPath?.cstr?.ptr,
                IMAGE_BITMAP.toUInt(), 0, 0,
                (LR_LOADFROMFILE or LR_DEFAULTSIZE).toUInt()
            )
        )
    }
    windowClass.hCursor       = (LoadCursor!!)(instance, IDC_ARROW)
    windowClass.hbrBackground = CreateSolidBrush(rgbToUINT(255, 0, 255))
    windowClass.lpszMenuName  = null

    memScoped {
        windowClass.lpszClassName = name.wcstr.ptr
    }
    windowClass.hIconSm       = null

    SetProcessDPIAware()

    if ((RegisterClassEx!!)(windowClass.ptr) == 0u.toUShort() && !allowMultipleInstances){
        messageBox("Failed to register", "Error!", MB_ICONEXCLAMATION or MB_OK)
        throw Error("Failed to register a window")
    }

    val dwStyle = if (fullScreen) WS_POPUPWINDOW.toInt() or WS_VISIBLE or WS_MAXIMIZE else (WS_OVERLAPPEDWINDOW or WS_VISIBLE)

    val programWindow: HWND? = CreateWindowExA(
        0u, name, name,
        dwStyle.toUInt(),
        CW_USEDEFAULT, CW_USEDEFAULT, x, y, null, null, instance, NULL
    )
    configureMonitorInfo(WindowHandle(x, y, programWindow!!))

    sgConfigureBuffer(x, y, bpp)

    UpdateWindow(programWindow)

    return WindowHandle(x, y, programWindow)
}

@ExperimentalUnsignedTypes
fun mainWindowsProc(windowHandle: HWND?, msg: UINT, wParam: WPARAM, lParam: LPARAM) : LRESULT {
    // This switch block differentiates between the message type that could have been received. If you want to
    // handle a specific type of message in your application, just define it in this block.
    when (msg) {
        // This message type is used by the OS to close a window. Just closes the window using DestroyWindow(windowHandle);
        WM_CLOSE.toUInt() -> {
            winSendMessage(windowHandle!!, WM_QUIT, 0, 0)
            windowShouldCloseVar = true
        }
        WM_QUIT.toUInt() -> {
            DestroyWindow(windowHandle)
        }

        WM_DESTROY.toUInt() -> PostQuitMessage(0)

        // When no message type is handled in your application, return the default window procedure. In this case the message
        // will be handled elsewhere or not handled at all.
        else -> return (DefWindowProc!!)(windowHandle, msg, wParam, lParam)
    }
    return 0
}

actual fun configureMonitorInfo(windowHandle: WindowHandle, dwFlags: Int) {
    monitorInfo.cbSize = sizeOf<MONITORINFO>().toUInt()

    if(GetMonitorInfoA(MonitorFromWindow(windowHandle.win32Handle, dwFlags.toUInt()), monitorInfo.ptr) == 0)
        throw Error("Couldn't get monitor for $monitorInfo")
}

actual fun windowShouldClose(): Boolean = windowShouldCloseVar

actual fun setWindowTitle(windowHandle: WindowHandle, text: String) {
    SetWindowTextA(windowHandle.win32Handle, text)
}

actual fun sgTerminate(windowHandle: WindowHandle) {
    winSendMessage(windowHandle.win32Handle, WM_CLOSE)
}
fun winSendMessage(windowHandle: HWND, msg: Int, wParam: Long = 0, lParam: Long = 0) {
    SendMessageA(windowHandle, msg.toUInt(), wParam.toULong(), lParam)
}

fun winPeekMessage(windowHandle: HWND, wMsgFilterMin: Int = 0, wMsgFilterMax: Int = 0, wRemoveMsg: Int = PM_REMOVE): Int {
    PeekMessageA(msg.ptr, windowHandle,
        wMsgFilterMin.toUInt(), wMsgFilterMax.toUInt(),
        wRemoveMsg.toUInt())
    val message = TranslateMessage(msg.ptr)
    (DispatchMessage!!)(msg.ptr)
    return message
}

fun winGetMessage(windowHandle: HWND?) {
    while (GetMessageA(msg.ptr, windowHandle, 0u, 0u) > 0) {
        TranslateMessage(msg.ptr)
        DispatchMessageA(msg.ptr)
    }
}
