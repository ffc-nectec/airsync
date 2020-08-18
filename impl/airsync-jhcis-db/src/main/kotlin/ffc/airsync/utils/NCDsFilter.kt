/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package ffc.airsync.utils

import java.util.regex.Pattern

fun String.ncdsFilter(): Boolean {
    val ncdFilterList = arrayListOf<String>().apply {
        add("""^e10\.\d$""")
        add("""^e11\.\d$""")
        add("""^i10$""")
    }

    ncdFilterList.forEach {
        val pattern = Pattern.compile(it, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(this)
        if (matcher.find()) return true
    }
    return false
}
