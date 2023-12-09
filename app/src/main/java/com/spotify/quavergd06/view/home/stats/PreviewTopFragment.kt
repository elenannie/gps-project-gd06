package com.spotify.quavergd06.view.home.stats

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.spotify.quavergd06.api.getNetworkService
import com.spotify.quavergd06.api.setKey
import com.spotify.quavergd06.data.ArtistsRepository
import com.spotify.quavergd06.data.fetchables.Fetchable
import com.spotify.quavergd06.data.model.Artist
import com.spotify.quavergd06.data.model.StatsItem
import com.spotify.quavergd06.data.toStatsItem
import com.spotify.quavergd06.database.QuaverDatabase
import com.spotify.quavergd06.databinding.FragmentTopPreviewBinding
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PreviewTopFragment(private val fetchable: Fetchable, private val onPreviewItemClick: (StatsItem) -> Unit) : Fragment() {
    private var _binding: FragmentTopPreviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var db : QuaverDatabase
    private lateinit var artistsRepository : ArtistsRepository

    private var adapter: StatsItemAdapter = StatsItemAdapter(emptyList(), {}, null)
    private var items: List<StatsItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        db = QuaverDatabase.getInstance(context)!!
        artistsRepository = ArtistsRepository.getInstance(db.artistDAO(), getNetworkService())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopPreviewBinding.inflate(inflater, container, false)
        Log.d("PreviewTopFragment", "onCreateView")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        Log.d("PreviewTopFragment", "onViewCreated")
        // Realizar una búsqueda de artistas en Spotify
        lifecycleScope.launch {
            try {
                setKey(obtenerSpotifyApiKey(requireContext())!!)
                items = fetchable.fetch()
                Log.d("PreviewTopFragment", "items: $items")
                adapter.updateData(items)

            } catch (e: Exception) {
                Log.d("PreviewTopFragment", "Error: ${e.message}")
            }
        }
    }

    private fun setUpRecyclerView() {
        adapter = StatsItemAdapter(
            statsItems = items,
            context = this.context,
            onClick = {statsItem ->
                onPreviewItemClick(statsItem)
            }
        )
        with(binding) {
            recyclerViewTopPreview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,  false)
            recyclerViewTopPreview.adapter = adapter
        }
        android.util.Log.d("ArtistFragment", "setUpRecyclerView")
    }

    private fun obtenerSpotifyApiKey(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }

    private fun subscribeUI(adapter: StatsItemAdapter) {
        artistsRepository.artists.observe(viewLifecycleOwner) { artists ->
            adapter.updateData(artists.map(Artist::toStatsItem))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        lifecycleScope.cancel()
    }


}