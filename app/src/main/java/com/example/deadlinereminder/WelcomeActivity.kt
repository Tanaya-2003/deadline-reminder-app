package com.example.deadlinereminder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class WelcomeActivity : AppCompatActivity() {

    private lateinit var quoteText: TextView

    private val quotes = listOf(
        "You're doing great. One step at a time 💪",
        "Small progress is still progress 🌱",
        "You've got this!",
        "Focus on what you can control today.",
        "Every deadline you meet builds confidence ✨",
        "One task at a time — you’re capable.",
        "Progress beats perfection."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        quoteText = findViewById(R.id.quoteText)
        val letsDoItButton = findViewById<Button>(R.id.letsDoItButton)

        letsDoItButton.setOnClickListener {
            startActivity(Intent(this, ViewDeadlinesActivity::class.java))
            finish()
        }
    }

    /**
     * ✅ This runs every time the app becomes visible.
     * Ensures a DIFFERENT motivational quote on each app open.
     */
    override fun onResume() {
        super.onResume()
        quoteText.text = quotes[Random.nextInt(quotes.size)]
    }
}