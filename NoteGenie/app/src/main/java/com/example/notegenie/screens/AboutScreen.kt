package com.example.notegenie.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notegenie.R

private val AboutHeaderGradient = Brush.linearGradient(
    colors = listOf(HeaderLightStart, HeaderLightMid, HeaderLightEnd)
)

private val techStack = listOf(
    "Kotlin", "Jetpack Compose", "Firebase Auth", "Room DB",
    "Retrofit", "FastAPI", "Google Gemini"
)

@Composable
fun AboutScreen(
    appVersion: String = "1.0.0",
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8FC))
            .verticalScroll(scrollState)
    ) {
        //  Gradient header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AboutHeaderGradient, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .padding(top = 48.dp, bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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
                Text(
                    text = "About",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App icon + name + version
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .shadow(elevation = 14.dp, shape = CircleShape, ambientColor = GoldAccent, spotColor = GoldAccent)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bhupesh_1),
                        contentDescription = "NoteGenie icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "NoteGenie",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = Color.White
                )
                Text(
                    text = "Version $appVersion",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(24.dp))

            // Description card
            InfoCard {
                Text(
                    text = "About NoteGenie",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PurpleDeep
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "NoteGenie turns your topics, uploaded files, or messy handwritten " +
                            "notes into clean, structured study notes using Google's Gemini AI. " +
                            "Customize note length, difficulty, language, and focus style to match " +
                            "exactly how you like to study.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tech stack card
            InfoCard {
                Text(
                    text = "Built With",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PurpleDeep
                )
                Spacer(modifier = Modifier.height(12.dp))
                FlowRowChips(items = techStack)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Developer / links card
            InfoCard {
                Text(
                    text = "Developer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PurpleDeep
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Built by Bhupesh as a portfolio project showcasing Android " +
                            "development with Kotlin, Jetpack Compose, and AI integration.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "© 2026 NoteGenie",
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = PurplePrimary.copy(alpha = 0.1f),
                spotColor = PurplePrimary.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(18.dp),
        content = content
    )
}

@Composable
private fun FlowRowChips(items: List<String>) {
    var currentRow = mutableListOf<String>()
    val rows = mutableListOf<List<String>>()
    var currentWidth = 0

    items.forEach { item ->
        val estimatedWidth = item.length * 9 + 32
        if (currentWidth + estimatedWidth > 300 && currentRow.isNotEmpty()) {
            rows.add(currentRow.toList())
            currentRow = mutableListOf()
            currentWidth = 0
        }
        currentRow.add(item)
        currentWidth += estimatedWidth
    }
    if (currentRow.isNotEmpty()) rows.add(currentRow.toList())

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { label ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(PurpleFieldBg)
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PurpleAccent)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AboutScreenPreview() {
    MaterialTheme {
        AboutScreen()
    }
}