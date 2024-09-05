Sound Mixer 🎧
Sound Mixer is an Android application that allows users to search, play, record, download, and merge audio files. The app integrates with the Freesound API to provide rich sound file search functionality, along with local audio recording and merging capabilities. It uses Kotlin, MVVM architecture, and follows clean architecture principles to provide a robust and scalable solution.

Features
🔍 Sound Search: Search for sound files from the Freesound API and display results.
🎵 Audio Playback: Play audio files from both URL and local storage using ExoPlayer.
🎙️ Audio Recording: Record audio and display a real-time visualizer.
💾 Download & Save: Download sound files and store them locally using Room DB.
🔄 Merge Audio Files: Select and merge two audio files (downloaded or recorded).
🎚️ User-Friendly Interface: Clean and intuitive UI built with Jetpack Compose and XML.
🚀 Foreground Service: Handle audio playback and recording using a foreground service.
🗂️ Local Storage: Manage and list all downloaded and recorded audio files.

Technologies & Libraries Used
Kotlin: The primary programming language.
MVVM Architecture: For separation of concerns and testability.
Coroutines: For asynchronous operations.
Retrofit: For network calls to the Freesound API.
Room Database: To store and manage audio files locally.
ExoPlayer: For advanced media playback.
AudioRecorder: For recording audio.
Dagger/Hilt: For dependency injection.
Foreground Service: For seamless audio recording and playback in the background.

Usage
Search Sounds: Enter a keyword in the search bar to find sounds using the Freesound API.
Play Audio: Tap on any sound result or saved file to play it using ExoPlayer.
Record Audio: Press the record button to start recording audio.
Download Files: Download any sound file and store it locally.
Merge Files: Select two files (downloaded or recorded) to merge them into one audio file.

Contributing
Fork the repository.
Create a feature branch (git checkout -b feature/your-feature).
Commit your changes (git commit -m 'Add some feature').
Push to the branch (git push origin feature/your-feature).
Open a pull request.

License
This project is licensed under the MIT License - see the LICENSE file for details.
