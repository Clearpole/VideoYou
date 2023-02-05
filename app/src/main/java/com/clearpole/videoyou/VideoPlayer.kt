package com.clearpole.videoyou

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.EncodeUtils.base64Encode
import com.clearpole.videoyou.VideoPlayer.VideoType.LOCAL
import com.clearpole.videoyou.VideoPlayer.VideoType.STREAM
import com.clearpole.videoyou.VideoPlayer.VideoType.WEBDAV
import com.clearpole.videoyou.code.VideoPlayerGestureListener
import com.clearpole.videoyou.databinding.ActivityVideoPlayerBinding
import com.clearpole.videoyou.model.VideoModel
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.objects.VideoPlayerObjects
import com.clearpole.videoyou.utils.SettingsItemsUntil
import com.clearpole.videoyou.utils.SubStringX.Companion.subStringX
import com.clearpole.videoyou.utils.TimeParse.Companion.timeParse
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.R.style.MaterialAlertDialog_Material3
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.hjq.toast.ToastUtils
import com.tencent.mmkv.MMKV
import com.thegrizzlylabs.sardineandroid.DavResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("UNUSED_EXPRESSION")
class VideoPlayer : BaseActivity<ActivityVideoPlayerBinding>() {
    private lateinit var player: ExoPlayer
    private var firstLod = true

    object VideoType {
        const val LOCAL = "LOCAL"
        const val STREAM = "STREAM"
        const val WEBDAV = "WEBDAV"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * binding相关操作
         */
        if (SettingsItemsUntil.readSettingData("isScreenOn").toBoolean()) {
            binding.videoView.keepScreenOn = true
        }
        binding.lifecycleOwner = this
        binding.videoModel = VideoModel()

        /**
         * 相关UI操作
         */

        uiStart()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (firstLod) {
            /**
             * 播放器组件
             */
            try {
                player = ExoPlayer.Builder(this).build()
                val list = VideoPlayObjects.list
                when (VideoPlayObjects.type) {
                    LOCAL -> {
                        list.forEachIndexed { _, s ->
                            player.addMediaItem(MediaItem.fromUri(s.toString()))
                        }
                    }

                    STREAM -> {
                        list.forEachIndexed { _, s ->
                            player.addMediaItem(MediaItem.fromUri(Uri.parse(s.toString())))
                        }
                    }

                    WEBDAV -> {
                        val username = intent.getStringExtra("username")
                        val password = intent.getStringExtra("password")

                        val httpDataSourceFactory =
                            DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
                        val dataSourceFactory = DataSource.Factory {
                            val dataSource = httpDataSourceFactory.createDataSource()
                            dataSource.setRequestProperty(
                                "Authorization",
                                "Basic " + base64Encode("$username:$password").decodeToString()
                            )
                            dataSource
                        }
                        player = ExoPlayer.Builder(this)
                            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory)).build()
                            .apply {
                                val kv = MMKV.mmkvWithID("WebDav")
                                VideoPlayObjects.list.forEachIndexed { _, davResource ->
                                    val uri =
                                        kv.decodeString("WebDavIpRoot") + (davResource as DavResource).path.subStringX(
                                            "/",
                                            null
                                        )
                                    addMediaItem(MediaItem.fromUri(uri))
                                }
                            }

                    }
                }
                binding.videoView.player = player
                player.seekToDefaultPosition(VideoPlayerObjects.newItem)
            } catch (e: Exception) {
                ToastUtils.show(e.message)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    finish()
                }
            } finally {
                player.prepare()
                if (SettingsItemsUntil.readSettingData("isAutoExit").toBoolean().not()) {
                    player.repeatMode = Player.REPEAT_MODE_ALL
                }
                var isFirst = true
                player.addListener(object : Player.Listener {
                    @SuppressLint("SwitchIntDef")
                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        /**
                         * 切换视频
                         */
                    }

                    @SuppressLint("ResourceType")
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        when (playbackState) {
                            Player.STATE_READY -> {
                                if (VideoPlayerObjects.videoWidth == 0 || VideoPlayerObjects.videoHeight != player.videoSize.height) {
                                    VideoPlayerObjects.videoHeight = player.videoSize.height
                                    VideoPlayerObjects.videoWidth = player.videoSize.width
                                }
                                binding.videoPlayerTopBarRoot.videoPlayerVideoSliderAllText.text =
                                    timeParse(player.duration).toString()
                                binding.videoPlayerBottomBarRoot.videoPlayerVideoSlider.valueTo =
                                    player.duration.toFloat()
                                val uri = VideoPlayObjects.list[player.currentMediaItemIndex].toString()
                                val title =
                                    uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."))
                                binding.videoPlayerTopBarRoot.title.text = title
                                if (isFirst) {
                                    player.playWhenReady = true
                                    binding.videoPlayerTopBarRoot.videoPlayerVideoSliderAllText.text =
                                        timeParse(player.duration).toString()
                                    VideoPlayerObjects.duration = player.duration
                                    VideoPlayerGestureListener.gestureListener(
                                        this@VideoPlayer, binding, resources
                                    )
                                    // 开启手势监听
                                    binding.videoModel?.allProgressFloat = player.duration.toFloat()

                                    binding.videoModel?.pauseImg = Drawable.createFromXml(
                                        resources, resources.getXml(R.drawable.baseline_pause_24)
                                    )
                                    // 设置pause icon
                                    binding.videoModel?.screenImg = Drawable.createFromXml(
                                        resources, resources.getXml(R.drawable.baseline_fullscreen_24)
                                    )
                                    // 设置screen control icon
                                    CoroutineScope(Dispatchers.IO).launch {
                                        //开启协程
                                        var nowProgress: Long
                                        while (true) {
                                            withContext(Dispatchers.Main) {
                                                nowProgress = player.currentPosition
                                                if (!VideoPlayerObjects.isMove) {
                                                    binding.videoPlayerTopBarRoot.videoPlayerVideoSliderNowText.text =
                                                        timeParse(nowProgress).toString()
                                                    binding.videoPlayerBottomBarRoot.videoPlayerVideoSlider.value =
                                                        nowProgress.toFloat()
                                                }
                                            }
                                            delay(500)
                                        }
                                    }
                                    isFirst = false
                                }
                                binding.videoPlayerAssemblyRoot.isPlayLodRoot.visibility = View.GONE
                            }

                            Player.STATE_BUFFERING -> {
                                binding.videoPlayerAssemblyRoot.isPlayLodRoot.visibility = View.VISIBLE
                            }

                            Player.STATE_ENDED -> {
                                if (SettingsItemsUntil.readSettingData("isAutoExit").toBoolean()) {
                                    VideoPlayerObjects.isAutoFinish = true
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        finish()
                                    }
                                }
                            }

                            Player.STATE_IDLE -> {
                                ToastUtils.show("播放出错")
                            }
                        }
                    }
                })
            }
            firstLod = false
        }
    }
    @Suppress("DEPRECATION")
    @SuppressLint("ResourceType")
    private fun uiStart() {
        ImmersionBar.with(this).transparentBar().hideBar(BarHide.FLAG_HIDE_BAR).init()
        VideoModel.videoTitle = VideoPlayObjects.title
        binding.videoPlayerBottomBarRoot.videoPlayerVideoSlider.setLabelFormatter { value: Float ->
            return@setLabelFormatter timeParse(value.toLong()).toString()
        }
        VideoPlayerObjects.isAutoFinish = false
        binding.videoPlayerBottomBarRoot.videoPlayerPause.setOnClickListener {
            if (!binding.videoView.player!!.isPlaying) {
                val draw = Drawable.createFromXml(
                    resources, resources.getXml(R.drawable.baseline_pause_24)
                )
                binding.videoView.player?.play()
                binding.videoModel?.pauseImg = draw
                binding.videoPlayerAssemblyRoot.isPlayPauseRoot.visibility = View.GONE
                binding.videoPlayerAssemblyRoot.isPlayPause.setImageDrawable(draw)
            } else {
                binding.videoView.player?.pause()
                val draw = Drawable.createFromXml(
                    resources, resources.getXml(R.drawable.baseline_play_arrow_24)
                )
                binding.videoPlayerAssemblyRoot.isPlayPauseRoot.visibility = View.VISIBLE
                binding.videoPlayerAssemblyRoot.isPlayPause.setImageDrawable(draw)
                binding.videoModel?.pauseImg = draw
            }
        }
        binding.videoPlayerTopBarRoot.videoPlayerTopBarBack.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.finish()
            }
        }
        binding.videoPlayerBottomBarRoot.videoPlayerBottomBar.setOnClickListener {
            true
        }
        binding.videoPlayerBottomBarRoot.videoPlayerLest.setOnClickListener {
            try {
                MediaMetadataRetriever().setDataSource(VideoPlayObjects.list[player.currentMediaItemIndex - 1].toString())
                player.seekToDefaultPosition(player.currentMediaItemIndex - 1)
            } catch (_: Exception) {
                player.seekToDefaultPosition(0)
            }
        }
        binding.videoPlayerBottomBarRoot.videoPlayerNext.setOnClickListener {
            try {
                player.seekToDefaultPosition(player.currentMediaItemIndex + 1)
            } catch (_: Exception) {
                player.seekToDefaultPosition(0)
            }
        }

        binding.videoPlayerBottomBarRoot.videoPlayerScreen.setOnClickListener {
            if (VideoPlayerObjects.isInFullScreen) {
                ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init()
                this.window.clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                VideoPlayerObjects.isInFullScreen = false
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                binding.videoModel?.screenImg = Drawable.createFromXml(
                    resources, resources.getXml(R.drawable.baseline_fullscreen_24)
                )
                val param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    4.0f
                )
                binding.videoPlayerTopRoot.layoutParams = param
                binding.videoPlayerBottomRoot.layoutParams = param
            } else {
                VideoPlayerObjects.isInFullScreen = true
                this.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                binding.videoModel?.screenImg = Drawable.createFromXml(
                    resources, resources.getXml(R.drawable.baseline_fullscreen_exit_24)
                )
                val param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.5f
                )
                binding.videoPlayerTopRoot.layoutParams = param
                binding.videoPlayerBottomRoot.layoutParams = param
            }
        }
        binding.videoPlayerBottomBarRoot.videoPlayerPic.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val builder = PictureInPictureParams.Builder()
                val rational =
                    Rational(VideoPlayerObjects.videoWidth, VideoPlayerObjects.videoHeight)
                builder.setAspectRatio(rational)
                this@VideoPlayer.enterPictureInPictureMode(builder.build())
            } else {
                ToastUtils.show("您的系统版本不支持画中画")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun finish() {
        if (SettingsItemsUntil.readSettingData("isDialogPlayer").toBoolean()) {
            if (!VideoPlayerObjects.isAutoFinish) {
                MaterialAlertDialogBuilder(
                    this,
                    com.google.android.material.R.style.MaterialAlertDialog_Material3_Title_Text_CenterStacked
                ).setTitle("退出播放").setMessage("您确定要退出播放？还是进入小窗？")
                    .setPositiveButton("取消") { _, _ -> }.setNegativeButton("进入小窗") { _, _ ->
                        Log.w("小窗宽度", VideoPlayerObjects.videoWidth.toString())
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            val builder = PictureInPictureParams.Builder()
                            val rational = Rational(
                                VideoPlayerObjects.videoWidth, VideoPlayerObjects.videoHeight
                            )
                            builder.setAspectRatio(rational)
                            this.enterPictureInPictureMode(builder.build())
                        } else {
                            ToastUtils.show("您的系统版本不支持画中画")
                        }
                    }.setNeutralButton("退出播放") { _, _ ->
                        player.release()
                        VideoPlayerObjects.isFirstLod = true
                        VideoPlayerObjects.isAutoFinish = true
                        super.finish()
                    }.show()
            } else {
                VideoPlayerObjects.isAutoFinish = true
                super.finish()
            }
        } else {
            player.release()
            VideoPlayerObjects.isFirstLod = true
            VideoPlayerObjects.isAutoFinish = true
            super.finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUserLeaveHint() {
        if (SettingsItemsUntil.readSettingData("isAutoPicture").toBoolean()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val builder = PictureInPictureParams.Builder()
                val rational =
                    Rational(VideoPlayerObjects.videoWidth, VideoPlayerObjects.videoHeight)
                builder.setAspectRatio(rational)
                this.enterPictureInPictureMode(builder.build())
            }
        } else {
            VideoPlayerObjects.isIntoHome = true
            player.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (VideoPlayerObjects.isIntoHome) {
            player.play()
            VideoPlayerObjects.isIntoHome = false
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean, newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            findViewById<RelativeLayout>(R.id.video_player_control_root).visibility = View.GONE
        } else {
            findViewById<RelativeLayout>(R.id.video_player_control_root).visibility = View.VISIBLE
        }
    }

}