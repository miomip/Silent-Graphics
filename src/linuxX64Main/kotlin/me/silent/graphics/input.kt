@file:Suppress("Unused")

package me.silent.graphics

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.coroutines.CoroutineScope
import platform.posix.STDIN_FILENO
import platform.posix.cfgetispeed
import platform.posix.tcgetattr
import platform.posix.termios

@OptIn(ExperimentalForeignApi::class)
actual fun keyPressed(key: Int): Boolean {
    val term = nativeHeap.alloc<termios>()
    val fd = STDIN_FILENO

    if (tcgetattr(fd, term.ptr) == -1)
        throw Error("tcgetattr failed: No keyboard input available")

    println(cfgetispeed(term.ptr))

    return false
//    GetKeyState(key) and 0x8000.toShort() != 0.toShort()
}
actual fun getKeyDown(key: Int) = true