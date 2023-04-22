import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Test {

    val list = mutableListOf<String>()

    @BeforeEach
    fun before() {
        (1..136).forEach {
            list.add("$it")
        }
    }

    @Test
    fun test() {
        splitItemsIntoChunks2(list).forEachIndexed { index, strings ->
            println("( $index | size: ${strings.size} ) $strings")
        }
    }

    fun splitItemsIntoChunks(items: List<String>): List<List<String>> {
        val chunks = mutableListOf<List<String>>()
        val numChunks = (items.size + 53) / 54 // round up division
        for (i in 0 until numChunks) {
            val start = i * 54
            val end = minOf((i + 1) * 54, items.size)
            val chunk = items.subList(start, end).toMutableList()
            if (chunk.size < 54) {
                chunk.addAll(List(54 - chunk.size) { "-1" })
            }
            chunks.add(chunk)
        }
        return chunks
    }
    fun splitItemsIntoChunks2(items: List<String>): List<List<String>> {
        val numChunks = (items.size + 53) / 54 // round up division
        return (0 until numChunks).map { i ->
            val start = i * 54
            val end = minOf((i + 1) * 54, items.size)
            items.subList(start, end).toMutableList().apply {
                if (size < 54) {
                    repeat(54 - size) { add("-1") }
                }
            }
        }
    }
}