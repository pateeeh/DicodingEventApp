package com.example.ujiandicoding.ui

import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.ujiandicoding.R
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.databinding.ActivityDetailEventBinding
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
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

        // Set Toolbar sebagai ActionBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Event"

        eventsRepository = EventsRepository(application)
        detailViewModel = ViewModelProvider(this, DetailViewModelFactory(eventsRepository))[DetailViewModel::class.java]

        val data = intent.getParcelableExtra<Parcelable>(EXTRA_DATA)
        if (data != null) {
            when (data) {
                is Events -> {
                    // Data adalah objek Events
                    eventId = data.id
                    Glide.with(this)
                        .load(data.image)
                        .into(binding.image)
                    binding.tvNameEvent.text = data.name
                    val cleanDesc = data.description?.replace(Regex("<img[^>]*>"), "")
                    binding.tvDesc.text = Html.fromHtml(cleanDesc, Html.FROM_HTML_MODE_LEGACY)
                    binding.tvSummary.text = data.summary
                    binding.tvOwnerName.text = data.ownerName
                    binding.tvBeginTime.text = data.beginTime
                    binding.tvEndTime.text = data.endTime
                }
                is ListEventsItem -> {
                    // Data adalah objek ListEventsItem
                    eventData = data
                    eventId = data.id
                    Glide.with(this)
                        .load(data.imageLogo)
                        .into(binding.image)
                    binding.tvNameEvent.text = data.name
                    val cleanDesc = data.description.replace(Regex("<img[^>]*>"), "")
                    binding.tvDesc.text = Html.fromHtml(cleanDesc, Html.FROM_HTML_MODE_LEGACY)
                    binding.tvSummary.text = data.summary
                    binding.tvOwnerName.text = data.ownerName
                    binding.tvBeginTime.text = data.beginTime
                    binding.tvEndTime.text = data.endTime
                }
                else -> {
                    // Tipe data tidak dikenal
                    Log.e("DetailEventActivity", "Unknown data type received")
                }
            }
            // Observe isFavorited LiveData menggunakan coroutine
            lifecycleScope.launch {
                isFavorited = detailViewModel.isEventFavorited(eventId)
                updateFavoriteButtons()
            }

            binding.btnAddFavorite.setOnClickListener {
                addToFavorites()
            }

            binding.btnRemoveFavorite.setOnClickListener {
                showDeleteConfirmationDialog()
            }
        } else {
            Log.e("DetailEventActivity", "User Data is Null")
        }
    }

    private fun updateFavoriteButtons() {
        if (isFavorited) {
            binding.btnAddFavorite.isEnabled = false
            binding.btnRemoveFavorite.isEnabled = true
        } else {
            binding.btnAddFavorite.isEnabled = true
            binding.btnRemoveFavorite.isEnabled = false
        }
    }

    private fun addToFavorites() {
        // Tambahkan ke favorit
        val event = Events(
            id = eventId,
            name = binding.tvNameEvent.text.toString(),
            description = binding.tvDesc.text.toString(),
            image = eventData?.imageLogo ?: "",
            beginTime = eventData?.beginTime ?: "",
            endTime = eventData?.endTime ?: "",
            isFavo = true // Set isFavo to true when adding to favorites
        )
        lifecycleScope.launch {
            Log.d("DetailEventActivity", "Adding to favorites: $event")
            detailViewModel.insert(event)
            isFavorited = true // Ubah menjadi true saat menambahkan ke favorit
            updateFavoriteButtons()
            Toast.makeText(this@DetailEventActivity, getString(R.string.added_fav_success), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apa kamu yakin ingin menghapus dari favorit?")
            .setPositiveButton("Ya") { _, _ ->
                val event = Events(
                    id = eventId,
                    name = binding.tvNameEvent.text.toString(),
                    description = binding.tvDesc.text.toString(),
                    image = eventData?.imageLogo ?: "",
                    beginTime = eventData?.beginTime ?: "",
                    endTime = eventData?.endTime ?: "",
                    isFavo = false // Set isFavo to false when removing from favorites
                )
                lifecycleScope.launch {
                    Log.d("DetailEventActivity", "Removing from favorites: $event")
                    detailViewModel.delete(event)
                    isFavorited = false // Ubah menjadi false saat menghapus dari favorit
                    updateFavoriteButtons()
                    Toast.makeText(this@DetailEventActivity, "Berhasil dihapus dari favorit", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Handle tombol back di Toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}