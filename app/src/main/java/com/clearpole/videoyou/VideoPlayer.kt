package com.clearpole.videoyou

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
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
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.EncodeUtils.base64Encode
import com.clearpole.videoyou.code.VideoPlayerGestureListener
import com.clearpole.videoyou.databinding.ActivityVideoPlayerBinding
import com.clearpole.videoyou.model.VideoModel
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.objects.VideoPlayerObjects
import com.clearpole.videoyou.utils.SettingsItemsUntil
import com.clearpole.videoyou.utils.TimeParse.Companion.timeParse
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep


@Suppress("UNUSED_EXPRESSION")
class VideoPlayer : BaseActivity<ActivityVideoPlayerBinding>() {
    private lateinit var player: ExoPlayer
    private var firstLod = true

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceType", "LongLogTag", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityVideoPlayerBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_video_player)
        binding.lifecycleOwner = this
        binding.videoModel = VideoModel()
        ImmersionBar.with(this).transparentBar().hideBar(BarHide.FLAG_HIDE_BAR).init()
        // 沉浸状态栏
        VideoModel.videoTitle = VideoPlayObjects.title
        // 设置标题
        binding.videoPlayerBottomBarRoot.videoPlayerVideoSlider.setLabelFormatter { value: Float ->
            return@setLabelFormatter timeParse(value.toLong()).toString()
        }
        // 设置拖动条标签文本
        VideoPlayerObjects.isAutoFinish = false
        binding.videoPlayerBottomBarRoot.videoPlayerPauseRoot.setOnClickListener {
            if (!binding.videoView.player!!.isPlaying) {
                val draw = Drawable.createFromXml(
                    resources,
                    resources.getXml(R.drawable.baseline_pause_24)
                )
                binding.videoView.player?.play()
                binding.videoModel?.pauseImg = draw
                binding.videoPlayerAssemblyRoot.isPlayPauseRoot.visibility = View.GONE
                binding.videoPlayerAssemblyRoot.isPlayPause.setImageDrawable(draw)
            } else {
                binding.videoView.player?.pause()
                val draw = Drawable.createFromXml(
                    resources,
                    resources.getXml(R.drawable.baseline_play_arrow_24)
                )
                binding.videoPlayerAssemblyRoot.isPlayPauseRoot.visibility = View.VISIBLE
                binding.videoPlayerAssemblyRoot.isPlayPause.setImageDrawable(draw)
                binding.videoModel?.pauseImg = draw
            }
        }
        binding.videoPlayerTopBarRoot.videoPlayerTopBarBack.setOnClickListener {
            finish()
        }
        // 设置播放/暂停
        binding.videoPlayerBottomBarRoot.videoPlayerBottomBar.setOnClickListener {
            true
        }
        binding.videoPlayerBottomBarRoot.videoPlayerScreenRoot.setOnClickListener {
            if (VideoPlayerObjects.isInFullScreen) {
                this.window.clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                // 设置播放器全屏
                VideoPlayerObjects.isInFullScreen = false
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                binding.videoModel?.screenImg = Drawable.createFromXml(
                    resources,
                    resources.getXml(R.drawable.baseline_fullscreen_24)
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
                // 设置播放器全屏
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                binding.videoModel?.screenImg = Drawable.createFromXml(
                    resources,
                    resources.getXml(R.drawable.baseline_fullscreen_exit_24)
                )
                val param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2.0f
                )
                binding.videoPlayerTopRoot.layoutParams = param
                binding.videoPlayerBottomRoot.layoutParams = param
            }
        }
        // 设置全屏/取消全屏
        binding.videoPlayerBottomBarRoot.videoPlayerPictureRoot.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val builder = PictureInPictureParams.Builder()
                val rational =
                    Rational(VideoPlayerObjects.videoWidth, VideoPlayerObjects.videoHeight)
                builder.setAspectRatio(rational)
                this.enterPictureInPictureMode(builder.build())
            } else {
                ToastUtils.show("您的系统版本不支持画中画")
            }
        }
        // 设置画中画
        try {
            player = ExoPlayer.Builder(this).build()
            if (intent.getStringExtra("webPath").isNullOrEmpty()) {
                binding.videoView.player = player
                when (VideoPlayObjects.type) {
                    "LOCAL" -> {
                        player.addMediaItem(MediaItem.fromUri(VideoPlayObjects.paths))
                        // 如果是本地就载入本地视频路径
                    }

                    "INTERNET" -> {
                        player.addMediaItem(MediaItem.fromUri(Uri.parse(VideoPlayObjects.paths)))
                        // 如果是网络就载入网络视频路径
                    }
                }
            } else {
                val username = intent.getStringExtra("username")
                val password = intent.getStringExtra("password")
                val webPath = intent.getStringExtra("webPath")

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
                    .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
                    .build().apply {
                        setMediaItem(MediaItem.fromUri(webPath.toString()))
                        prepare()
                    }
                binding.videoView.player = player
            }
        } catch (e: Exception) {
            ToastUtils.show(e.message)
            finish()
            // 捕捉到错误就停止播放
        } finally {
            player.prepare()
            var isFirst = true
            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when (playbackState) {
                        Player.STATE_READY -> {
                            if (VideoPlayerObjects.videoWidth == 0 || VideoPlayerObjects.videoHeight != player.videoSize.height) {
                                VideoPlayerObjects.videoHeight = player.videoSize.height
                                VideoPlayerObjects.videoWidth = player.videoSize.width
                            }
                            if (isFirst) {
                                player.playWhenReady = true
                                VideoPlayerGestureListener.gestureListener(
                                    this@VideoPlayer,
                                    binding,
                                    resources
                                )
                                // 开启手势监听
                                binding.videoModel?.allProgressString =
                                    timeParse(player.duration).toString()
                                // 全部时长
                                binding.videoModel?.allProgressFloat = player.duration.toFloat()
                                // 全部时长
                                binding.videoModel?.pauseImg =
                                    Drawable.createFromXml(
                                        resources,
                                        resources.getXml(R.drawable.baseline_pause_24)
                                    )
                                // 设置pause icon
                                binding.videoModel?.screenImg = Drawable.createFromXml(
                                    resources,
                                    resources.getXml(R.drawable.baseline_fullscreen_24)
                                )
                                val allProgress = player.duration
                                // 设置screen control icon
                                CoroutineScope(Dispatchers.IO).launch {
                                    //开启协程
                                    var nowProgress = 0L
                                    while (true) {
                                        withContext(Dispatchers.Main) {
                                            nowProgress = player.currentPosition
                                        }
                                        if (!VideoPlayerObjects.isMove && nowProgress <= allProgress) {
                                            binding.videoModel?.nowProgressString =
                                                timeParse(nowProgress).toString()
                                            binding.videoModel?.nowProgressLong = nowProgress
                                        }
                                        sleep(500)
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
                            VideoPlayerObjects.isAutoFinish = true
                            player.release()
                            finish()
                        }

                        Player.STATE_IDLE -> {

                        }
                    }
                }
            })
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (firstLod) {
            firstLod = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun finish() {
        if (SettingsItemsUntil.readSettingData("isDialogPlayer").toBoolean()) {
            if (!VideoPlayerObjects.isAutoFinish) {
                MaterialAlertDialogBuilder(
                    this,
                    com.google.android.material.R.style.MaterialAlertDialog_Material3_Title_Text_CenterStacked
                )
                    .setTitle("退出播放")
                    .setMessage("您确定要退出播放？还是进入小窗？")
                    .setPositiveButton("取消") { _, _ -> }
                    .setNegativeButton("进入小窗") { _, _ ->
                        Log.w("小窗宽度", VideoPlayerObjects.videoWidth.toString())
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            val builder = PictureInPictureParams.Builder()
                            val rational =
                                Rational(
                                    VideoPlayerObjects.videoWidth,
                                    VideoPlayerObjects.videoHeight
                                )
                            builder.setAspectRatio(rational)
                            this.enterPictureInPictureMode(builder.build())
                        } else {
                            ToastUtils.show("您的系统版本不支持画中画")
                        }
                    }
                    .setNeutralButton("退出播放") { _, _ ->
                        player.release()
                        VideoPlayerObjects.isFirstLod = true
                        VideoPlayerObjects.isAutoFinish = true
                        super.finish()
                    }
                    .show()
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
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            findViewById<RelativeLayout>(R.id.video_player_control_root).visibility = View.GONE
        } else {
            findViewById<RelativeLayout>(R.id.video_player_control_root).visibility = View.VISIBLE
        }
    }

}