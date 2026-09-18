from dotenv import load_dotenv
load_dotenv()  

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routes import summarize, upload

app = FastAPI(title="NoteGenie API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(summarize.router)
app.include_router(upload.router)


@app.get("/")
async def root():
    return {"status": "NoteGenie API is running"}


@app.get("/health")
async def health():
    return {"status": "ok"}