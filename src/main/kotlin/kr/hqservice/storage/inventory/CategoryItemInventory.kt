package kr.hqservice.storage.inventory

import kr.hqservice.storage.HQItemStorage
import kr.hqservice.storage.HQItemStorage.Companion.prefix
import kr.hqservice.storage.data.Category
import kr.hqservice.storage.extension.later
import kr.hqservice.storage.extension.setLore
import kr.hqservice.storage.inventory.InventoryUtils.playButtonSound
import kr.hqservice.storage.inventory.InventoryUtils.setItem
import kr.hqservice.storage.util.InvisibleSign
import kr.ms.core.util.ItemStackNameUtil.getItemName
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class CategoryItemInventory(
    private val plugin: HQItemStorage,
    private val beforeCategoryPage: Int? = null,
    private val category: Category,
    private var page: Int,
    private val isSearched: (CategoryItemInventory) -> Unit = {}
) : CategoryInventoryHolder("${category.name} 카테고리 : 아이템 목록", 6, true) {

    private var items = category.getItems()
    private val chunkedItems get() = items.chunked(45)
    private val nowPageItems get() = chunkedItems[page]

    private var isPrevented = false

    override fun prevInit(inventory: Inventory) {
        inventory.setItem(45..53, InventoryUtils.background)

        inventory.setItem(45, InventoryUtils.beforePageButton)
        inventory.setItem(53, InventoryUtils.nextPageButton)

        inventory.setItem(47, InventoryUtils.itemSearchButton)
        inventory.setItem(49, InventoryUtils.itemEditorButton)
        inventory.setItem(51, InventoryUtils.itemSortButton)
    }

    override fun init(inventory: Inventory) {
        isSearched(this)
        inventory.setItem(0..44, InventoryUtils.air)
        if (items.isEmpty()) return
        nowPageItems.forEachIndexed { index, item ->
            inventory.setItem(index, item.clone().setLore("", "§a[ 클릭 시 해당 아이템을 지급 받습니다. ]"))
        }
    }

    override fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null) return

        val slot = event.rawSlot
        if (slot > 53) return

        val player = event.whoClicked as Player

        event.pageController(player)
        event.itemController(player)
        event.functionController(player)
    }

    private fun InventoryClickEvent.itemController(player: Player) {
        if (items.isEmpty()) return
        if (slot > 45 || slot >= nowPageItems.size) return

        player.playButtonSound()

        val item = nowPageItems[slot]
        player.inventory.addItem(item)
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
            player.sendMessage("$prefix 이전 페이지가 존재하지 않습니다.")
            return
        }
        page--
        init(inventory)
    }

    private fun showNextPage(player: Player) {
        player.playButtonSound()
        if (page + 1 >= chunkedItems.size) {
            player.sendMessage("$prefix 다음 페이지가 존재하지 않습니다.")
            return
        }
        page++
        init(inventory)
    }

    private fun InventoryClickEvent.functionController(player: Player) {
        when (slot) {
            47 -> showSearchWindow(player)

            49 -> showItemEditorInventory(player)

            51 -> showItemSortList(player, click)
        }
    }

    private fun showSearchWindow(player: Player) {
        isPrevented = true
        player.closeInventory()

        player.playButtonSound()
        player.sendMessage("$prefix 검색하실 단어를 입력해주세요.")

        items = category.getItems()
        InvisibleSign(player) { texts ->
            val text = "${texts[0]}${texts[1]}${texts[2]}${texts[3]}"

            if (text == "") {
                player.sendMessage("$prefix 검색이 취소되었습니다.")
                later { CategoryItemInventory(plugin, beforeCategoryPage, category, page).openInventory(player) }
                return@InvisibleSign true
            }

            player.sendMessage("$prefix $text 단어의 검색 결과입니다.")
            later {
                CategoryItemInventory(plugin, beforeCategoryPage, category, page) { categoryItem ->
                    categoryItem.items = items.filter { it.isIncludeText(text) }
                }.openInventory(player)
            }
            true
        }.open()
    }

    private fun ItemStack.isIncludeText(text: String): Boolean {
        return itemMeta?.displayName?.contains(text) == true || type.name.lowercase().contains(text.lowercase()) || getItemName(this).contains(text)
    }

    private fun showItemEditorInventory(player: Player) {
        isPrevented = true
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
        later { CategoryItemEditorInventory(plugin, category, page).openInventory(player) }
    }

    private fun showItemSortList(player: Player, click: ClickType) {
        player.playButtonSound()
        if (click == ClickType.LEFT) {
            items = items.sortedBy { it.type.name }
            init(inventory)
        }
        if (click == ClickType.RIGHT) {
            items = items.sortedBy {
                if (it.itemMeta?.hasDisplayName() == true) {
                    it.itemMeta!!.displayName
                } else {
                    getItemName(it)
                }
            }
            init(inventory)
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        if (isPrevented) return
        val player = event.player as Player
        if (beforeCategoryPage != null) {
            later { CategoryInventory(plugin, beforeCategoryPage).openInventory(player) }
        }
    }
}