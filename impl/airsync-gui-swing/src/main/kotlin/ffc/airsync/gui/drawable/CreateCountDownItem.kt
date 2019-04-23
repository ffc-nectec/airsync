package ffc.airsync.gui.drawable

import ffc.airsync.ui.AirSyncGUI.CoutDown
import ffc.airsync.ui.KEY
import java.awt.Component
import java.awt.Dimension

internal class CreateCountDownItem(
    val listComponent: HashMap<KEY, Component>,
    val data: Pair<KEY, Any>,
    val airsync: MainGUI,
    val callBackCountDone: () -> Unit
) {
    fun create(width: Int, height: Int) {
        val message = data.second as CoutDown
        if (listComponent[data.first] == null) {
            val countDownItem = CoutDownMessage { callBackCountDone() }
            countDownItem.preferredSize = Dimension(width, height)
            countDownItem.message.font = kanitMedium.deriveFont(30f)
            countDownItem.couter.font = kanitMedium.deriveFont(22f)
            listComponent[data.first] = countDownItem
            airsync.statusPanel.add(countDownItem)
        }

        val countDownItem = listComponent[data.first] as CoutDownMessage
        countDownItem.setMessage(message.message, message.count)
        countDownItem.startCount()
    }
}