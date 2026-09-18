import os
import google.generativeai as genai
from app.models.schemas import NotePreferences

genai.configure(api_key=os.getenv("GEMINI_API_KEY"))
model = genai.GenerativeModel("gemini-3.6-flash")


_FORMATTING_RULES = """
Formatting rules — follow these exactly:
- Use only these formatting elements: headings with #, ##, or ###, and bullet points with -.
- Do NOT use bold text (no ** around words).
- Do NOT use LaTeX or math notation with $ signs. Write math plainly, e.g. "O(n log n)" not "$O(n \\log n)$".
- Do NOT use markdown tables (no | pipe characters). If comparing multiple items, use a bulleted list instead, e.g.:
  - Access: O(1) — direct index lookup
  - Search: O(n) — linear scan required
- Do NOT use horizontal rules (no --- lines).
- Do NOT use inline code formatting (no backticks).
- Plain, clean text only — this will be rendered in a simple text viewer, not a full markdown renderer.
"""


def _preferences_to_instructions(prefs: NotePreferences) -> str:
    """
    Mirrors AiPreferences.toPromptInstructions() on the Android side,
    so preferences saved in the app actually shape what Gemini generates.
    """
    length_guidance = {
        "SHORT": "concise, key points only",
        "MEDIUM": "balanced detail with brief explanations",
        "DETAILED": "thorough, with full explanations and examples",
    }.get(prefs.note_length, "balanced detail with brief explanations")

    style_guidance = {
        "EXAM_FOCUSED": "prioritize likely exam questions, definitions, and key facts to memorize",
        "CONCEPT_FOCUSED": "prioritize deep conceptual understanding and how ideas connect",
    }.get(prefs.note_style, "prioritize likely exam questions, definitions, and key facts to memorize")

    return (
        "Generate the notes following these preferences:\n"
        f"- Length: {prefs.note_length} ({length_guidance})\n"
        f"- Difficulty level: {prefs.difficulty}\n"
        f"- Language: {prefs.language}\n"
        f"- Style: {style_guidance}\n"
    )


def _build_prompt(topic: str, raw_text: str | None, prefs: NotePreferences) -> str:
    instructions = _preferences_to_instructions(prefs)

    if raw_text:
        return f"""{instructions}
{_FORMATTING_RULES}
You are a study notes assistant. The student has messy or incomplete notes on '{topic}'.
Clean them up, organize them, fill any gaps with accurate information, and format
the result as structured study notes following the formatting rules above.
Do not include a generic introduction — start directly with the notes.

Student's raw notes:
{raw_text}"""
    else:
        return f"""{instructions}
{_FORMATTING_RULES}
Generate clear, well-structured study notes on the topic: '{topic}'.
Follow the formatting rules above. Include a short summary section at the end.
Do not include a generic introduction — start directly with the notes."""


def generate_notes(topic: str, raw_text: str | None, preferences: NotePreferences) -> str:
    """Text-only generation: used by /summarize and for .txt/.docx uploads
    where the file's text was already extracted before calling this."""
    prompt = _build_prompt(topic, raw_text, preferences)
    response = model.generate_content(prompt)
    return response.text


def generate_notes_from_image(topic: str, image_bytes: bytes, mime_type: str, preferences: NotePreferences) -> str:
    """
    Multimodal generation: sends the image directly to Gemini instead of
    running OCR first. Gemini's vision capability reads handwritten or
    photographed notes directly, which is more accurate than a separate
    OCR step and avoids needing a Tesseract dependency.
    """
    instructions = _preferences_to_instructions(preferences)
    prompt = f"""{instructions}
{_FORMATTING_RULES}
You are a study notes assistant. The attached image contains a student's
handwritten or photographed notes on '{topic}'. Read the content in the
image, clean it up, organize it, fill any gaps with accurate information,
and format the result as structured study notes following the formatting
rules above. Do not include a generic introduction — start directly with the notes."""

    response = model.generate_content([
        prompt,
        {"mime_type": mime_type, "data": image_bytes}
    ])
    return response.text