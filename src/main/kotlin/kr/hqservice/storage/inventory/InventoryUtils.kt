package kr.hqservice.storage.inventory

import kr.hqservice.storage.extension.setDisplayName
import kr.hqservice.storage.extension.setLore
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

internal object InventoryUtils {

    val air = ItemStack(Material.AIR)

    val background = ItemStack(Material.WHITE_STAINED_GLASS_PANE).setDisplayName("§f")

    val beforePageButton = ItemStack(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c이전 페이지로 이동")

    val nextPageButton = ItemStack(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("§a다음 페이지로 이동")

    val itemEditorButton = ItemStack(Material.ANVIL).setDisplayName("§b아이템 편집")

    val itemSearchButton = ItemStack(Material.OAK_SIGN).setDisplayName("§d아이템 검색")

    val itemSortButton = ItemStack(Material.PISTON)
        .setDisplayName("§e아이템 정렬")
        .setLore(
            "§f- 좌클릭: Material 정렬",
            "§f- 우클릭: 한글 정렬"
        )

    fun Inventory.setItem(range: IntRange, item: ItemStack) {
        range.forEach { setItem(it, item) }
    }

    fun Inventory.setItem(item: ItemStack, vararg slot: Int) {
        slot.forEach { setItem(it, item) }
    }

    fun Player.playButtonSound(volume: Float = 1f, pitch: Float = 1f) {
        playSound(this, Sound.UI_BUTTON_CLICK, volume, pitch)
    }
}