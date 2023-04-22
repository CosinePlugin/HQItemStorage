package kr.hqservice.storage.extension

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private val air = ItemStack(Material.AIR)

internal fun List<ItemStack>.getSplitAndFilledList(): List<Array<ItemStack>> {
    val numChunks = (this.size + 53) / 54
    return (0 until numChunks).map { i ->
        val start = i * 54
        val end = minOf((i + 1) * 54, this.size)
        this.subList(start, end).toMutableList().apply {
            if (size < 54) {
                repeat(54 - size) { add(air) }
            }
        }.toTypedArray()
    }
}