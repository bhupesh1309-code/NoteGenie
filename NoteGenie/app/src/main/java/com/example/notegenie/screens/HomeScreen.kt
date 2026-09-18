package com.example.notegenie.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notegenie.R

val PurplePrimary = Color(0xFF7B1FA2)
val PurpleDark = Color(0xFF4A0072)
val PurpleDeep = Color(0xFF2E0854)
val PurpleCardBg = Color(0xFFF4ECFB)
val PurpleUploadBg = Color(0xFFF9F5FE)
val PurpleFieldBg = Color(0xFFF1E6FB)
val PurpleAccent = Color(0xFF9C27B0)
val GoldAccent = Color(0xFFFFC94A)
val BottomBarBg = Color(0xFFFFFFFF)
val TextMuted = Color(0xFF6B6B7A)

val HeaderLightStart = Color(0xFF9C4DCC)
val HeaderLightMid = Color(0xFFAB6FD8)
val HeaderLightEnd = Color(0xFFC896E8)

private val HeaderGradient = Brush.linearGradient(
    colors = listOf(HeaderLightStart, HeaderLightMid, HeaderLightEnd)
)
private val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(PurpleDark, PurplePrimary)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String = "Bhupesh",
    selectedFileName: String? = null,
    onProfileClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onGenerateClick: (topic: String) -> Unit = {},
    onHomeTabClick: () -> Unit = {},
    onNotesTabClick: () -> Unit = {},
    selectedTab: BottomTab = BottomTab.HOME
) {
    var topicText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            NoteGenieBottomBar(
                selectedTab = selectedTab,
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
                .verticalScroll(scrollState)
        ) {
            // Gradient Hero Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderGradient, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .padding(top = 48.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.size(48.dp))
                    Text(
                        text = "NoteGenie",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                    ProfileAvatar(onClick = onProfileClick)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hi, $userName \uD83D\uDC4B",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ready to turn your notes into knowledge?",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )
                    }
                    GenieBadge()
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Generate Note Card
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                GenerateNoteCard(
                    topicText = topicText,
                    onTopicChange = { topicText = it },
                    selectedFileName = selectedFileName,
                    onUploadClick = onUploadClick,
                    onGenerateClick = { onGenerateClick(topicText) }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Profile Avatar with ring
@Composable
fun ProfileAvatar(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

// Genie badge with glowing gradient ring
@Composable
fun GenieBadge() {
    Image(
        painter = painterResource(id = R.drawable.hello_genie),
        contentDescription = "NoteGenie mascot",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(width = 100.dp, height = 110.dp)
    )
}

// Generate Note Card (elevated, premium)
@Composable
fun GenerateNoteCard(
    topicText: String,
    onTopicChange: (String) -> Unit,
    selectedFileName: String? = null,
    onUploadClick: () -> Unit,
    onGenerateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = PurplePrimary.copy(alpha = 0.25f),
                spotColor = PurplePrimary.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Generate Note",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = PurpleDeep
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ENTER TOPIC",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = topicText,
            onValueChange = onTopicChange,
            placeholder = { Text("e.g. Newton's Laws of Motion", color = TextMuted.copy(alpha = 0.7f)) },
            leadingIcon = {
                Icon(Icons.Default.Edit, contentDescription = null, tint = PurpleAccent)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = PurpleFieldBg,
                focusedContainerColor = PurpleFieldBg,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PurplePrimary,
                unfocusedTextColor = PurpleDeep,
                focusedTextColor = PurpleDeep
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(22.dp))

        // OR divider with badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFE0D6EE), thickness = 1.dp)
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clip(CircleShape)
                    .background(PurpleFieldBg)
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(text = "OR", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PurpleAccent)
            }
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFE0D6EE), thickness = 1.dp)
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "UPLOAD MEDIA",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        DashedUploadBox(selectedFileName = selectedFileName, onClick = onUploadClick)

        Spacer(modifier = Modifier.height(24.dp))

        // Gradient Generate Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(50),
                    ambientColor = PurpleDark.copy(alpha = 0.4f),
                    spotColor = PurpleDark.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(50))
                .background(ButtonGradient)
                .clickable { onGenerateClick() },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Generate", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

// Dashed upload box (premium touch)
@Composable
fun DashedUploadBox(selectedFileName: String? = null, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PurpleUploadBg)
            .clickable { onClick() }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = PurpleAccent.copy(alpha = 0.5f),
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
                style = Stroke(
                    width = 1.6.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 26.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(PurpleDark, PurplePrimary))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (selectedFileName != null) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (selectedFileName != null) "File selected" else "Upload",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (selectedFileName != null) {
                Text(
                    text = selectedFileName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = PurpleAccent,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tap to change file",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            } else {
                Text(
                    text = "PDF • Image • DOCX • TXT",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = PurpleAccent
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tap to browse files",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }
    }
}

// Floating Bottom Navigation
enum class BottomTab { HOME, NOTES }

@Composable
fun NoteGenieBottomBar(
    selectedTab: BottomTab,
    onHomeClick: () -> Unit,
    onNotesClick: () -> Unit
) {
    Surface(
        color = BottomBarBg,
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.height(56.dp)
        ) {
            NavigationBarItem(
                selected = selectedTab == BottomTab.HOME,
                onClick = onHomeClick,
                icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(20.dp)) },
                label = { Text("Home", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PurplePrimary,
                    selectedTextColor = PurplePrimary,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = PurpleFieldBg
                )
            )
            NavigationBarItem(
                selected = selectedTab == BottomTab.NOTES,
                onClick = onNotesClick,
                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Notes", modifier = Modifier.size(20.dp)) },
                label = { Text("Notes", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PurplePrimary,
                    selectedTextColor = PurplePrimary,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = PurpleFieldBg
                )
            )
        }
    }
}


// Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}