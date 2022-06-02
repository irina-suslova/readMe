package com.example.readme.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.readme.R
import org.json.JSONException


class DashboardFragment : Fragment() {
    private val JSON_URL = "https://000readme.000webhostapp.com/";

    lateinit var recyclerViewBests : RecyclerView;

    lateinit var mRequestQueue : RequestQueue;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewBests = view.findViewById(R.id.recyclerViewBest)

        mRequestQueue = Volley.newRequestQueue(getActivity()?.getApplicationContext());

        recyclerViewBests.layoutManager = LinearLayoutManager(activity)
        recyclerViewBests.adapter = CustomRecyclerBestsAdapter(fillList(JSON_URL))
    }

    private fun fillList(url: String): MutableList<List<String>> {
        var bests: MutableList<List<String>> = mutableListOf();
        var checker = false
        val request = JsonObjectRequest(
            Request.Method.GET,
            url + "bests.php", null, { response ->
                try {
                    val BestArrayNames = response.getJSONArray("best_texts_names")
                    val BestArrayTexts = response.getJSONArray("best_texts")
                    val BestArrayIDs = response.getJSONArray("best_texts_ids")
                    bests[0] = BestArrayNames.join("\n").split("\n")
                    bests.add(BestArrayTexts.join("\n").split("\n"));
                    bests.add(BestArrayIDs.join("\n").split("\n"));
                    checker = true
                    recyclerViewBests.adapter = CustomRecyclerBestsAdapter(bests)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
            Toast.makeText(activity, "Отсутствует подключение к интернету", Toast.LENGTH_SHORT).show()
        }
        mRequestQueue.add(request)
        if (!checker) bests.add(emptyList())
        return bests
    }

}