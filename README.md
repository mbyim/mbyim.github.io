# Vocab Reading to Anki

An Android app that turns circled words in book photos into Anki flashcards.

## How it works

1. **Take a photo** of a book page where you've circled unknown words with a pen
2. **Circle detection** (OpenCV) identifies the hand-drawn circles
3. **OCR** (Google ML Kit) reads the text on the page
4. **Matching** determines which words fall inside circled regions and merges multi-word phrases
5. **Translation** (LibreTranslate) translates the words to English
6. **Review** the detected words — select/deselect before creating cards
7. **Export to AnkiDroid** creates flashcards (front: original word, back: English translation)

## Tech stack

| Component | Library |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Camera | CameraX |
| OCR | Google ML Kit Text Recognition |
| Circle detection | OpenCV Android SDK |
| Translation | LibreTranslate (free, self-hostable) |
| Flashcards | AnkiDroid Content Provider API |

## Setup

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 34
- An Android device or emulator running API 26+
- [AnkiDroid](https://play.google.com/store/apps/details?id=com.ichi2.anki) installed on the device

### Build & run

```bash
git clone <this-repo>
cd vocab_reading_to_anki
```

Open the project in Android Studio, sync Gradle, and run on your device.

### Configuration

In the app's **Settings** screen you can configure:

- **Source Language** — the language you're reading (default: Spanish)
- **LibreTranslate Server** — URL of the translation server (default: `https://libretranslate.com`)
- **Deck Name** — the AnkiDroid deck to add cards to (default: `Vocab Reading`)
- **Note Type** — the AnkiDroid note type/model (default: `Basic`)

> Make sure the deck and note type already exist in AnkiDroid before creating flashcards.

## Project structure

```
app/src/main/java/com/vocabreading/app/
├── MainActivity.kt          # Entry point, permission handling, OpenCV init
├── MainViewModel.kt         # App state management and processing pipeline
├── camera/
│   └── CameraManager.kt     # CameraX setup and photo capture
├── ocr/
│   └── TextRecognitionProcessor.kt  # ML Kit text recognition
├── detection/
│   └── CircleDetector.kt    # OpenCV contour detection + text matching
├── translation/
│   └── LibreTranslateService.kt     # LibreTranslate API client
├── anki/
│   └── AnkiDroidHelper.kt   # AnkiDroid content provider integration
├── model/
│   ├── DetectedWord.kt      # Data classes
│   └── AppSettings.kt       # Settings model
└── ui/
    ├── theme/                # Material 3 theme
    └── screens/
        ├── CameraScreen.kt      # Camera preview + capture button
        ├── ProcessingScreen.kt   # Loading indicator during processing
        ├── ResultsScreen.kt      # Word list with select/deselect
        └── SettingsScreen.kt     # Language, server, Anki config
```

## Tips for best results

- Use a **dark pen or marker** to circle words — thin pencil circles are harder to detect
- Circle **individual words or short phrases** rather than entire sentences
- Keep the page **flat and well-lit** when taking the photo
- Make sure circles are **closed** (no large gaps)

## License

MIT
