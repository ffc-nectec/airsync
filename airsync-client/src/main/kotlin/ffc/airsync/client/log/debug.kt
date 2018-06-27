package ffc.airsync.client.log

val debug = System.getenv("FFC_DEBUG")

inline fun printDebug(infoDebug: String) {
    if (debug == null)
        println(infoDebug)
}
