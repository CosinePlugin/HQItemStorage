package kr.hqservice.storage.inventory

import kr.hqservice.storage.HQItemStorage
import kr.hqservice.storage.HQItemStorage.Companion.prefix
import kr.hqservice.storage.data.Category
import kr.hqservice.storage.extension.amount
import kr.hqservice.storage.extension.later
import kr.hqservice.storage.inventory.InventoryUtils.playButtonSound
import kr.hqservice.storage.inventory.InventoryUtils.setItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class CategoryItemEditorInventory(
    private val plugin: HQItemStorage,
    private val category: Category,
    private var page: Int
) : CategoryInventoryHolder("${category.name} 카테고리 : 아이템 편집", 6, true) {

    private val items = category.getItems()
    private val chunkedItems get() = items.chunked(45)
    private val nowPageItems get() = chunkedItems[page]

    override fun prevInit(inventory: Inventory) {
        inventory.setItem(45, InventoryUtils.beforePageButton)
        inventory.setItem(46..52, InventoryUtils.background)
        inventory.setItem(53, InventoryUtils.nextPageButton)
    }

    override fun init(inventory: Inventory) {
        inventory.setItem(0..44, InventoryUtils.air)
        if (items.isEmpty()) return
        nowPageItems.forEachIndexed { index, item ->
            inventory.setItem(index, item)
        }
    }

    override fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null) return

        val player = event.whoClicked as Player

        event.itemController(player)
        event.pageController(player)
    }

    private fun InventoryClickEvent.itemController(player: Player) {
        // 제거
        val slot = rawSlot
        if (items.isNotEmpty() && slot < 45 && slot < nowPageItems.size) {
            val item = nowPageItems[slot]

            category.removeItem(item)
            player.sendMessage("$prefix 아이템이 제거되었습니다.")

            if (page != 0 && page >= chunkedItems.size) {
                page--
            }
            init(inventory)
        }
        // 등록
        if (slot > 53) {
            val clickedItem = currentItem ?: return
            val item = clickedItem.clone().amount()

            if (category.contains(item)) {
                player.sendMessage("$prefix 이미 등록되어 있는 아이템입니다.")
                return
            }

            category.addItem(item)
            player.sendMessage("$prefix 아이템이 등록되었습니다.")

            if (inventory.getItem(44) != null) {
                page++
            }
            init(inventory)
        }
    }

    private fun InventoryClickEvent.pageController(player: Player) {
        if (slot in 45..53) isCancelled = true
        when (slot) {
            45 -> {
                player.playButtonSound()
                if (page == 0) {
                    player.sendMessage("$prefix 이전 페이지가 존재하지 않습니다.")
                    return
                }
                page--
                init(inventory)
            }
            53 -> {
                player.playButtonSound()
                if (page + 1 >= chunkedItems.size) {
                    player.sendMessage("$prefix 다음 페이지가 존재하지 않습니다.")
                    return
                }
                page++
                init(inventory)
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        if (category.isChanged) {
            category.save()
            player.sendMessage("$prefix 변경 내용이 저장되었습니다.")
        }
        later { CategoryItemInventory(plugin, 0, category, page).openInventory(player) }
    }
}