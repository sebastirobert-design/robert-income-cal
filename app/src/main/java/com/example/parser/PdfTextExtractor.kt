package com.example.parser

import android.content.Context
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.regex.Pattern
import java.util.zip.Inflater

object PdfTextExtractor {

    /**
     * Extracts plain text from an IFHRMS or standard PDF stream.
     * Uses Java Inflater to decompress FlateDecode streams and regex to extract text tokens.
     */
    fun extractTextFromUri(context: Context, uri: Uri): String {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                extractTextFromInputStream(stream)
            } ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun extractTextFromInputStream(inputStream: InputStream): String {
        val bytes = inputStream.readBytes()
        if (bytes.isEmpty()) return ""

        val textBuilder = StringBuilder()

        // Scan for "stream" and "endstream" markers
        val streamMarker = "stream".toByteArray(Charsets.ISO_8859_1)
        val endStreamMarker = "endstream".toByteArray(Charsets.ISO_8859_1)

        var idx = 0
        while (idx < bytes.size - streamMarker.size) {
            val startIdx = indexOf(bytes, streamMarker, idx)
            if (startIdx == -1) break

            var dataStart = startIdx + streamMarker.size
            // Skip newline after "stream"
            if (dataStart < bytes.size && bytes[dataStart] == '\r'.code.toByte()) dataStart++
            if (dataStart < bytes.size && bytes[dataStart] == '\n'.code.toByte()) dataStart++

            val endIdx = indexOf(bytes, endStreamMarker, dataStart)
            if (endIdx == -1) break

            val streamLength = endIdx - dataStart
            if (streamLength > 0) {
                val streamBytes = bytes.copyOfRange(dataStart, endIdx)
                val decompressed = tryDecompress(streamBytes) ?: streamBytes
                extractStringsFromPdfStream(decompressed, textBuilder)
            }

            idx = endIdx + endStreamMarker.size
        }

        return textBuilder.toString()
    }

    private fun tryDecompress(data: ByteArray): ByteArray? {
        return try {
            val inflater = Inflater()
            inflater.setInput(data)
            val outputStream = ByteArrayOutputStream(data.size * 3)
            val buffer = ByteArray(4096)
            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                if (count == 0 && inflater.needsInput()) break
                outputStream.write(buffer, 0, count)
            }
            inflater.end()
            outputStream.toByteArray()
        } catch (e: Exception) {
            null
        }
    }

    private fun extractStringsFromPdfStream(streamData: ByteArray, out: StringBuilder) {
        val streamText = String(streamData, Charsets.ISO_8859_1)

        // Matches text inside parentheses: (some text)
        val p = Pattern.compile("\\(((?:[^()\\\\]|\\\\.)*)\\)")
        val m = p.matcher(streamText)
        while (m.find()) {
            val raw = m.group(1) ?: continue
            val decoded = raw.replace("\\(", "(").replace("\\)", ")").replace("\\\\", "\\").trim()
            if (decoded.length >= 2 && !decoded.all { it.code < 32 }) {
                out.append(decoded).append("\n")
            }
        }
    }

    private fun indexOf(source: ByteArray, target: ByteArray, fromIndex: Int): Int {
        if (fromIndex >= source.size) return -1
        outer@ for (i in fromIndex..(source.size - target.size)) {
            for (j in target.indices) {
                if (source[i + j] != target[j]) continue@outer
            }
            return i
        }
        return -1
    }
}
