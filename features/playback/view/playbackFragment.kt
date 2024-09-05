package com.example.soundmixer.features.playback.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.soundmixer.R
import com.example.soundmixer.databinding.FragmentPlaybackBinding
import com.example.soundmixer.services.AudioService
import com.example.soundmixer.MainActivity
import com.example.soundmixer.viewmodels.SoundViewModel

class PlaybackFragment : Fragment() {

    private lateinit var binding: FragmentPlaybackBinding
    private lateinit var viewModel: SoundViewModel
    private var isPlaying = true
    private var currentFilePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaybackBinding.inflate(inflater, container, false)
        viewModel = (requireActivity() as MainActivity).obtainViewModel()
        setupObservers()
        setupPlayPauseButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Start playback automatically when the fragment is displayed
        viewModel._currentfile.observe(viewLifecycleOwner) { soundDetails ->
            currentFilePath = soundDetails?.filePath
            currentFilePath?.let { startPlayback(it) }
        }
    }

    private fun setupObservers() {
        viewModel._currentfile.observe(viewLifecycleOwner) { soundDetails ->
            currentFilePath = soundDetails?.filePath
        }

    }

    private fun setupPlayPauseButton() {
        binding.btnPlayPause.setOnClickListener {
            if (isPlaying) {
                stopPlayback()
            } else {
                startPlayback()
            }
        }

        // Update the button state based on playback status
        updatePlayPauseButton(isPlaying)
    }

    private fun startPlayback() {
        currentFilePath?.let { filePath ->
            Log.d("PlaybackFragment", "Starting playback for file: $filePath")
            val intent = Intent(requireContext(), AudioService::class.java).apply {
                action = AudioService.ACTION_START_PLAYBACK
                putExtra(AudioService.EXTRA_FILE_PATH, filePath)
            }
            requireContext().startService(intent)
            isPlaying = true
            updatePlayPauseButton(isPlaying)
        } ?: Log.e("PlaybackFragment", "Current file path is null")
    }

    private fun stopPlayback() {
        val intent = Intent(requireContext(), AudioService::class.java).apply {
            action = AudioService.ACTION_STOP_PLAYBACK
        }
        requireContext().startService(intent)
        isPlaying = false
        updatePlayPauseButton(isPlaying)
    }

    private fun updatePlayPauseButton(isPlaying: Boolean) {
        if (isPlaying) {
            binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
        } else {
            binding.btnPlayPause.setImageResource(R.drawable.play_arrow)
        }
    }

    private fun startPlayback(filePath: String) {
        val intent = Intent(requireContext(), AudioService::class.java).apply {
            action = AudioService.ACTION_START_PLAYBACK
            putExtra(AudioService.EXTRA_FILE_PATH, filePath)
        }
        requireContext().startService(intent)
    }

    private fun onSomeEvent(filePath: String) { // Replace with actual file path
        startPlayback(filePath)
    }
}

