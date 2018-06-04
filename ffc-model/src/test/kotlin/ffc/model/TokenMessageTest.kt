package ffc.model

import org.junit.Test
import java.util.regex.Pattern

class TokenMessageTest {

    @Test
    fun enumPrint() {
        println(TokenMessage.TYPEROLE.USER)
        val regex = "\\b(\\d{3})(\\d{3})(\\d{4})\\b"

        val pattern = Pattern.compile(regex)
        val baseUrl = "1234567890, 12345,  and  9876543210"

        val m = pattern.matcher(baseUrl)

        while (m.find()) {
            System.out.println("Phone: " + m.group() + ", Formatted Phone:  ("
              + m.group(1) + ") " + m.group(2) + "-" + m.group(3))
        }
    }

    @Test
    fun testMach() {
        val pattern = Pattern.compile("^org/(?<orgId>[\\w\\d]+)/.*$")
        val baseUrl = "org/0/person"
        val matcher = pattern.matcher(baseUrl.trim())

        matcher.find()

        val orgId = matcher.group(1)

        printDebug("Auth filter parth url $baseUrl")
        printDebug("\t Org id = $orgId")
        printDebug("\t ${matcher.find()}")
    }

    @Test
    fun testIf() {
        if (print1() && print2()) {
            println("Finish")
        }
    }

    fun print1(): Boolean {
        println('1')
        return true
    }

    fun print2(): Boolean {
        println("2")
        return true
    }
}
