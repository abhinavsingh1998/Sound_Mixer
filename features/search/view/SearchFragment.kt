package com.example.soundmixer.features.search.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soundmixer.R
import com.example.soundmixer.databinding.FragmentSearchBinding
import com.example.soundmixer.features.merge.data_models.SelectedSoundDetails
import com.example.soundmixer.features.recordings.data_models.SoundDetails
import com.example.soundmixer.viewmodels.SoundViewModel
import com.example.soundmixer.utils.OnItemClickListener
import com.example.soundmixer.utils.ApiResult
import com.example.soundmixer.utils.downloadFile
import com.example.soundmixer.MainActivity
import com.example.soundmixer.features.playback.view.PlaybackFragment
import com.example.soundmixer.features.search.adapter.SearchListAdapter

class SearchFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SoundViewModel
    private lateinit var adapter: SearchListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment and bind the layout to the binding object
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Set up layout insets to handle system bars, ensuring proper padding for views
        setupLayoutInsets()

        // Show the bottom navigation bar (assumes MainActivity manages this)
        (requireActivity() as MainActivity).showBottomNavigation()

        // Obtain the ViewModel instance from the activity
        viewModel = (requireActivity() as MainActivity).obtainViewModel()

        // Initialize UI components and observe data changes
        setupRecyclerView()
        setupSearchView()
        observeData()

        return binding.root
    }

    /**
     * Adjusts the padding of the RecyclerView to accommodate system bars.
     * This is crucial for proper alignment and spacing, especially with devices
     * that have gesture navigation or other system UI elements at the bottom.
     */
    private fun setupLayoutInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            // Get the bottom inset for system bars (e.g., navigation bar)
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            // Apply the bottom inset as padding to the RecyclerView
            binding.rvSounds.setPadding(0, 0, 0, bottomInset)
            insets
        }
    }

    /**
     * Sets up the RecyclerView with an adapter and layout manager.
     * The adapter is responsible for displaying search results in a list format.
     */
    private fun setupRecyclerView() {
        // Initialize the adapter with an empty list and set this fragment as the item click listener
        adapter = SearchListAdapter(emptyList(), this)
        // Use a LinearLayoutManager to arrange items in a vertical list
        binding.rvSounds.layoutManager = LinearLayoutManager(requireContext())
        // Attach the adapter to the RecyclerView
        binding.rvSounds.adapter = adapter
    }

    /**
     * Sets up the SearchView to listen for text changes and trigger searches.
     * The SearchView is designed to automatically search as the user types.
     */
    private fun setupSearchView() {
        binding.evSearchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed during the text change
            }

            override fun afterTextChanged(editable: Editable?) {
                editable?.let {
                    if (it.isNotEmpty()) {
                        // Trigger a search query if the input is not empty
                        viewModel.searchSounds(it.toString())
                    } else {
                        // Clear the adapter data if the search query is empty
                        adapter.updateData(emptyList())
                    }
                }
            }
        })
    }

    /**
     * Observes changes in search results and updates the UI accordingly.
     * The results are displayed in the RecyclerView, and a loading indicator is shown during data fetching.
     */
    private fun observeData() {
        viewModel.soundResults.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ApiResult.Loading -> {
                    // Show the loading indicator while the search is in progress
                    binding.loader.visibility = View.VISIBLE
                }
                is ApiResult.Success -> {
                    // Hide the loading indicator and update the RecyclerView with search results
                    binding.loader.visibility = View.GONE
                    adapter.updateData(state.data)
                }
                is ApiResult.Error -> {
                    // Hide the loading indicator and clear the RecyclerView on error
                    binding.loader.visibility = View.GONE
                    adapter.updateData(emptyList())
                }
            }
        })
    }

    /**
     * Handles the download button click event in the RecyclerView items.
     * Fetches file details from the API and triggers the file download.
     * @param soundId The ID of the sound to be downloaded.
     * @param soundName The name of the sound to be downloaded.
     * @param url The URL of the sound file.
     */
    override fun onDownloadClick(soundId: Int, soundName: String, url: String) {
        handleFileDetails(soundId, soundName, url) { fileTitle, fileUrl ->
            // Implement the download functionality using the obtained file details
            downloadFile(requireContext(), fileTitle, fileUrl)
        }
    }

    /**
     * Handles the play button click event in the RecyclerView items.
     * Fetches file details from the API and navigates to the PlaybackFragment to play the sound.
     * @param soundId The ID of the sound to be played.
     * @param soundName The name of the sound to be played.
     * @param file The file identifier (could be a URL or file path).
     */
    override fun onPlayClick(soundId: Int, soundName: String, file: String) {
        handleFileDetails(soundId, soundName, file) { _, fileUrl ->
            // Set the current file details in the ViewModel for playback
            viewModel.setCurrentFile(SoundDetails(soundId, soundName, fileUrl))

            // Create an instance of PlaybackFragment to handle the sound playback
            val playbackFragment = PlaybackFragment()

            // Replace the current fragment with PlaybackFragment and optionally add the transaction to the back stack
            val fragmentTransaction = (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, playbackFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    override fun onItemsSelected(selectedDetails: SelectedSoundDetails) {

    }


    /**
     * Handles the common logic for fetching file details and processing the result.
     * This method is used by both onDownloadClick and onPlayClick to avoid code duplication.
     * @param soundId The ID of the sound.
     * @param soundName The name of the sound.
     * @param identifier The file identifier (could be a URL or file path).
     * @param onSuccess A lambda function to handle the success result with file title and URL.
     */
    private fun handleFileDetails(
        soundId: Int,
        soundName: String,
        identifier: String,
        onSuccess: (fileTitle: String, fileUrl: String) -> Unit
    ) {
        // Fetch file details based on the provided identifier (URL or file path)
        viewModel.fetchFileDetails(identifier)

        // Observe the result of file details fetching
        viewModel.fileDetails.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ApiResult.Loading -> {
                    // Optionally show a loader while fetching the file details
                }
                is ApiResult.Success -> {
                    // Hide the loader after successfully fetching the file details
                    binding.loader.visibility = View.GONE

                    // Extract file details from the result and pass them to the onSuccess lambda
                    result.data.query.pages.let { pages ->
                        val page = pages.values.firstOrNull()
                        val fileTitle = page?.title
                        val fileUrl = page?.imageinfo?.firstOrNull()?.url

                        if (fileTitle != null && fileUrl != null) {
                            onSuccess(fileTitle, fileUrl)
                        }
                    }
                }
                is ApiResult.Error -> {
                    // Handle the error scenario (e.g., display an error message)
                }
            }
        })
    }
}

