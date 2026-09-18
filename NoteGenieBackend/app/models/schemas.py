from pydantic import BaseModel
from typing import Optional


class NotePreferences(BaseModel):
    """
    Mirrors com.example.notegenie.data.AiPreferences on the Android side.
    Kept as plain strings (not a Python enum) so any value the app sends
    is accepted as-is and mapped to prompt text in gemini_service.
    """
    note_length: str = "MEDIUM"      
    difficulty: str = "BEGINNER"   
    language: str = "ENGLISH"         
    note_style: str = "EXAM_FOCUSED"  


class SummarizeRequest(BaseModel):
    """Used for topic-only generation (no uploaded file)."""
    topic: str
    raw_text: Optional[str] = None  
    preferences: NotePreferences = NotePreferences()


class NoteResponse(BaseModel):
    """
    Stateless response — no id/uid/timestamps here, since the server
    doesn't persist anything. The Android app owns storage via Room.
    """
    topic: str
    content: str