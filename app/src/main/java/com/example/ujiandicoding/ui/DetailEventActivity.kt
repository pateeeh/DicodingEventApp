package com.example.ujiandicoding.ui

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ujiandicoding.R
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.data.utills.AppExecutors
import com.example.ujiandicoding.databinding.ActivityDetailEventBinding

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private lateinit var eventsRepository: EventsRepository
    private lateinit var detailViewModel: DetailViewModel
    private var isFavorited: Boolean = false
    private var eventId: Int = 0
    private var eventData: ListEventsItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventsRepository = EventsRepository(application, AppExecutors())
        detailViewModel = ViewModelProvider(this, DetailViewModelFactory(eventsRepository))[DetailViewModel::class.java]

        eventData = intent.getParcelableExtra<ListEventsItem>(EXTRA_DATA)
        if (eventData != null) {
            Log.d("DetailEventActivity", "User Data Received: $eventData")
            eventId = eventData!!.id

            Glide.with(this)
                .load(eventData!!.mediaCover)
                .into(binding.image)
            binding.tvNameEvent.text = eventData!!.name
            val cleanDesc = eventData!!.description.replace(Regex("<img[^>]*>"), "")
            binding.tvDesc.text = Html.fromHtml(cleanDesc, Html.FROM_HTML_MODE_LEGACY)
            binding.tvSummary.text = eventData!!.summary
            binding.tvOwnerName.text = eventData!!.ownerName
            binding.tvBeginTime.text = eventData!!.beginTime
            binding.tvEndTime.text = eventData!!.endTime

            // Observe isFavorited LiveData
            detailViewModel.isEventFavorited(eventId).observe(this) { favorited ->
                isFavorited = favorited
                setFavoriteIcon(isFavorited)
            }

            binding.fabFav.setOnClickListener {
                if (isFavorited) {
                    // Delete from favorites
                    showDeleteConfirmationDialog()
                } else {
                    // Add to favorites
                    addToFavorites()
                }
            }
        } else {
            Log.e("DetailEventActivity", "User Data is Null")
        }
    }

    private fun setFavoriteIcon(isFavorited: Boolean) {
        if (isFavorited) {
            binding.fabFav.setImageResource(R.drawable.ic_favorite)
            binding.fabFav.contentDescription = getString(R.string.remove_fav)
        } else {
            binding.fabFav.setImageResource(R.drawable.ic_favorite_outline)
            binding.fabFav.contentDescription = getString(R.string.add_fav)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.remove_fav)
            .setPositiveButton("Yes") { _, _ ->
                // Delete dari favorit
                val event = Events(
                    id = eventId,
                    name = binding.tvNameEvent.text.toString(),
                    description = binding.tvDesc.text.toString(),
                    image = eventData?.imageLogo ?: "",
                    isFavo = false
                )
                detailViewModel.delete(event)
                Toast.makeText(this, R.string.remove_fav_success, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun addToFavorites() {
        val event = Events(
            id = eventId,
            name = binding.tvNameEvent.text.toString(),
            description = binding.tvDesc.text.toString(),
            image = eventData?.imageLogo ?: "",
            isFavo = true
        )
        detailViewModel.insert(event)
        Toast.makeText(this, getString(R.string.added_fav_success), Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}