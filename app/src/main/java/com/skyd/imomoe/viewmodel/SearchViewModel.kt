package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.SearchModel
import com.skyd.imomoe.model.interfaces.ISearchModel
import com.skyd.imomoe.util.showToast


class SearchViewModel : ViewModel() {
    private val searchModel: ISearchModel by lazy {
        DataSourceManager.create(ISearchModel::class.java) ?: SearchModel()
    }

    var searchHistoryList: List<Any> = ArrayList()
    var mldSearchResultList: MutableLiveData<List<Any>?> = MutableLiveData()
    var mldLoadMoreSearchResultList: MutableLiveData<List<Any>?> = MutableLiveData()
    var keyWord = ""
    var mldSearchHistoryList: MutableLiveData<List<Any>?> = MutableLiveData()
    var mldInsertCompleted: MutableLiveData<List<SearchHistoryBean>?> = MutableLiveData()
    var mldDeleteCompleted: MutableLiveData<SearchHistoryBean> = MutableLiveData()
    private var pageNumberBean: PageNumberBean? = null

    fun getSearchData(keyWord: String, partUrl: String = "") {
        request(request = { searchModel.getSearchData(keyWord, partUrl) }, success = {
            pageNumberBean = it.second
            this@SearchViewModel.keyWord = keyWord
            mldSearchResultList.postValue(it.first)
        }, error = {
            mldSearchResultList.postValue(null)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun loadMoreSearchData() {
        val partUrl = pageNumberBean?.actionUrl
        if (partUrl == null) {
            App.context.getString(R.string.no_more_info).showToast()
            mldLoadMoreSearchResultList.postValue(ArrayList())
            return
        }
        request(request = { searchModel.getSearchData(keyWord, partUrl) }, success = {
            pageNumberBean = it.second
            mldLoadMoreSearchResultList.postValue(it.first)
        }, error = {
            mldLoadMoreSearchResultList.postValue(null)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun getSearchHistoryData() {
        request(request = {
            getAppDataBase().searchHistoryDao().getSearchHistoryList()
        }, success = {
            searchHistoryList = it
            mldSearchHistoryList.postValue(it)
        }, error = {
            mldSearchHistoryList.postValue(null)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun insertSearchHistory(searchHistoryBean: SearchHistoryBean) {
        request(request = {
            val list = getAppDataBase().searchHistoryDao().getSearchHistoryList().toMutableList()
            val index = list.indexOf(searchHistoryBean)
            if (index != -1) {
                list.removeAt(index)
                list.add(0, searchHistoryBean)
                getAppDataBase().searchHistoryDao().deleteSearchHistory(searchHistoryBean.title)
                getAppDataBase().searchHistoryDao().insertSearchHistory(searchHistoryBean)
            } else {
                list.add(0, searchHistoryBean)
                getAppDataBase().searchHistoryDao().insertSearchHistory(searchHistoryBean)
            }
            list
        }, success = {
            searchHistoryList = it
            mldInsertCompleted.postValue(it)
        }, error = {
            searchHistoryList = emptyList()
            mldInsertCompleted.postValue(null)
        })
    }

    fun deleteSearchHistory(searchHistoryBean: SearchHistoryBean) {
        request(request = {
            getAppDataBase().searchHistoryDao().deleteSearchHistory(searchHistoryBean.timeStamp)
        }, success = {
            searchHistoryList =
                searchHistoryList.toMutableList().apply { remove(searchHistoryBean) }
            mldDeleteCompleted.postValue(searchHistoryBean)
        }, error = {
            mldDeleteCompleted.postValue(null)
        })
    }
}