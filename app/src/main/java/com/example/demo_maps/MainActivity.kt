package com.example.demo_maps

import AddressAdapter
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // Khai báo các biến
    private lateinit var searchEditText: EditText
    private lateinit var resultsListView: ListView
    private val client = OkHttpClient()
    private val apiKey = "bYD06IsE1Nw3ZkvWK_5729WDu24uzKBeErVmAaNkdYY"
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText = findViewById(R.id.searchEditText)
        resultsListView = findViewById(R.id.resultsListView)

        // Thêm TextWatcher cho EditText để theo dõi sự thay đổi văn bản
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    searchAddress(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    //tìm kiếm địa chỉ dựa trên từ khóa
    private fun searchAddress(query: String) {
        val url = "https://geocode.search.hereapi.com/v1/geocode?q=${Uri.encode(query)}&apiKey=$apiKey"

        Log.d("url", "url: $url")
        val request = Request.Builder().url(url).build()

        // Gửi request HTTP
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = responseBody.string()
                    val results = parseResults(json)
                    // Cập nhật UI trên thread chính
                    runOnUiThread {
                        displayResults(results, query)
                    }
                }
            }
        })
    }

    //phân tích kết quả JSON từ API
    private fun parseResults(json: String): List<String> {
        val results = mutableListOf<String>()
        val jsonObject = JSONObject(json)
        if (jsonObject.has("items")) {
            val items = jsonObject.getJSONArray("items")
            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val address = item.getJSONObject("address")
                val label = address.getString("label")
                results.add(label)
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show()
            }
        }
        return results
    }

    private fun displayResults(results: List<String>, keyword: String?) {
        val adapter = AddressAdapter(this, results, keyword)
        resultsListView.adapter = adapter

        resultsListView.setOnItemClickListener { parent, view, position, id ->
            val selectedAddress = results[position]
            adapter.openGoogleMaps(selectedAddress)
        }
    }

}
