package kr.hqservice.storage.inventory

import kr.hqservice.storage.HQItemStorage.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class CategoryInventoryHolder(
    private val display: String = "",
    private val row: Int = 1,
    private val isCancelled: Boolean = false,
    private val inventoryType: InventoryType = InventoryType.CHEST,
): InventoryHolder {

    private var inv: Inventory? = null

    init {
        createInventory()
    }

    private fun createInventory() : Inventory {
        val server = plugin.server
        val createInventory = if (inventoryType == InventoryType.CHEST) {
            server.createInventory(this, row * 9, display)
        } else {
            server.createInventory(this, inventoryType, display)
        }
        inv = createInventory
        return createInventory
    }

    override fun getInventory(): Inventory = inv ?: createInventory()

    open fun prevInit(inventory: Inventory) {}

    open fun prevInitWithPlayer(inventory: Inventory, player: Player) {}

    open fun init(inventory: Inventory) {}

    open fun initWithPlayer(inventory: Inventory, player: Player) {}

    fun openInventory(player: Player) {
        prevInit(inventory)
        prevInitWithPlayer(inventory, player)
        init(inventory)
        initWithPlayer(inventory, player)
        player.openInventory(inventory)
    }

    internal fun onInventoryClick(event: InventoryClickEvent) {
        if (isCancelled) event.isCancelled = true
        onClick(event)
    }

    internal fun onInventoryClose(event: InventoryCloseEvent) {
        onClose(event)
    }

    protected open fun onClick(event: InventoryClickEvent) {}

    protected open fun onClose(event: InventoryCloseEvent) {}
}