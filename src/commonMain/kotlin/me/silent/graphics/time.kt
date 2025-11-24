package me.silent.graphics

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn( ExperimentalTime::class)
fun getCurrentTime() = Clock.System.now().toEpochMilliseconds()