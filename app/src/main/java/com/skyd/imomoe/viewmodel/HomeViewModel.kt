package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.HomeModel
import com.skyd.imomoe.model.interfaces.IHomeModel
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.adapter.SerializableRecycledViewPool


class HomeViewModel : ViewModel() {
    private val homeModel: IHomeModel by lazy {
        DataSourceManager.create(IHomeModel::class.java) ?: HomeModel()
    }
    val viewPool = SerializableRecycledViewPool()
    var allTabList: MutableList<TabBean> = ArrayList()
    var mldGetAllTabList: MutableLiveData<Boolean> = MutableLiveData()

    fun getAllTabData() {
        request(request = { homeModel.getAllTabData() }, success = {
            allTabList.clear()
            allTabList.addAll(it)
            mldGetAllTabList.postValue(true)
        }, error = {
            allTabList.clear()
            mldGetAllTabList.postValue(false)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast(Toast.LENGTH_LONG)
        })
    }
}