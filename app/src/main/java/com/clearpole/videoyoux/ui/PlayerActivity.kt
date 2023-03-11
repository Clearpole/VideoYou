package com.clearpole.videoyoux.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.clearpole.videoyoux.ui.theme.VideoYouTheme
import com.drake.serialize.intent.bundle
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer


class PlayerActivity : ComponentActivity() {

    private val url: String by bundle()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoYouTheme(hideBar = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
                    AndroidView(factory = {
                        StandardGSYVideoPlayer(it).apply {
                            val gsyVideoOption = GSYVideoOptionBuilder()
                            val videoOptionModel = VideoOptionModel(
                                IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 50
                            )
                            val list: MutableList<VideoOptionModel> = ArrayList()
                            list.add(videoOptionModel)
                            list.add(
                                VideoOptionModel(
                                    IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                                    "mediacodec",
                                    1
                                )
                            )
                            GSYVideoManager.instance().optionModelList = list
                            gsyVideoOption.setUrl(url).build(this)
                        }
                    })
                }
            }
        }
    }
}
