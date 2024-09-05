package com.example.soundmixer.features.playback.usecase.usecase


import com.arthenica.mobileffmpeg.FFmpeg

class AudioMerger {

    /**
     * Merges two audio files into one.
     *
     * @param file1 Path to the first audio file.
     * @param file2 Path to the second audio file.
     * @param outputPath Path where the merged audio file will be saved.
     * @param callback Callback to be invoked with the result of the merge operation.
     */
    fun mergeAudioFiles(file1: String, file2: String, outputPath: String, callback: (Boolean) -> Unit) {
        // Command to merge audio files using FFmpeg
        val command = arrayOf(
            "-y", // Overwrite output files
            "-i", file1, // Input file 1
            "-i", file2, // Input file 2
            "-filter_complex", "[0:a][1:a]amix=inputs=2:duration=longest[a]", // Mix both audio streams
            "-map", "[a]", // Map the merged audio stream to output
            "-ac", "2", // Set output to stereo
            outputPath // Output file path
        )

        // Execute FFmpeg command
        val result = FFmpeg.execute(command)

        // Notify callback about success or failure
        callback(result == 0)
    }
}

//val command = arrayOf(
//    "-y", // Overwrite output files
//    "-i", file1, // Input file 1
//    "-i", file2, // Input file 2
//    "-filter_complex", "[0:a][1:a]concat=n=2:v=0:a=1[outa]", // Concatenate audio streams
//    "-map", "[outa]", // Map the concatenated audio stream to output
//    "-ac", "2", // Set output to stereo
//    outputPath // Output file path



