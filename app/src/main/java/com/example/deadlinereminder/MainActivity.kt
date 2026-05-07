package com.example.deadlinereminder

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.deadlinereminder.data.DeadlineDbHelper
import java.util.*

class MainActivity : AppCompatActivity() {

    private var selectedId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val titleInput = findViewById<EditText>(R.id.titleInput)
        val dateInput = findViewById<EditText>(R.id.dateInput)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val listView = findViewById<ListView>(R.id.deadlineList)

        val dbHelper = DeadlineDbHelper(this)

        fun refreshList(): List<Pair<Int, String>> {
            val data = dbHelper.getAllDeadlines()
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                data.map { it.second }
            )
            listView.adapter = adapter
            return data
        }

        var currentData = refreshList()

        // INITIAL STATE
        saveButton.visibility = Button.INVISIBLE
        updateButton.visibility = Button.INVISIBLE

        // ✅ DATE PICKER (FOR BOTH CREATE & UPDATE)
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val formattedDate = "%02d/%02d/%04d".format(
                        dayOfMonth,
                        month + 1,
                        year
                    )
                    dateInput.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // ✅ TEXT WATCHER (CREATE MODE)
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveButton.visibility =
                    if (
                        titleInput.text.isNotEmpty() &&
                        dateInput.text.isNotEmpty() &&
                        selectedId == null
                    ) Button.VISIBLE else Button.INVISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        titleInput.addTextChangedListener(textWatcher)
        dateInput.addTextChangedListener(textWatcher)

        // ✅ CREATE
        saveButton.setOnClickListener {
            dbHelper.insertDeadline(
                titleInput.text.toString(),
                dateInput.text.toString()
            )

            Toast.makeText(this, "Deadline saved", Toast.LENGTH_SHORT).show()
            titleInput.text.clear()
            dateInput.text.clear()
            selectedId = null
            saveButton.visibility = Button.INVISIBLE
            currentData = refreshList()
        }

        // ✅ SELECT FOR UPDATE
        listView.setOnItemClickListener { _, _, position, _ ->
            selectedId = currentData[position].first
            val parts = currentData[position].second.split(" - ")

            titleInput.setText(parts[0])
            dateInput.setText(parts[1])

            saveButton.visibility = Button.INVISIBLE
            updateButton.visibility = Button.VISIBLE
        }

        // ✅ UPDATE
        updateButton.setOnClickListener {
            if (selectedId != null) {
                dbHelper.deleteById(selectedId!!)
                dbHelper.insertDeadline(
                    titleInput.text.toString(),
                    dateInput.text.toString()
                )

                Toast.makeText(this, "Deadline updated", Toast.LENGTH_SHORT).show()

                titleInput.text.clear()
                dateInput.text.clear()
                selectedId = null
                updateButton.visibility = Button.INVISIBLE
                currentData = refreshList()
            }
        }

        // ✅ DELETE WITH CONFIRMATION
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val idToDelete = currentData[position].first

            AlertDialog.Builder(this)
                .setTitle("Delete Deadline")
                .setMessage("Are you sure you want to delete this deadline?")
                .setPositiveButton("Yes") { _, _ ->
                    dbHelper.deleteById(idToDelete)
                    Toast.makeText(this, "Deadline deleted", Toast.LENGTH_SHORT).show()
                    selectedId = null
                    updateButton.visibility = Button.INVISIBLE
                    currentData = refreshList()
                }
                .setNegativeButton("No", null)
                .show()

            true
        }
    }
}