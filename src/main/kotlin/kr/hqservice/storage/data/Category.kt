package kr.hqservice.storage.data

import kr.hqservice.storage.extension.*
import kr.hqservice.storage.extension.getSplitAndFilledList
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.File

class Category(
    categoryFolder: File,
    val name: String
) {

    private companion object {
        val none = ItemStack(Material.BEDROCK)
    }

    private val categoryFile = File(categoryFolder, "$name.yml")
    private val categoryConfig = YamlConfiguration.loadConfiguration(categoryFile)

    private val items = mutableListOf<ItemStack>()

    var isChanged = false

    init {
        load()
    }

    fun load() {
        val itemList = categoryConfig.getStringList("items")
            .map(Base64Coder::decodeLines)
            .map(ByteArray::toDecompressItemArray)
        val finalItemList = itemList.flatMap { list -> list.filter { it.type != Material.AIR } }
        items.addAll(finalItemList)
    }

    fun save() {
        async {
            val compress = items.getSplitAndFilledList()
                .map(Array<ItemStack>::toCompressByteArray)
                .map(Base64Coder::encodeLines)
            categoryConfig.set("items", compress)
            categoryConfig.save(categoryFile)
            isChanged = false
        }
    }

    fun reload() {
        categoryConfig.load(categoryFile)
        items.clear()
        load()
    }

    fun contains(item: ItemStack): Boolean {
        return items.contains(item)
    }

    fun addItem(item: ItemStack) {
        items.add(item)
        isChanged = true
    }

    fun addItems(contents: Array<ItemStack?>) {
        val finalContents = contents
            .filterNotNull()
            .filter { it.type != Material.AIR }
            .map { it.amount() }
        items.addAll(finalContents)
    }

    fun removeItem(item: ItemStack) {
        items.remove(item)
        isChanged = true
    }

    fun getItems(): List<ItemStack> {
        return items
    }

    fun getFirstItem(): ItemStack {
        return items.firstOrNull() ?: none
    }
}