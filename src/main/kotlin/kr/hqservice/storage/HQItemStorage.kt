package kr.hqservice.storage

import kr.hqservice.storage.command.AdminCommand
import kr.hqservice.storage.listener.CategoryInventoryListener
import kr.hqservice.storage.repository.CategoryRepository
import kr.hqservice.storage.util.InvisibleSign
import kr.ms.core.bstats.Metrics
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class HQItemStorage : JavaPlugin() {

    companion object {
        internal const val prefix = "§b[ ItemStorage ]§f"
        internal lateinit var plugin: Plugin
    }

    lateinit var categoryRepository: CategoryRepository
        private set

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        if (server.pluginManager.getPlugin("MS-Core") == null) {
            logger.warning("MS-Core 플러그인을 찾을 수 없어, 플러그인이 비활성화됩니다.")
            server.pluginManager.disablePlugin(this)
            return
        }
        if (server.pluginManager.getPlugin("ProtocolLib") == null) {
            logger.warning("ProtocolLib 플러그인을 찾을 수 없어, 플러그인이 비활성화됩니다.")
            server.pluginManager.disablePlugin(this)
            return
        }
        Metrics(this, 18264)

        InvisibleSign.registerListener()

        categoryRepository = CategoryRepository(this)
        categoryRepository.loadAll()

        server.pluginManager.registerEvents(CategoryInventoryListener(), this)

        getCommand("storage")?.setExecutor(AdminCommand(this))
    }
}