@file:Suppress("DEPRECATION", "PreviewAnnotationInFunctionWithParameters") @file:OptIn(
    ExperimentalMaterial3Api::class
)

package com.clearpole.videoyou

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.clearpole.videoyou.model.PlayerSideSheetModel
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.objects.VideoPlayerObjects
import com.clearpole.videoyou.objects.VideoPlayerObjects.Companion.player
import com.clearpole.videoyou.objects.VideoPlayerObjects.Companion.slider
import com.clearpole.videoyou.ui.theme.VideoYouOptTheme
import com.clearpole.videoyou.utils.SettingsItemsUntil
import com.clearpole.videoyou.utils.SubStringX.Companion.subStringX
import com.clearpole.videoyou.utils.TimeParse
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.tencent.mmkv.MMKV
import com.thegrizzlylabs.sardineandroid.DavResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoPlayerActivity : ComponentActivity() {
    object VideoType {
        const val LOCAL = "LOCAL"
        const val STREAM = "STREAM"
        const val WEBDAV = "WEBDAV"
    }

    private val all = mutableStateOf(true)

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val kv = MMKV.mmkvWithID("theme")
        when (kv.decodeInt("theme")) {
            0 -> {
                DynamicColors.applyToActivityIfAvailable(this)
            }

            R.style.hzt -> {
                setTheme(R.style.hzt)
            }

            R.style.cxw -> {
                setTheme(R.style.cxw)
            }

            R.style.szy -> {
                setTheme(R.style.szy)
            }

            R.style.xfy -> {
                setTheme(R.style.xfy)
            }
        }
        if (SettingsItemsUntil.readSettingData("isScreenOn").toBoolean()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        VideoPlayerObjects.isCrash = false
        player = ExoPlayer.Builder(this).build()
        setContent {
            val loading = remember {
                mutableStateOf(true)
            }
            val speed = remember {
                mutableStateOf(false)
            }
            val pause = remember {
                mutableStateOf(false)
            }
            val toolBar = remember {
                mutableStateOf(false)
            }
            val nowPosition = remember {
                mutableStateOf(0f)
            }
            val allPosition = remember {
                mutableStateOf(0f)
            }
            val lock = remember {
                mutableStateOf(false)
            }
            val lockIcon = remember {
                mutableStateOf(R.drawable.baseline_lock_open_24)
            }
            val seekTo = remember {
                mutableStateOf(0F)
            }
            val seeking = remember {
                mutableStateOf(false)
            }
            val volumeTo = remember {
                mutableStateOf(0F)
            }
            val voluming = remember {
                mutableStateOf(false)
            }
            val brightTo = remember {
                mutableStateOf(0F)
            }
            val brightIng = remember {
                mutableStateOf(false)
            }
            val screen = remember {
                mutableStateOf(false)
            }
            val sideSheet = remember {
                mutableStateOf(false)
            }
            val volume = remember {
                mutableStateOf(60f)
            }
            VideoYouOptTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    VideoView(
                        this,
                        loading,
                        nowPosition,
                        allPosition,
                        seekTo,
                        pause,
                        screen,
                        resources,
                        kv.decodeInt("theme"),
                        volume
                    )
                    Control(
                        this,
                        loading,
                        speed,
                        pause,
                        toolBar,
                        nowPosition,
                        allPosition,
                        lock,
                        lockIcon,
                        seekTo,
                        seeking,
                        brightTo,
                        brightIng,
                        volumeTo,
                        voluming,
                        all,
                        screen,
                        sideSheet,
                        resources,
                        kv.decodeInt("theme"),
                        volume
                    )
                    SideSheet(kv.decodeInt("theme"), screen, sideSheet, volume)
                }
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish(this, player)
        }
        return true
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
            VideoPlayerObjects.pausing = true
            player.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (VideoPlayerObjects.isIntoHome) {
            player.play()
            VideoPlayerObjects.pausing = false
            VideoPlayerObjects.isIntoHome = false
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean, newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        all.value = !isInPictureInPictureMode
    }
}

@SuppressLint("ComposableNaming", "InflateParams")
@Composable
fun SideSheet(
    theme: Int,
    screen: MutableState<Boolean>,
    sideSheet: MutableState<Boolean>,
    volume: MutableState<Float>
) {
    val speed = remember {
        mutableStateOf(2.0f)
    }
    val play = remember {
        mutableStateOf(ExoPlayer.REPEAT_MODE_ALL)
    }
    AnimatedVisibility(
        visible = sideSheet.value,
        enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        sideSheet.value = false
                    })
                }, horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(if (screen.value) 0.7f else 2.5f, true)
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.8f))
                        .fillMaxSize()
                ) {
                    if (screen.value) {
                        Spacer(modifier = Modifier.width(25.dp))
                        Column(
                            modifier = if (screen.value) Modifier
                                .fillMaxSize()
                                .weight(1f, true) else Modifier
                                .width(0.dp)
                                .verticalScroll(
                                    rememberScrollState()
                                )
                        ) {
                            Text(
                                text = "倍速选项",
                                color = Color.White,
                                modifier = Modifier.padding(start = 15.dp, top = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "仅更改倍速速率，开启倍速请长按视频",
                                color = Color.White,
                                modifier = Modifier.padding(start = 15.dp),
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            val speedMode = arrayListOf(1.25f, 1.5f, 2f, 2.5f, 3f, 4f, 5f, 7f, 10f)
                            LazyRow {
                                items(speedMode.size) {
                                    Spacer(modifier = Modifier.width(10.dp))
                                    FilterChip(selected = true,
                                        onClick = {
                                            speed.value = speedMode[it]
                                            VideoPlayerObjects.speed = speedMode[it]
                                        },
                                        label = { Text(text = "${speedMode[it]}x") },
                                        leadingIcon = if (speed.value == speedMode[it]) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Filled.Done,
                                                    contentDescription = "Localized Description",
                                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                )
                                            }
                                        } else {
                                            null
                                        })
                                }
                            }

                            Text(
                                text = "循环选项",
                                color = Color.White,
                                modifier = Modifier.padding(start = 15.dp, top = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "列表播放是不循环，播放完毕即结束",
                                color = Color.White,
                                modifier = Modifier.padding(start = 15.dp),
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            val playMode = arrayListOf("列表播放", "列表循环", "单片循环")
                            LazyRow {
                                items(playMode.size) {
                                    val mode = when (playMode[it]) {
                                        "列表播放" -> {
                                            ExoPlayer.REPEAT_MODE_OFF
                                        }

                                        "列表循环" -> {
                                            ExoPlayer.REPEAT_MODE_ALL
                                        }

                                        "单片循环" -> {
                                            ExoPlayer.REPEAT_MODE_ONE
                                        }

                                        else -> {
                                            ExoPlayer.REPEAT_MODE_ALL
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    FilterChip(selected = true,
                                        onClick = {
                                            VideoPlayerObjects.playMode = mode
                                            play.value = mode
                                            player.repeatMode = VideoPlayerObjects.playMode
                                        },
                                        label = { Text(text = playMode[it]) },
                                        leadingIcon = if (play.value == mode) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Filled.Done,
                                                    contentDescription = "Localized Description",
                                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                )
                                            }
                                        } else {
                                            null
                                        },
                                        colors = FilterChipDefaults.filterChipColors())
                                }
                            }

                            Text(
                                text = "音量增益",
                                color = Color.White,
                                modifier = Modifier.padding(start = 15.dp, top = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "设置播放时的音量增益..而不是手机音量",
                                color = Color.White,
                                modifier = Modifier.padding(start = 15.dp),
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Slider(value = volume.value, onValueChange = {
                                player.volume = it
                                volume.value = it
                            }, colors = SliderDefaults.colors(), valueRange = 0f..1f)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(0.8f, true),
                        verticalArrangement = Arrangement.Center
                    ) {
                        AndroidView(modifier = Modifier.fillMaxWidth(), factory = {
                            LayoutInflater.from(it).inflate(R.layout.compose_videolist, null)
                                .apply {
                                    val rv = this.findViewById<RecyclerView>(R.id.list_side)
                                    VideoPlayerObjects.rv = rv
                                    rv.linear().setup {
                                        addType<PlayerSideSheetModel> { R.layout.play_list_videos_item }
                                    }.models =
                                        getPlayList(resources, theme, VideoPlayerObjects.newItem)
                                }
                        })
                    }
                    Spacer(modifier = Modifier.weight(0.1f))
                }
            }
        }
    }
}

private fun getPlayList(resources: Resources, theme: Int, item: Int): MutableList<Any> {
    return mutableListOf<Any>().apply {
        for (index in 0 until VideoPlayObjects.list.size) {
            add(
                PlayerSideSheetModel(
                    VideoPlayObjects.list[index].toString(), resources, item
                )
            )
        }
    }
}

@SuppressLint("InflateParams")
@Composable
fun Control(
    activity: Activity,
    loading: MutableState<Boolean>,
    speeding: MutableState<Boolean>,
    pausing: MutableState<Boolean>,
    toolBarIng: MutableState<Boolean>,
    nowPosition: MutableState<Float>,
    allPosition: MutableState<Float>,
    lock: MutableState<Boolean>,
    lockIcon: MutableState<Int>,
    seekTo: MutableState<Float>,
    seeking: MutableState<Boolean>,
    brightTo: MutableState<Float>,
    brightIng: MutableState<Boolean>,
    volumeTo: MutableState<Float>,
    voluming: MutableState<Boolean>,
    all: MutableState<Boolean>,
    screen: MutableState<Boolean>,
    sideSheet: MutableState<Boolean>,
    resources: Resources,
    theme: Int,
    volume: MutableState<Float>
) {
    val screenX = ScreenUtils.getAppScreenWidth()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(if (all.value) 1f else 0f)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .draggable(orientation = Orientation.Vertical,
                state = rememberDraggableState(onDelta = {
                    if (VideoPlayerObjects.fingerX < screenX / 2) {
                        val seek = (brightTo.value + it / 20)
                        brightTo.value = if (seek > 100) {
                            100f
                        } else if (seek < 0) {
                            0f
                        } else {
                            seek
                        }
                    } else {
                        val seek = (volumeTo.value + it / 20)
                        volumeTo.value = if (seek > 100) {
                            100f
                        } else if (seek < 0) {
                            0f
                        } else {
                            seek
                        }
                    }
                }),
                onDragStarted = {
                    if (VideoPlayerObjects.fingerX < screenX / 2) {
                        brightIng.value = true
                    } else {
                        voluming.value = true
                    }
                },
                onDragStopped = {
                    if (VideoPlayerObjects.fingerX < screenX / 2) {
                        brightIng.value = false
                    } else {
                        voluming.value = false
                    }
                })
            .draggable(orientation = Orientation.Horizontal,
                state = rememberDraggableState(onDelta = {
                    if (lock.value.not()) {
                        val seek = seekTo.value + it * 20
                        seekTo.value = if (seek > allPosition.value) {
                            allPosition.value
                        } else if (seek < 0) {
                            0f
                        } else {
                            seek
                        }
                        slider!!.value = seekTo.value
                    }
                }),
                onDragStarted = {
                    if (lock.value.not()) {
                        player.pause()
                        VideoPlayerObjects.isMove = true
                        seekTo.value = nowPosition.value
                        seeking.value = true
                    }
                },
                onDragStopped = {
                    if (lock.value.not()) {
                        player.seekTo(seekTo.value.toLong())
                        VideoPlayerObjects.isMove = false
                        seeking.value = false
                        VideoPlayerObjects.pausing = false
                        pausing.value = false
                    }
                })
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    val speed = VideoPlayerObjects.isSpeedMode
                    if (speed) {
                        player.setPlaybackSpeed(1f)
                        VideoPlayerObjects.isSpeedMode = false
                        speeding.value = false
                    } else {
                        player.setPlaybackSpeed(VideoPlayerObjects.speed)
                        VideoPlayerObjects.isSpeedMode = true
                        speeding.value = true
                    }
                }, onDoubleTap = {
                    if (lock.value.not()) {
                        if (VideoPlayerObjects.pausing) {
                            player.play()
                            pausing.value = false
                            VideoPlayerObjects.pausing = false
                        } else {
                            player.pause()
                            pausing.value = true
                            VideoPlayerObjects.pausing = true
                        }
                    }
                }, onTap = {
                    val toolBar = VideoPlayerObjects.isToolBarOpen
                    if (toolBar) {
                        VideoPlayerObjects.isToolBarOpen = false
                        toolBarIng.value = false
                        if (lock.value.not()) {
                            ImmersionBar
                                .with(activity)
                                .hideBar(BarHide.FLAG_HIDE_BAR)
                                .init()
                        }
                    } else {
                        VideoPlayerObjects.isToolBarOpen = true
                        toolBarIng.value = true
                        if (lock.value.not()) {
                            ImmersionBar
                                .with(activity)
                                .hideBar(BarHide.FLAG_SHOW_BAR)
                                .transparentBar()
                                .init()
                        }
                    }
                }, onPress = {
                    VideoPlayerObjects.fingerX = it.x.toLong()
                })
            }) {

        }
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .width(if (loading.value) 90.dp else 0.dp)
                .height(90.dp)
        )

        Box(
            modifier = if (toolBarIng.value && lock.value.not()) Modifier.fillMaxSize() else Modifier.height(
                0.dp
            )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f, true)
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.5f), Color.Transparent
                                )
                            )
                        )
                ) {}
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(2f)
                )
                Column(
                    modifier = Modifier
                        .weight(1f, true)
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent, Color.Black.copy(alpha = 0.5f)
                                )
                            )
                        )
                ) {}
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (VideoPlayerObjects.isInFullScreen) 90.dp else 110.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "退出",
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .align(Alignment.BottomStart)
                        .padding(start = 25.dp, bottom = 15.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { finish(activity, player) })
                        },
                    tint = Color.White
                )
                Text(
                    text = VideoPlayObjects.title,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 65.dp, bottom = 18.dp, end = 65.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "更多",
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .align(Alignment.BottomEnd)
                        .padding(end = 25.dp, bottom = 15.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                sideSheet.value = true
                                ImmersionBar
                                    .with(activity)
                                    .hideBar(BarHide.FLAG_HIDE_BAR)
                                    .init()
                                VideoPlayerObjects.rv?.scrollToPosition(VideoPlayerObjects.newItem)
                            })
                        },
                    tint = Color.White
                )
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .pointerInput(Unit) {
                    detectTapGestures { }
                }) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = TimeParse.timeParse(nowPosition.value.toLong()).toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .padding(start = 25.dp, top = 25.dp)
                            .align(Alignment.TopStart)
                    )
                    Text(
                        text = TimeParse.timeParse(allPosition.value.toLong()).toString(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 25.dp, top = 25.dp)
                            .align(Alignment.TopEnd)
                    )
                }
                AndroidView(factory = {
                    LayoutInflater.from(it).inflate(R.layout.compose_videoslider, null).apply {
                        val sliderView = findViewById<Slider>(R.id.video_slider)
                        sliderView.setLabelFormatter { value: Float ->
                            return@setLabelFormatter TimeParse.timeParse(value.toLong()).toString()
                        }
                        slider = sliderView
                        sliderView.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                            override fun onStartTrackingTouch(slider: Slider) {
                                VideoPlayerObjects.isMove = true
                                player.pause()
                            }

                            override fun onStopTrackingTouch(slider: Slider) {
                                player.seekTo(slider.value.toLong())
                                VideoPlayerObjects.isMove = false
                            }

                        })
                    }
                })
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (VideoPlayerObjects.isInFullScreen) 25.dp else 50.dp)
                ) {
                    if (VideoPlayerObjects.isInFullScreen) {
                        Row(modifier = Modifier
                            .weight(1f, true)
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        activity.enterPictureInPictureMode()
                                    }
                                })
                            }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_picture_in_picture_24),
                                tint = Color.White,
                                contentDescription = "小窗播放",
                                modifier = Modifier
                                    .align(
                                        Alignment.CenterVertically
                                    )
                                    .padding(start = 25.dp)
                            )
                            Text(
                                text = "小窗播放",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(
                                        start = 15.dp
                                    )
                                    .align(Alignment.CenterVertically)
                            )
                        }
                        Row(modifier = Modifier
                            .weight(1f, true)
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    sideSheet.value = true
                                })
                            }) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_speed_24),
                                tint = Color.White,
                                contentDescription = "倍速设置",
                                modifier = Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                            Text(
                                text = "倍速设置",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(
                                        start = 15.dp
                                    )
                                    .align(Alignment.CenterVertically)
                            )
                        }
                        val noVolume = remember {
                            mutableStateOf(false)
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f, true)
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(onTap = {
                                        if (noVolume.value){
                                            player.volume = 1f
                                            volume.value = 1f
                                            noVolume.value = false
                                        }else{
                                            volume.value = 0f
                                            player.volume = 0f
                                            noVolume.value = true
                                        }
                                    })
                                }
                        ) {
                            Icon(
                                painter = painterResource(id = if (noVolume.value) R.drawable.baseline_hearing_24 else R.drawable.outline_hearing_disabled_24),
                                tint = Color.White,
                                contentDescription = "静音播放",
                                modifier = Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                            Text(
                                text = if (noVolume.value) "开启声音" else "静音播放",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(
                                        start = 15.dp
                                    )
                                    .align(Alignment.CenterVertically)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f, true)
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                                tint = Color.White,
                                contentDescription = "上一首",
                                modifier = Modifier
                                    .align(
                                        Alignment.CenterVertically
                                    )
                                    .fillMaxSize()
                                    .weight(1f)
                                    .pointerInput(Unit) {
                                        detectTapGestures(onTap = {
                                            try {
                                                MediaMetadataRetriever().setDataSource(
                                                    VideoPlayObjects.list[player.currentMediaItemIndex - 1].toString()
                                                )
                                                player.seekToDefaultPosition(player.currentMediaItemIndex - 1)
                                            } catch (_: Exception) {
                                                player.seekToDefaultPosition(0)
                                            }
                                            pausing.value = false
                                        })
                                    })
                            Icon(painter = painterResource(id = if (pausing.value && VideoPlayerObjects.pausing) R.drawable.baseline_play_arrow_24 else R.drawable.baseline_pause_24),
                                tint = Color.White,
                                contentDescription = "暂停播放",
                                modifier = Modifier
                                    .align(
                                        Alignment.CenterVertically
                                    )
                                    .fillMaxSize()
                                    .weight(1f)
                                    .pointerInput(Unit) {
                                        detectTapGestures(onTap = {
                                            if (pausing.value) {
                                                player.play()
                                                pausing.value = false
                                                VideoPlayerObjects.pausing = false
                                            } else {
                                                player.pause()
                                                pausing.value = true
                                                VideoPlayerObjects.pausing = true
                                            }
                                        })
                                    })
                            Icon(painter = painterResource(id = R.drawable.baseline_skip_next_24),
                                tint = Color.White,
                                contentDescription = "下一首",
                                modifier = Modifier
                                    .align(
                                        Alignment.CenterVertically
                                    )
                                    .padding(end = 25.dp)
                                    .fillMaxSize()
                                    .weight(1f)
                                    .pointerInput(Unit) {
                                        detectTapGestures(onTap = {
                                            try {
                                                player.seekToDefaultPosition(player.currentMediaItemIndex + 1)
                                            } catch (_: Exception) {
                                                player.seekToDefaultPosition(0)
                                            }
                                            pausing.value = false
                                        })
                                    })
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically)
                        ) {
                            Row(modifier = Modifier
                                .weight(1f, true)
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(onTap = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            activity.enterPictureInPictureMode()
                                        }
                                    })
                                }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_picture_in_picture_24),
                                    tint = Color.White,
                                    contentDescription = "小窗播放",
                                    modifier = Modifier
                                        .align(
                                            Alignment.CenterVertically
                                        )
                                        .padding(start = 25.dp)
                                )
                                Text(
                                    text = "小窗播放",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(
                                            start = 15.dp
                                        )
                                        .align(Alignment.CenterVertically)
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f, true),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                                    tint = Color.White,
                                    contentDescription = "上一首",
                                    modifier = Modifier
                                        .align(
                                            Alignment.CenterVertically
                                        )
                                        .width(80.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(onTap = {
                                                try {
                                                    MediaMetadataRetriever().setDataSource(
                                                        VideoPlayObjects.list[player.currentMediaItemIndex - 1].toString()
                                                    )
                                                    player.seekToDefaultPosition(player.currentMediaItemIndex - 1)
                                                } catch (_: Exception) {
                                                    player.seekToDefaultPosition(0)
                                                }
                                                pausing.value = false
                                            })
                                        })
                                Icon(painter = painterResource(id = if (pausing.value && VideoPlayerObjects.pausing) R.drawable.baseline_play_arrow_24 else R.drawable.baseline_pause_24),
                                    tint = Color.White,
                                    contentDescription = "暂停播放",
                                    modifier = Modifier
                                        .align(
                                            Alignment.CenterVertically
                                        )
                                        .width(40.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(onTap = {
                                                if (pausing.value) {
                                                    player.play()
                                                    pausing.value = false
                                                    VideoPlayerObjects.pausing = false
                                                } else {
                                                    player.pause()
                                                    pausing.value = true
                                                    VideoPlayerObjects.pausing = true
                                                }
                                            })
                                        })
                                Icon(painter = painterResource(id = R.drawable.baseline_skip_next_24),
                                    tint = Color.White,
                                    contentDescription = "下一首",
                                    modifier = Modifier
                                        .align(
                                            Alignment.CenterVertically
                                        )
                                        .width(80.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(onTap = {
                                                try {
                                                    player.seekToDefaultPosition(player.currentMediaItemIndex + 1)
                                                } catch (_: Exception) {
                                                    player.seekToDefaultPosition(0)
                                                }
                                                pausing.value = false
                                            })
                                        })
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.height(
                        ConvertUtils.px2dp(
                            BarUtils.getNavBarHeight().toFloat()
                        ).dp
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 25.dp)
                .width(50.dp)
                .height(if (toolBarIng.value && lock.value.not()) 50.dp else 0.dp)
                .clip(
                    RoundedCornerShape(25.dp)
                )
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.2f))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        if (VideoPlayerObjects.isInFullScreen) {
                            VideoPlayerObjects.isInFullScreen = false
                            screen.value = false
                            activity.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        } else {
                            VideoPlayerObjects.isInFullScreen = true
                            screen.value = true
                            activity.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                    })
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_screen_rotation_24),
                    tint = Color.White,
                    contentDescription = "横竖屏",
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 25.dp)
                .width(50.dp)
                .height(if (toolBarIng.value) 50.dp else 0.dp)
                .clip(
                    RoundedCornerShape(25.dp)
                )
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.2f))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        if (lock.value) {
                            lock.value = false
                            lockIcon.value = R.drawable.baseline_lock_open_24
                        } else {
                            lock.value = true
                            lockIcon.value = R.drawable.outline_lock_24
                            ImmersionBar
                                .with(activity)
                                .hideBar(BarHide.FLAG_HIDE_BAR)
                                .transparentBar()
                                .init()
                        }
                    })
                }) {
                Icon(
                    painter = painterResource(id = lockIcon.value),
                    tint = Color.White,
                    contentDescription = "锁定",
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(50.dp)
                .height(if (pausing.value && VideoPlayerObjects.pausing) 50.dp else 0.dp)
                .clip(
                    RoundedCornerShape(25.dp)
                )
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(0.7f))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        player.play()
                        VideoPlayerObjects.isPauseMode = false
                        pausing.value = false
                    })
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                    contentDescription = "暂停",
                    modifier = Modifier.align(
                        Alignment.Center
                    ),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (seeking.value) {
            Text(
                text = "${TimeParse.timeParse(seekTo.value.toLong())} / ${
                    TimeParse.timeParse(
                        allPosition.value.toLong()
                    )
                }",
                color = Color.DarkGray,
                modifier = Modifier
                    .offset(
                        x = 1.dp, y = 1.dp
                    )
                    .alpha(0.75f)
                    .align(Alignment.Center),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${TimeParse.timeParse(seekTo.value.toLong())} / ${
                    TimeParse.timeParse(
                        allPosition.value.toLong()
                    )
                }",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (voluming.value) {
            Text(
                text = "画饼ing（右）：${volumeTo.value.toInt()}%",
                color = Color.DarkGray,
                modifier = Modifier
                    .offset(
                        x = 1.dp, y = 1.dp
                    )
                    .alpha(0.75f)
                    .align(Alignment.Center),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "画饼ing（右）：${volumeTo.value.toInt()}%",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (brightIng.value) {
            Text(
                text = "画饼ing（左）：${brightTo.value.toInt()}%",
                color = Color.DarkGray,
                modifier = Modifier
                    .offset(
                        x = 1.dp, y = 1.dp
                    )
                    .alpha(0.75f)
                    .align(Alignment.Center),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "画饼ing（左）：${brightTo.value.toInt()}%",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }


        Box(
            modifier = if (speeding.value) Modifier
                .padding(top = 40.dp)
                .fillMaxSize() else Modifier.height(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .align(Alignment.TopCenter)
            ) {
                Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.5f))) {
                    Text(
                        text = "当前速率：${VideoPlayerObjects.speed}x",
                        color = Color.White,
                        modifier = Modifier.padding(5.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

}

@SuppressLint("InflateParams")
@Composable
fun VideoView(
    activity: VideoPlayerActivity,
    loading: MutableState<Boolean>,
    nowPosition: MutableState<Float>,
    allPosition: MutableState<Float>,
    seekTo: MutableState<Float>,
    pausing: MutableState<Boolean>,
    screen: MutableState<Boolean>,
    resources: Resources,
    theme: Int,
    volume: MutableState<Float>
) {
    var videoView: PlayerView? = null
    AndroidView(factory = {
        LayoutInflater.from(it).inflate(R.layout.compose_videoplayer, null).apply {
            videoView = findViewById(R.id.video_view)
        }
    }) {
        player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        CoroutineScope(Dispatchers.Main).launch {
            val list = VideoPlayObjects.list
            when (VideoPlayObjects.type) {
                VideoPlayerActivity.VideoType.LOCAL -> {
                    list.forEachIndexed { _, s ->
                        player.addMediaItem(MediaItem.fromUri(s.toString()))
                    }
                }

                VideoPlayerActivity.VideoType.STREAM -> {
                    player.addMediaItem(MediaItem.fromUri(Uri.parse(VideoPlayObjects.paths)))
                }

                VideoPlayerActivity.VideoType.WEBDAV -> {
                    val username = activity.intent.getStringExtra("username")
                    val password = activity.intent.getStringExtra("password")

                    val httpDataSourceFactory =
                        DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
                    val dataSourceFactory = DataSource.Factory {
                        val dataSource = httpDataSourceFactory.createDataSource()
                        dataSource.setRequestProperty(
                            "Authorization",
                            "Basic " + EncodeUtils.base64Encode("$username:$password")
                                .decodeToString()
                        )
                        dataSource
                    }
                    player = ExoPlayer.Builder(activity)
                        .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory)).build()
                        .apply {
                            val kv = MMKV.mmkvWithID("WebDav")
                            VideoPlayObjects.list.forEachIndexed { _, davResource ->
                                val uri =
                                    kv.decodeString("WebDavIpRoot") + (davResource as DavResource).path.subStringX(
                                        "/", null
                                    )
                                addMediaItem(MediaItem.fromUri(uri))
                            }
                        }

                }

                else -> {
                    ToastUtils.showShort("出现错误")
                }
            }
            player.seekToDefaultPosition(VideoPlayerObjects.newItem)
            videoView!!.player = player
            player.prepare()
            player.addListener(object : Player.Listener {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    try {
                        VideoPlayerObjects.rv!!.models =
                            getPlayList(resources, theme, player.currentMediaItemIndex)
                        VideoPlayerObjects.chose!!.setTextColor(android.graphics.Color.parseColor("#ffffff"))
                        VideoPlayerObjects.newItem = player.currentMediaItemIndex
                    } catch (_: Exception) {
                    }
                }

                @SuppressLint("SwitchIntDef")
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when (playbackState) {
                        Player.STATE_READY -> {
                            volume.value = player.volume
                            player.play()
                            loading.value = false
                            seekTo.value = 0f
                            VideoPlayerObjects.pausing = false
                            pausing.value = false
                            VideoPlayerObjects.videoHeight = player.videoSize.height
                            VideoPlayerObjects.videoWidth = player.videoSize.width
                            if (player.videoSize.width > player.videoSize.height && VideoPlayerObjects.first) {
                                VideoPlayerObjects.isInFullScreen = true
                                screen.value = true
                                activity.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                VideoPlayerObjects.first = false
                            }
                            allPosition.value = player.duration.toFloat()
                            slider!!.valueTo = player.duration.toFloat()
                            CoroutineScope(Dispatchers.Main).launch {
                                while (true) {

                                    if (VideoPlayerObjects.isCrash) {
                                        break
                                    } else if (player.duration > player.currentPosition && VideoPlayerObjects.isMove.not()) {
                                        nowPosition.value = player.currentPosition.toFloat()
                                        slider!!.value = player.currentPosition.toFloat()
                                        slider!!.valueTo = player.duration.toFloat()
                                    }
                                    delay(500)
                                }
                            }
                        }

                        Player.STATE_BUFFERING -> {
                            loading.value = true
                        }

                        Player.STATE_ENDED -> {
                            if (SettingsItemsUntil.readSettingData("isAutoExit").toBoolean()) {
                                VideoPlayerObjects.isAutoFinish = true
                                VideoPlayerObjects.fix()
                                activity.finish()
                            }
                        }

                        Player.STATE_IDLE -> {
                            ToastUtils.showShort("播放出错")
                        }
                    }
                }
            })
        }
    }
}

private fun finish(activity: Activity, player: Player) {
    if (SettingsItemsUntil.readSettingData("isDialogPlayer").toBoolean()) {
        MaterialAlertDialogBuilder(
            activity,
            com.google.android.material.R.style.MaterialAlertDialog_Material3_Title_Text_CenterStacked
        ).setTitle("退出播放").setMessage("您确定要退出播放？还是进入小窗？")
            .setPositiveButton("取消") { _, _ -> }.setNegativeButton("进入小窗") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val builder = PictureInPictureParams.Builder()
                    val rational = Rational(
                        VideoPlayerObjects.videoWidth, VideoPlayerObjects.videoHeight
                    )
                    builder.setAspectRatio(rational)
                    activity.enterPictureInPictureMode(builder.build())
                } else {
                   ToastUtils.showShort("您的系统版本不支持画中画")
                }
            }.setNeutralButton("退出播放") { _, _ ->
                VideoPlayerObjects.fix()
                player.release()
                activity.finish()
            }.show()
    } else {
        VideoPlayerObjects.fix()
        player.release()
        activity.finish()
    }
}

