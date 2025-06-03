package com.cse.shutterbook

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class PhotographerListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PhotographerAdapter
    private lateinit var photographerList: ArrayList<PhotographerData>
    private lateinit var filteredList: ArrayList<PhotographerData>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var searchView: SearchView

    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photographer_list)

        recyclerView = findViewById(R.id.photographerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchView = findViewById(R.id.searchView)

        photographerList = ArrayList()
        filteredList = ArrayList()

        category = intent.getStringExtra("category") ?: ""

        adapter = PhotographerAdapter(this, filteredList, category)
        recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Photographers")

        loadPhotographers()
        setupSearch()
    }

    private fun loadPhotographers() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                photographerList.clear()
                for (photographerSnapshot in snapshot.children) {
                    val photographer = photographerSnapshot.getValue(PhotographerData::class.java)
                    if (photographer != null) {
                        when (category.lowercase()) {
                            "wedding" -> if (!photographer.weddingCost.isNullOrEmpty()) photographerList.add(photographer)
                            "birthday" -> if (!photographer.birthdayCost.isNullOrEmpty()) photographerList.add(photographer)
                            "outdoor" -> if (!photographer.outdoorCost.isNullOrEmpty()) photographerList.add(photographer)
                            "event" -> if (!photographer.eventCost.isNullOrEmpty()) photographerList.add(photographer)
                            else -> photographerList.add(photographer)
                        }
                    }
                }
                filteredList.clear()
                filteredList.addAll(photographerList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PhotographerListActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchText = newText?.trim()?.lowercase() ?: ""
                filterList(searchText)
                return true
            }
        })
    }

    private fun filterList(searchText: String) {
        if (searchText.isEmpty()) {
            filteredList.clear()
            filteredList.addAll(photographerList)
        } else {
            val filtered = photographerList.filter {
                val nameMatch = it.name?.lowercase()?.contains(searchText) == true
                val locationMatch = it.address?.lowercase()?.contains(searchText) == true
                nameMatch || locationMatch
            }
            filteredList.clear()
            filteredList.addAll(filtered)
        }
        adapter.notifyDataSetChanged()
    }
}