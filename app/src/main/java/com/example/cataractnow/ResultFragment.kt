import com.example.cataractnow.R
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class ResultFragment : Fragment() {

    private var data: Uri? = null
    private var resultCataract: String? = null
    private var conclusion: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        // Retrieve arguments
        arguments?.let {
            data = it.getParcelable(EXTRA_IMAGE_URI)
            resultCataract = it.getString(EXTRA_RESULT)
            conclusion = it.getString(EXTRA_CONCLUSION)
        }

        // Find and set views
        val resultText = view.findViewById<TextView>(R.id.result_text)
        val conclusionText = view.findViewById<TextView>(R.id.conclusion_text)
        val imageView = view.findViewById<ImageView>(R.id.result_image)

        resultText.text = resultCataract
        conclusionText.text = conclusion
        imageView.setImageURI(data)

        return view
    }

    companion object {
        private const val EXTRA_IMAGE_URI = "extra_image_uri"
        private const val EXTRA_RESULT = "result"
        private const val EXTRA_CONCLUSION = "conclusion"

        fun newInstance(data: Uri?, result: String?, conclusion: String?): ResultFragment {
            val fragment = ResultFragment()
            val args = Bundle()
            args.putParcelable(EXTRA_IMAGE_URI, data)
            args.putString(EXTRA_RESULT, result)
            args.putString(EXTRA_CONCLUSION, conclusion)
            fragment.arguments = args
            return fragment
        }
    }
}