package com.example.notegenie.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notegenie.R
import com.example.notegenie.data.AiPreferences
import com.example.notegenie.data.Difficulty
import com.example.notegenie.data.NoteLanguage
import com.example.notegenie.data.NoteLength
import com.example.notegenie.data.NoteStyle
import androidx.compose.ui.graphics.Brush

private val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(HeaderLightStart, HeaderLightMid)
)

@Composable
fun AiPreferencesScreen(
    noteLength: NoteLength,
    onNoteLengthChange: (NoteLength) -> Unit,
    difficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    language: NoteLanguage,
    onLanguageChange: (NoteLanguage) -> Unit,
    noteStyle: NoteStyle,
    onNoteStyleChange: (NoteStyle) -> Unit,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8FC))
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(44.dp)
                .border(1.5.dp, PurplePrimary, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PurplePrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title + genie
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Preferences",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    color = PurpleDeep
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Customize how NoteGenie creates your notes.",
                    fontSize = 14.sp,
                    color = TextMuted,
                    lineHeight = 19.sp
                )
            }
            Image(
                painter = painterResource(id = R.drawable.preferences_genie),
                contentDescription = "NoteGenie mascot",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(width = 140.dp, height = 150.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        //  Note Length
        PreferenceCard(
            icon = Icons.Default.Description,
            title = "Note Length",
            subtitle = "Choose how detailed you want your notes."
        ) {
            PreferenceOptionsRow(
                options = NoteLength.entries,
                selected = noteLength,
                labelFor = { it.label },
                onSelect = onNoteLengthChange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Difficulty
        PreferenceCard(
            icon = Icons.Default.SignalCellularAlt,
            title = "Difficulty",
            subtitle = "Set the complexity level of the notes."
        ) {
            PreferenceOptionsRow(
                options = Difficulty.entries,
                selected = difficulty,
                labelFor = { it.label },
                onSelect = onDifficultyChange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language
        PreferenceCard(
            icon = Icons.Default.Language,
            title = "Language",
            subtitle = "Choose the language for your notes."
        ) {
            PreferenceOptionsRow(
                options = NoteLanguage.entries,
                selected = language,
                labelFor = { it.label },
                onSelect = onLanguageChange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Note Style
        PreferenceCard(
            icon = Icons.Default.GpsFixed,
            title = "Note Style",
            subtitle = "Select the focus style for the notes."
        ) {
            PreferenceOptionsRow(
                options = NoteStyle.entries,
                selected = noteStyle,
                labelFor = { it.label },
                onSelect = onNoteStyleChange
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Save button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(50),
                    ambientColor = PurpleDark.copy(alpha = 0.4f),
                    spotColor = PurpleDark.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(50))
                .background(ButtonGradient)
                .clickable { onSaveClick() },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Save Preferences", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PreferenceCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
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
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PurpleFieldBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.Black)
                Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        content()
    }
}

@Composable
private fun <T> PreferenceOptionsRow(
    options: List<T>,
    selected: T,
    labelFor: (T) -> String,
    onSelect: (T) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(50))
                    .then(
                        if (isSelected) Modifier.background(ButtonGradient)
                        else Modifier.background(PurpleFieldBg)
                    )
                    .clickable { onSelect(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labelFor(option),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = if (isSelected) Color.White else PurpleDeep,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AiPreferencesScreenPreview() {
    MaterialTheme {
        val prefs = AiPreferences()
        AiPreferencesScreen(
            noteLength = prefs.noteLength,
            onNoteLengthChange = {},
            difficulty = prefs.difficulty,
            onDifficultyChange = {},
            language = prefs.language,
            onLanguageChange = {},
            noteStyle = prefs.noteStyle,
            onNoteStyleChange = {},
            onSaveClick = {}
        )
    }
}

