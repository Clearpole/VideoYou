package com.clearpole.videoyou

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.size
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.*
import com.blankj.utilcode.util.ActivityUtils.startActivity
import com.blankj.utilcode.util.KeyboardUtils
import com.clearpole.videoyou.MainActivity.Utils.firstInto
import com.clearpole.videoyou.adapter.MainViewPager
import com.clearpole.videoyou.code.PlayList
import com.clearpole.videoyou.databinding.ActivityMainBinding
import com.clearpole.videoyou.model.FolderModel
import com.clearpole.videoyou.model.FolderTreeModel
import com.clearpole.videoyou.model.MediaStoreListModel
import com.clearpole.videoyou.model.PlayListNameModel
import com.clearpole.videoyou.objects.MainObjects
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.utils.DatabaseStorage
import com.clearpole.videoyou.utils.GetVideoThumbnail
import com.clearpole.videoyou.utils.ReadMediaStore
import com.clearpole.videoyou.utils.RefreshMediaStore
import com.clearpole.videoyou.utils.SetBarTransparent
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Main.UI.start(binding, context = this, resources = resources, activity = this)
        ViewPager.UI.start(binding, activity = this, context = this)
        Main.Logic.start(binding, activity = this, context = this)
        ViewPager.Logic.start(binding, activity = this, context = this)
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
                binding: ActivityMainBinding,
                context: Context
            ) {
                binding.mainToolbar.title = "预播放"
                binding.mainToolbar.setNavigationOnClickListener {
                    binding.mainDrawerLayout.open()
                }
                binding.mainToolbar.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_refreshMediaStore -> {
                            DatabaseStorage.clearData(context.contentResolver)
                            RefreshMediaStore.updateMedia(
                                context,
                                Environment.getExternalStorageDirectory().toString()
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
            private var nowIn = 0
            fun start(
                binding: ActivityMainBinding,
                activity: Activity,
                context: Context
            ) {
                navigationDrawer(binding, context, activity)
                bottomNavigation(binding)
            }

            @SuppressLint("MissingInflatedId")
            private fun navigationDrawer(
                binding: ActivityMainBinding,
                context: Context,
                activity: Activity
            ) {
                binding.mainNavigationDrawerView.setCheckedItem(R.id.toHome)
                binding.mainNavigationDrawerView.getHeaderView(0)
                    .findViewById<ImageView>(R.id.header_back).setOnClickListener {
                        binding.mainDrawerLayout.close()
                    }
                binding.mainNavigationDrawerView.setNavigationItemSelectedListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.toHome -> {
                            //animationListener(binding.mainPageViewPager, binding.mainPageSetting, true)
                            binding.mainToolbar.title = "预播放"
                            nowIn = 0
                            binding.mainDrawerLayout.close()
                            menuItem.isChecked = true
                        }

                        R.id.toSettings -> {
                            //animationListener(binding.mainPageSetting, binding.mainPageViewPager, false)
                            binding.mainToolbar.title = "设置"
                            nowIn = 1
                            binding.mainDrawerLayout.close()
                            menuItem.isChecked = true
                        }

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

            private fun stream(context: Context, activity: Activity) {
                val view = activity.layoutInflater.inflate(R.layout.material_dialog_edit_1, null)
                view.findViewById<TextInputLayout>(R.id.edit_layout).hint = "输入视频直链"
                MaterialAlertDialogBuilder(
                    context,
                    MaterialAlertDialog_Material3
                )
                    .setTitle("输入链接")
                    .setView(view)
                    .setNegativeButton("开看！") { _, _ ->
                        VideoPlayObjects.type = "INTERNET"
                        VideoPlayObjects.title = "网络视频"
                        VideoPlayObjects.paths =
                            view.findViewById<TextInputEditText>(R.id.edit_view).text.toString()
                        startActivity(Intent(context, VideoPlayer::class.java))
                    }
                    .show()
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
                        context,
                        MaterialAlertDialog_Material3
                    )
                        .setTitle("WebDav")
                        .setView(view)
                        .setNegativeButton("登录") { _, _ ->
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
                        }
                        .setNeutralButton("登录帮助") { _, _ ->
                            val uri =
                                Uri.parse("https://clearpole.gitee.io/videoyou-website/docs/videoyou/%E4%BD%BF%E7%94%A8%E5%B8%AE%E5%8A%A9/WebDav%E6%8F%90%E7%A4%BA%E8%BF%9E%E6%8E%A5%E5%A4%B1%E8%B4%A5%EF%BC%9F")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                        .show()
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
                binding: ActivityMainBinding,
                activity: Activity,
                context: Context
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
                val bindingViews = ArrayList<View>()
                bindingViews.add(page1)
                bindingViews.add(page2)
                bindingViews.add(page3)
                binding.mainViewpager.adapter = MainViewPager(bindingViews)

                val ycKv = MMKV.defaultMMKV()
                if (ycKv.decodeString("isFirst") == "true") {
                } else {
                    firstInto(binding, Permission.READ_MEDIA_VIDEO, true, ycKv, activity, context)
                }

                return bindingViews
            }
        }

        object Logic {
            fun start(
                binding: ActivityMainBinding,
                activity: Activity,
                context: Context
            ) {
                BRV.modelId = BR.model
                viewPagerListener(binding)
                Playlist.addPlayList(bindingViews!!, activity, context)
                Playlist.loadPlayList(bindingViews!!)
                MediaStore.mediaStoreList(bindingViews!!, binding, context)
                MediaStore.addPlayListFile(binding, bindingViews!!, context)
                FolderList.folderList(bindingViews!!, context)
                MediaStore.refreshList(bindingViews!!, context, binding)
                FolderList.refreshList(bindingViews!!, context, binding)
            }

            object Playlist {
                fun addPlayList(
                    bindingViews: ArrayList<View>,
                    activity: Activity,
                    context: Context
                ) {
                    val rv = bindingViews[0].findViewById<RecyclerView>(R.id.listview)
                    val addActionButton =
                        bindingViews[0].findViewById<FloatingActionButton>(R.id.add_playlist)
                    addActionButton.setOnClickListener {
                        val view =
                            activity.layoutInflater.inflate(R.layout.material_dialog_edit_1, null)
                        view.findViewById<TextInputLayout>(R.id.edit_layout).hint = "请输入列表名称"
                        MaterialAlertDialogBuilder(
                            context,
                            MaterialAlertDialog_Material3
                        )
                            .setTitle("添加播放列表")
                            .setView(view)
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
                                        if (!PlayList.readList()!!.contains(name)) {
                                            PlayList.addList(name)
                                            if (rv.size != 0) {
                                                val json =
                                                    PlayList.readListContent(name)
                                                        ?.let { it1 -> JSONObject(it1) }
                                                rv.addModels(
                                                    listOf(
                                                        PlayListNameModel(
                                                            name,
                                                            json!!.getString("time"),
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
                    if (!PlayList.readList().isNullOrEmpty()) {
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
                        for (index in 0 until PlayList.readList()!!.size) {
                            val name = PlayList.readList()!![index]
                            val json = PlayList.readListContent(name)?.let { JSONObject(it) }
                            add(
                                PlayListNameModel(
                                    name,
                                    json!!.getString("time"),
                                    json.getInt("count").toString()
                                )
                            )
                        }
                    }
                }
            }

            object MediaStore {
                @SuppressLint("CutPasteId", "SetTextI18n")
                fun mediaStoreList(
                    bindingViews: ArrayList<View>,
                    binding: ActivityMainBinding,
                    context: Context
                ) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val models =
                            getMediaStores(DatabaseStorage.readDataByData(), context, binding)
                        launch(Dispatchers.Main) {
                            bindingViews[1].findViewById<RecyclerView>(R.id.listview).linear()
                                .setup {
                                    addType<MediaStoreListModel> { R.layout.media_store_list_item }
                                }.models = models
                            binding.mainNavigationDrawerView.getHeaderView(0)
                                .findViewById<TextView>(R.id.header_title).text =
                                "您的设备\n共有${bindingViews[1].findViewById<RecyclerView>(R.id.listview)?.adapter?.itemCount}个视频"
                        }
                    }
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                fun addPlayListFile(
                    binding: ActivityMainBinding,
                    bindingViews: ArrayList<View>,
                    context: Context
                ) {
                    bindingViews[1].findViewById<FloatingActionButton>(R.id.add_playlist_file)
                        .setOnClickListener {
                            if (!MainObjects.isChoose) {
                                (it as FloatingActionButton).setImageDrawable(context.getDrawable(R.drawable.baseline_check_24))
                                binding.mainToolbar.title = "已选择 0 项"
                                MainObjects.isChoose = true
                            } else {
                                (it as FloatingActionButton).setImageDrawable(context.getDrawable(R.drawable.baseline_playlist_add_24))
                                binding.mainToolbar.title = "媒体库"
                                MainObjects.isChoose = false
                                ToastUtils.show(MainObjects.chooseList.toString())
                                mediaStoreList(bindingViews, binding, context)
                                CoroutineScope(Dispatchers.IO).launch {
                                    while (MainObjects.chooseList.keys().hasNext()) {
                                        MainObjects.chooseList.remove(
                                            MainObjects.chooseList.keys().next().toString()
                                        )
                                    }
                                }
                            }
                        }
                }

                private fun getMediaStores(
                    kv: JSONArray,
                    context: Context,
                    binding: ActivityMainBinding
                ): MutableList<Any> {
                    return mutableListOf<Any>().apply {
                        for (index in 0 until kv.length()) {
                            val jsonObject = JSONObject(kv.getString(index))
                            add(
                                MediaStoreListModel(
                                    title = jsonObject.getString("title"),
                                    size = jsonObject.getString("size"),
                                    img = GetVideoThumbnail.getVideoThumbnail(
                                        context.contentResolver,
                                        Uri.parse(jsonObject.getString("uri"))
                                    ),
                                    path = jsonObject.getString("path"),
                                    mainBind = binding
                                )
                            )
                        }
                    }
                }

                fun refreshList(
                    bindingViews: ArrayList<View>,
                    context: Context,
                    binding: ActivityMainBinding
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
                                    //ToastUtils.show("软件遇到了意外的错误！\n或者是您的手机没有视频？")
                                }
                                launch(Dispatchers.Main) {
                                    binding.mainToolbar.title = "媒体库"
                                    MainObjects.isChoose = false
                                    mediaStoreList(bindingViews, binding, context)
                                    bindingViews[1].findViewById<FloatingActionButton>(R.id.add_playlist_file)
                                        .setImageDrawable(context.getDrawable(R.drawable.baseline_playlist_add_24))
                                    bindingViews[1].findViewById<SwipeRefreshLayout>(R.id.refresh_view).isRefreshing =
                                        false
                                    withContext(Dispatchers.IO) {
                                        while (MainObjects.chooseList.keys().hasNext()) {
                                            MainObjects.chooseList.remove(
                                                MainObjects.chooseList.keys().next().toString()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                }
            }

            object FolderList {
                fun folderList(bindingViews: ArrayList<View>, context: Context) {
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
                        R.id.folder_item.onFastClick {
                            VideoPlayObjects.paths = getModel<FolderModel>(layoutPosition).path
                            VideoPlayObjects.title = getModel<FolderModel>(layoutPosition).title
                            VideoPlayObjects.type = "LOCAL"
                            val intent = Intent(context, VideoPlayer::class.java)
                            startActivity(intent)
                        }
                    }.models = getData(ReadMediaStore.getFolder(context.contentResolver), context)
                }

                @SuppressLint("ResourceType")
                private fun getData(
                    kv: ArrayList<String>,
                    context: Context
                ): MutableList<FolderTreeModel> {
                    return mutableListOf<FolderTreeModel>().apply {
                        for (index in 0 until kv.size) {
                            val json = DatabaseStorage.readDataByData()
                            add(
                                FolderTreeModel(
                                    context.contentResolver,
                                    kv[index],
                                    json,
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

                fun refreshList(
                    bindingViews: ArrayList<View>,
                    context: Context,
                    binding: ActivityMainBinding
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
                                    folderList(bindingViews, context)
                                    binding.mainToolbar.title = "文件夹"
                                    MainObjects.isChoose = false
                                    withContext(Dispatchers.IO) {
                                        while (MainObjects.chooseList.keys().hasNext()) {
                                            MainObjects.chooseList.remove(
                                                MainObjects.chooseList.keys().next().toString()
                                            )
                                        }
                                    }
                                    bindingViews[2].findViewById<SwipeRefreshLayout>(R.id.refresh_view).isRefreshing =
                                        false
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
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                    }

                    override fun onPageSelected(position: Int) {
                        when (position) {
                            0 -> {
                                binding.mainNavigationView.selectedItemId = R.id.page1
                                binding.mainToolbar.title = "预播放"
                            }

                            1 -> {
                                binding.mainNavigationView.selectedItemId = R.id.page2
                                binding.mainToolbar.title = "媒体库"
                            }

                            2 -> {
                                binding.mainNavigationView.selectedItemId = R.id.page3
                                binding.mainToolbar.title = "文件夹"
                            }
                        }
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }
                }
                )
            }
        }
    }

    object Utils {
        @Suppress("DEPRECATION")
        fun firstInto(
            binding: ActivityMainBinding,
            permission: String,
            isBoolean: Boolean,
            ycKv: MMKV,
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
                    context,
                    MaterialAlertDialog_Material3
                )
                    .setTitle("用户协议与隐私政策")
                    .setCancelable(false)
                    .setMessage("您需要阅读并同意用户协议与隐私政策，否则将无法使用软件。")
                    .setView(view)
                    .setNegativeButton("不同意") { _, _ ->
                        activity.finish()
                    }
                    .setPositiveButton("我已知晓并承诺遵循") { _, _ ->
                        XXPermissions.with(context)
                            .permission(permission)
                            .request(object : OnPermissionCallback {
                                override fun onGranted(
                                    permissions: MutableList<String>,
                                    allGranted: Boolean
                                ) {
                                    if (!allGranted) {
                                        ToastUtils.show("获取部分权限成功，但部分权限未正常授予")
                                        activity.finish()
                                        return
                                    }
                                    CoroutineScope(Dispatchers.IO).launch {
                                        ycKv.encode("isFirst", "true")
                                        if (!DatabaseStorage.writeDataToData(
                                                ReadMediaStore.start(
                                                    context.contentResolver
                                                )
                                            )
                                        ) {
                                        }
                                        launch(Dispatchers.Main) {
                                            ViewPager.Logic.start(
                                                binding,
                                                activity = activity,
                                                context = context
                                            )
                                        }
                                    }
                                }

                                override fun onDenied(
                                    permissions: MutableList<String>,
                                    doNotAskAgain: Boolean
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
                    }
                    .show()
            }
        }
    }

    object Anim {
        fun anim(context: Context) {
            val slateAnimaRightSlideIn = TranslateAnimation(
                1000f, -0f, 0f, 0f
            )
            slateAnimaRightSlideIn.duration = 200

            val slateAnimaLeftSlideOut = TranslateAnimation(
                0f, -1000f, 0f, 0f
            )
            slateAnimaLeftSlideOut.duration = 200

            val slateAnimaBottomSlideIn = AnimationUtils.loadAnimation(
                context,
                com.google.android.material.R.anim.abc_slide_in_bottom
            )
            //slateAnimaBottomSlideIn.duration = 150L
            val slateAnimaBottomSlideOut = AnimationUtils.loadAnimation(
                context,
                com.google.android.material.R.anim.abc_slide_out_bottom
            )
        }
    }
}