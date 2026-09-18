from fastapi import APIRouter, UploadFile, Form, Depends, HTTPException
from app.auth import verify_token
from app.services.gemini_service import generate_notes, generate_notes_from_image
from app.models.schemas import NoteResponse, NotePreferences
import fitz  # PyMuPDF — PDF text extraction
import docx  # python-docx — DOCX text extraction
import json

router = APIRouter()

IMAGE_TYPES = {"image/jpeg", "image/png", "image/webp", "image/heic", "image/heif"}


@router.post("/upload", response_model=NoteResponse)
async def upload_note(
    file: UploadFile,
    topic: str = Form(...),
    preferences: str = Form("{}"),  
    uid: str = Depends(verify_token),
):
    prefs = NotePreferences(**json.loads(preferences)) if preferences and preferences != "{}" else NotePreferences()
    content_bytes = await file.read()
    content_type = file.content_type or ""

    if content_type in IMAGE_TYPES:
        generated_text = generate_notes_from_image(topic, content_bytes, content_type, prefs)

    elif content_type == "application/pdf":
        raw_text = _extract_pdf_text(content_bytes)
        generated_text = generate_notes(topic, raw_text, prefs)

    elif content_type in (
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  # .docx
        "application/msword",  
    ):
        raw_text = _extract_docx_text(content_bytes)
        generated_text = generate_notes(topic, raw_text, prefs)

    elif content_type == "text/plain":
        raw_text = content_bytes.decode("utf-8", errors="ignore")
        generated_text = generate_notes(topic, raw_text, prefs)

    else:
        raise HTTPException(status_code=400, detail=f"Unsupported file type: {content_type}")

    return NoteResponse(topic=topic, content=generated_text)


def _extract_pdf_text(content_bytes: bytes) -> str:
    doc = fitz.open(stream=content_bytes, filetype="pdf")
    text = "\n".join(page.get_text() for page in doc)
    doc.close()
    return text


def _extract_docx_text(content_bytes: bytes) -> str:
    import io
    doc = docx.Document(io.BytesIO(content_bytes))
    return "\n".join(paragraph.text for paragraph in doc.paragraphs)