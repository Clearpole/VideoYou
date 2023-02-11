@file:Suppress("DEPRECATION", "PreviewAnnotationInFunctionWithParameters")

package com.clearpole.videoyou

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.objects.VideoPlayerObjects
import com.clearpole.videoyou.objects.VideoPlayerObjects.Companion.player
import com.clearpole.videoyou.objects.VideoPlayerObjects.Companion.slider
import com.clearpole.videoyou.ui.theme.VideoYouOptTheme
import com.clearpole.videoyou.utils.SettingsItemsUntil
import com.clearpole.videoyou.utils.SubStringX.Companion.subStringX
import com.clearpole.videoyou.utils.TimeParse
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
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
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                mutableStateOf(R.drawable.outline_lock_24)
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
                        seeking
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
                        voluming
                    )
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
    voluming: MutableState<Boolean>
) {
    val screenX = ScreenUtils.getAppScreenWidth()
    Box(modifier = Modifier.fillMaxSize()) {
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
                    val seek = seekTo.value + it * 20
                    seekTo.value = if (seek > allPosition.value) {
                        allPosition.value
                    } else if (seek < 0) {
                        0f
                    } else {
                        seek
                    }
                    slider!!.value = seekTo.value
                }),
                onDragStarted = {
                    player.pause()
                    VideoPlayerObjects.isMove = true
                    seekTo.value = nowPosition.value
                    seeking.value = true
                },
                onDragStopped = {
                    player.seekTo(seekTo.value.toLong())
                    VideoPlayerObjects.isMove = false
                    seeking.value = false
                })
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    val speed = VideoPlayerObjects.isSpeedMode
                    if (speed) {
                        player.setPlaybackSpeed(1f)
                        VideoPlayerObjects.isSpeedMode = false
                        speeding.value = false
                    } else {
                        player.setPlaybackSpeed(2f)
                        VideoPlayerObjects.isSpeedMode = true
                        speeding.value = true
                    }
                }, onDoubleTap = {
                    if (lock.value.not()) {
                        val pause = VideoPlayerObjects.isPauseMode
                        if (pause) {
                            player.play()
                            VideoPlayerObjects.isPauseMode = false
                            pausing.value = false
                        } else {
                            player.pause()
                            VideoPlayerObjects.isPauseMode = true
                            pausing.value = true
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
                        .clickable(
                            interactionSource = MutableInteractionSource(), indication = null
                        ) {
                            finish(activity, player)
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
                        .clickable(
                            interactionSource = MutableInteractionSource(), indication = null
                        ) {
                            finish(activity, player)
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
                        Row(
                            modifier = Modifier
                                .weight(1f, true)
                                .fillMaxSize()
                        ) {
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
                                .weight(1f, true)
                                .fillMaxSize()
                        ) {
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
                        Row(
                            modifier = Modifier
                                .weight(1f, true)
                                .fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_hearing_disabled_24),
                                tint = Color.White,
                                contentDescription = "静音播放",
                                modifier = Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                            Text(
                                text = "静音播放",
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
                                    .clickable {
                                        try {
                                            MediaMetadataRetriever().setDataSource(VideoPlayObjects.list[player.currentMediaItemIndex - 1].toString())
                                            player.seekToDefaultPosition(player.currentMediaItemIndex - 1)
                                        } catch (_: Exception) {
                                            player.seekToDefaultPosition(0)
                                        }
                                        pausing.value = false
                                    })
                            Icon(painter = painterResource(id = if (pausing.value) R.drawable.baseline_play_arrow_24 else R.drawable.baseline_pause_24),
                                tint = Color.White,
                                contentDescription = "暂停播放",
                                modifier = Modifier
                                    .align(
                                        Alignment.CenterVertically
                                    )
                                    .fillMaxSize()
                                    .weight(1f)
                                    .clickable{
                                        if (pausing.value) {
                                            player.play()
                                            pausing.value = false
                                        } else {
                                            player.pause()
                                            pausing.value = true
                                        }
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
                                    .clickable {
                                        try {
                                            player.seekToDefaultPosition(player.currentMediaItemIndex + 1)
                                        } catch (_: Exception) {
                                            player.seekToDefaultPosition(0)
                                        }
                                        pausing.value = false
                                    })
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically),
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
                                    .clickable {
                                        try {
                                            MediaMetadataRetriever().setDataSource(VideoPlayObjects.list[player.currentMediaItemIndex - 1].toString())
                                            player.seekToDefaultPosition(player.currentMediaItemIndex - 1)
                                        } catch (_: Exception) {
                                            player.seekToDefaultPosition(0)
                                        }
                                        pausing.value = false
                                    })
                            Icon(painter = painterResource(id = if (pausing.value) R.drawable.baseline_play_arrow_24 else R.drawable.baseline_pause_24),
                                tint = Color.White,
                                contentDescription = "暂停播放",
                                modifier = Modifier
                                    .align(
                                        Alignment.CenterVertically
                                    )
                                    .width(40.dp)
                                    .clickable {
                                        if (pausing.value) {
                                            player.play()
                                            pausing.value = false
                                        } else {
                                            player.pause()
                                            pausing.value = true
                                        }
                                    })
                            Icon(painter = painterResource(id = R.drawable.baseline_skip_next_24),
                                tint = Color.White,
                                contentDescription = "下一首",
                                modifier = Modifier
                                    .align(
                                        Alignment.CenterVertically
                                    )
                                    .width(80.dp)
                                    .clickable{
                                        try {
                                            player.seekToDefaultPosition(player.currentMediaItemIndex + 1)
                                        } catch (_: Exception) {
                                            player.seekToDefaultPosition(0)
                                        }
                                        pausing.value = false
                                    })
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
                .clickable {
                    if (VideoPlayerObjects.isInFullScreen) {
                        VideoPlayerObjects.isInFullScreen = false
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    } else {
                        VideoPlayerObjects.isInFullScreen = true
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
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
                .clickable {
                    if (lock.value) {
                        lock.value = false
                        lockIcon.value = R.drawable.outline_lock_24
                    } else {
                        lock.value = true
                        lockIcon.value = R.drawable.baseline_lock_open_24
                        ImmersionBar
                            .with(activity)
                            .hideBar(BarHide.FLAG_HIDE_BAR)
                            .transparentBar()
                            .init()
                    }
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
                .width(80.dp)
                .height(if (pausing.value) 80.dp else 0.dp)
                .clip(
                    RoundedCornerShape(40.dp)
                )
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(0.7f))
                .clickable {
                    player.play()
                    VideoPlayerObjects.isPauseMode = false
                    pausing.value = false
                }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                    contentDescription = "暂停",
                    modifier = Modifier.align(
                        Alignment.Center
                    )
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
                        text = "当前速率：2x",
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
    seeking: MutableState<Boolean>
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
                                        "/",
                                        null
                                    )
                                addMediaItem(MediaItem.fromUri(uri))
                            }
                        }

                }

                else -> {
                    ToastUtils.showShort("出现错误")
                }
            }
            videoView!!.player = player
            player.prepare()
            player.addListener(object : Player.Listener {
                @SuppressLint("SwitchIntDef")
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when (playbackState) {
                        Player.STATE_READY -> {
                            player.play()
                            loading.value = false
                            seekTo.value = 0f
                            allPosition.value = player.duration.toFloat()
                            slider!!.valueTo = player.duration.toFloat()
                            CoroutineScope(Dispatchers.Main).launch {
                                while (true) {
                                    if (VideoPlayerObjects.isCrash) {
                                        break
                                    } else if (player.duration > player.currentPosition && VideoPlayerObjects.isMove.not()) {
                                        nowPosition.value = player.currentPosition.toFloat()
                                        slider!!.value = player.currentPosition.toFloat()
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
                            com.hjq.toast.ToastUtils.show("播放出错")
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
                    com.hjq.toast.ToastUtils.show("您的系统版本不支持画中画")
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

