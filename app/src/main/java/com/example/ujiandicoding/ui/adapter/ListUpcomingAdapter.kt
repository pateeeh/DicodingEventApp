package com.example.ujiandicoding.ui.adapter

import android.content.Intent
import android.text.Html
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.databinding.CardListEventBinding
import com.example.ujiandicoding.ui.DetailEventActivity
import com.example.ujiandicoding.ui.DetailEventActivity.Companion.EXTRA_DATA
import com.example.ujiandicoding.ui.adapter.ListEventAdapter.Companion.DIFF_CALLBACK

class ListUpcomingAdapter: ListAdapter<ListEventsItem, ListUpcomingAdapter.ViewHolder>(DIFF_CALLBACK) {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val upcomingItem = getItem(position)
        holder.bind(upcomingItem)
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_DATA, upcomingItem )
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardListEventBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

}