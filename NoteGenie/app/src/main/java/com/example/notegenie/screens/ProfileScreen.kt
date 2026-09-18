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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notegenie.R

private val ProfileHeaderGradient = Brush.linearGradient(
    colors = listOf(HeaderLightStart, HeaderLightMid, HeaderLightEnd)
)
private val DangerGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFE0555F), Color(0xFFEB7A72))
)

@Composable
fun ProfileScreen(
    userName: String = "there",
    userEmail: String = "",
    onBackClick: () -> Unit = {},
    onMyNotesClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onAiPreferencesClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8FC))
            .verticalScroll(scrollState)
    ) {
        // Gradient hero header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ProfileHeaderGradient, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
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
                    text = "Profile",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Glowing avatar + name/email
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .shadow(
                            elevation = 18.dp,
                            shape = CircleShape,
                            ambientColor = GoldAccent,
                            spotColor = GoldAccent
                        )
                        .clip(CircleShape)
                        .background(Brush.sweepGradient(listOf(GoldAccent, PurpleAccent, GoldAccent)))
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dp_genie),
                        contentDescription = "Profile avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = userEmail,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(24.dp))

            // Menu items
            ProfileMenuItem(
                icon = Icons.Default.Description,
                title = "My Notes",
                subtitle = "View all your generated notes",
                onClick = onMyNotesClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            ProfileMenuItem(
                icon = Icons.Default.Lock,
                title = "Change Password",
                subtitle = "Update your account password",
                onClick = onChangePasswordClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            ProfileMenuItem(
                icon = Icons.Default.Tune,
                title = "AI Preferences",
                subtitle = "Customize how notes are generated",
                onClick = onAiPreferencesClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = "About",
                subtitle = "App version & information",
                onClick = onAboutClick
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Logout button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(50),
                        ambientColor = PurplePrimary.copy(alpha = 0.15f),
                        spotColor = PurplePrimary.copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .border(1.5.dp, PurplePrimary.copy(alpha = 0.4f), RoundedCornerShape(50))
                    .clickable { onLogoutClick() }
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Logout", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PurplePrimary)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Delete Account button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(50),
                        ambientColor = Color(0xFFE0555F).copy(alpha = 0.4f),
                        spotColor = Color(0xFFE0555F).copy(alpha = 0.5f)
                    )
                    .clip(RoundedCornerShape(50))
                    .background(DangerGradient)
                    .clickable { showDeleteDialog = true }
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Delete Account", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0555F).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFE0555F),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    "Delete your account?",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = PurpleDeep
                )
            },
            text = {
                Text(
                    "This permanently deletes your account and all your saved notes. " +
                            "This action cannot be undone.",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(DangerGradient)
                        .clickable {
                            showDeleteDialog = false
                            onDeleteAccountClick()
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .border(1.5.dp, PurplePrimary.copy(alpha = 0.4f), RoundedCornerShape(50))
                        .clickable { showDeleteDialog = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Cancel", color = PurplePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = PurplePrimary.copy(alpha = 0.1f),
                spotColor = PurplePrimary.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PurpleFieldBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = PurpleDeep
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextMuted.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(
            userName = "Bhupesh",
            userEmail = "bhupesh1309@gmail.com"
        )
    }
}