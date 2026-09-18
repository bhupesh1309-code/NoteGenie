package com.example.notegenie.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding

private val NoteDetailHeaderGradient = Brush.linearGradient(
    colors = listOf(HeaderLightStart, HeaderLightMid, HeaderLightEnd)
)
private val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(HeaderLightStart, HeaderLightMid)
)

/**
 * A single parsed line of Markdown-ish content, so the raw AI text
 * (headings, bullets, plain paragraphs) can be styled distinctly
 * without pulling in a full Markdown rendering library.
 */
private sealed class NoteLine {
    data class Heading(val text: String, val level: Int) : NoteLine()
    data class Bullet(val text: String) : NoteLine()
    data class Paragraph(val text: String) : NoteLine()
    object Blank : NoteLine()
}

private fun parseMarkdownLite(raw: String): List<NoteLine> {
    return raw.lines().map { line ->
        val trimmed = line.trim()
        when {
            trimmed.isEmpty() -> NoteLine.Blank
            trimmed.startsWith("### ") -> NoteLine.Heading(trimmed.removePrefix("### "), level = 3)
            trimmed.startsWith("## ") -> NoteLine.Heading(trimmed.removePrefix("## "), level = 2)
            trimmed.startsWith("# ") -> NoteLine.Heading(trimmed.removePrefix("# "), level = 1)
            trimmed.startsWith("- ") || trimmed.startsWith("* ") -> NoteLine.Bullet(trimmed.drop(2))
            else -> NoteLine.Paragraph(trimmed)
        }
    }
}

@Composable
fun NoteDetailScreen(
    topic: String,
    dateGenerated: String,
    content: String,
    isSaved: Boolean = false,
    onSaveClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onCopyClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onExportPdfClick: () -> Unit = {},
    onRegenerateClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val parsedLines = remember(content) { parseMarkdownLite(content) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8FC))
    ) {
        // Gradient header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NoteDetailHeaderGradient, RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .padding(top = 48.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topic,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = "Generated $dateGenerated",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Save button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .height(52.dp)
                .shadow(
                    elevation = if (isSaved) 0.dp else 10.dp,
                    shape = RoundedCornerShape(50),
                    ambientColor = PurpleDark.copy(alpha = 0.4f),
                    spotColor = PurpleDark.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(50))
                .then(
                    if (isSaved) Modifier.background(PurpleFieldBg)
                    else Modifier.background(ButtonGradient)
                )
                .then(
                    if (isSaved) Modifier else Modifier.clickable { onSaveClick() }
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Check else Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = if (isSaved) PurplePrimary else Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSaved) "Saved to My Notes" else "Save Note",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isSaved) PurplePrimary else Color.White
                )
            }
        }

        // Action row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionChip(icon = Icons.Default.ContentCopy, label = "Copy", onClick = onCopyClick, modifier = Modifier.weight(1f))
            ActionChip(icon = Icons.Default.Share, label = "Share", onClick = onShareClick, modifier = Modifier.weight(1f))
            ActionChip(icon = Icons.Default.PictureAsPdf, label = "PDF", onClick = onExportPdfClick, modifier = Modifier.weight(1f))
            ActionChip(icon = Icons.Default.Refresh, label = "Redo", onClick = onRegenerateClick, modifier = Modifier.weight(1f))
        }

        // Note content card
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = PurplePrimary.copy(alpha = 0.15f),
                        spotColor = PurplePrimary.copy(alpha = 0.18f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                parsedLines.forEach { line ->
                    when (line) {
                        is NoteLine.Heading -> {
                            Spacer(modifier = Modifier.height(if (line.level == 1) 12.dp else 8.dp))
                            Text(
                                text = line.text,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = when (line.level) {
                                    1 -> 22.sp
                                    2 -> 19.sp
                                    else -> 17.sp
                                },
                                color = PurpleDeep
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        is NoteLine.Bullet -> {
                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                Text(text = "•  ", color = PurpleAccent, fontWeight = FontWeight.Bold)
                                Text(text = line.text, fontSize = 15.sp, color = Color.Black, lineHeight = 21.sp)
                            }
                        }
                        is NoteLine.Paragraph -> {
                            Text(
                                text = line.text,
                                fontSize = 15.sp,
                                color = Color.Black,
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                        is NoteLine.Blank -> {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PurpleFieldBg)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = PurpleAccent, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PurpleDeep)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NoteDetailScreenPreview() {
    MaterialTheme {
        NoteDetailScreen(
            topic = "Newton's Laws of Motion",
            dateGenerated = "Sep 5, 2026",
            content = """
                # Newton's Laws of Motion

                ## First Law: Inertia
                An object at rest stays at rest, and an object in motion stays in motion, unless acted on by an external force.

                - Also called the Law of Inertia
                - Explains why passengers lurch forward when a car stops suddenly

                ## Second Law: F = ma
                Force equals mass times acceleration.

                - The greater the mass, the more force needed for the same acceleration
                - This is the most commonly used of the three laws in physics problems

                ## Third Law: Action-Reaction
                For every action, there is an equal and opposite reaction.

                - Explains recoil in guns, rocket propulsion, and walking
            """.trimIndent()
        )
    }
}