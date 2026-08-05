# PokeLingo

[日本語のドキュメントはこちら](README_ja.md)

A Japanese language learning game inspired by Pokemon. Explore a gym, challenge trainers and the gym leader, and defeat them by answering Japanese language questions correctly.

## Features

- **RPG-style exploration**: Walk around maps, interact with NPCs, and progress through rooms
- **Battle system**: Defeat opponents by answering Japanese language questions
- **Multiple question types**: Hiragana, Katakana, vocabulary (weather, greetings, actions), sentence forming, and particle usage
- **Combo system**: Answer correctly in a row to increase your damage multiplier
- **Bilingual support**: Switch between English and Japanese (kanji/kana) in settings
- **Save & Load**: Pick up where you left off (auto-saves after battles)
- **Customizable settings**: Adjust volume, resolution, and rebind keys

## Tech Stack

- **Language:** Java
- **Framework:** LibGDX 1.11.0
- **Build tool:** Gradle
- **Serialization:** Kryo (save data)
- **YAML parsing:** SnakeYAML (settings)
- **Text rendering:** LibGDX FreeType (dynamic fonts for Japanese characters)

## Voice Generation

All voice lines and question audio in this game are generated using [VOICEVOX](https://voicevox.hiroshiba.jp/), a Japanese open-source text-to-speech engine.

## How to Run (Windows)

The easiest way to play is through a pre-built release package.

### Option 1: Download a Release (Recommended)

1. Go to the **Releases** page and download the latest `pokelingo.zip`
2. Extract the zip file to any folder
3. Double-click `run.cmd` to start the game

### Option 2: Build from Source

**Prerequisites:**
- JDK 17 or later
- `JAVA_HOME` environment variable set to your JDK installation path
- Git

**Steps:**

1. Clone the repository and navigate into the project folder
2. Run the build script:
   ```
   python tools/build.py
   ```
3. This will create a `dist/` folder containing `run.cmd`
4. Double-click `run.cmd` to start the game

## Controls

| Action | Key |
|---|---|
| Move | W / A / S / D |
| Interact | Enter |
| Pause / Menu | Escape |
| Save Game | F12 |
| Repeat Voice | V |

Controls can be changed in the Settings menu.

## Game Overview

1. From the main menu, select **Play Game** to start
2. Walk your character around the map using **WASD**
3. Approach an NPC and press **Enter** to talk
4. (In the Gym) After dialogue, a battle will begin. Answer the Japanese question correctly to deal damage
5. Get consecutive correct answers to build a **combo** and deal extra damage
6. Answer incorrectly and the opponent gains a combo against you
7. Defeat all trainers and challenge the **Gym Leader** to complete the game

## Question Types

- **Hiragana**: Identify or match hiragana characters
- **Katakana**: Identify or match katakana characters
- **Vocabulary (Weather)**: Japanese words related to weather
- **Vocabulary (Greetings)**: Common Japanese greetings and expressions
- **Vocabulary (Actions)**: Japanese action verbs and phrases
- **Sentence Forming**: Arrange words to form correct Japanese sentences
- **Particles**: Choose the correct Japanese particle (wa, ga, ni, wo, etc.)

## Language Support

The game supports two language modes, selectable in Settings:

- **English (EN)**
- **Japanese (JP)**: Uses only hiragana and katakana notation

In every language mode, interactive objects and dialogue are shown together with romanization.

## Project Structure

```
core/           : Core game logic, screens, actors, and question system
desktop/        : Desktop launcher (LWJGL3)
assets/         : Game assets (maps, audio, fonts, strings, dialogs, questions)
tools/          : Build script to package the game for distribution
docs/           : Developer documentation
```

## Development

### Building

```bash
./gradlew desktop:dist
```

This produces `desktop/build/libs/desktop-1.0.jar`.

### Running from Source (Developers)

```bash
./gradlew desktop:run
```

Requires `JAVA_HOME` to be set.

## License

Private / Personal project
