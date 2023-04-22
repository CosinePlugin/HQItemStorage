package kr.hqservice.storage.extension

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater
import kotlin.jvm.Throws

class ByteArrayCastException(msg: String): Exception(msg)
@Throws(ByteArrayCastException::class)
fun ItemStack.toByteArray(): ByteArray {
    val result: ByteArray
    try {
        ByteArrayOutputStream().use { bos ->
            BukkitObjectOutputStream(bos).use {
                it.writeInt(1)
                it.writeObject(this)
                result = bos.toByteArray()
            }
        }
    } catch (e: Exception) { throw ByteArrayCastException("cannot cast itemStack to bytearray -> ${itemMeta?.displayName?: type.name.lowercase()} ") }
    return result
}

@JvmName("toByteArrayNullableItemStack")
@Throws(ByteArrayCastException::class)
fun Array<ItemStack?>.toByteArray(): ByteArray {
    val result: ByteArray
    try {
        ByteArrayOutputStream().use { bos ->
            BukkitObjectOutputStream(bos).use {
                it.writeInt(size)
                forEach { item ->
                    if(item == null) it.writeObject(ItemStack(Material.AIR))
                    else it.writeObject(item)
                }
                result = bos.toByteArray()
            }
        }
    } catch (e: Exception) { throw ByteArrayCastException("cannot cast itemArray to bytearray") }
    return result
}

@JvmName("toByteArrayItemStack")
@Throws(ByteArrayCastException::class)
fun Array<ItemStack>.toByteArray(): ByteArray {
    val result: ByteArray
    try {
        ByteArrayOutputStream().use { bos ->
            BukkitObjectOutputStream(bos).use {
                it.writeInt(size)
                forEach { item ->
                    it.writeObject(item)
                }
                result = bos.toByteArray()
            }
        }
    } catch (e: Exception) { throw ByteArrayCastException("cannot cast itemArray to bytearray") }
    return result
}
@JvmName("toByteArrayCompress")
fun ItemStack.toCompressByteArray(): ByteArray = compressByteArray(toByteArray())
@JvmName("toByteArrayCompressNullableItemStack")
fun ItemStack?.toCompressByteArray(): ByteArray = compressByteArray(ItemStack(Material.AIR).toByteArray())
@JvmName("toByteArrayCompressNullableItemStack")
fun Array<ItemStack?>.toCompressByteArray(): ByteArray = compressByteArray(toByteArray())
@JvmName("toByteArrayCompressItemStack")
fun Array<ItemStack>.toCompressByteArray(): ByteArray = compressByteArray(toByteArray())

internal fun compressByteArray(array: ByteArray): ByteArray {
    val dfl = Deflater()
    dfl.setLevel(Deflater.BEST_COMPRESSION)
    dfl.setInput(array)
    dfl.finish()
    val out = ByteArrayOutputStream(array.size)
    val buffer = ByteArray(1024)
    while(!dfl.finished()) {
        val count = dfl.deflate(buffer)
        out.write(buffer, 0, count)
    }
    out.close()
    return out.toByteArray()
}

internal fun deCompressByteArray(array: ByteArray): ByteArray {
    val ifl = Inflater()
    ifl.setInput(array)
    val out = ByteArrayOutputStream(array.size)

    val buffer = ByteArray(1024)
    while(!ifl.finished()) {
        val count = ifl.inflate(buffer)
        out.write(buffer, 0, count)
    }
    out.close()
    return out.toByteArray()
}

@Throws(ByteArrayCastException::class)
fun ByteArray.toItemArray(): Array<ItemStack> {
    val result: Array<ItemStack>
    try {
        ByteArrayInputStream(this).use { input ->
            BukkitObjectInputStream(input).use { bos ->
                result = Array(bos.readInt()) { bos.readObject() as ItemStack }
            }
        }
    } catch (e: Exception) { throw ByteArrayCastException("cannot cast byteArray to itemStack: ${e.message}")
    }
    return result
}

@Throws(ByteArrayCastException::class)
fun ByteArray.toDecompressItemArray(): Array<ItemStack> {
    val result: Array<ItemStack>
    try {
        ByteArrayInputStream(deCompressByteArray(this)).use { input ->
            BukkitObjectInputStream(input).use { bos ->
                result = Array(bos.readInt()) { bos.readObject() as ItemStack }
            }
        }
    } catch (e: Exception) { throw ByteArrayCastException("cannot cast byteArray to itemStack: ${e.message}")
    }
    return result
}