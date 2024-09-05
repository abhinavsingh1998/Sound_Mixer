package com.example.soundmixer.utils

import com.example.soundmixer.features.merge.data_models.SelectedSoundDetails

interface OnItemClickListener {
    fun onDownloadClick(soundId: Int, soundname:String, url:String)
    fun onPlayClick(soundId: Int, soundname:String, file:String)
    fun onItemsSelected(selectedDetails: SelectedSoundDetails)

}
