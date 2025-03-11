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
import com.bumptech.glide.Glide
import com.example.ujiandicoding.R
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.AppExecutors
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventsItem
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

        // Set Toolbar sebagai ActionBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Event"

        eventsRepository = EventsRepository(application, AppExecutors())
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
                    binding.tvSummary.text = ""
                    binding.tvOwnerName.text = ""
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
            // Observe isFavorited LiveData
            // Pindahkan pemanggilan observe ke sini, setelah eventId diinisialisasi
            detailViewModel.isEventFavorited(eventId).observe(this) { favorited ->
                Log.d("DetailEventActivity", "isEventFavorited observe: favorited = $favorited")
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
        Log.d("DetailEventActivity", "setFavoriteIcon: isFavorited = $isFavorited")
        if (isFavorited) {
            binding.fabFav.setImageResource(R.drawable.ic_favorite)
            binding.fabFav.contentDescription = getString(R.string.remove_fav)
        } else {
            binding.fabFav.setImageResource(R.drawable.ic_favorite_outline)
            binding.fabFav.contentDescription = getString(R.string.add_fav)
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
            isFavo = true
        )
        detailViewModel.insert(event)
        isFavorited = true // Ubah menjadi true saat menambahkan ke favorit
        setFavoriteIcon(isFavorited)
        Toast.makeText(this, getString(R.string.added_fav_success), Toast.LENGTH_SHORT).show()
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
                    isFavo = false
                )
                detailViewModel.delete(event)
                isFavorited = false // Ubah menjadi false saat menghapus dari favorit
                setFavoriteIcon(isFavorited)
                Toast.makeText(this, "Berhasil dihapus dari favorit", Toast.LENGTH_SHORT).show()
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