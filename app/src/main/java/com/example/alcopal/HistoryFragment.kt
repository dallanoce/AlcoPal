package com.example.alcopal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
// import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alcopal.data.Drink
import com.example.alcopal.ui.BACViewModel // Make sure this import is correct

class HistoryFragment : Fragment() {

    private lateinit var viewModel: BACViewModel
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = view.findViewById(R.id.historyRecyclerView) // Ensure this ID matches fragment_history.xml
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize with an empty list, it will be updated by the observer
        historyAdapter = HistoryAdapter(emptyList<Drink>())
        recyclerView.adapter = historyAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the ViewModel from the Activity scope to share data
        viewModel = ViewModelProvider(requireActivity()).get(BACViewModel::class.java)

        // Observe the drink history LiveData
        viewModel.drinkHistory.observe(viewLifecycleOwner) { drinks ->
            // Update the adapter with the new list of drinks
            historyAdapter.updateDrinks(drinks)
        }
    }
}
