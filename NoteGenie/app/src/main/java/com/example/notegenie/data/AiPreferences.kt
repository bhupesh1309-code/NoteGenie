package com.example.notegenie.data

enum class NoteLength(val label: String) {
    SHORT("Short"),
    MEDIUM("Medium"),
    DETAILED("Detailed")
}

enum class Difficulty(val label: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}

enum class NoteLanguage(val label: String) {
    ENGLISH("English"),
    HINDI("Hindi"),
    HINGLISH("Hinglish")
}

enum class NoteStyle(val label: String) {
    EXAM_FOCUSED("Exam-focused"),
    CONCEPT_FOCUSED("Concept-focused")
}

data class AiPreferences(
    val noteLength: NoteLength = NoteLength.MEDIUM,
    val difficulty: Difficulty = Difficulty.BEGINNER,
    val language: NoteLanguage = NoteLanguage.ENGLISH,
    val noteStyle: NoteStyle = NoteStyle.EXAM_FOCUSED
) {
    fun toPromptInstructions(): String = buildString {
        appendLine("Generate the notes following these preferences:")
        appendLine("- Length: ${noteLength.label} (${lengthGuidance()})")
        appendLine("- Difficulty level: ${difficulty.label}")
        appendLine("- Language: ${language.label}")
        appendLine("- Style: ${noteStyleGuidance()}")
    }

    private fun lengthGuidance(): String = when (noteLength) {
        NoteLength.SHORT -> "concise, key points only"
        NoteLength.MEDIUM -> "balanced detail with brief explanations"
        NoteLength.DETAILED -> "thorough, with full explanations and examples"
    }

    private fun noteStyleGuidance(): String = when (noteStyle) {
        NoteStyle.EXAM_FOCUSED -> "prioritize likely exam questions, definitions, and key facts to memorize"
        NoteStyle.CONCEPT_FOCUSED -> "prioritize deep conceptual understanding and how ideas connect"
    }
}