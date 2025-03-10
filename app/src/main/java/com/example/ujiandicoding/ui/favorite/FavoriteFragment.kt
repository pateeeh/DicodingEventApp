package com.example.ujiandicoding.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ujiandicoding.data.db.EventsRoomDatabase
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.data.utills.AppExecutors
import com.example.ujiandicoding.databinding.FragmentFavoriteBinding
import com.example.ujiandicoding.ui.DetailEventActivity
import com.example.ujiandicoding.ui.DetailEventActivity.Companion.EXTRA_DATA
import com.example.ujiandicoding.ui.adapter.FavoriteAdapter
import com.example.ujiandicoding.ui.adapter.ListEventAdapter

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi ViewModel
        val eventsDao = EventsRoomDatabase.getDatabase(requireContext()).eventsDao()
        val eventsRepository = EventsRepository(requireActivity().application, AppExecutors())
        favoriteViewModel = ViewModelProvider(this,
            FavoriteViewModelFactory(eventsRepository)
        ).get(FavoriteViewModel::class.java)

        // Inisialisasi RecyclerView dan Adapter
        adapter = FavoriteAdapter()
        binding.rvListEvent.layoutManager = LinearLayoutManager(context)
        binding.rvListEvent.setHasFixedSize(true)
        binding.rvListEvent.adapter = adapter

        // Mengamati data dari ViewModel
        favoriteViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        favoriteViewModel.eventList.observe(viewLifecycleOwner) { eventList ->
            setFavoriteList(eventList)
        }

        return root
    }

    private fun setFavoriteList(eventList: List<ListEventsItem>?): List<ListEventsItem>? {
        val adapter = ListEventAdapter()
        adapter.submitList(eventList)
        binding.rvListEvent.adapter = adapter
        return eventList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}