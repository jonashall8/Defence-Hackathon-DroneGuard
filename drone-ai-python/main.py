from fastapi import FastAPI, UploadFile, File, HTTPException
from inference_engine import DroneDetector
import uvicorn

app = FastAPI(title="DroneGuard AI Service")

# Global variable to hold the model
detector = None

@app.on_event("startup")
def load_model():
    """Load the heavy AI model once when the server starts."""
    global detector
    try:
        detector = DroneDetector()
        print("✅ Model loaded and ready for inference.")
    except Exception as e:
        print(f"❌ Failed to load model: {e}")
        raise e

@app.get("/")
def health_check():
    return {"status": "running", "service": "DroneGuard AI"}

@app.post("/analyze")
async def analyze_audio(file: UploadFile = File(...)):
    """
    Receives an audio file (WAV), runs YAMNet, and returns the detection.
    """
    if not detector:
        raise HTTPException(status_code=503, detail="Model not loaded yet")

    try:
        # Read the file into memory
        file_bytes = await file.read()

        # Get prediction from our inference engine
        result = detector.predict(file_bytes)

        return result

    except Exception as e:
        print(f"Error processing file: {e}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    # This allows you to run the file directly for debugging
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)