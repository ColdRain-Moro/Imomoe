package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewStub
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityFavoriteBinding
import com.skyd.imomoe.view.adapter.decoration.AnimeEpisodeItemDecoration
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover8Proxy
import com.skyd.imomoe.viewmodel.FavoriteViewModel

class FavoriteActivity : BaseActivity<ActivityFavoriteBinding>() {
    private val viewModel: FavoriteViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy { VarietyAdapter(mutableListOf(AnimeCover8Proxy())) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
            atbFavoriteActivity.setBackButtonClickListener { finish() }

            srlFavoriteActivity.setOnRefreshListener { viewModel.getFavoriteData() }
            rvFavoriteActivity.layoutManager = GridLayoutManager(this@FavoriteActivity, 3)
            rvFavoriteActivity.adapter = adapter
            rvFavoriteActivity.addItemDecoration(AnimeEpisodeItemDecoration())
        }

        viewModel.mldFavoriteList.observe(this) {
            mBinding.srlFavoriteActivity.isRefreshing = false
            if (it != null) {
                if (it.isEmpty()) showLoadFailedTip(getString(R.string.no_favorite))
                adapter.dataList = it
            }
        }

        mBinding.srlFavoriteActivity.isRefreshing = true
        viewModel.getFavoriteData()
    }

    override fun getBinding(): ActivityFavoriteBinding =
        ActivityFavoriteBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutFavoriteActivityNoFavorite

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeSkin() {
        super.onChangeSkin()
        adapter.notifyDataSetChanged()
    }
}

