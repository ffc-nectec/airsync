package ffc.airsync.gui.drawable

fun String.getFileResource() = Thread.currentThread().contextClassLoader.getResourceAsStream(this)
