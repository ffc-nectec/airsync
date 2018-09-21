import ffc.airsync.utils.toTime
import ffc.entity.gson.toJson
import org.amshove.kluent.`should be equal to`
import org.joda.time.DateTime
import org.junit.Test
import java.sql.Time

class DateFunctionTest {

    private val dateTime = DateTime(1536218895967)
    private val time = Time(dateTime.millis)

    @Test
    fun toTime() {
        "16:00:59".toTime().toString() `should be equal to` "16:00:59"
        (time.toTime() < "16:00:00".toTime()) `should be equal to` true
    }

    @Test
    fun condition() {
        ("14:28:15".toTime() < "16:00:00".toTime()) `should be equal to` true
    }

    @Test
    fun joda() {
        dateTime.millis `should be equal to` 1536218895967
    }

    @Test
    fun time() {
        time.toJson() `should be equal to` "14:28:15".toTime().toJson()
    }
}
