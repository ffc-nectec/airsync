package ffc.airsync.utils

import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test

class UploadSpliterTest {
    val list = ArrayList<String>()

    @Before
    fun setUp() {
        for (i in 1..100) {
            list.add("$i")
        }
    }

    @Test
    fun uploadBestCase() {
        val array = arrayListOf<List<String>>()
        UploadSpliter.upload(10, list) { it, index ->
            array.add(it)
        }

        array.size `should be equal to` 10
        array.first().longStr() `should be equal to` "1 2 3 4 5 6 7 8 9 10"
        array.last().longStr() `should be equal to` "91 92 93 94 95 96 97 98 99 100"
    }

    @Test
    fun uploadScraps() {
        val array = arrayListOf<List<String>>()
        UploadSpliter.upload(7, list) { it, index ->
            array.add(it)
        }

        array.size `should be equal to` 15
        array.first().longStr() `should be equal to` "1 2 3 4 5 6 7"
        array[array.size - 2].longStr() `should be equal to` "92 93 94 95 96 97 98"
        array.last().longStr() `should be equal to` "99 100"
    }

    fun List<String>.longStr(): String {
        var strOut = ""
        forEach {
            strOut += "$it "
        }
        return strOut.trimEnd()
    }
}
