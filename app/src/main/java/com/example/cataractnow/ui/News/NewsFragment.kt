// NotificationsFragment.kt
package com.example.cataractnow.ui.News

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cataractnow.databinding.FragmentNewsBinding

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsViewModel
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        articleAdapter = ArticleAdapter()

        binding.progressBar.visibility = View.VISIBLE // Menampilkan ProgressBar

        binding.rvStory.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.articleList.observe(viewLifecycleOwner) { articles ->
            articleAdapter.submitList(articles)
            binding.progressBar.visibility = View.GONE // Menghilangkan ProgressBar ketika data sudah dimuat
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
