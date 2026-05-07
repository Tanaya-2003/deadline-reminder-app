package com.example.deadlinereminder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.deadlinereminder.data.DeadlineDbHelper
import java.text.SimpleDateFormat
import java.util.*

class ViewDeadlinesActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var editButton: Button
    private lateinit var recommendationText: TextView
    private lateinit var dbHelper: DeadlineDbHelper

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_deadlines)

        listView = findViewById(R.id.viewDeadlineList)
        editButton = findViewById(R.id.goToEditButton)
        recommendationText = findViewById(R.id.recommendationText)
        dbHelper = DeadlineDbHelper(this)

        editButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadDeadlines()
    }

    private fun loadDeadlines() {
        val nowMillis = System.currentTimeMillis()

        val deadlines = dbHelper.getAllDeadlines().mapNotNull { pair ->
            val parts = pair.second.split(" - ")
            if (parts.size != 2) return@mapNotNull null

            try {
                val title = parts[0]
                val date = dateFormat.parse(parts[1]) ?: return@mapNotNull null
                val hoursLeft = (date.time - nowMillis) / (1000 * 60 * 60)

                Triple(title, date, hoursLeft)
            } catch (e: Exception) {
                null
            }
        }

        val sorted = deadlines.sortedBy { it.second.time }
        var highPriorityCount = 0

        val displayList = sorted.map {
            val (priority, dueText) = when {
                it.third <= 24 -> {
                    highPriorityCount++
                    Pair("🔴 HIGH", "Due in less than a day")
                }
                it.third <= 72 -> Pair("🟠 MEDIUM", "Due in less than 3 days")
                else -> Pair("🟢 LOW", "Due later")
            }

            // ✅ Two-line display using newline
            "$priority — ${it.first} (${dateFormat.format(it.second)})\n$dueText"
        }

        // ✅ Recommendation logic (already added)
        if (highPriorityCount > 0) {
            recommendationText.visibility = View.VISIBLE
            recommendationText.text =
                "You have $highPriorityCount high‑priority deadline${if (highPriorityCount > 1) "s" else ""}. Consider starting today."
        } else {
            recommendationText.visibility = View.GONE
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            displayList
        )
        listView.adapter = adapter
    }
}