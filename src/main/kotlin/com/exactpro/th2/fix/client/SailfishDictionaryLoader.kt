package com.exactpro.th2.fix.client

import com.exactpro.sf.configuration.dictionary.converter.SailfishDictionaryToQuckfixjConverter
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

object SailfishDictionaryLoader {
    fun load(stream: InputStream, outputDirectory: Path): Dictionaries {
        SailfishDictionaryToQuckfixjConverter().convertToQuickFixJ(stream, outputDirectory.absolutePathString())
        val dictionaries = outputDirectory.listDirectoryEntries()

        return when (dictionaries.size) {
            0 -> error("No dictionaries were loaded")
            1 -> Dictionaries(dictionaries[0], dictionaries[0])
            2 -> Dictionaries(dictionaries.first { it.name == "FIXT11.xml" }, dictionaries.first { it.name != "FIXT11.xml" })
            else -> error("More than 2 dictionaries were loaded")
        }
    }

    data class Dictionaries(val transport: Path, val app: Path)
}