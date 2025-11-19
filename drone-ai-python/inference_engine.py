import tensorflow as tf
import tensorflow_hub as hub
import numpy as np
import librosa
import csv
import os
import tempfile

class DroneDetector:
    def __init__(self):
        print("Loading YAMNet model from TensorFlow Hub...")
        self.model = hub.load('https://tfhub.dev/google/yamnet/1')

        class_map_path = self.model.class_map_path().numpy()
        self.class_names = self._load_class_names(class_map_path)
        print("Model loaded successfully.")

    def _load_class_names(self, csv_path):
        class_names = []
        with tf.io.gfile.GFile(csv_path) as csvfile:
            reader = csv.DictReader(csvfile)
            for row in reader:
                class_names.append(row['display_name'])
        return class_names

    def preprocess_audio(self, file_bytes):
        """
        Save bytes to a temp file so librosa can auto-detect the format (WebM/WAV),
        then load it as 16kHz mono.
        """
        # 1. Create a temporary file
        # We use suffix='.wav' but librosa will detect if it's actually WebM
        with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as temp_file:
            temp_file.write(file_bytes)
            temp_path = temp_file.name

        try:
            # 2. Load from disk (More robust than loading from memory)
            audio, sample_rate = librosa.load(temp_path, sr=16000, mono=True)
        except Exception as e:
            print(f"Error loading audio file: {e}")
            raise e
        finally:
            # 3. Clean up the temp file
            if os.path.exists(temp_path):
                os.remove(temp_path)

        # Ensure float32
        return audio.astype(np.float32)

    def predict(self, file_bytes):
        # Preprocess using the new temp-file method
        waveform = self.preprocess_audio(file_bytes)

        scores, embeddings, spectrogram = self.model(waveform)
        mean_scores = np.mean(scores, axis=0)
        top_class_index = np.argmax(mean_scores)
        top_class_name = self.class_names[top_class_index]
        confidence = float(np.max(mean_scores))

        is_drone_threat = top_class_name in ['Aircraft', 'Helicopter', 'Drone', 'Propeller']

        return {
            "class_name": top_class_name,
            "confidence": confidence,
            "is_drone": is_drone_threat
        }