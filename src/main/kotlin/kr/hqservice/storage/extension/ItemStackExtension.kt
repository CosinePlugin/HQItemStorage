package kr.hqservice.storage.extension

import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

fun ItemStack.amount(amount: Int = 1): ItemStack {
    this.amount = amount
    return this
}

fun ItemStack.setDisplayName(displayName: String): ItemStack {
    return apply { itemMeta = itemMeta?.apply { setDisplayName(displayName) } }
}

fun ItemStack.setNewLore(vararg lores: String?): ItemStack {
    return apply { itemMeta = itemMeta?.apply { lore = lores.filterNotNull() } }
}

fun ItemStack.setLore(vararg lores: String?): ItemStack {
    return apply {
        itemMeta = itemMeta?.apply {
            val newLore = mutableListOf<String>()
            lore?.let { newLore.addAll(it) }
            newLore.addAll(lores.filterNotNull())
            lore = newLore
        }
    }
}

fun ItemStack.hideAllItemFlags(): ItemStack {
    return apply { itemMeta = itemMeta?.apply { addItemFlags(*ItemFlag.values()) } }
}