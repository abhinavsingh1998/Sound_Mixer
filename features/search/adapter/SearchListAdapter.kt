package com.example.soundmixer.features.search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soundmixer.R
import com.example.soundmixer.databinding.VoiceListItemBinding
import com.example.soundmixer.features.search.data_models.SearchResult
import com.example.soundmixer.utils.OnItemClickListener

class SearchListAdapter(var list: List<SearchResult>, val itemClickListener: OnItemClickListener ) : RecyclerView.Adapter<SearchListAdapter.SoundViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return R.layout.voice_list_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        val binding = VoiceListItemBinding.bind(view)
        return SoundViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        val item = list[holder.adapterPosition]
        holder.binding.apply {
            tvSoundName.text = item.title

            ivPlayButton.setOnClickListener {
                itemClickListener.onPlayClick(item.ns!!, item.title!!, item.title)
            }

            ivDownloadButton.setOnClickListener {
                itemClickListener.onDownloadClick(item.ns!!, item.title!!, item.title)
            }
        }
    }

    inner class SoundViewHolder(val binding: VoiceListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Function to update the data in the RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newSounds: List<SearchResult>) {
        list = newSounds
        notifyDataSetChanged()
    }

}