package com.example.soundmixer.features.recordings.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.soundmixer.R
import com.example.soundmixer.data_base.Recording
import com.example.soundmixer.databinding.AllSoundsListItemBinding
import com.example.soundmixer.features.merge.data_models.SelectedSoundDetails
import com.example.soundmixer.utils.OnItemClickListener

class SoundListAdapter(
    private val list: List<Recording>,
    private val itemClickListener: OnItemClickListener,
    private val showCheckBox: Boolean,
    private val onSelectionChanged: (Boolean) -> Unit // Callback to notify about selection changes
) : RecyclerView.Adapter<SoundListAdapter.SoundViewHolder>() {

    // List to keep track of selected items
    private val selectedItems = mutableListOf<Recording>()

    override fun getItemViewType(position: Int): Int {
        return R.layout.all_sounds_list_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        val binding = AllSoundsListItemBinding.bind(view)
        return SoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        val item = list[holder.adapterPosition]

        holder.binding.apply {
            // Set the name of the sound file in the TextView
            tvMusicName.text = item.fileName

            // Show or hide the checkbox based on the showCheckBox flag
            checkbox.visibility = if (showCheckBox) View.VISIBLE else View.GONE

            // Update checkbox state based on whether the item is selected
            checkbox.isChecked = selectedItems.contains(item)

            // Handle checkbox state changes
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (selectedItems.size < 2) {
                        selectedItems.add(item)
                    } else {
                        // If more than 2 items are selected, uncheck the box and notify the user
                        checkbox.isChecked = false
                        Toast.makeText(root.context, "You can select only two items", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    selectedItems.remove(item)
                }
                // Notify about the selection state change
                checkSelectionAndNavigate()
            }

            // Handle item click when checkboxes are not shown
            root.setOnClickListener {
                if (!showCheckBox) {
                    itemClickListener.onPlayClick(item.id, item.fileName, item.filePath)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * Check the selection state and notify the callback.
     * If exactly two items are selected, notify the callback and pass selected items to the listener.
     */
    private fun checkSelectionAndNavigate() {
        // Determine if the "Done" button should be shown
        val showDoneButton = selectedItems.size == 2
        onSelectionChanged(showDoneButton)

        // If exactly two items are selected, pass the selected details to the listener
        if (showDoneButton) {
            itemClickListener.onItemsSelected(getSelectedDetails())
        }
    }

    /**
     * Get the details of selected items.
     * @return A data class containing IDs, names, and file paths of the selected items.
     */
    fun getSelectedDetails(): SelectedSoundDetails = SelectedSoundDetails(
        ids = selectedItems.map { it.id },
        names = selectedItems.map { it.fileName },
        filePaths = selectedItems.map { it.filePath }
    )

    inner class SoundViewHolder(val binding: AllSoundsListItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}





