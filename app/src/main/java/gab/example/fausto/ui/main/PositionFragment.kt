package gab.example.fausto.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import gab.example.fausto.R

class PositionFragment : Fragment() {

    private val viewModel by activityViewModels<MyViewModel>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private lateinit var positionText: TextView
    private lateinit var send: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_position, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        (childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment).getMapAsync {
            map = it
        }
        send = view.findViewById(R.id.send)
        send.isEnabled = false
        positionText = view.findViewById(R.id.position)
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder
            .setMessage(R.string.confirm_message)
            .setTitle(R.string.confirm_title)
            .setPositiveButton(R.string.ok) { dialog, _ -> viewModel.send(requireContext()){
                findNavController().popBackStack(); dialog.dismiss()
            }}
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
        val dialog: AlertDialog = builder.create()
        send.setOnClickListener {
            dialog.show()
        }
        viewModel.position.observe(viewLifecycleOwner) {
            val pos =
                if(it != null) it.latitude.toString() + " : " + it.longitude.toString()
                else ""
            positionText.text = pos
            send.isEnabled = pos != ""
        }
        view.findViewById<Button>(R.id.get_position).setOnClickListener { getLocation() }
    }

    private fun getLocation() {
        if(checkPermission()) {
            val locationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener {
                    val marker = MarkerOptions()
                    val pos = LatLng(it.latitude, it.longitude)
                    marker.position(pos)
                    map.clear()
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 10F))
                    map.addMarker(marker)
                    viewModel.position.value = it
                }
            else startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        else requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            100
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100 && grantResults.size > 0 &&
            grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED
        )
            getLocation()
        else
            Toast.makeText(
                requireActivity(),
                "Impossibile procedere senza le autorizzazioni",
                Toast.LENGTH_SHORT
            ).show()
    }

    private fun checkPermission(): Boolean {
        return (
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        )
    }
}