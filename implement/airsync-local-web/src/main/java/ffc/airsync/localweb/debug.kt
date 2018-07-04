package ffc.airsync.localweb

val debug = System.getenv("FFC_DEBUG")

inline fun printDebug(infoDebug: String) {
    if (debug == null)
        println(infoDebug)
}
