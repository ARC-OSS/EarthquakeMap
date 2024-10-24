package com.arc.earthquakemap

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.arc.earthquakemap.databinding.ActivityMainBinding
import com.arc.earthquakemap.network.ApiClient
import com.arc.earthquakemap.network.EarthquakeList
import com.arc.earthquakemap.network.Feature
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var earthquakeAdapter: EarthquakeAdapter
    private var recyclerViewState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        binding.earthquakeRecyclerView.layoutManager = LinearLayoutManager(this)

        // Restore RecyclerView state
        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable("recycler_state")
            binding.earthquakeRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }

        fetchEarthquakeData()
    }

    private fun fetchEarthquakeData() {
        val call = ApiClient.apiService.getEarthquakes()

        call.enqueue(object : Callback<EarthquakeList> {
            override fun onResponse(call: Call<EarthquakeList>, response: Response<EarthquakeList>) {
                if (response.isSuccessful) {
                    val earthquakeList = response.body()?.features ?: emptyList()
                    populateRecyclerView(earthquakeList)
                }
            }

            override fun onFailure(call: Call<EarthquakeList>, t: Throwable) {
                Log.e("EarthquakeList", "Error Fetching List")
            }
        })
    }

    private fun populateRecyclerView(earthquakeList: List<Feature>) {
        earthquakeAdapter = EarthquakeAdapter(earthquakeList) { earthquake ->
            val lat = earthquake.geometry.coordinates[1]
            val lon = earthquake.geometry.coordinates[0]
            val mag = earthquake.properties.mag
            val title = earthquake.properties.title

            // Save the RecyclerView state before navigating
            recyclerViewState = binding.earthquakeRecyclerView.layoutManager?.onSaveInstanceState()

            // Hide the RecyclerView and show the MapFragment
            binding.earthquakeRecyclerView.visibility = View.GONE
            binding.fragmentContainer.visibility = View.VISIBLE

            // Replace the current fragment with MapFragment
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, MapFragment.newInstance(lat, lon, mag, title))
                .addToBackStack(null) // Adds the transaction to the back stack
                .commit()
        }
        binding.earthquakeRecyclerView.adapter = earthquakeAdapter
    }

    override fun onBackPressed() {
        // Handle back press to restore RecyclerView
        if (binding.fragmentContainer.visibility == View.VISIBLE) {
            binding.fragmentContainer.visibility = View.GONE
            binding.earthquakeRecyclerView.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        // Save the RecyclerView state in case of any interruption
        recyclerViewState = binding.earthquakeRecyclerView.layoutManager?.onSaveInstanceState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the RecyclerView state in the Bundle
        recyclerViewState?.let {
            outState.putParcelable("recycler_state", it)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the RecyclerView state
        recyclerViewState = savedInstanceState.getParcelable("recycler_state")
        binding.earthquakeRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }
}
