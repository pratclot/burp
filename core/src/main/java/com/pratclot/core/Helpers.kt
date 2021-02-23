package com.pratclot.core

import java.text.DecimalFormat

fun Double.toKm() = "${DecimalFormat("#").format(this / 1000)} km"
fun Double.shorten() = "${DecimalFormat("#").format(this)}"
