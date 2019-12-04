package ffc.airsync.gui.drawable

import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.KEY
import java.awt.Component
import java.awt.Dimension

internal class CreateProgreassDataItem(
    val listComponent: HashMap<KEY, Component>,
    val data: Pair<KEY, Any>,
    val airsync: MainGUI
) {
    fun create(width: Int, height: Int) {
        if (listComponent[data.first] == null) {
            val newStatusProgress = StatusProgress()
            newStatusProgress.preferredSize = Dimension(width, height)
            listComponent[data.first] = newStatusProgress
            airsync.statusPanel.add(newStatusProgress)
        }

        val progressData = data.second as AirSyncGUI.ProgressData
        val statusProgress = listComponent[data.first] as StatusProgress
        statusProgress.jProgressBar.minimum = 0
        statusProgress.jProgressBar.maximum = progressData.max
        statusProgress.jProgressBar.value = progressData.current
        statusProgress.label.text =
            data.first + if (progressData.message != null) ":${progressData.message}" else ""
    }
}
