package com.example.soundmixer.features.merge.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.soundmixer.databinding.FragmentMergeBinding
import com.example.soundmixer.features.recordings.view.AudioListFragment
import com.example.soundmixer.features.merge.data_models.SelectedSoundDetails
import com.example.soundmixer.features.playback.usecase.usecase.AudioMerger
import com.example.soundmixer.utils.GenerateUniqueFileName
import com.example.soundmixer.MainActivity
import com.example.soundmixer.viewmodels.SoundViewModel
import java.io.File

class MergeFragment : Fragment() {

    private lateinit var binding: FragmentMergeBinding
    private lateinit var viewModel: SoundViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMergeBinding.inflate(inflater, container, false)
        viewModel = (requireActivity() as MainActivity).obtainViewModel()

        // Show bottom navigation
        (requireActivity() as MainActivity).showBottomNavigation()

        // Observe selected sound details
        viewModel.selectedSoundDetails.observe(viewLifecycleOwner, Observer { selectedSoundDetails ->
            if (selectedSoundDetails != null) {
                handleSelectedSounds(selectedSoundDetails)
            } else {
                showToast("No sound files selected.")
            }
        })

        // Navigate to AudioListFragment when the select button is clicked
        binding.btnSelect.setOnClickListener {
            viewModel.setCheckBoxFlag(true)
            (requireActivity() as MainActivity).replaceFragment(AudioListFragment(), true)
        }

        return binding.root
    }

    /**
     * Handles merging of selected sound files.
     */
    @SuppressLint("SetTextI18n")
    private fun handleSelectedSounds(selectedSoundDetails: SelectedSoundDetails) {
        if (selectedSoundDetails.filePaths.size == 2) {
            binding.tvFirstSong.text = "Input file 1: " + selectedSoundDetails.names[0]
            binding.tvSecondSong.text = "Input file 2: " + selectedSoundDetails.names[1]
            binding.btnMerge.isEnabled = true // Enable the merge button
            binding.btnMerge.setOnClickListener {
                mergeFiles(selectedSoundDetails.filePaths)
            }
        } else {
            showToast("Please select exactly two files.")
        }
    }

    /**
     * Merges the selected sound files and provides feedback.
     *
     * @param filePaths List of file paths to be merged.
     */
    @SuppressLint("SetTextI18n")
    private fun mergeFiles(filePaths: List<String>) {
        val file1 = filePaths[0]
        val file2 = filePaths[1]

        // Generate a unique file name for the merged audio
        val outputFileName = GenerateUniqueFileName(requireContext()).generateUniqueMergedFileName()
        val outputFile = File(requireContext().filesDir, outputFileName)

        binding.tvMergedSound.text = "Merged file: " + outputFile.name
        // Insert file information into the database

        val audioMergeUtil = AudioMerger()
        audioMergeUtil.mergeAudioFiles(
            file1,
            file2,
            outputFile.absolutePath
        ) { success ->
            if (success) {
                viewModel.insert(outputFile.name, outputFile.path)
                showToast("Merged successfully")

                // Clear the selected sounds after a successful merge
                clearSelectedSounds()
            } else {
                showToast("Failed to merge files.")
            }
        }
    }

    /**
     * Clears the selected sound details and resets the UI after a successful merge.
     */
    @SuppressLint("SetTextI18n")
    private fun clearSelectedSounds() {
        // Clear the selected sound details in ViewModel
        viewModel.clearSelectedSounds()

        // Clear the UI elements showing the selected sound files
        binding.tvFirstSong.text = "Input file 1: "
        binding.tvSecondSong.text = "Input file 2: "
        binding.tvMergedSound.text = "Merged file: "
        // Disable the merge button until new files are selected
        binding.btnMerge.isClickable = false
    }

    /**
     * Displays a toast message to the user.
     *
     * @param message The message to display.
     */
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}



