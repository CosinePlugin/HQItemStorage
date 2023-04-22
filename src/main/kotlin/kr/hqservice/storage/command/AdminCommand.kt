package kr.hqservice.storage.command

import kr.hqservice.storage.HQItemStorage
import kr.hqservice.storage.HQItemStorage.Companion.prefix
import kr.hqservice.storage.extension.later
import kr.hqservice.storage.inventory.CategoryInventory
import kr.hqservice.storage.inventory.CategoryItemInventory
import kr.ms.core.util.ItemStackNameUtil.getItemName
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class AdminCommand(
    private val plugin: HQItemStorage
) : CommandExecutor, TabCompleter {

    private companion object {
        val commandTabList = listOf("add", "remove", "list", "give")
        val categoryTabList = listOf("remove", "list")
    }

    private val categoryRepository = plugin.categoryRepository

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        if (args.size <= 1) {
            return commandTabList
        }
        if (args.size == 2 && categoryTabList.contains(args[0])) {
            return categoryRepository.getCategoryNames()
        }
        return emptyList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        val player: Player = sender
        if (args.isEmpty()) {
            printHelp(player)
            return true
        }
        checker(player, args)
        return true
    }

    private fun printHelp(player: Player) {
        player.sendMessage(
            "$prefix HQItemStorage 명령어 도움말",
            "",
            "$prefix /storage add [이름] : 카테고리를 추가합니다.",
            "$prefix /storage remove [이름] : 카테고리를 제거합니다.",
            "$prefix /storage list [이름] : 카테고리를 오픈합니다.",
            "$prefix /storage give : 손에 있는 아이템을 모든 유저에게 아이템을 지급합니다.",
            "§7[ list 이름 칸에 미입력 시 최상위 카테고리를 오픈합니다. ]"
        )
    }

    private fun checker(player: Player, args: Array<out String>) {
        when (args[0]) {
            "add" -> add(player, args)
            "remove" -> remove(player, args)
            "list" -> list(player, args)
            "give" -> give(player)
        }
    }

    private fun add(player: Player, args: Array<out String>) {
        if (args.size == 1) {
            player.sendMessage("$prefix 등록하실 카테고리의 이름을 입력해주세요.")
            return
        }
        val categoryName = args[1]
        if (categoryRepository.isCreated(categoryName)) {
            player.sendMessage("$prefix 이미 존재하는 카테코리입니다.")
            return
        }
        categoryRepository.addCategory(categoryName)
        player.sendMessage("$prefix $categoryName 카테고리가 추가되었습니다.")
    }

    private fun remove(player: Player, args: Array<out String>) {
        if (args.size == 1) {
            player.sendMessage("$prefix 등록하실 카테고리의 이름을 입력해주세요.")
            return
        }
        val categoryName = args[1]
        if (categoryName == "default") {
            player.sendMessage("$prefix default 카테고리는 제거할 수 없습니다.")
            return
        }
        if (!categoryRepository.isCreated(categoryName)) {
            player.sendMessage("$prefix 존재하지 않는 카테고리입니다.")
            return
        }
        categoryRepository.removeCategory(categoryName)
        player.sendMessage("$prefix $categoryName 카테고리가 제거되었습니다.")
    }

    private fun list(player: Player, args: Array<out String>) {
        if (args.size == 1) {
            later { CategoryInventory(plugin, 0).openInventory(player) }
            return
        }
        val categoryName = args[1]
        if (!categoryRepository.isCreated(categoryName)) {
            player.sendMessage("$prefix 존재하지 않는 카테고리입니다.")
            return
        }
        val category = categoryRepository.getCategory(categoryName) ?: run {
            player.sendMessage("$prefix 카테고리를 불러오지 못했습니다.")
            return
        }
        later { CategoryItemInventory(plugin, null, category, 0).openInventory(player) }
    }

    private fun give(player: Player) {
        val item = player.inventory.itemInMainHand.clone()
        if (item.type == Material.AIR) {
            player.sendMessage("$prefix 손에 아이템을 들어주세요.")
            return
        }
        val server = player.server
        server.onlinePlayers.forEach {
            it.inventory.addItem(item)
        }
        server.broadcastMessage("$prefix ${player.name}님이 모두에게 ${getItemName(item)}을(를) 지급하였습니다!")
    }
}