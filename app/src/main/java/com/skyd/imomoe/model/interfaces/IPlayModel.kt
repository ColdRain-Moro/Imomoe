package com.skyd.imomoe.model.interfaces

import android.app.Activity
import com.skyd.imomoe.bean.AnimeEpisodeDataBean
import com.skyd.imomoe.bean.ImageBean
import com.skyd.imomoe.bean.PlayBean

/**
 * 获取播放界面数据的接口
 */
interface IPlayModel : IBase {
    @Deprecated("This function will cause a memory leak!!!")
    fun setActivity(activity: Activity)

    fun clearActivity()

    /**
     * ！！！暂未使用此方法！！！。获取弹幕数据，包括弹幕地址和发送弹幕参数。显示弹幕需重写此方法，否则无需重写
     *
     * @return HashMap<String, String>，可为null。弹幕数据HashMap
     */
    suspend fun getDanmakuData(): HashMap<String, String>? {
        return null
    }

    /**
     * 获取此部番剧封面
     *
     * @param detailPartUrl 页面部分url，不为null
     * @return ImageBean，可为null。番剧封面
     */
    suspend fun getAnimeCoverImageBean(detailPartUrl: String): ImageBean?

    /**
     * 获取播放页面相关数据
     *
     * @param partUrl              页面部分url，不为null
     * @param animeEpisodeDataBean 此集番剧数据，不为null，直接对此引用进行数据设置，不要更改此变量指向的对象
     * @return Triple，不可为null
     * ArrayList<Any>：播放页下方数据ArrayList，不为null；
     * ArrayList<AnimeEpisodeDataBean>：番剧集数列表，不为null；
     * PlayBean：此集番剧数据，不为null
     */
    suspend fun getPlayData(
        partUrl: String,
        animeEpisodeDataBean: AnimeEpisodeDataBean
    ): Triple<ArrayList<Any>, ArrayList<AnimeEpisodeDataBean>, PlayBean>

    /**
     * 获取partUrl页面对应番剧的下载地址
     *
     * @param partUrl  页面部分url，不为null
     * @return String，可为null。此页面播放的视频地址
     */
    suspend fun getAnimeDownloadUrl(partUrl: String): String?

    /**
     * 播放另一集，获取传入partUrl页面对应的视频的数据
     *
     * @param partUrl 页面部分url，不为null
     * @return AnimeEpisodeDataBean，可为null。partUrl页面对应的视频的数据Bean
     */
    suspend fun playAnotherEpisode(partUrl: String): AnimeEpisodeDataBean?

    companion object {
        const val implName = "PlayModel"
    }
}