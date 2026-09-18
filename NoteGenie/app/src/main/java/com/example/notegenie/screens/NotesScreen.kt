package com.example.notegenie.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NoteItem(
    val id: String,
    val topic: String,
    val preview: String,
    val date: String
)

private val NotesHeaderGradient = Brush.linearGradient(
    colors = listOf(HeaderLightStart, HeaderLightMid, HeaderLightEnd)
)

@Composable
fun NotesScreen(
    notes: List<NoteItem> = sampleNotes(),
    onProfileClick: () -> Unit = {},
    onNoteClick: (NoteItem) -> Unit = {},
    onHomeTabClick: () -> Unit = {},
    onNotesTabClick: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            NoteGenieBottomBar(
                selectedTab = BottomTab.NOTES,
                onHomeClick = onHomeTabClick,
                onNotesClick = onNotesTabClick
            )
        },
        containerColor = Color(0xFFFAF8FC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            //  Gradient Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NotesHeaderGradient, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                    Text(
                        text = "My Notes",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                    ProfileAvatar(onClick = onProfileClick)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${notes.size} saved ${if (notes.size == 1) "note" else "notes"}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            if (notes.isEmpty()) {
                EmptyNotesState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(notes) { note ->
                        NoteCard(note = note, onClick = { onNoteClick(note) })
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCard(note: NoteItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = PurplePrimary.copy(alpha = 0.15f),
                spotColor = PurplePrimary.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon badge
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(PurpleFieldBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Description, contentDescription = null, tint = PurpleAccent)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = note.topic,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = PurpleDeep,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.preview,
                fontSize = 13.sp,
                color = TextMuted,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = note.date,
                fontSize = 11.sp,
                color = TextMuted.copy(alpha = 0.7f)
            )
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun EmptyNotesState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(PurpleFieldBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = PurpleAccent,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No notes yet",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = PurpleDeep,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Generate your first note from the Home tab",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun sampleNotes(): List<NoteItem> = listOf(
    NoteItem("1", "Newton's Laws of Motion", "Three fundamental laws describing the relationship between a body and the forces acting on it.", "Sep 2, 2026"),
    NoteItem("2", "Photosynthesis", "Process by which green plants convert light energy into chemical energy stored in glucose.", "Aug 30, 2026"),
    NoteItem("3", "Data Structures - Trees", "Hierarchical data structure with nodes connected by edges, including binary trees and BSTs.", "Aug 28, 2026")
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotesScreenPreview() {
    MaterialTheme {
        NotesScreen()
    }
}

