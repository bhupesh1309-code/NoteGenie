package com.example.notegenie.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notegenie.R

private val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(HeaderLightStart, HeaderLightMid)
)
@Composable
fun ForgotPasswordScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendResetLinkClick: () -> Unit,
    onBackClick: () -> Unit = {}
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

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = PurplePrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )
                    ) {
                        append("NoteGenie")
                    }
                    append("\n")
                    withStyle(SpanStyle(
                        color = TextMuted,
                        fontSize = 13.sp
                    )) {
                        append("Your Study Buddy")
                    }
                },
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Genie mascot
        Image(
            painter = painterResource(id = R.drawable.idea_genie),
            contentDescription = "NoteGenie mascot",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Title + subtitle
        Text(
            text = "Forgot Password?",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            color = PurpleDeep,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No worries! Enter your email address and we'll send you a link to reset your password.",
            fontSize = 14.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Email field
        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Email Address",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = PurpleAccent)
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Send Reset Link button
        Box(
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
                .clickable { onSendResetLinkClick() },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Send Reset Link",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // OR divider
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFDCD0EE), thickness = 1.dp)
            Text(
                text = "OR",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFDCD0EE), thickness = 1.dp)
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Back to Log in card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFECE4F7), RoundedCornerShape(18.dp))
                .clickable { onBackClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = PurplePrimary)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Remember your password?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Back",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PurplePrimary
                )
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = PurplePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    MaterialTheme {
        ForgotPasswordScreen(
            email = "",
            onEmailChange = {},
            onSendResetLinkClick = {}
        )
    }
}

