package com.example.ujiandicoding.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.databinding.FragmentFavoriteBinding
import com.example.ujiandicoding.ui.adapter.FavoriteAdapter

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
        val eventsRepository = EventsRepository(requireActivity().application)
        favoriteViewModel = ViewModelProvider(this,
            FavoriteViewModelFactory(eventsRepository)
        )[FavoriteViewModel::class.java]

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

    private fun setFavoriteList(eventList: List<Events>?) {
        adapter.submitList(eventList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}