package com.arc.earthquakemap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arc.earthquakemap.databinding.ItemEarthquakeBinding
import com.arc.earthquakemap.network.Feature

class EarthquakeAdapter(
    private val earthquakeList: List<Feature>,
    private val onItemClick: (Feature) -> Unit
) : RecyclerView.Adapter<EarthquakeAdapter.EarthquakeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthquakeViewHolder {
        val binding = ItemEarthquakeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EarthquakeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EarthquakeViewHolder, position: Int) {
        val earthquake = earthquakeList[position]
        holder.bind(earthquake, onItemClick)
    }

    override fun getItemCount(): Int = earthquakeList.size

    class EarthquakeViewHolder(private val binding: ItemEarthquakeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(earthquake: Feature, onItemClick: (Feature) -> Unit) {
            // Set the title and magnitude
            binding.earthquakeTitle.text = earthquake.properties.title
            binding.earthquakeMagnitude.text = "Magnitude: ${earthquake.properties.mag}"

            // Get latitude and longitude from the coordinates array
            val lat = earthquake.geometry.coordinates[1]  // Latitude is at index 1
            val lon = earthquake.geometry.coordinates[0]  // Longitude is at index 0

            // Set the latitude and longitude text
            binding.earthquakeLatLng.text = "Latitude: $lat, Longitude: $lon"

            // Set click listener to handle item clicks
            binding.root.setOnClickListener {
                onItemClick(earthquake)
            }
        }
    }
}
