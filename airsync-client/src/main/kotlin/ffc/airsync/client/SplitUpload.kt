package ffc.airsync.client

import ffc.airsync.client.module.ApiFactory
import ffc.model.printDebug

object SplitUpload {


    interface HowToSendCake<T> {
        fun send(cakePlate: ArrayList<T>)
    }

    interface StatusSendCake {
        fun status(noCakePlate: Int) {}
    }


    fun <T> sendCake(volumeOfSend: Int, table: ArrayList<ArrayList<T>>, howToSend: HowToSendCake<T>, statusCallbackSendCake: StatusSendCake = object : StatusSendCake {}) {


        var slotSend = 0
        var stackSlot = 0
        val size = table.size

        for (createSlot in 0..size) {


            synchronized(slotSend) {

                if (slotSend < volumeOfSend) {
                    slotSend++


                    Thread(Runnable {

                        var cakePlate: ArrayList<T>? = null
                        synchronized(slotSend) {
                            if (stackSlot < size) {
                                cakePlate = table[stackSlot]
                                stackSlot++
                            }
                        }


                        if (cakePlate != null) {
                            howToSend.send(cakePlate!!)

                            synchronized(slotSend) {
                                statusCallbackSendCake.status(stackSlot)
                                slotSend--
                            }
                        }
                    })
                }


            }


            // putCake(it, howToSend)
        }
    }


    fun <T> upload(fixSizeCake: Int, list: List<T>, howToSend: HowToSendCake<T>) {
        val cakePound = cutCake(fixSizeCake, list)
        putCakeOld(cakePound, howToSend)
    }

    private fun <T> putCakeNew(table: ArrayList<ArrayList<T>>, howToSend: HowToSendCake<T>) {


        val thread = Thread {
            Runnable {

            }
        }


        printDebug("Run size ${table.size}")
        val slotRunning = 3

        var stackRun = 0
        for (noSlot: Int in 1..slotRunning) {


        }


    }


    private fun <T> startRun(list: T, howToSend: HowToSendCake<T>) {

    }



    private fun <T> putCakeOld(table: ArrayList<ArrayList<T>>, howToSend: HowToSendCake<T>) {

        printDebug("Run size ${table.size}")
        var i = 1
        table.forEach {
            val thread = Thread(Runnable {
                var runNo: Int = 0


                synchronized(i) {
                    runNo = i++
                }
                printDebug("\t\t Start upload $runNo")
                howToSend.send(it)
                printDebug("\t\t Finish upload $runNo")
            })
            thread.start()
            thread.join()
            Thread.sleep(1000)
        }

    }


    private fun <T> cutCake(fixSizeCake: Int, list: List<T>): ArrayList<ArrayList<T>> {

        val cake = list.size
        printDebug("SplitUpload cutCake size $cake Row per req $fixSizeCake")

        val noOfCake = cake / fixSizeCake  //แบ่งได้กี่ชุด
        val cakeScraps = cake % fixSizeCake  //เคษเหลือ
        val table = arrayListOf<ArrayList<T>>()


        for (cakeNo in 0..(noOfCake - 1)) {
            val cakePlate = arrayListOf<T>()

            val startPositionCutCake = cakeNo * fixSizeCake
            val endPositionCutCake = startPositionCutCake + fixSizeCake

            for (pieceOfCake in startPositionCutCake..endPositionCutCake) {
                cakePlate.add(list[pieceOfCake])
            }
            table.add(cakePlate)
        }


        if (cakeScraps != 0) {
            val cakePlate = arrayListOf<T>()
            val startPositionCutCake = (noOfCake - 1) * fixSizeCake
            val endPositionCutCake = startPositionCutCake + cakeScraps

            for (pieceOfCake in startPositionCutCake..endPositionCutCake) {
                cakePlate.add(list[pieceOfCake])
            }
            table.add(cakePlate)
        }


        return table
    }
}
