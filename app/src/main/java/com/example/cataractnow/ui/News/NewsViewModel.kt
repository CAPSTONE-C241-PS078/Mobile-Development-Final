// NotificationsViewModel.kt
package com.example.cataractnow.ui.News

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cataractnow.data.Article
import com.example.cataractnow.response.NewsResponse
import com.example.cataractnow.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsViewModel : ViewModel() {

    private val _articleList = MutableLiveData<List<Article>>()
    val articleList: LiveData<List<Article>> get() = _articleList

    init {
        fetchArticles()
    }

    private fun fetchArticles() {
        val call = ApiConfig.getApiService2().getNews()
        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val articlesResponse = response.body()
                    val articles = articlesResponse?.articles ?: emptyList()

                    val mappedArticles = articles.mapNotNull { articleItem ->
                        if (!articleItem?.title.isNullOrEmpty() && !articleItem?.url.isNullOrEmpty()) {
                            Article(articleItem?.title!!, articleItem.url!!)
                        } else {
                            null
                        }
                    }

                    _articleList.postValue(mappedArticles)
                } else {
                    _articleList.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.e("API_CALL", "Permintaan gagal: ${t.message}", t)
                _articleList.postValue(emptyList())
            }
        })
    }

}
