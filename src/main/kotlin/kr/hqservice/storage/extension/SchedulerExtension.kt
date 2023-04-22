package kr.hqservice.storage.extension

import kr.hqservice.storage.HQItemStorage.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask

fun async(block: () -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable(block))
}

fun later(delay: Int = 1, async: Boolean = false, block: () -> Unit = {}): BukkitTask {
    return if (async) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, Runnable(block), delay.toLong())
    } else {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable(block), delay.toLong())
    }
}