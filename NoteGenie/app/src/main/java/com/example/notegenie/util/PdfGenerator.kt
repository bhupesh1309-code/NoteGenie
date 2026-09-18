package com.example.notegenie.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    // Standard US Letter size in points (72 points = 1 inch)
    private const val PAGE_WIDTH = 612
    private const val PAGE_HEIGHT = 792
    private const val MARGIN = 48f

    private val titlePaint = Paint().apply {
        color = Color.rgb(0x2E, 0x08, 0x54)
        textSize = 20f
        isFakeBoldText = true
    }
    private val headingPaint = Paint().apply {
        color = Color.rgb(0x2E, 0x08, 0x54)
        textSize = 15f
        isFakeBoldText = true
    }
    private val bodyPaint = Paint().apply {
        color = Color.BLACK
        textSize = 12f
    }
    private val tablePaint = Paint().apply {
        color = Color.rgb(0x33, 0x33, 0x33)
        textSize = 11f
    }
    private val mutedPaint = Paint().apply {
        color = Color.GRAY
        textSize = 10f
    }
    private val rulePaint = Paint().apply {
        color = Color.rgb(0xDD, 0xDD, 0xDD)
        strokeWidth = 1f
    }

    private val TABLE_SEPARATOR_REGEX = Regex("^\\|?[\\s:|-]+\\|?$")

    private val HORIZONTAL_RULE_REGEX = Regex("^([-_*])\\1{2,}$")

    fun generateNotePdf(context: Context, topic: String, dateGenerated: String, content: String): File {
        val document = PdfDocument()
        val contentWidth = PAGE_WIDTH - (MARGIN * 2)

        var page = newPage(document, pageNumber = 1)
        var canvas = page.canvas
        var y = MARGIN
        var pageNumber = 1

        fun drawTextBlock(text: String, paint: Paint, indent: Float = 0f) {
            val wrapped = wrapText(text, paint, contentWidth - indent)
            wrapped.forEach { line ->
                if (y + 20f > PAGE_HEIGHT - MARGIN) {
                    document.finishPage(page)
                    pageNumber++
                    page = newPage(document, pageNumber)
                    canvas = page.canvas
                    y = MARGIN
                }
                canvas.drawText(line, MARGIN + indent, y, paint)
                y += paint.textSize + 6f
            }
        }

        // Title
        canvas.drawText(cleanInlineMarkdown(topic), MARGIN, y, titlePaint)
        y += 22f
        canvas.drawText("Generated $dateGenerated", MARGIN, y, mutedPaint)
        y += 28f

        // Body content, line by line
        content.lines().forEach { rawLine ->
            val line = rawLine.trim()

            when {
                line.isEmpty() -> {
                    y += 10f
                }

                line.contains("|") && TABLE_SEPARATOR_REGEX.matches(line) -> {}

                HORIZONTAL_RULE_REGEX.matches(line) -> {
                    if (y + 14f > PAGE_HEIGHT - MARGIN) {
                        document.finishPage(page)
                        pageNumber++
                        page = newPage(document, pageNumber)
                        canvas = page.canvas
                        y = MARGIN
                    }
                    canvas.drawLine(MARGIN, y, (PAGE_WIDTH - MARGIN), y, rulePaint)
                    y += 14f
                }

                line.startsWith("|") && line.endsWith("|") -> {
                    val cells = line.trim('|').split("|").map { cleanInlineMarkdown(it.trim()) }
                    drawTextBlock(cells.joinToString("    •    "), tablePaint)
                    y += 2f
                }

                line.startsWith("### ") -> {
                    drawTextBlock(cleanInlineMarkdown(line.removePrefix("### ")), headingPaint)
                    y += 4f
                }
                line.startsWith("## ") -> {
                    drawTextBlock(cleanInlineMarkdown(line.removePrefix("## ")), headingPaint)
                    y += 4f
                }
                line.startsWith("# ") -> {
                    drawTextBlock(cleanInlineMarkdown(line.removePrefix("# ")), headingPaint)
                    y += 4f
                }

                line.startsWith("- ") || line.startsWith("* ") -> {
                    drawTextBlock("•  " + cleanInlineMarkdown(line.drop(2)), bodyPaint, indent = 16f)
                }

                else -> {
                    drawTextBlock(cleanInlineMarkdown(line), bodyPaint)
                }
            }
        }

        document.finishPage(page)

        val fileName = "NoteGenie_${topic.replace(Regex("[^A-Za-z0-9]"), "_")}.pdf"
        val outputDir = File(context.filesDir, "notes_pdf").apply { mkdirs() }
        val outputFile = File(outputDir, fileName)

        FileOutputStream(outputFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        return outputFile
    }

    private fun newPage(document: PdfDocument, pageNumber: Int): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        return document.startPage(pageInfo)
    }

    private fun cleanInlineMarkdown(text: String): String {
        return text
            .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1") // **bold** -> bold
            .replace(Regex("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)"), "$1") // *italic* -> italic
            .replace("$", "")
            .replace("`", "")
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in words) {
            val candidate = if (currentLine.isEmpty()) word else "${currentLine} $word"
            if (paint.measureText(candidate) <= maxWidth) {
                currentLine = StringBuilder(candidate)
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
        return lines
    }
}