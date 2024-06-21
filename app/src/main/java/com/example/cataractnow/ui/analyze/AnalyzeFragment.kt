package com.example.cataractnow.ui.analyze

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cataractnow.R
import com.example.cataractnow.databinding.FragmentAnalyzeBinding
import com.example.cataractnow.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AnalyzeFragment : Fragment(), ImageClassifierHelper.ClassifierListener {

    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var currentImageUri: Uri? = null
    private var result: String? = null
    private var conclusion: String? = null
    private var firstCategory: String? = null
    private var secondCategory: String? = null
    private var thirdCategory: String? = null
    private var _binding: FragmentAnalyzeBinding? = null

    // Property only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Binding gallery button
        val buttonGallery = binding.btnGalery
        buttonGallery.setOnClickListener {
            startGallery()
        }

        // Binding analyze button
        val buttonAnalyze = binding.btnAnalyze

        val dashboardViewModel = ViewModelProvider(this).get(AnalyzeViewModel::class.java)
        val textView: TextView = binding.tvAnalyzetitle
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Initialize imageClassifierHelper
        imageClassifierHelper = ImageClassifierHelper(
            context = requireActivity(),
            classifierListener = this
        )

        buttonAnalyze.setOnClickListener {
            analyzeImage()
        }

        return root
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherIntentGallery.launch(intent)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg = result.data?.data
            if (selectedImg != null) {
                currentImageUri = selectedImg
                showImage()
                classifyImage()
            }
        }
    }

    private fun showImage() {
        val imageView = binding.previewImage
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            imageView.setImageURI(it)
        }

        val buttonAnalyze = binding.btnAnalyze
        buttonAnalyze.visibility = View.VISIBLE
    }

    private fun analyzeImage() {
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDateTime = dateFormat.format(currentDateTime)

        moveToResult(currentImageUri, result, conclusion)
    }

    private fun moveToResult(image: Uri?, resultFinal: String?, conclusion: String?) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.nav_host_fragment_activity_main2,
                ResultFragment.newInstance(image, resultFinal, conclusion)
            )
            .addToBackStack(null)
            .commit()
    }

    private fun classifyImage() {
        currentImageUri?.let {
            imageClassifierHelper.classifyStaticImage(it)
        }
    }

    override fun onError(error: String) {
        showToast(error)
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        results?.let {
            val sortedCategories = it[0].categories.sortedByDescending { it.score }
            if (sortedCategories.isNotEmpty()) {
                val displayResult = sortedCategories.joinToString("\n") {
                    "${it.label} " + NumberFormat.getPercentInstance().format(it.score).trim()
                }
                val nonCataractCategory = sortedCategories.firstOrNull { it.label.trim() == "Normal" }
                val halfCataractCategory = sortedCategories.firstOrNull { it.label.trim() == "Immature" }
                val cataractFully = sortedCategories.firstOrNull { it.label == "Mature" }

                if (cataractFully != null && halfCataractCategory != null && nonCataractCategory != null) {
                    val nonCataractScore = nonCataractCategory.score
                    val halfCataractScore = halfCataractCategory.score
                    val cataractFullyScore = cataractFully.score

                    conclusion =
                        if (nonCataractScore > cataractFullyScore && nonCataractScore > halfCataractScore) {
                            "Foto tersebut bukan merupakan katarak. Untuk diagnosa lebih lanjut, silahkan hubungi dokter terdekat"
                        } else if (halfCataractScore > nonCataractScore && halfCataractScore > cataractFullyScore) {
                            "Foto tersebut menunjukkan katarak yang tidak terlalu parah. Disarankan untuk berkonsultasi dengan dokter."
                        } else if (cataractFullyScore > nonCataractScore && cataractFullyScore > halfCataractScore) {
                            "Foto tersebut menunjukkan katarak parah. Segera hubungi dokter untuk penanganan lebih lanjut."
                        } else {
                            "Hasil analisis tidak konklusif. Silakan coba lagi atau hubungi dokter untuk pemeriksaan lebih lanjut."
                        }

                    firstCategory = "${nonCataractCategory.label} - ${NumberFormat.getPercentInstance().format(cataractFullyScore).trim()}"
                    secondCategory = "${halfCataractCategory.label} - ${NumberFormat.getPercentInstance().format(halfCataractScore).trim()}"
                    thirdCategory = "${cataractFully.label} - ${NumberFormat.getPercentInstance().format(nonCataractScore).trim()}"

                    result = displayResult
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
