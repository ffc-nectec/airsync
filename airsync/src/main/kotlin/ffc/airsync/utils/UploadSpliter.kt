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
    private val logger by lazy { getLogger(this) }
    fun <T> upload(fixSizeCake: Int, list: List<T>, howToPutCake: (list: List<T>, block: Int) -> Unit) {
        val cakePound = cutCake(fixSizeCake, list)
        var runtime = 0L
        val size = cakePound.size

        cakePound.forEachIndexed { index, it ->
            var log = "Run ${index + 1}"
            val time = measureTimeMillis {
                log += (":$size ....")
                howToPutCake(it, index + 1)
            }
            runtime += time
            val realIndex = index + 1
            val timeString = ((size - realIndex) * (runtime / realIndex)).toStringTime()
            log += "push $timeString"
            logger.debug(log)
        }
    }

    private fun <T> cutCake(fixSizeCake: Int, list: List<T>): List<List<T>> {
        val cake = list.size
        logger.info("UploadSpliter cutCake size $cake Row per req $fixSizeCake")

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
