package kr.hqservice.storage.inventory

import kr.hqservice.storage.HQItemStorage
import kr.hqservice.storage.extension.hideAllItemFlags
import kr.hqservice.storage.extension.later
import kr.hqservice.storage.extension.setDisplayName
import kr.hqservice.storage.extension.setNewLore
import kr.hqservice.storage.inventory.InventoryUtils.playButtonSound
import kr.hqservice.storage.inventory.InventoryUtils.setItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

class CategoryInventory(
    private val plugin: HQItemStorage,
    private var page: Int
) : CategoryInventoryHolder("최상위 카테고리", 6, true) {

    private val categoryRepository = plugin.categoryRepository
    private val categories = categoryRepository.getCategories()
    private val chunkedCategories get() = categories.chunked(45)
    private val nowPageCategories get() = chunkedCategories[page]

    override fun prevInit(inventory: Inventory) {
        inventory.setItem(45, InventoryUtils.beforePageButton)
        inventory.setItem(46..52, InventoryUtils.background)
        inventory.setItem(53, InventoryUtils.nextPageButton)
    }

    override fun init(inventory: Inventory) {
        inventory.setItem(0..44, InventoryUtils.air)
        nowPageCategories.forEachIndexed { index, category ->
            val firstItem = category.getFirstItem().clone()
                .setDisplayName("§f${category.name} 카테고리")
                .setNewLore("§7클릭 시 해당 카테고리로 이동합니다.")
                .hideAllItemFlags()
            inventory.setItem(index, firstItem)
        }
    }

    override fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null) return

        val slot = event.rawSlot
        if (slot > 54) return

        val player = event.whoClicked as Player

        event.pageController(player)
        event.categoryContoller(player)
    }

    private fun InventoryClickEvent.pageController(player: Player) {
        when (slot) {
            45 -> showBeforePage(player)

            53 -> showNextPage(player)
        }
    }

    private fun showBeforePage(player: Player) {
        player.playButtonSound()
        if (page == 0) {
            player.sendMessage("${HQItemStorage.prefix} 이전 페이지가 존재하지 않습니다.")
            return
        }
        page--
        init(inventory)
    }

    private fun showNextPage(player: Player) {
        player.playButtonSound()
        if (page + 1 >= chunkedCategories.size) {
            player.sendMessage("${HQItemStorage.prefix} 다음 페이지가 존재하지 않습니다.")
            return
        }
        page++
        init(inventory)
    }

    private fun InventoryClickEvent.categoryContoller(player: Player) {
        if (nowPageCategories.isEmpty() || slot >= nowPageCategories.size) return

        val category = nowPageCategories[slot]
        player.playButtonSound()
        later { CategoryItemInventory(plugin, page, category, 0).openInventory(player) }
    }
}