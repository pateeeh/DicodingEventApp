package com.example.ujiandicoding.ui.adapter

import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.databinding.CardListEventBinding
import com.example.ujiandicoding.ui.DetailEventActivity
import com.example.ujiandicoding.ui.DetailEventActivity.Companion.EXTRA_DATA

class ListEventAdapter: ListAdapter<ListEventsItem, ListEventAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(val binding: CardListEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(eventsItem: ListEventsItem){
            Glide.with(itemView.context)
                .load(eventsItem.imageLogo)
                .into(binding.img)
            binding.tvNameEvent.text = eventsItem.name
            val cleanDesc = eventsItem.description.replace(Regex("<img[^>]*>"), "")
            binding.tvDescEvent.text = Html.fromHtml(cleanDesc, Html.FROM_HTML_MODE_LEGACY)
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>(){
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eventsItem = getItem(position)
        holder.bind(eventsItem)

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_DATA, eventsItem)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardListEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
}