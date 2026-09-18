# NoteGenie

> Turn your notes into knowledge.

NoteGenie is an AI-powered study notes application that transforms topics, handwritten notes, images, PDFs, DOCX files, and text files into structured study notes.

The project consists of an Android application built with Kotlin and Jetpack Compose and a FastAPI backend that handles file processing, authentication, and AI-powered note generation using Google Gemini.

---

## ✨ Features

- 📝 Generate study notes from any topic
- 📷 Process handwritten notes and images using Gemini's multimodal capabilities
- 📄 Upload PDF, DOCX, and TXT files
- 🤖 AI-powered note generation using Google Gemini
- 🎯 Customize generated notes:
  - Note length
  - Difficulty level
  - Language
  - Note style
- 🔐 Firebase Authentication
- 💾 Local note storage using Room Database
- 📄 Generate and export notes as PDF
- 🎨 Modern UI built with Jetpack Compose
- 🔄 REST API communication using Retrofit
- 🔑 Firebase ID token authentication between Android and backend

---

## 🏗️ Architecture

```text
┌───────────────────────────────┐
│         Android App           │
│     Kotlin + Jetpack Compose  │
└───────────────┬───────────────┘
                │
             Retrofit
                │
                ▼
┌───────────────────────────────┐
│        FastAPI Backend         │
│                               │
│  Authentication               │
│  File Processing              │
│  API Routes                   │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────────────┐
│         Gemini API             │
│      AI Note Generation        │
└───────────────┬───────────────┘
                │
                ▼
          Generated Notes
                │
                ▼
┌───────────────────────────────┐
│         Android App            │
│                               │
│  Room Database    PDF Export  │
└───────────────────────────────┘




👨‍💻 Author

Bhupesh

B.Tech Computer Science and Engineering
IIIT Agartala

GitHub: bhupesh1309-code