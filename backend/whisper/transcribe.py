import sys
from pathlib import Path
from faster_whisper import WhisperModel
from itertools import chain
from  datetime import datetime, timedelta
from timeit import default_timer as timer

"""Simple batch transcriber

Usage inside container:
    python /app/transcribre.py /recordings
    (Path is passed by docker‑compose command)

Outputs a .txt next to each .opus/.wav once, using the same file name.
Already‑transcribed files are skipped (looks for .txt or *.done flag).
"""
MODEL_NAME="tiny"

if len(sys.argv) < 2:
    print("Usage: transcribre.py <recordings_dir>")
    sys.exit(1)

rec_dir = Path(sys.argv[1])
#model = WhisperModel("base", compute_type="int8")
model = WhisperModel(
    model_size_or_path=MODEL_NAME,
    device="cpu",                  # Usa GPU si está disponible
    compute_type="int8"         # float16: rápido y preciso en GPU
)


for audio_file in chain(rec_dir.rglob("*.opus"), rec_dir.rglob("*.wav")):
    txt_file = audio_file.with_suffix(".txt")
    done_flag = audio_file.with_suffix(".done")

    if txt_file.exists() or done_flag.exists():
        continue  # already processed

    start = timer()
    print(f"Inicia: {datetime.now()}")
    print(f"[Whisper] Transcribiendo {audio_file.name}…")

    try:
        segments, info = model.transcribe(
            str(audio_file),
            beam_size=5,               # beam search más preciso (más lento que greedy)
            language="es",              # forzar español mejora resultados
            vad_filter=True             # filtra silencios

        )
        #with txt_file.open("w", encoding="utf-8") as f:
        #    for s in segments:
        #        f.write(f"{s.start:06.2f}-{s.end:06.2f}: {s.text}\n")
        #done_flag.touch()
        with open(txt_file, "w", encoding="utf-8") as f:
            for segment in segments:
                f.write(f"[{segment.start:.2f}s - {segment.end:.2f}s] {segment.text}\n")
        print(f"[Whisper] OK → {txt_file.name}")
    except Exception as e:
        print(f"[Whisper] ERROR en {audio_file.name}: {e}")
    print(f"Termina: {datetime.now()}")
    end = timer()
    exec_time = timedelta(seconds=end-start)
    print(f"Tiempo de ejecución {exec_time}")
