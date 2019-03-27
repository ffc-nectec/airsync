package ffc.airsync

import mysql.config.copyFileUsingStream
import mysql.config.read.ReadOptionMyini
import org.amshove.kluent.`should be equal to`
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import java.io.File

class InitJhcisConfigTest {
    private val mySmall = File("src/test/resources/my-small.ini")
    private val fileTest = File("mmyy.ini")

    private lateinit var mangeConfig: InitJhcisConfig

    @Before
    fun setUp() {
        copyFileUsingStream(mySmall, fileTest)
        mangeConfig = InitJhcisConfig(fileTest)
    }

    @After
    fun tearDown() {
        fileTest.deleteOnExit()
    }

    @Ignore("Require admin")
    fun `checkConfig$airsync`() {
        mangeConfig.writeConfig() `should be equal to` true

        ReadOptionMyini(fileTest).read().getValue("mysqld").getValue("log") `should be equal to` "jlog.log"
    }
}
