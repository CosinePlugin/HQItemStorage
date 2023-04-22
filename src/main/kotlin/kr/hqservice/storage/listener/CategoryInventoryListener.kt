package kr.hqservice.storage.listener

import kr.hqservice.storage.inventory.CategoryInventoryHolder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class CategoryInventoryListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        event.inventory.holder?.let {
            if (it is CategoryInventoryHolder) {
                it.onInventoryClick(event)
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        event.inventory.holder?.let {
            if (it is CategoryInventoryHolder) {
                it.onInventoryClose(event)
            }
        }
    }
}