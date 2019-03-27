package ffc.airsync.retrofit

import org.junit.Ignore
import org.junit.Test

class DefaultInterceptorTest {

    @Ignore
    @Test
    fun systemProperties() {
        println(System.getProperty("os.name"))
        println(System.getProperty("os.arch"))
        println(System.getProperty("sun.arch.data.model"))
        println(System.getProperty("java.version"))
    }
}
