package com.example.ujiandicoding.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ujiandicoding.data.repository.AppExecutors
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.databinding.FragmentUpcomingBinding
import com.example.ujiandicoding.ui.adapter.ListEventAdapter

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var upcomingViewModel: UpcomingViewModel
    private lateinit var eventsRepository: EventsRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventsRepository = EventsRepository(requireActivity().application, AppExecutors())
        val factory = UpcomingViewModelFactory(eventsRepository)
        upcomingViewModel = ViewModelProvider(this, factory).get(UpcomingViewModel::class.java)
        upcomingViewModel.eventList.observe(viewLifecycleOwner) { event ->
            setEventList(event)
        }
        upcomingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

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