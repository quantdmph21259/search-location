import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.demo_maps.MainActivity
import com.example.demo_maps.R

class AddressAdapter(context: Context, private val addresses: List<String>, private val keyword: String?) :
    ArrayAdapter<String>(context, R.layout.address_list_item, addresses) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.address_list_item, parent, false)
        val addressTextView: TextView = view.findViewById(R.id.addressTextView)
        val directionsImageView: ImageView = view.findViewById(R.id.directionsImageView)

        val address = addresses[position]
        addressTextView.text = highlightKeyword(address)

        val clickListener = View.OnClickListener {
           openGoogleMaps(address)
        }

        directionsImageView.setOnClickListener(clickListener)

        return view
    }

    private fun highlightKeyword(text: String): CharSequence {
        val highlightedText = SpannableString(text)
        keyword?.let {
            val startIndex = text.indexOf(it, ignoreCase = true)
            if (startIndex != -1) {
                highlightedText.setSpan(
                    ForegroundColorSpan(Color.RED),
                    startIndex,
                    startIndex + it.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return highlightedText
    }

    //mở Google Maps với địa chỉ được chọn
    fun openGoogleMaps(address: String) {
        println("Opening Google Maps with address: $address")
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        Log.d("Check_Maps_Uri", "Uri: $gmmIntentUri")
        Log.d("Check_Maps_Package", "Package: ${mapIntent.`package`}")
        (context as MainActivity).startActivity(mapIntent)
    }
}
