package com.example.ujiandicoding.ui.finished

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ujiandicoding.data.repository.AppExecutors
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.databinding.FragmentFinishedBinding
import com.example.ujiandicoding.ui.adapter.ListEventAdapter

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var finishedViewModel: FinishedViewModel
    private lateinit var eventsRepository: EventsRepository

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
        // Inisialisasi EventsRepository
        eventsRepository = EventsRepository(requireActivity().application, AppExecutors())
        // Buat factory dengan EventsRepository
        val factory = FinishedViewModelFactory(eventsRepository)
        // Dapatkan ViewModel dengan factory
        finishedViewModel = ViewModelProvider(this, factory).get(FinishedViewModel::class.java)

        // Observe data dari ViewModel
        finishedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        finishedViewModel.eventList.observe(viewLifecycleOwner) { eventList ->
            setEventList(eventList)
        }

        // Set layout manager untuk RecyclerView
        _binding?.rvListEvent?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEventList(dataEvent: List<ListEventsItem>?) {
        val adapter = ListEventAdapter()
        adapter.submitList(dataEvent)
        binding.rvListEvent.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        _binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}