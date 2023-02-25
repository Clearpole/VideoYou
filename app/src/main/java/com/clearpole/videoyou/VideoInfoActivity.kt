package com.clearpole.videoyou

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.clearpole.videoyou.code.PlayList
import com.clearpole.videoyou.databinding.ActivityVideoInfoBinding
import com.clearpole.videoyou.model.VideoInfoModel
import com.clearpole.videoyou.model.VideoUtilsModel
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.utils.IsNightMode
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gyf.immersionbar.ImmersionBar


class VideoInfoActivity : BaseActivity<ActivityVideoInfoBinding>() {
    private var first = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).transparentBar()
            .statusBarDarkFont(!IsNightMode.isNightMode(resources)).init()
        val uri = if (intent.getStringExtra("int") == "0") {
            VideoPlayObjects.paths
        } else {
            val uri = intent.data!!.path
            VideoPlayObjects.paths = uri.toString()
            VideoPlayObjects.title = uri!!.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."))
            VideoPlayObjects.type = "LOCAL"
            VideoPlayObjects.list = mutableListOf(intent.data!!)
            VideoPlayObjects.uri = intent.data!!
            uri
        }
        binding.topAppBar.title = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."))
        Glide.with(this).load(uri).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(binding.image)
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(TranslateAnimation(0f, 0f, 120f, 0f).apply {
            duration = 500
        })
        animationSet.addAnimation(AlphaAnimation(0f, 1f).apply {
            duration = 1000
        })
        binding.appbar.startAnimation(animationSet)
        val alpha = AlphaAnimation(0f, 1f)
        alpha.duration = 1000
        binding.foreground.startAnimation(alpha)
        binding.play.startAnimation(animationSet)
        binding.gone.startAnimation(animationSet)
        if (intent.getStringExtra("type") == "1") {
            binding.card.startAnimation(animationSet)
        }
        animationSet.reset()
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    ToastUtils.showShort("太难了不会写，会的快联系我🥵🥵🥵")
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (first) {
            val uri = if (intent.getStringExtra("int") == "0") {
                VideoPlayObjects.paths
            } else {
                val uri = intent.data!!.path
                VideoPlayObjects.paths = uri.toString()
                VideoPlayObjects.title =
                    uri!!.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."))
                VideoPlayObjects.type = "LOCAL"
                VideoPlayObjects.list = mutableListOf(intent.data!!)
                VideoPlayObjects.uri = intent.data!!
                uri
            }
            binding.infoRecycle.linear().setup {
                addType<VideoInfoModel> { R.layout.video_info_item }
            }.models = setList(uri)
            binding.utilRecycle.linear().setup {
                addType<VideoUtilsModel> { R.layout.video_info_utils_item }
            }.models = setUtils(uri)
            binding.foreground.setOnClickListener {
                val intent = Intent(this, VideoPlayerActivity::class.java)
                ActivityUtils.startActivity(intent)
            }
            first = false
        }
    }

    private fun setList(paths: String): MutableList<Any> {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(paths)
        return mutableListOf<Any>().apply {
            add(
                VideoInfoModel(
                    "标题", paths.substring(paths.lastIndexOf("/") + 1, paths.lastIndexOf("."))
                )
            )
            add(
                VideoInfoModel(
                    "时长",
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        .toString()
                )
            )
            add(
                VideoInfoModel(
                    "分辨率",
                    (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                        .toString() + "x" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
                )
            )
            add(
                VideoInfoModel(
                    "帧率", if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT)!!
                            .toLong() / (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                            .toLong() / 1000)).toString()
                    } else {
                        "系统未达到要求，无法计算"
                    }
                )
            )
            add(
                VideoInfoModel(
                    "比特率",
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                        .toString()
                )
            )
            add(
                VideoInfoModel(
                    "创建时间",
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE).toString()
                )
            )
            add(
                VideoInfoModel(
                    "MIME类型",
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                        .toString()
                )
            )
            add(
                VideoInfoModel(
                    "音轨数",
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)
                        .toString()
                )
            )
            retriever.release()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUtils(paths: String): MutableList<Any> {
        return mutableListOf<Any>().apply {
            add(getDrawable(R.drawable.baseline_playlist_add_24)?.let {
                VideoUtilsModel(
                    it, "添加到播放列表"
                ) {
                    val playList = PlayList.readList()
                    MaterialAlertDialogBuilder(
                        this@VideoInfoActivity
                    ).setTitle(VideoPlayObjects.title).setSingleChoiceItems(
                        playList.toTypedArray(), 0, null
                    ).setNegativeButton("确定") { dialog, _ ->
                        if (PlayList.readList().isNotEmpty()) {
                            val name =
                                playList[(dialog as AlertDialog).listView.checkedItemPosition]
                            PlayList.addPlayListContent(
                                name, arrayListOf(paths)
                            )
                        } else {

                        }

                    }.setNeutralButton("取消") { _, _ -> }
                        .setOnDismissListener { it ->
                            it.cancel()
                        }.show()
                }
            }!!)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(TranslateAnimation(0f, 0f, 0f, 120f).apply {
            duration = 300
        })
        animationSet.addAnimation(AlphaAnimation(1f, 0f).apply {
            duration = 200
        })
        binding.gone.startAnimation(animationSet)
        binding.gone.visibility = View.GONE
        binding.foreground.startAnimation(animationSet)
        binding.foreground.visibility = View.GONE
        super.onBackPressed()
        animationSet.reset()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun delete(uris: List<Uri>) {
        val pendingIntent = MediaStore.createDeleteRequest(contentResolver, uris.filter {
            checkUriPermission(
                it,
                Binder.getCallingPid(),
                Binder.getCallingUid(),
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            ) != PackageManager.PERMISSION_GRANTED
        })
        startIntentSenderForResult(pendingIntent.intentSender, 0, null, 0, 0, 0)
    }
}
