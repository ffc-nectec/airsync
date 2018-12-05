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

import kotlin.system.measureTimeMillis

object UploadSpliter {
    fun <T> upload(fixSizeCake: Int, list: List<T>, howToPutCake: (list: List<T>, block: Int) -> Unit) {
        val cakePound = cutCake(fixSizeCake, list)
        var runtime = 0L
        val size = cakePound.size

        printDebug("Run size $size")
        cakePound.forEachIndexed { index, it ->

            val time = measureTimeMillis {
                print("\nStart push $index ....")
                howToPutCake(it, index + 1)
                print(" Finish push")
            }
            runtime += time
            val realIndex = index + 1
            ((size - realIndex) * (runtime / realIndex)).printTime()
        }
        println()
    }

    private fun <T> cutCake(fixSizeCake: Int, list: List<T>): List<List<T>> {
        val cake = list.size
        printDebug("UploadSpliter cutCake size $cake Row per req $fixSizeCake")

        val noOfCake = cake / fixSizeCake // แบ่งได้กี่ชุด
        val cakeScraps = cake % fixSizeCake // เคษเหลือ
        val table = arrayListOf<ArrayList<T>>()

        for (cakeNo in 0..(noOfCake - 1)) {
            val cakePlate = arrayListOf<T>()

            val startPositionCutCake = cakeNo * fixSizeCake
            val endPositionCutCake = (startPositionCutCake + fixSizeCake) - 1

            for (pieceOfCake in startPositionCutCake..endPositionCutCake) {
                cakePlate.add(list[pieceOfCake])
            }
            table.add(cakePlate)
        }

        if (cakeScraps != 0) {
            val cakePlate = arrayListOf<T>()
            val startPositionCutCake = noOfCake * fixSizeCake
            val endPositionCutCake = (startPositionCutCake + cakeScraps) - 1

            for (pieceOfCake in startPositionCutCake..endPositionCutCake) {
                cakePlate.add(list[pieceOfCake])
            }
            table.add(cakePlate)
        }

        return table
    }
}
