package kr.hqservice.storage.repository

import kr.hqservice.storage.data.Category
import kr.hqservice.storage.extension.async
import org.bukkit.plugin.Plugin
import java.io.File

class CategoryRepository(plugin: Plugin) {

    private val categoryFolder = File(plugin.dataFolder, "Category")

    private val categories = mutableMapOf<String, Category>()

    init {
        if (!categoryFolder.exists()) {
            categoryFolder.mkdirs()
        }
        val categoryName = "default"
        val defaultFile = File(categoryFolder, "$categoryName.yml")
        if (!defaultFile.exists()) {
            categories[categoryName] = Category(categoryFolder, categoryName).apply { save() }
        }
    }

    fun loadAll() {
        categoryFolder.listFiles()?.forEach { categoryFile ->
            val categoryName = categoryFile.name.run { substring(0, length - 4) }
            categories[categoryName] = Category(categoryFolder, categoryName)
        }
    }

    fun saveAll() {
        categories.values.forEach(Category::save)
    }

    fun reloadAll() {
        categories.values.forEach(Category::reload)
    }

    fun addCategory(categoryName: String) {
        categories[categoryName] = Category(categoryFolder, categoryName).apply { save() }
    }

    fun removeCategory(categoryName: String) {
        categories.remove(categoryName)
        async { File(categoryFolder, "$categoryName.yml").delete() }
    }

    fun isCreated(categoryName: String): Boolean {
        return categories.containsKey(categoryName)
    }

    fun getCategory(categoryName: String): Category? {
        return categories[categoryName]
    }

    fun getCategoryNames(): List<String> {
        return categories.keys.toList()
    }

    fun getCategories(): List<Category> {
        return categories.values.toList()
    }
}