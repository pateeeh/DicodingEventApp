package com.example.ujiandicoding.ui.finished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import com.example.ujiandicoding.data.repository.AppExecutors
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.databinding.FragmentFinishedBinding
import com.example.ujiandicoding.ui.adapter.ListEventAdapter
import com.example.ujiandicoding.ui.adapter.ListFinishedAdapter

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var finishedViewModel: FinishedViewModel
    private lateinit var eventsRepository: EventsRepository
    private lateinit var listFinishedAdapter: ListFinishedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventsRepository = EventsRepository(requireActivity().application, AppExecutors())
        val factory = FinishedViewModelFactory(eventsRepository)
        finishedViewModel = ViewModelProvider(this, factory)[FinishedViewModel::class.java]

        listFinishedAdapter = ListFinishedAdapter { event ->
            Toast.makeText(requireContext(), "Favorit: ${event.name}", Toast.LENGTH_SHORT).show()
        }

        binding.rvListEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listFinishedAdapter
        }

        finishedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        finishedViewModel.eventList.observe(viewLifecycleOwner) { eventList ->
            setEventList(eventList)
        }

        finishedViewModel.searchedEvents.observe(viewLifecycleOwner) { eventList ->
            setEventList(eventList)
        }

        val searchView = binding.searchBar
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { finishedViewModel.setSearchQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { finishedViewModel.setSearchQuery(it) }
                return true
            }
        })

        finishedViewModel.searchedEvents.observe(viewLifecycleOwner) { events ->
            listFinishedAdapter.submitList(events) // Pastikan adapter sudah diinisialisasi
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEventList(dataEvent: List<ListEventsItem>?) {
        listFinishedAdapter.submitList(dataEvent)
    }

    private fun showLoading(isLoading: Boolean) {
        _binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}