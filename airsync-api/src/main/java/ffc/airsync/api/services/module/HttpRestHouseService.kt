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

package ffc.airsync.api.services.module

import ffc.airsync.api.dao.DaoFactory
import me.piruin.geok.geometry.FeatureCollection

class HttpRestHouseService : HouseServices {

    val houseDao = DaoFactory().buildHouseDao()

    override fun getHouse(page: Int, per_page: Int, id: String): FeatureCollection {

        var pageLocal = page
        var per_pageLocal = per_page

        if (pageLocal == 0) {
            pageLocal = 1
        }
        if (per_pageLocal == 0) {
            per_pageLocal = 200
        }


        val geoJson = FeatureCollection()

        //val houseList=houseDao.find().filter { it.xgis!=null && it.ygis != null }

        // houseList.get(1).


        return geoJson


    }

}
