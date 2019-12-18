package ffc.airsync.utils

import ffc.airsync.getLogger

inline fun <reified MainClass, reified ReturnType> ignoreW(
    clazz: MainClass,
    function: () -> ReturnType?
): ReturnType? {
    return try {
        function()
    } catch (ex: Exception) {
        getLogger(MainClass::class).warn(t = ex) { "ignoreW ${ex.message}" }
        null
    }
}
