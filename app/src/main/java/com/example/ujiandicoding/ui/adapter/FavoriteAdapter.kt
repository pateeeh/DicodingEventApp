package com.example.ujiandicoding.ui.adapter

import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.databinding.CardListEventBinding
import com.example.ujiandicoding.ui.DetailEventActivity
import com.example.ujiandicoding.ui.DetailEventActivity.Companion.EXTRA_DATA

class FavoriteAdapter : ListAdapter<Events, FavoriteAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(val binding: CardListEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Events) {
            binding.tvNameEvent.text = event.name
            val cleanDesc = event.description?.replace(Regex("<img[^>]*>"), "")
            binding.tvDescEvent.text = Html.fromHtml(cleanDesc, Html.FROM_HTML_MODE_LEGACY)
            Glide.with(itemView.context)
                .load(event.image)
                .into(binding.img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CardListEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailEventActivity::class.java)
            intent.putExtra(EXTRA_DATA, event)
            holder.itemView.context.startActivity(intent)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Events>() {
            override fun areItemsTheSame(oldItem: Events, newItem: Events): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Events, newItem: Events): Boolean {
                return oldItem == newItem
            }
        }
    }
}