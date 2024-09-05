package com.example.soundmixer.features.recordings.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.soundmixer.R
import com.example.soundmixer.databinding.FragmentRecordingBinding
import com.example.soundmixer.services.AudioService
import com.example.soundmixer.utils.GenerateUniqueFileName
import com.example.soundmixer.MainActivity
import com.example.soundmixer.viewmodels.SoundViewModel
import java.io.File


class RecordingFragment : Fragment() {

    private lateinit var binding: FragmentRecordingBinding
    private var file: File? = null
    private lateinit var viewModel: SoundViewModel
    private var isRecording = false // Flag to track the recording state

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecordingBinding.inflate(inflater, container, false)

        // Show the bottom navigation bar
        (requireActivity() as MainActivity).showBottomNavigation()

        // Obtain the ViewModel instance
        viewModel = (requireActivity() as MainActivity).obtainViewModel()

        // Request necessary permissions for recording and storage
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            0
        )

        // Initialize and observe the list of recorded files
        getSoundList()

        // Observe the elapsed time from ViewModel
        viewModel.elapsedTime.observe(viewLifecycleOwner) { elapsedTime ->
            binding.timerText.text = viewModel.getFormattedTime()
        }

        // Set up the button click listener for starting and stopping recording
        binding.startStopBtn.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        // Set up the button click listener for saving the recorded file
        binding.saveBtn.setOnClickListener {
            file?.let {
                val fileName = it.name
                val filePath = it.absolutePath
                // Insert the file details into the ViewModel
                viewModel.insert(fileName, filePath)
                viewModel.resetTimer()
                viewModel.stopTimer()
                Toast.makeText(requireContext(), "File saved successfully", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(requireContext(), "No file to save", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    /**
     * Starts the recording process, initializes the file, and starts the timer.
     */
    private fun startRecording() {
        // Generate a unique file name and initialize the file
        file = File(requireContext().cacheDir, GenerateUniqueFileName(requireContext()).generateUniqueAudioFileName())

        // Start the recording service
        startService(AudioService.ACTION_START_RECORDING)

        // Update the recording state flag and button text
        isRecording = true
        viewModel.startTimer() // Start the timer
        updateStartStopButtonText()
    }

    /**
     * Stops the recording process and stops the timer.
     */
    private fun stopRecording() {
        // Stop the recording service
        startService(AudioService.ACTION_STOP_RECORDING)

        // Stop the timer
        viewModel.stopTimer()

        // Update the recording state flag and button text
        isRecording = false
        updateStartStopButtonText()
    }

    /**
     * Starts the AudioService with the specified action.
     *
     * @param action The action to perform (e.g., start or stop recording)
     */
    private fun startService(action: String) {
        val intent = Intent(requireContext(), AudioService::class.java).apply {
            this.action = action
        }
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    /**
     * Observes the list of recorded files from the ViewModel and displays their count.
     */
    private fun getSoundList() {
        viewModel.allRecordedFiles.observe(viewLifecycleOwner) { recordedFiles ->
            if (recordedFiles != null) {
                Log.d("RecordingFragment", "Number of recorded files: ${recordedFiles.size}")
            } else {
                Log.d("RecordingFragment", "No recorded files found")
                Toast.makeText(requireContext(), "No recorded files found", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    /**
     * Updates the text of the start/stop button based on the recording state.
     */
    private fun updateStartStopButtonText() {
        // Set button text based on whether recording is in progress
        if (isRecording) {
            binding.startStopBtn.setImageResource(R.drawable.play_arrow)
            Toast.makeText(requireContext(), "Recording has started", Toast.LENGTH_SHORT).show()
        } else {
            binding.startStopBtn.setImageResource(R.drawable.circle)
            Toast.makeText(requireContext(), "Recording has stopped", Toast.LENGTH_SHORT).show()
        }
    }
}




