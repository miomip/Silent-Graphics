@file:Suppress("Unused")

package me.silent.graphics

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import platform.windows.GetAsyncKeyState
import platform.windows.GetKeyState
import kotlin.experimental.and

actual fun keyPressed(key: Int): Boolean = GetKeyState(key) and 0x8000.toShort() != 0.toShort()

@OptIn(ExperimentalCoroutinesApi::class)
actual fun getKeyDown(key: Int) = GetAsyncKeyState(key).toInt() != 0