package ru.senin.kotlin.wiki

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import ru.senin.kotlin.wiki.Handler.Companion.regex
import java.io.File
import java.io.FileInputStream
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.xml.parsers.SAXParserFactory

enum class Mods {
    TEXT,
    TITLE,
    TIME,
    SIZE,
    NONE
}

object Paths {
    const val TITLE = "page/title"
    const val TEXT = "page/revision/text"
    const val TIME = "page/revision/timestamp"
}

class Handler(
    private val mapTitles: ConcurrentHashMap<String, Int>,
    private val mapText: ConcurrentHashMap<String, Int>,
    private val mapBytes: ConcurrentHashMap<Int, Int>,
    private val mapDate: ConcurrentHashMap<Int, Int>,
) : DefaultHandler() {
    private var prevTags = mutableListOf<String>()
    private var currentPage: XMLPageBuilder? = null

    companion object {
        val regex = "[а-я]{3,}".toRegex()
    }

    private fun checkPath(str: String): Boolean {
        val path = str.split("/")
        if (prevTags.size > path.size) {
            for (i in path.indices) {
                if (prevTags[prevTags.size - path.size + i] != path[i]) {
                    return false
                }
            }
        }
        return true
    }

    private fun getMode() = when {
        checkPath(Paths.TITLE) -> Mods.TITLE
        checkPath(Paths.TEXT) -> Mods.TEXT
        checkPath(Paths.TIME) -> Mods.TIME
        else -> Mods.NONE
    }


    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        if (qName != null && qName == "page") {
            currentPage = XMLPageBuilder(mapTitles, mapText, mapBytes, mapDate)
        }

        qName?.let { prevTags.add(qName) }

        if (getMode() == Mods.TEXT) {
            attributes?.getValue("bytes")?.let {
                currentPage?.add(
                    it, Mods.SIZE
                )
            }
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        qName?.let {
            assert(prevTags.last() == qName)
            prevTags.removeLast()
        }

        if (qName == "page") {
            currentPage?.build()
            currentPage = null
        }
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        ch ?: return
        val str = String(ch, start, length).lowercase()
        currentPage?.add(str, getMode())
    }
}

class XMLPageBuilder(
    private val mapTitles: ConcurrentHashMap<String, Int>,
    private val mapText: ConcurrentHashMap<String, Int>,
    private val mapBytes: ConcurrentHashMap<Int, Int>,
    private val mapDate: ConcurrentHashMap<Int, Int>
) {
    private var title: StringBuilder? = null
    private var text: StringBuilder? = null
    private var time: Int? = null
    private var size: Int? = null

    fun add(string: String, mode: Mods) {
        when (mode) {
            Mods.TITLE -> title?.append(string) ?: run { title = StringBuilder(string) }
            Mods.TEXT -> text?.append(string) ?: run { text = StringBuilder(string) }
            Mods.TIME -> time = string.substring(0..3).toInt()
            Mods.SIZE -> size = string.toInt()
            else -> return
        }
    }

    companion object {
        fun putString(map: ConcurrentHashMap<String, Int>, string: String, regex: Regex) {
            regex
                .findAll(string)
                .map { it.value }
                .forEach {
                    map.compute(it) { _, v -> v?.inc() ?: 1 }
                }
        }

        fun putInt(map: ConcurrentHashMap<Int, Int>, i: Int) {
            map.compute(i) { _, v -> v?.inc() ?: 1 }
        }
    }

    fun build() {
        if (title == null || text == null || time == null || size == null) {
            return
        } else {
            putString(mapTitles, title!!.toString(), regex)
            putString(mapText, text!!.toString(), regex)
            putInt(mapDate, time!!)
            putInt(mapBytes, size!!.toString().length - 1)

        }
    }
}

private val Comparator = Comparator<Pair<Int, String>> { a, b ->
    when {
        (a.first == b.first) -> compareValues(a.second, b.second)
        else -> compareValues(b.first, a.first)
    }
}

fun fastSortToString(map: ConcurrentHashMap<String, Int>): String {
    val ans = sortedSetOf(Comparator)
    for (i in map) {
        ans.add(i.value to i.key)
        if (ans.size <= 300) {
            continue
        }
        ans.remove(ans.last())
    }
    return ans.joinToString(separator = "") { "${it.first} ${it.second}\n" }
}

fun toStringDateAndBytes(map: ConcurrentHashMap<Int, Int>) = buildString {
    var tmp = map.minByOrNull { it.key }?.key ?: return@buildString
    var cnt = 0
    while (cnt != map.size) {
        append(tmp).append(" ")
        val value = map[tmp++]
        if (value == null) {
            append(0)
        } else {
            cnt++
            append(value)
        }
        append("\n")
    }
}


fun solve(parameters: Parameters) {
    val mapTitles = ConcurrentHashMap<String, Int>()
    val mapText = ConcurrentHashMap<String, Int>()
    val mapBytes = ConcurrentHashMap<Int, Int>()
    val mapDate = ConcurrentHashMap<Int, Int>()

    val service = Executors.newFixedThreadPool(parameters.threads)
    parameters.inputs.forEach {
        service.submit {
            val fin = FileInputStream(it)
            val input = BZip2CompressorInputStream(fin)
            val sax = SAXParserFactory.newInstance().newSAXParser()
            sax.parse(input, Handler(mapTitles, mapText, mapBytes, mapDate))
        }
    }

    service.shutdown()
    try {
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (e: InterruptedException) {
        println(e.message)
    }

    File(parameters.output).writeText(
        """
            |Топ-300 слов в заголовках статей:
            |${fastSortToString(mapTitles)}
            |Топ-300 слов в статьях:
            |${fastSortToString(mapText)}
            |Распределение статей по размеру:
            |${toStringDateAndBytes(mapBytes)}
            |Распределение статей по времени:
            |${toStringDateAndBytes(mapDate)}
        """.trimMargin()
    )
}