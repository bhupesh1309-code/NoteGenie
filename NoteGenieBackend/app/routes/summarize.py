from fastapi import APIRouter, Depends
from app.auth import verify_token
from app.services.gemini_service import generate_notes
from app.models.schemas import SummarizeRequest, NoteResponse

router = APIRouter()


@router.post("/summarize", response_model=NoteResponse)
async def summarize(request: SummarizeRequest, uid: str = Depends(verify_token)):
    content = generate_notes(
        topic=request.topic,
        raw_text=request.raw_text,
        preferences=request.preferences,
    )
    return NoteResponse(topic=request.topic, content=content)