package ffc.airsync.gui.drawable

import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.AirSyncGUI.MESSAGE_TYPE.ERROR
import ffc.airsync.ui.AirSyncGUI.MESSAGE_TYPE.INFO
import ffc.airsync.ui.AirSyncGUI.MESSAGE_TYPE.OK
import ffc.airsync.ui.KEY
import java.awt.Component
import java.awt.Dimension
import javax.swing.ImageIcon

internal class CreateMessageItem(
    val listComponent: HashMap<KEY, Component>,
    val data: Pair<KEY, Any>,
    val airsync: MainGUI
) {
    fun create(width: Int, height: Int) {
        if (listComponent[data.first] == null) {
            val newCheckData = SuccessConfirm()
            newCheckData.preferredSize = Dimension(width, height)
            listComponent[data.first] = newCheckData
            airsync.statusPanel.add(newCheckData)
        }
        val checkData = data.second as AirSyncGUI.Message
        val checkDataConfirm = listComponent[data.first] as SuccessConfirm
        checkDataConfirm.icon.icon = when (checkData.type) {
            OK -> {
                ImageIcon(
                    "check.png".getImageScalingResource(
                        height - 10,
                        height - 10
                    )
                )
            }
            ERROR -> {
                ImageIcon(
                    "error.png".getImageScalingResource(
                        height - 10,
                        height - 10
                    )
                )
            }
            INFO -> {
                ImageIcon(
                    "info.png".getImageScalingResource(
                        height - 10,
                        height - 10
                    )
                )
            }
        }
        checkDataConfirm.text.text = checkData.message
    }
}
