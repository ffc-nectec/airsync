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

package ffc.airsync.utils

object UploadSpliterMap {

    fun <K, T> upload(fixSizeCake: Int, list: Map<K, T>, howToPutCake: (list: Map<K, T>, block: Int) -> Unit) {
        val cakePound = cutCake(fixSizeCake, list)

        val size = cakePound.size
        printDebug("Run size $size")

        var index = 1
        cakePound.forEach { someCake ->
            print("\nStart push ${index++} ....")
            howToPutCake(someCake.value, someCake.key)
            print(" Finish push")
        }
    }

    private fun <K, T> cutCake(fixSizeCake: Int, list: Map<K, T>): Map<Int, Map<K, T>> {
        val table = hashMapOf<Int, HashMap<K, T>>()

        var index = 1
        var cakeNo = 1
        list.forEach { item ->
            if (table[cakeNo] == null)
                table[cakeNo] = hashMapOf()
            table[cakeNo]!![item.key] = item.value

            if ((index % fixSizeCake) == 0)
                cakeNo++
            index++
        }

        return table
    }
}
