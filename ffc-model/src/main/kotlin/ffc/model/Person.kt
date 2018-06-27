/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.model

/*
 * Copyright (c) 2018 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.google.gson.*
import me.piruin.geok.LatLng
import me.piruin.geok.geometry.Point
import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.lang.reflect.Type
import java.util.*

data class Person(val id: Long = Random().nextLong() * -1) {


    //เพิ่ม พ่อ แม่ ภรรยา id และ ชื่อ
    var orgId: Int? = null
    var hospCode: String? = null
    var pid: Long? = null
    var prename: String = ""
    var firstname: String = ""
    var lastname: String = ""
    val name: String
        get() = "$prename$firstname $lastname"
    var birthData: LocalDate? = null
    var identities: MutableList<Identity> = mutableListOf()
    var house: Address? = null
    var chronics: MutableList<Chronic>? = null
    var houseId: Int? = null
}


data class Address(var _id: String = "", var dateUpdate: DateTime = DateTime.now()) {
    var _shortId: String = ""
    var identity: Identity? = null
    var type: Type = Type.House
    var no: String? = null
    var road: String? = null
    var tambon: String? = null
    var ampur: String? = null
    var changwat: String? = null
    @Deprecated("Use location", ReplaceWith("location"))
    var coordinates: LatLng? = null
    var location: Point? = null
    var hid: Int? = null  // คือ hcode ในฐาน jhcis

    var haveChronics: Boolean? = null
    var people: List<People>? = null
    var pcuCode: String? = null

    var _sync:Boolean = false


    enum class Type {
        House, Condo
    }

    fun clone(): Address {
        val cloneObj = Address(this._id, this.dateUpdate)
        cloneObj._shortId = this._shortId
        cloneObj.identity = this.identity
        cloneObj.type = this.type
        cloneObj.no = this.no
        cloneObj.road = this.road
        cloneObj.tambon = this.tambon
        cloneObj.ampur = this.ampur
        cloneObj.changwat = this.changwat
        cloneObj.coordinates = this.coordinates
        cloneObj.hid = this.hid
        cloneObj.haveChronics = this.haveChronics
        cloneObj.people = this.people
        cloneObj.pcuCode = this.pcuCode
        cloneObj._sync=this._sync

        return cloneObj
    }

}

data class Chronic(val idc10: String, val diagDate: LocalDate) {
    var diagHospCode: String? = null
    var careHospCode: String? = null
    var status: String = "active"
    var dischardDate: LocalDate? = null
    var houseId: Int? = null
    var pid: Long? = null
}

interface Identity {
    val id: String
    val type: String
    fun isValid(): Boolean
}

class ThaiCitizenId(override val id: String) : Identity {
    override val type: String = "thailand-citizen-id"

    override fun isValid(): Boolean = id.length == 13
}

class ThaiHouseholdId(override val id: String) : Identity {
    override val type: String = "thailand-household-id"

    override fun isValid(): Boolean = id.length == 11
}


class IdentityDeserializer : JsonDeserializer<Identity>, JsonSerializer<Identity> {
    override fun serialize(src: Identity?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return context!!.serialize(src)

    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Identity {
        val jsonObj = json.asJsonObject
        return when (jsonObj.get("type").asString) {
            "thailand-citizen-id" -> ThaiCitizenId(jsonObj.get("id").asString)
            "thailand-household-id" -> ThaiHouseholdId(jsonObj.get("id").asString)
            else -> throw IllegalArgumentException("Not support Identity type")
        }
    }
}
