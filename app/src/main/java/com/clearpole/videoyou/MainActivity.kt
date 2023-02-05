package com.clearpole.videoyou

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.size
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.*
import com.blankj.utilcode.util.ActivityUtils.startActivity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.TimeUtils
import com.clearpole.videoyou.MainActivity.Utils.firstInto
import com.clearpole.videoyou.adapter.MainViewPager
import com.clearpole.videoyou.code.MarqueeTextView
import com.clearpole.videoyou.code.PlayList
import com.clearpole.videoyou.databinding.ActivityMainBinding
import com.clearpole.videoyou.model.FolderModel
import com.clearpole.videoyou.model.FolderTreeModel
import com.clearpole.videoyou.model.MainSettingModel
import com.clearpole.videoyou.model.MediaStoreListModel
import com.clearpole.videoyou.model.PlayListNameModel
import com.clearpole.videoyou.objects.MainObjects
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.utils.ByteToString
import com.clearpole.videoyou.utils.DatabaseStorage
import com.clearpole.videoyou.utils.ReadMediaStore
import com.clearpole.videoyou.utils.RefreshMediaStore
import com.clearpole.videoyou.utils.SetBarTransparent
import com.clearpole.videoyou.utils.SettingsItemsUntil
import com.clearpole.videoyou.utils.SubStringX.Companion.subStringX
import com.drake.brv.item.ItemExpand
import com.drake.brv.layoutmanager.HoverGridLayoutManager
import com.drake.brv.utils.BRV
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.R.style.MaterialAlertDialog_Material3
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.tencent.mmkv.MMKV
import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var firstLoad = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Main.UI.start(binding, context = this, resources = resources, activity = this)
        ViewPager.UI.start(binding, activity = this, context = this)
        Main.Logic.start(binding, activity = this, context = this)
        DatabaseStorage.clearData(this.contentResolver)
        RefreshMediaStore.updateMedia(
            this, Environment.getExternalStorageDirectory().toString()
        )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (firstLoad) {
            ViewPager.Logic.start(binding, activity = this, context = this)
            firstLoad = false
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        AppUtils.relaunchApp()
    }

    object Main {
        object UI {
            fun start(
                binding: ActivityMainBinding,
                activity: Activity,
                resources: Resources,
                context: Context
            ) {
                SetBarTransparent.setBarTransparent(binding.mainStatusBar, activity, resources)
                topBar(binding, context)
            }

            private fun topBar(
                binding: ActivityMainBinding, context: Context
            ) {
                binding.mainToolbar.title = "播放列表"
                binding.mainToolbar.setNavigationOnClickListener {
                    binding.mainDrawerLayout.open()
                }
                binding.mainToolbar.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_refreshMediaStore -> {
                            DatabaseStorage.clearData(context.contentResolver)
                            RefreshMediaStore.updateMedia(
                                context, Environment.getExternalStorageDirectory().toString()
                            )
                            ToastUtils.show(context.getString(R.string.onClickRefreshMedia))
                            true
                        }

                        else -> false
                    }
                }
            }
        }

        object Logic {
            @SuppressLint("UseCompatLoadingForDrawables", "SwitchIntDef")
            fun start(
                binding: ActivityMainBinding, activity: Activity, context: Context
            ) {
                navigationDrawer(binding, context, activity)
                bottomNavigation(binding)
                val noticeView = binding.mainNavigationDrawerView.getHeaderView(0)
                    .findViewById<MarqueeTextView>(R.id.notice_text)
                CoroutineScope(Dispatchers.IO).launch {
                    val notice =
                        URL("https://clearpole.gitee.io/video-you-notice/notice.html").readText()
                            .subStringX("【", "】")
                    withContext(Dispatchers.Main) {
                        noticeView.text = notice
                        binding.mainNavigationDrawerView.getHeaderView(0)
                            .findViewById<LinearLayout>(R.id.notice).setOnClickListener {
                                MaterialAlertDialogBuilder(
                                    context, MaterialAlertDialog_Material3
                                ).setTitle("公告").setMessage(notice)
                                    .setNegativeButton("我知道了") { _, _ -> }.show()
                            }
                    }
                }
                val darkModeView = binding.mainNavigationDrawerView.getHeaderView(0)
                    .findViewById<ImageView>(R.id.header_mode)

                if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES) {
                    darkModeView.setImageDrawable(context.getDrawable(R.drawable.outline_light_mode_24))
                } else {
                    darkModeView.setImageDrawable(context.getDrawable(R.drawable.outline_mode_night_24))
                }

                darkModeView.setOnClickListener {
                    if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES) {
                        SettingsItemsUntil.writeSettingData("darkMode", "2")
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    } else {
                        SettingsItemsUntil.writeSettingData("darkMode", "1")
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
            }

            @SuppressLint("MissingInflatedId")
            private fun navigationDrawer(
                binding: ActivityMainBinding, context: Context, activity: Activity
            ) {
                binding.mainNavigationDrawerView.getHeaderView(0)
                    .findViewById<ImageView>(R.id.header_back).setOnClickListener {
                        binding.mainDrawerLayout.close()
                    }
                binding.mainNavigationDrawerView.setNavigationItemSelectedListener { menuItem ->
                    when (menuItem.itemId) {

                        R.id.toSearch -> {
                            startActivity(Intent(context, SearchActivity::class.java))
                        }

                        R.id.toInternet -> {
                            stream(context, activity)
                        }

                        R.id.toWebDav -> {
                            webdav(activity, context)
                        }

                        else -> {
                            ToastUtils.show(menuItem.itemId)
                        }
                    }
                    true
                }

            }

            @SuppressLint("CutPasteId")
            private fun stream(context: Context, activity: Activity) {
                val view = activity.layoutInflater.inflate(R.layout.material_dialog_edit_1, null)
                view.findViewById<TextInputLayout>(R.id.edit_layout).hint = "输入视频直链"
                MaterialAlertDialogBuilder(
                    context, MaterialAlertDialog_Material3
                ).setTitle("输入链接").setView(view).setNegativeButton("开看！") { _, _ ->
                    VideoPlayObjects.type = "INTERNET"
                    VideoPlayObjects.title =
                        view.findViewById<TextInputEditText>(R.id.edit_view).text.toString()
                    VideoPlayObjects.paths =
                        view.findViewById<TextInputEditText>(R.id.edit_view).text.toString()
                    startActivity(Intent(context, VideoPlayer::class.java))
                }.show()
            }

            private fun webdav(activity: Activity, context: Context) {
                val kv = MMKV.mmkvWithID("WebDav")
                if (kv.decodeInt("isLogin") != 1) {
                    val view =
                        activity.layoutInflater.inflate(R.layout.material_dialog_edit_4, null)
                    view.findViewById<TextInputLayout>(R.id.edit_3_layout_1).hint =
                        "WebDav 服务器根目录"
                    view.findViewById<TextInputLayout>(R.id.edit_3_layout_2).hint =
                        "WebDav 服务器完整目录"
                    view.findViewById<TextInputLayout>(R.id.edit_3_layout_3).hint =
                        "WebDav 服务器账户"
                    view.findViewById<TextInputLayout>(R.id.edit_3_layout_4).hint =
                        "WebDav 服务器密码"
                    MaterialAlertDialogBuilder(
                        context, MaterialAlertDialog_Material3
                    ).setTitle("WebDav").setView(view).setNegativeButton("登录") { _, _ ->
                        val ip =
                            view.findViewById<TextInputEditText>(R.id.edit_3_view_2).text.toString()
                        val ipRoot =
                            view.findViewById<TextInputEditText>(R.id.edit_3_view_1).text.toString()
                        val username =
                            view.findViewById<TextInputEditText>(R.id.edit_3_view_3).text.toString()
                        val password =
                            view.findViewById<TextInputEditText>(R.id.edit_3_view_4).text.toString()
                        val webdavIp = if (ip.takeLast(1) == "/") {
                            ip
                        } else {
                            "$ip/"
                        }
                        val webdavIpRoot = if (ipRoot.takeLast(1) == "/") {
                            ipRoot
                        } else {
                            "$ipRoot/"
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            val sardine: Sardine = OkHttpSardine()
                            try {
                                if (!ip.contains("http")) {
                                    ToastUtils.show("WebDav 服务器格式不正确！")
                                    return@launch
                                } else if (!ip.contains(ipRoot)) {
                                    ToastUtils.show("根目录或Dav目录输入有误！")
                                    return@launch
                                }
                                sardine.list(webdavIp)
                            } catch (e: Exception) {
                                try {
                                    sardine.setCredentials(username, password)
                                    sardine.list(webdavIp)
                                    ToastUtils.show("登录成功！")
                                    kv.encode("isLogin", 1)
                                } catch (e: Exception) {
                                    ToastUtils.showLong("连接失败\n${e.message}")
                                }
                            }
                        }
                        kv.encode("WebDavUser", username)
                        kv.encode("WebDavPassword", password)
                        kv.encode("WebDavIp", webdavIp)
                        kv.encode("WebDavIpRoot", webdavIpRoot)
                    }.setNeutralButton("登录帮助") { _, _ ->
                        val uri =
                            Uri.parse("https://clearpole.gitee.io/videoyou-website/docs/videoyou/%E4%BD%BF%E7%94%A8%E5%B8%AE%E5%8A%A9/WebDav%E6%8F%90%E7%A4%BA%E8%BF%9E%E6%8E%A5%E5%A4%B1%E8%B4%A5%EF%BC%9F")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }.show()
                    view.findViewById<TextInputEditText>(R.id.edit_3_view_2)
                        .setText(kv.decodeString("WebDavIp"))
                    view.findViewById<TextInputEditText>(R.id.edit_3_view_3)
                        .setText(kv.decodeString("WebDavUser"))
                    view.findViewById<TextInputEditText>(R.id.edit_3_view_4)
                        .setText(kv.decodeString("WebDavPassword"))
                    view.findViewById<TextInputEditText>(R.id.edit_3_view_1)
                        .setText(kv.decodeString("WebDavIpRoot"))
                } else {
                    val int = Intent(context, WebDavActivity::class.java)
                    int.putExtra("webLine", kv.decodeString("WebDavIp"))
                    int.putExtra("name", "/")
                    int.putExtra("dir", kv.decodeString("WebDavIp"))
                    startActivity(int)
                }
            }

            private fun bottomNavigation(binding: ActivityMainBinding) {
                binding.mainNavigationView.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.page1 -> {
                            binding.mainViewpager.currentItem = 0
                            true
                        }

                        R.id.page2 -> {
                            binding.mainViewpager.currentItem = 1
                            true
                        }

                        R.id.page3 -> {
                            binding.mainViewpager.currentItem = 2
                            true
                        }

                        R.id.page4 -> {
                            binding.mainViewpager.currentItem = 3
                            true
                        }

                        else -> false
                    }
                }
            }
        }
    }

    object ViewPager {
        private var bindingViews: java.util.ArrayList<View>? = null

        object UI {
            fun start(
                binding: ActivityMainBinding, activity: Activity, context: Context
            ) {
                bindingViews = pageViewer(binding, activity.layoutInflater, context, activity)
            }

            @SuppressLint("InflateParams")
            private fun pageViewer(
                binding: ActivityMainBinding,
                inflater: LayoutInflater,
                context: Context,
                activity: Activity
            ): ArrayList<View> {
                val page1 = inflater.inflate(R.layout.main_page_playlist, null)
                val page2 = inflater.inflate(R.layout.main_page_mediastore, null)
                val page3 = inflater.inflate(R.layout.main_page_folder, null)
                val page4 = inflater.inflate(R.layout.main_page_setting, null)
                val bindingViews = ArrayList<View>()
                bindingViews.add(page1)
                bindingViews.add(page2)
                bindingViews.add(page3)
                bindingViews.add(page4)
                binding.mainViewpager.adapter = MainViewPager(bindingViews)

                if (SettingsItemsUntil.readSettingData("agreeApp") == "true") {
                } else {
                    firstInto(binding, Permission.READ_MEDIA_VIDEO, true, activity, context)
                }

                return bindingViews
            }
        }

        object Logic {
            fun start(
                binding: ActivityMainBinding, activity: Activity, context: Context
            ) {
                BRV.modelId = BR.model
                viewPagerListener(binding)
                Playlist.addPlayList(bindingViews!!, activity, context)
                Playlist.loadPlayList(bindingViews!!)
                Playlist.refreshList(bindingViews!!,context,binding)
                MediaStore.mediaStoreList(bindingViews!!, binding)
                FolderList.addPlayListFile(binding, bindingViews!!, context)
                MediaStore.refreshList(bindingViews!!, context, binding)
                FolderList.refreshList(bindingViews!!, context, binding)
                Settings.settings(context, bindingViews!!)
                CoroutineScope(Dispatchers.Main).launch {
                    FolderList.folderList(bindingViews!!, context, binding)
                }
            }

            object Settings {
                fun settings(context: Context, bindingViews: ArrayList<View>) {
                    bindingViews[3].findViewById<RelativeLayout>(R.id.intoAbout)
                        .setOnClickListener {
                            val int = Intent(context, SettingActivity::class.java)
                            int.putExtra("name", "关于")
                            startActivity(int)
                        }
                    val rv = bindingViews[3].findViewById<RecyclerView>(R.id.listview)
                    rv.linear().setup {
                        addType<MainSettingModel> { R.layout.main_page_setting_item }
                    }.models = models(context)
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                private fun models(context: Context): MutableList<Any> {
                    return mutableListOf<Any>().apply {
                        add(
                            MainSettingModel(
                                "通用",
                                "个人爱好的定制选项",
                                context.getDrawable(R.drawable.baseline_explore_24)!!
                            )
                        )
                        add(
                            MainSettingModel(
                                "主题",
                                "个人审美的定制选项",
                                context.getDrawable(R.drawable.baseline_auto_fix_high_24)!!
                            )
                        )
                        add(
                            MainSettingModel(
                                "手势",
                                "个人习惯的定制选项",
                                context.getDrawable(R.drawable.outline_gesture_24)!!
                            )
                        )
                    }
                }
            }

            object Playlist {
                fun addPlayList(
                    bindingViews: ArrayList<View>, activity: Activity, context: Context
                ) {
                    val rv = bindingViews[0].findViewById<RecyclerView>(R.id.listview)
                    val addActionButton =
                        bindingViews[0].findViewById<FloatingActionButton>(R.id.add_playlist)
                    addActionButton.setOnClickListener {
                        val view =
                            activity.layoutInflater.inflate(R.layout.material_dialog_edit_1, null)
                        view.findViewById<TextInputLayout>(R.id.edit_layout).hint = "请输入列表名称"
                        MaterialAlertDialogBuilder(
                            context, MaterialAlertDialog_Material3
                        ).setTitle("添加播放列表").setView(view)
                            .setNegativeButton("确认新建") { _, _ ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    KeyboardUtils.hideSoftInput(activity.window)
                                    withContext(Dispatchers.Main) {
                                        val name =
                                            view.findViewById<TextInputEditText>(R.id.edit_view).text.toString()
                                        if (name.isEmpty()) {
                                            ToastUtils.show("请输入播放列表名称")
                                            return@withContext
                                        }
                                        if (!PlayList.readList().contains(name)) {
                                            PlayList.addList(name)
                                            if (rv.size != 0) {
                                                val json = PlayList.readListContent(name)
                                                    ?.let { it1 -> JSONObject(it1) }
                                                rv.addModels(
                                                    listOf(
                                                        PlayListNameModel(
                                                            name,
                                                            TimeUtils.millis2String(json!!.getLong("time")),
                                                            json.getString("count")
                                                        )
                                                    ), index = 0
                                                )
                                            } else {
                                                bindingViews[0].findViewById<MaterialTextView>(R.id.not_found_playlist).visibility =
                                                    View.GONE
                                                loadPlayList(bindingViews)
                                            }
                                            rv.scrollToPosition(0)
                                        } else {
                                            ToastUtils.show("存在相同名称的播放列表，无法创建")
                                        }
                                    }
                                }
                            }.show()
                    }
                    if (PlayList.readList().isNotEmpty()) {
                        bindingViews[0].findViewById<MaterialTextView>(R.id.not_found_playlist).visibility =
                            View.GONE
                        loadPlayList(bindingViews)
                    }
                }

                fun loadPlayList(bindingViews: ArrayList<View>) {
                    val rv = bindingViews[0].findViewById<RecyclerView>(R.id.listview)
                    CoroutineScope(Dispatchers.IO).launch {
                        val models = getPlayLists()
                        withContext(Dispatchers.Main) {
                            rv.linear().setup {
                                addType<PlayListNameModel> { R.layout.play_list_name_item }
                            }.models = models
                        }
                    }
                }

                private fun getPlayLists(): MutableList<Any> {
                    return mutableListOf<Any>().apply {
                        for (index in 0 until PlayList.readList().size) {
                            val name = PlayList.readList()[index]
                            val json = PlayList.readListContent(name)?.let { JSONObject(it) }
                            add(
                                PlayListNameModel(
                                    name,
                                    TimeUtils.millis2String(json!!.getLong("time")),
                                    json.getInt("count").toString()
                                )
                            )
                        }
                    }
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                fun refreshList(
                    bindingViews: ArrayList<View>, context: Context, binding: ActivityMainBinding
                ) {
                    bindingViews[0].findViewById<SwipeRefreshLayout>(R.id.refresh_view)
                        .setOnRefreshListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                if (!DatabaseStorage.writeDataToData(
                                        ReadMediaStore.start(
                                            context.contentResolver
                                        )
                                    )
                                ) {
                                }
                                withContext(Dispatchers.Main) {
                                    loadPlayList(bindingViews)
                                    bindingViews[0].findViewById<SwipeRefreshLayout>(R.id.refresh_view).isRefreshing =
                                        false
                                }
                            }
                        }
                }
            }

            object MediaStore {
                @SuppressLint("CutPasteId", "SetTextI18n")
                fun mediaStoreList(
                    bindingViews: ArrayList<View>, binding: ActivityMainBinding
                ) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val models = getMediaStores(DatabaseStorage.readDataByData()!!, binding)
                        launch(Dispatchers.Main) {
                            bindingViews[1].findViewById<RecyclerView>(R.id.listview).linear()
                                .setup {
                                    addType<MediaStoreListModel> { R.layout.media_store_list_item }
                                }.models = models
                            binding.mainNavigationDrawerView.getHeaderView(0)
                                .findViewById<MaterialTextView>(R.id.all_count).text =
                                bindingViews[1].findViewById<RecyclerView>(R.id.listview)?.adapter?.itemCount.toString()
                            binding.mainNavigationDrawerView.getHeaderView(0)
                                .findViewById<MaterialTextView>(R.id.all_size).text =
                                ByteToString.byteToString(MainObjects.allSize)
                        }
                    }
                }

                private fun getMediaStores(
                    kv: JSONArray, binding: ActivityMainBinding
                ): MutableList<Any> {
                    return mutableListOf<Any>().apply {
                        MainObjects.allSize = 0
                        for (index in 0 until kv.length()) {
                            val jsonObject = JSONObject(kv.getString(index))
                            val size = jsonObject.getString("size").toLong()
                            MainObjects.allSize = MainObjects.allSize + size
                            add(
                                MediaStoreListModel(
                                    title = jsonObject.getString("title"),
                                    size = ByteToString.byteToString(size),
                                    uri = Uri.parse(jsonObject.getString("uri")),
                                    path = jsonObject.getString("path"),
                                    mainBind = binding
                                )
                            )
                        }
                    }
                }


                @SuppressLint("UseCompatLoadingForDrawables")
                fun refreshList(
                    bindingViews: ArrayList<View>, context: Context, binding: ActivityMainBinding
                ) {
                    bindingViews[1].findViewById<SwipeRefreshLayout>(R.id.refresh_view)
                        .setOnRefreshListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                if (!DatabaseStorage.writeDataToData(
                                        ReadMediaStore.start(
                                            context.contentResolver
                                        )
                                    )
                                ) {
                                }
                                withContext(Dispatchers.Main) {
                                    mediaStoreList(bindingViews, binding)
                                    bindingViews[1].findViewById<SwipeRefreshLayout>(R.id.refresh_view).isRefreshing =
                                        false
                                }
                            }
                        }
                }
            }

            object FolderList {
                @SuppressLint("UseCompatLoadingForDrawables")
                fun addPlayListFile(
                    binding: ActivityMainBinding, bindingViews: ArrayList<View>, context: Context
                ) {
                    bindingViews[2].findViewById<FloatingActionButton>(R.id.add_playlist_file)
                        .setOnClickListener {
                            if (!MainObjects.isChoose) {
                                if (PlayList.readList().isNotEmpty()) {
                                    (it as FloatingActionButton).setImageDrawable(
                                        context.getDrawable(
                                            R.drawable.baseline_check_24
                                        )
                                    )
                                    binding.mainCheck.visibility = View.VISIBLE
                                    MainObjects.isChoose = true
                                }else{
                                    ToastUtils.show("您还没有新建任何播放列表")
                                }
                            } else {
                                MainObjects.isChoose = false
                                (it as FloatingActionButton).setImageDrawable(context.getDrawable(R.drawable.baseline_playlist_add_24))
                                val playList = PlayList.readList()
                                MaterialAlertDialogBuilder(
                                    context, MaterialAlertDialog_Material3
                                ).setTitle("您已选择${MainObjects.chooseList.size}个视频")
                                    .setSingleChoiceItems(
                                        playList.toTypedArray(), 0, null
                                    ).setNegativeButton("确定") { dialog, _ ->
                                        if (MainObjects.chooseList.size==0) {
                                            ToastUtils.show("您还没有选择任何视频")
                                            val rv =
                                                bindingViews[2].findViewById<RecyclerView>(R.id.listview)
                                            rv.bindingAdapter.checkedAll(false)
                                            binding.mainCheck.visibility = View.GONE
                                            MainObjects.chooseList.clear()
                                        }else {
                                            val name =
                                                playList[(dialog as AlertDialog).listView.checkedItemPosition]
                                            PlayList.addPlayListContent(
                                                name,
                                                MainObjects.chooseList
                                            )
                                            ToastUtils.showShort("添加成功")
                                            val rv =
                                                bindingViews[2].findViewById<RecyclerView>(R.id.listview)
                                            rv.bindingAdapter.checkedAll(false)
                                            binding.mainCheck.visibility = View.GONE
                                            binding.mainCheck.getBadge(R.id.chose)!!.number = 0
                                            MainObjects.chooseList.clear()
                                        }
                                    }.setNeutralButton("取消") { _, _ ->
                                        val rv =
                                            bindingViews[2].findViewById<RecyclerView>(R.id.listview)
                                        rv.bindingAdapter.checkedAll(false)
                                        binding.mainCheck.visibility = View.GONE
                                        MainObjects.chooseList.clear()
                                        binding.mainCheck.getBadge(R.id.chose)!!.number = 0
                                    }.show()
                            }
                        }
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                suspend fun folderList(
                    bindingViews: ArrayList<View>, context: Context, bind: ActivityMainBinding
                ) {
                    val rv = bindingViews[2].findViewById<RecyclerView>(R.id.listview)
                    val layoutManager = HoverGridLayoutManager(context, 2)
                    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            if (position < 0) return 1
                            return when (rv.bindingAdapter.getItemViewType(position)) {
                                R.layout.folder_list_item -> 1
                                else -> 2
                            }
                        }
                    }
                    rv.layoutManager = layoutManager
                    rv.setup {
                        addType<FolderTreeModel>(R.layout.folder_list_tree_item)
                        addType<FolderModel>(R.layout.folder_list_item)
                        R.id.folder_item_dad.onFastClick {
                            when (itemViewType) {
                                R.layout.folder_list_tree_item -> {
                                    if (getModel<ItemExpand>().itemExpand) {
                                        expandOrCollapse()
                                    } else {
                                        expandOrCollapse()
                                    }
                                }
                            }
                        }
                        R.id.folder_item.onClick {
                            if (MainObjects.isChoose) {
                                var checked = getModel<FolderModel>().checked
                                if (it == R.id.folder_item) checked = !checked
                                setChecked(layoutPosition, checked)
                            } else {
                                VideoPlayObjects.paths = getModel<FolderModel>(layoutPosition).path
                                VideoPlayObjects.title = getModel<FolderModel>(layoutPosition).title
                                VideoPlayObjects.type = "LOCAL"
                                val intent = Intent(context, VideoPlayer::class.java)
                                startActivity(intent)
                            }
                        }
                        onChecked { position, isChecked, _ ->

                            val badge = bind.mainCheck.getOrCreateBadge(R.id.chose)
                            badge.isVisible = true
                                val model = getModel<FolderModel>(position)
                                model.checked = isChecked
                                if (isChecked) {
                                    MainObjects.chooseList.add(model.path)
                                    model.vis = View.VISIBLE
                                    badge.number = MainObjects.chooseList.size
                                } else {
                                    if (MainObjects.isChoose) {
                                        MainObjects.chooseList.remove(model.path)
                                    }
                                    model.vis = View.GONE
                                    badge.number = MainObjects.chooseList.size
                                }
                                model.notifyChange()
                        }

                    }.models = getData(ReadMediaStore.getFolder(context.contentResolver), context)
                    rv.bindingAdapter.setCheckableType(R.layout.folder_list_item)
                    bind.mainCheck.setOnItemSelectedListener {
                        when (it.itemId) {
                            R.id.chooseAll -> {
                                rv.bindingAdapter.checkedAll()
                                true
                            }

                            R.id.chooseEmpty -> {
                                rv.bindingAdapter.checkedReverse()
                                true
                            }

                            R.id.exit -> {
                                MainObjects.isChoose = false
                                bind.mainCheck.visibility = View.GONE
                                bindingViews[2].findViewById<FloatingActionButton>(R.id.add_playlist_file)
                                    .setImageDrawable(context.getDrawable(R.drawable.baseline_playlist_add_24))
                                CoroutineScope(Dispatchers.IO).launch {
                                    MainObjects.chooseList.clear()
                                    rv.bindingAdapter.checkedAll(false)
                                    bind.mainCheck.getBadge(R.id.chose)!!.number = 0
                                    this.cancel()
                                }
                                true
                            }

                            else -> false
                        }
                    }
                }

                @SuppressLint("ResourceType")
                private fun getData(
                    kv: ArrayList<String>, context: Context
                ): MutableList<FolderTreeModel> {
                    return mutableListOf<FolderTreeModel>().apply {
                        val json = DatabaseStorage.readDataByData()
                        for (index in 0 until kv.size) {
                            add(
                                FolderTreeModel(
                                    context.contentResolver,
                                    kv[index],
                                    json!!,
                                    Drawable.createFromXml(
                                        context.resources,
                                        context.resources.getXml(R.drawable.baseline_keyboard_arrow_down_24)
                                    ),
                                    Drawable.createFromXml(
                                        context.resources,
                                        context.resources.getXml(R.drawable.baseline_keyboard_arrow_right_24)
                                    )
                                )
                            )
                        }
                    }
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                fun refreshList(
                    bindingViews: ArrayList<View>, context: Context, binding: ActivityMainBinding
                ) {
                    bindingViews[2].findViewById<SwipeRefreshLayout>(R.id.refresh_view)
                        .setOnRefreshListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                if (!DatabaseStorage.writeDataToData(
                                        ReadMediaStore.start(
                                            context.contentResolver
                                        )
                                    )
                                ) {
                                    //ToastUtils.show("软件遇到了意外的错误！\n或者是您的手机没有视频？")
                                }
                                launch(Dispatchers.Main) {
                                    folderList(bindingViews, context, binding)
                                    binding.mainToolbar.title = "媒体所属"
                                    MainObjects.isChoose = false
                                    MainObjects.chooseList.clear()
                                    bindingViews[2].findViewById<SwipeRefreshLayout>(R.id.refresh_view).isRefreshing =
                                        false
                                    bindingViews[2].findViewById<FloatingActionButton>(R.id.add_playlist_file)
                                        .setImageDrawable(context.getDrawable(R.drawable.baseline_playlist_add_24))
                                    binding.mainCheck.visibility = View.GONE
                                }
                            }
                        }
                }
            }

            private fun viewPagerListener(
                binding: ActivityMainBinding
            ) {
                binding.mainViewpager.addOnPageChangeListener(object :
                    androidx.viewpager.widget.ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                        position: Int, positionOffset: Float, positionOffsetPixels: Int
                    ) {
                    }

                    override fun onPageSelected(position: Int) {
                        when (position) {
                            0 -> {
                                binding.mainNavigationView.selectedItemId = R.id.page1
                                binding.mainToolbar.title = "播放列表"
                            }

                            1 -> {
                                binding.mainNavigationView.selectedItemId = R.id.page2
                                binding.mainToolbar.title = "全部媒体"
                            }

                            2 -> {
                                binding.mainNavigationView.selectedItemId = R.id.page3
                                binding.mainToolbar.title = "媒体所属"
                            }

                            3 -> {
                                binding.mainNavigationView.selectedItemId = R.id.page4
                                binding.mainToolbar.title = "软件设置"
                            }
                        }
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }
                })
            }
        }
    }

    object Utils {
        @Suppress("DEPRECATION")
        fun firstInto(
            binding: ActivityMainBinding,
            permission: String,
            isBoolean: Boolean,
            activity: Activity,
            context: Context
        ) {
            if (isBoolean) {
                val view = activity.layoutInflater.inflate(R.layout.app_is_activation, null)
                val tV = view.findViewById<TextView>(R.id.yszc)
                tV.movementMethod = LinkMovementMethod.getInstance()
                tV.text =
                    Html.fromHtml("<a href='https://clearpole.gitee.io/videoyou-website/docs/videoyou/%E8%BD%AF%E4%BB%B6/%E9%9A%90%E7%A7%81%E6%94%BF%E7%AD%96'>前往阅读隐私政策</a>")
                val tV1 = view.findViewById<TextView>(R.id.yhxy)
                tV1.movementMethod = LinkMovementMethod.getInstance()
                tV1.text =
                    Html.fromHtml("<a href='https://clearpole.gitee.io/videoyou-website/docs/videoyou/%E8%BD%AF%E4%BB%B6/%E7%94%A8%E6%88%B7%E5%8D%8F%E8%AE%AE'>前往阅读用户协议</a>")
                MaterialAlertDialogBuilder(
                    context, MaterialAlertDialog_Material3
                ).setTitle("用户协议与隐私政策").setCancelable(false)
                    .setMessage("您需要阅读并同意用户协议与隐私政策，否则将无法使用软件。")
                    .setView(view).setNegativeButton("不同意") { _, _ ->
                        activity.finish()
                    }.setPositiveButton("我已知晓并承诺遵循") { _, _ ->
                        SettingsItemsUntil.writeSettingData("agreeApp", "true")
                        XXPermissions.with(context).permission(permission)
                            .request(object : OnPermissionCallback {
                                override fun onGranted(
                                    permissions: MutableList<String>, allGranted: Boolean
                                ) {
                                    if (!allGranted) {
                                        ToastUtils.show("获取部分权限成功，但部分权限未正常授予")
                                        activity.finish()
                                        return
                                    }
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (!DatabaseStorage.writeDataToData(
                                                ReadMediaStore.start(
                                                    context.contentResolver
                                                )
                                            )
                                        ) {
                                        }
                                        launch(Dispatchers.Main) {
                                            ViewPager.Logic.start(
                                                binding, activity = activity, context = context
                                            )
                                        }
                                    }
                                }

                                override fun onDenied(
                                    permissions: MutableList<String>, doNotAskAgain: Boolean
                                ) {
                                    if (doNotAskAgain) {
                                        ToastUtils.show("请授权读取视频文件权限")
                                        XXPermissions.startPermissionActivity(context, permissions)
                                    } else {
                                        ToastUtils.show("获取读取视频文件权限失败")
                                        activity.finish()
                                    }
                                }
                            })
                    }.show()
            }
        }
    }

}