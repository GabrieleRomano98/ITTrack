package gab.example.fausto.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import gab.example.fausto.R

class MainFragment : Fragment() {

    private val viewModel by activityViewModels<MyViewModel>()

    private lateinit var scanButton: Button
    private lateinit var nextButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var codeAdapter: CodeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextButton = view.findViewById(R.id.next)
        nextButton.isEnabled = false
        nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_positionFragment)
        }

        recyclerView = view.findViewById(R.id.codes_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        codeAdapter = CodeAdapter(mutableListOf()) {
            viewModel.deleteCode(it)
            nextButton.isEnabled = viewModel.codes.value!!.size != 0
        }
        recyclerView.adapter = codeAdapter

        val scanner = GmsBarcodeScanning.getClient(requireContext())
        scanButton = view.findViewById(R.id.start_scan)
        scanButton.setOnClickListener {
            scanner.startScan().addOnSuccessListener {
                viewModel.addCode(it.rawValue?:"")
                codeAdapter.notifyItemInserted(viewModel.codes.value!!.size - 1)
                nextButton.isEnabled = true
            }
        }

        viewModel.codes.observe(viewLifecycleOwner) {
            codeAdapter.setCodeList(it)
        }

    }
}
