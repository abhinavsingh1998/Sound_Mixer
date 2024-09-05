package com.example.soundmixer.features.recordings.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soundmixer.R
import com.example.soundmixer.features.recordings.adapter.SoundListAdapter
import com.example.soundmixer.data_base.Recording
import com.example.soundmixer.databinding.FragmentAudioListBinding
import com.example.soundmixer.features.merge.data_models.SelectedSoundDetails
import com.example.soundmixer.features.recordings.data_models.SoundDetails
import com.example.soundmixer.utils.OnItemClickListener
import com.example.soundmixer.MainActivity
import com.example.soundmixer.features.merge.view.MergeFragment
import com.example.soundmixer.features.playback.view.PlaybackFragment

import com.example.soundmixer.viewmodels.SoundViewModel

class AudioListFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentAudioListBinding
    private lateinit var adapter: SoundListAdapter
    private lateinit var viewModel: SoundViewModel
    private var showCheckBox = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAudioListBinding.inflate(inflater, container, false)

        (requireActivity() as MainActivity).showBottomNavigation()

        // Obtain the ViewModel instance from the activity
        viewModel = (requireActivity() as MainActivity).obtainViewModel()

        // Observe changes in the list of recorded files
        observeData()

        return binding.root
    }

    /**
     * Sets up an observer for the list of recorded files.
     * This method will be called when the data in the ViewModel changes.
     */
    private fun observeData() {
        viewModel.getCheckBoxFlag.observe(viewLifecycleOwner, Observer {
            showCheckBox = it
        })
        viewModel.allRecordedFiles.observe(viewLifecycleOwner) { data ->
            // Update the RecyclerView with the new data
            setupRecyclerView(data)
        }
    }

    /**
     * Sets up the RecyclerView with the provided list of recordings.
     *
     * @param list The list of recordings to display.
     */
    private fun setupRecyclerView(list: List<Recording>) {
        adapter = SoundListAdapter(list, this, showCheckBox) { showDoneButton ->
            // Update the visibility of the Done button
            binding.btnDone.visibility = if (showDoneButton) View.VISIBLE else View.GONE
        }
        binding.rvSoundList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSoundList.adapter = adapter
    }

    override fun onDownloadClick(soundId: Int, soundname: String, url: String) {
        TODO("Not yet implemented")
    }

    /**
     * Handles the click event for an item in the list.
     *
     * @param soundId The ID of the clicked sound.
     * @param soundName The name of the clicked sound.
     * @param filePath The file path of the clicked sound.
     */
    override fun onPlayClick(soundId: Int, soundName: String, filePath: String) {
        // Set the current file details in the ViewModel
        viewModel.setCurrentFile(SoundDetails(soundId, soundName, filePath))

        // Create an instance of PlaybackFragment
        val playbackFragment = PlaybackFragment()

        // Replace the current fragment with PlaybackFragment
        val fragmentTransaction = (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, playbackFragment)
        fragmentTransaction.addToBackStack(null) // Optional: add the transaction to the back stack
        fragmentTransaction.commit()
    }

    override fun onItemsSelected(selectedDetails: SelectedSoundDetails) {
        viewModel.setSelectedSoundDetails(selectedDetails)
        onDoneButtonClicked()
    }

    private fun onDoneButtonClicked() {
// Observe changes to selected sound details
        viewModel.selectedSoundDetails.observe(viewLifecycleOwner) { selectedDetails ->
            // Handle UI or navigation based on selectedDetails
            binding.btnDone.setOnClickListener {
                if (selectedDetails!!.ids.size == 2) {
                    // Navigate to the MergeFragment
                    (requireActivity() as MainActivity).replaceFragment(MergeFragment(), true)
                    viewModel.setCheckBoxFlag(false)
                } else {
                    Toast.makeText(requireContext(), "Please select exactly two items.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.setCheckBoxFlag(false)
    }
}

