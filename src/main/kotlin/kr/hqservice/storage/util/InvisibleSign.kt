package kr.hqservice.storage.util

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import kr.hqservice.storage.HQItemStorage.Companion.plugin
import kr.hqservice.storage.extension.later
import net.minecraft.core.BlockPosition
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer
import org.bukkit.entity.Player

class InvisibleSign(
    val player: Player,
    val response: (Array<String>) -> Boolean
) {

    private val playerLocation = player.location
    private val blockPosition = BlockPosition(playerLocation.blockX, player.world.minHeight, playerLocation.blockZ)
    private var originalBlock = Material.AIR

    companion object {
        private val receivers = HashMap<Player, InvisibleSign>()

        fun BlockPosition.toLocation(world: World): Location {
            return Location(world, u().toDouble(), v().toDouble(), w().toDouble())
        }

        fun registerListener() {
            ProtocolLibrary.getProtocolManager().addPacketListener(
                object : PacketAdapter(plugin, PacketType.Play.Client.UPDATE_SIGN) {
                    override fun onPacketReceiving(event: PacketEvent) {
                        val player = event.player
                        val sign = receivers.remove(player) ?: return

                        event.isCancelled = true

                        if (sign.response(event.packet.stringArrays.read(0)))
                            player.sendBlockChange(
                                sign.blockPosition.toLocation(player.world),
                                Bukkit.createBlockData(sign.originalBlock)
                            )
                        else
                            later { sign.open() }
                    }
                }
            )
        }
    }

    fun open() {
        blockPosition.toLocation(player.world).apply {
            originalBlock = block.type
            player.sendBlockChange(this, Bukkit.createBlockData(Material.OAK_SIGN))
        }
        val signPacket = PacketPlayOutOpenSignEditor(blockPosition)
        // val signEditorPacket = ClientboundOpenSignEditorPacket(blockPosition)
        (player as CraftPlayer).handle.b.a(signPacket)
        //(player as CraftPlayer).handle.connection.send(signEditorPacket)
        receivers[player] = this
    }
}