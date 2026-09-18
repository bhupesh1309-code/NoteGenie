package com.example.notegenie.navigation

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.core.content.FileProvider
import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.notegenie.data.AiPreferencesRepository
import com.example.notegenie.data.AuthRepository
import com.example.notegenie.data.AuthResult
import com.example.notegenie.data.local.NoteDatabase
import com.example.notegenie.screens.AboutScreen
import com.example.notegenie.screens.AiPreferencesScreen
import com.example.notegenie.screens.ForgotPasswordScreen
import com.example.notegenie.screens.HomeScreen
import com.example.notegenie.screens.LoginScreen
import com.example.notegenie.screens.NoteDetailScreen
import com.example.notegenie.screens.NoteItem
import com.example.notegenie.screens.NotesScreen
import com.example.notegenie.screens.ProfileScreen
import com.example.notegenie.screens.SignUpScreen
import com.example.notegenie.util.PdfGenerator
import com.example.notegenie.viewmodel.AiPreferencesViewModel
import com.example.notegenie.viewmodel.AuthUiState
import com.example.notegenie.viewmodel.AuthViewModel
import com.example.notegenie.viewmodel.GenerationUiState
import com.example.notegenie.viewmodel.NotesViewModel
import java.util.UUID
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val authRepository = remember { AuthRepository() }
    val startDestination = remember {
        if (authRepository.isLoggedIn()) Home else Login
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login
        composable<Login> {
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()
            val email by authViewModel.email.collectAsState()
            val password by authViewModel.password.collectAsState()
            val passwordVisible by authViewModel.passwordVisible.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(uiState) {
                when (val state = uiState) {
                    is AuthUiState.Success -> {
                        authViewModel.clearForm()
                        navController.navigate(Home) {
                            popUpTo(0) { inclusive = true }
                        }
                        authViewModel.resetState()
                    }
                    is AuthUiState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        authViewModel.resetState()
                    }
                    else -> Unit
                }
            }

            Box {
                LoginScreen(
                    email = email,
                    onEmailChange = authViewModel::onEmailChange,
                    password = password,
                    onPasswordChange = authViewModel::onPasswordChange,
                    passwordVisible = passwordVisible,
                    onTogglePasswordVisibility = authViewModel::togglePasswordVisibility,
                    onLoginClick = authViewModel::login,
                    onForgotPasswordClick = {
                        navController.navigate(ForgotPassword)
                    },
                    onSignUpClick = {
                        navController.navigate(SignUp)
                    }
                )
                if (uiState is AuthUiState.Loading) {
                    LoadingOverlay()
                }
            }
        }

        // Forgot Password
        composable<ForgotPassword> {
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()
            val email by authViewModel.email.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(uiState) {
                when (val state = uiState) {
                    is AuthUiState.Success -> {
                        Toast.makeText(
                            context,
                            "Reset link sent! Check your email.",
                            Toast.LENGTH_LONG
                        ).show()
                        authViewModel.resetState()
                        navController.navigateUp()
                    }
                    is AuthUiState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        authViewModel.resetState()
                    }
                    else -> Unit
                }
            }

            Box {
                ForgotPasswordScreen(
                    email = email,
                    onEmailChange = authViewModel::onEmailChange,
                    onSendResetLinkClick = authViewModel::sendResetEmail,
                    onBackClick = { navController.navigateUp() },
                )
                if (uiState is AuthUiState.Loading) {
                    LoadingOverlay()
                }
            }
        }

        // Sign Up
        composable<SignUp> {
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()
            val name by authViewModel.name.collectAsState()
            val email by authViewModel.email.collectAsState()
            val password by authViewModel.password.collectAsState()
            val passwordVisible by authViewModel.passwordVisible.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(uiState) {
                when (val state = uiState) {
                    is AuthUiState.Success -> {
                        authViewModel.clearForm()
                        navController.navigate(Home) {
                            popUpTo(0) { inclusive = true }
                        }
                        authViewModel.resetState()
                    }
                    is AuthUiState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        authViewModel.resetState()
                    }
                    else -> Unit
                }
            }

            Box {
                SignUpScreen(
                    name = name,
                    onNameChange = authViewModel::onNameChange,
                    email = email,
                    onEmailChange = authViewModel::onEmailChange,
                    password = password,
                    onPasswordChange = authViewModel::onPasswordChange,
                    passwordVisible = passwordVisible,
                    onTogglePasswordVisibility = authViewModel::togglePasswordVisibility,
                    onCreateAccount = authViewModel::signUp,
                    onLoginClick = {
                        navController.navigateUp()
                    }
                )
                if (uiState is AuthUiState.Loading) {
                    LoadingOverlay()
                }
            }
        }

        // Home
        composable<Home> {
            val userName = remember { authRepository.currentUserName()?.takeIf { it.isNotBlank() } ?: "there" }
            val context = LocalContext.current
            var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
            var selectedFileName by remember { mutableStateOf<String?>(null) }
            var currentTopic by remember { mutableStateOf("") }

            val notesViewModel: NotesViewModel = viewModel()
            val generationState by notesViewModel.uiState.collectAsState()
            val filePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                if (uri != null) {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    selectedFileUri = uri
                    selectedFileName = queryFileName(context, uri)
                }
            }

            LaunchedEffect(generationState) {
                when (val state = generationState) {
                    is GenerationUiState.Success -> {
                        navController.navigate(
                            NoteDetail(
                                noteId = UUID.randomUUID().toString(), // not saved yet — this id is only used if/when the user taps Save
                                topic = state.topic,
                                content = state.content,
                                dateGenerated = state.dateGenerated,
                                isSaved = false
                            )
                        )
                        notesViewModel.resetState()
                        selectedFileUri = null
                        selectedFileName = null
                    }
                    is GenerationUiState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        notesViewModel.resetState()
                    }
                    else -> Unit
                }
            }

            Box {
                HomeScreen(
                    userName = userName,
                    selectedFileName = selectedFileName,
                    onProfileClick = {
                        navController.navigate(Profile)
                    },
                    onUploadClick = {
                        filePickerLauncher.launch(
                            arrayOf(
                                "application/pdf",
                                "image/*",
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "text/plain"
                            )
                        )
                    },
                    onGenerateClick = { topic ->
                        currentTopic = topic
                        val fileUri = selectedFileUri
                        if (fileUri != null) {
                            notesViewModel.generateFromFile(fileUri, topic)
                        } else {
                            notesViewModel.generateFromTopic(topic)
                        }
                    },
                    onHomeTabClick = {},
                    onNotesTabClick = {
                        navController.navigate(Notes) {
                            popUpTo(Home) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
                if (generationState is GenerationUiState.Loading) {
                    LoadingOverlay()
                }
            }
        }

        // Profile
        composable<Profile> {
            val userName = remember { authRepository.currentUserName()?.takeIf { it.isNotBlank() } ?: "there" }
            val userEmail = remember { authRepository.currentUserEmail().orEmpty() }
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            var isDeletingAccount by remember { mutableStateOf(false) }

            Box {
                ProfileScreen(
                    userName = userName,
                    userEmail = userEmail,
                    onBackClick = { navController.navigateUp() },
                    onMyNotesClick = {
                        navController.navigate(Notes) {
                            launchSingleTop = true
                        }
                    },
                    onChangePasswordClick = {
                        navController.navigate(ForgotPassword)
                    },
                    onAiPreferencesClick = {
                        navController.navigate(AiPreferencesRoute)
                    },
                    onAboutClick = {
                        navController.navigate(About)
                    },
                    onLogoutClick = {
                        authRepository.logout()
                        navController.navigate(Login) {
                            popUpTo(0) { inclusive = true } // clear entire back stack
                        }
                    },
                    onDeleteAccountClick = {
                        coroutineScope.launch {
                            isDeletingAccount = true
                            val uidToClean = authRepository.currentUserId()

                            when (val result = authRepository.deleteAccount()) {
                                is AuthResult.Success -> {
                                    if (uidToClean != null) {
                                        NoteDatabase.getInstance(context).noteDao().deleteNotesForUser(uidToClean)
                                    }
                                    AiPreferencesRepository(context).clearAll()

                                    Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Login) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                is AuthResult.Error -> {
                                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                }
                            }
                            isDeletingAccount = false
                        }
                    }
                )
                if (isDeletingAccount) {
                    LoadingOverlay()
                }
            }
        }

        // AI Preferences
        composable<AiPreferencesRoute> {
            val aiPreferencesViewModel: AiPreferencesViewModel = viewModel()
            val preferences by aiPreferencesViewModel.preferences.collectAsState()
            val saveConfirmed by aiPreferencesViewModel.saveConfirmed.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(saveConfirmed) {
                if (saveConfirmed) {
                    Toast.makeText(context, "Preferences saved!", Toast.LENGTH_SHORT).show()
                    aiPreferencesViewModel.resetSaveConfirmation()
                    navController.navigateUp()
                }
            }

            AiPreferencesScreen(
                noteLength = preferences.noteLength,
                onNoteLengthChange = aiPreferencesViewModel::onNoteLengthChange,
                difficulty = preferences.difficulty,
                onDifficultyChange = aiPreferencesViewModel::onDifficultyChange,
                language = preferences.language,
                onLanguageChange = aiPreferencesViewModel::onLanguageChange,
                noteStyle = preferences.noteStyle,
                onNoteStyleChange = aiPreferencesViewModel::onNoteStyleChange,
                onBackClick = { navController.navigateUp() },
                onSaveClick = aiPreferencesViewModel::savePreferences
            )
        }

        // About
        composable<About> {
            AboutScreen(
                onBackClick = { navController.navigateUp() }
            )
        }

        // Notes list
        composable<Notes> {
            val notesViewModel: NotesViewModel = viewModel()
            val savedNotes by notesViewModel.allNotes.collectAsState()

            val noteItems = savedNotes.map { entity ->
                NoteItem(
                    id = entity.id,
                    topic = entity.topic,
                    preview = entity.content.take(120).replace("\n", " ").trim(),
                    date = entity.dateGenerated
                )
            }

            NotesScreen(
                notes = noteItems,
                onProfileClick = {
                    navController.navigate(Profile)
                },
                onNoteClick = { note: NoteItem ->
                    val fullNote = savedNotes.firstOrNull { it.id == note.id }
                    navController.navigate(
                        NoteDetail(
                            noteId = note.id,
                            topic = note.topic,
                            content = fullNote?.content ?: note.preview,
                            dateGenerated = fullNote?.dateGenerated ?: note.date,
                            isSaved = true
                        )
                    )
                },
                onHomeTabClick = {
                    navController.navigate(Home) {
                        popUpTo(Home) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNotesTabClick = {}
            )
        }

        // Note detail
        composable<NoteDetail> { backStackEntry ->
            val args: NoteDetail = backStackEntry.toRoute()
            val context = LocalContext.current
            val clipboardManager = LocalClipboardManager.current
            val notesViewModel: NotesViewModel = viewModel()

            var isSaved by remember(args.noteId) { mutableStateOf(args.isSaved) }

            NoteDetailScreen(
                topic = args.topic,
                dateGenerated = args.dateGenerated,
                content = args.content,
                isSaved = isSaved,
                onSaveClick = {
                    notesViewModel.saveNote(
                        noteId = args.noteId,
                        topic = args.topic,
                        content = args.content,
                        dateGenerated = args.dateGenerated
                    )
                    isSaved = true
                    Toast.makeText(context, "Note saved!", Toast.LENGTH_SHORT).show()
                },
                onBackClick = { navController.navigateUp() },
                onCopyClick = {
                    clipboardManager.setText(AnnotatedString(args.content))
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                onShareClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, args.topic)
                        putExtra(Intent.EXTRA_TEXT, args.content)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share note"))
                },
                onExportPdfClick = {
                    try {
                        val pdfFile = PdfGenerator.generateNotePdf(
                            context = context,
                            topic = args.topic,
                            dateGenerated = args.dateGenerated,
                            content = args.content
                        )
                        val pdfUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            pdfFile
                        )
                        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(pdfUri, "application/pdf")
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        try {
                            context.startActivity(viewIntent)
                        } catch (e: android.content.ActivityNotFoundException) {
                            Toast.makeText(context, "No PDF viewer app installed", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to create PDF: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                },
                onRegenerateClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.25f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

private fun queryFileName(context: android.content.Context, uri: Uri): String? {
    var name: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}