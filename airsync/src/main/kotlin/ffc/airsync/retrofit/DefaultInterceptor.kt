package ffc.airsync.retrofit

import ffc.airsync.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.lang.String.format

internal class DefaultInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder()
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Accept", "application/json; charset=utf-8")
                .addHeader("Accept-Charset", "utf-8")
                .addHeader("X-Requested-By", "ffc-airsync")

        return chain.proceed(builder.build())
    }

    companion object {
        private val OS = format("%s; %s",
                System.getProperty("os.name"),
                System.getProperty("os.arch"))
        private val JAVA = format("Java/%s (%s)",
                System.getProperty("java.version"),
                System.getProperty("sun.arch.data.model"))
        private val USER_AGENT = "FFC-AirSync/${BuildConfig.VERSION} ($OS) $JAVA"
    }
}
