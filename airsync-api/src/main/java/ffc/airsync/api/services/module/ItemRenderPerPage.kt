package ffc.airsync.api.services.module

import ffc.model.printDebug
import javax.ws.rs.NotFoundException

interface AddItmeAction {
    fun onAddItemAction(itemIndex: Int)
}

fun itemRenderPerPage(page: Int, per_page: Int, count: Int, onAddItemAction: AddItmeAction) {


    val fromItem = ((page - 1) * per_page) + 1
    var toItem = (page) * per_page


    if (fromItem > count)
        throw NotFoundException("Query เกินหน้าสุดท้ายของบ้านแล้ว")
    if (toItem > count) {
        toItem = count
    }



    printDebug("page $page per_page $per_page")
    printDebug("from $fromItem to $toItem")

    (fromItem..toItem).forEach {
        onAddItemAction.onAddItemAction(it - 1)
    }


}
