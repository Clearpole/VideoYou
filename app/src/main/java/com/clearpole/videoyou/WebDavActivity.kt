package com.clearpole.videoyou

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import com.clearpole.videoyou.databinding.ActivityWebDavBinding
import com.clearpole.videoyou.databinding.WebdavListItemBinding
import com.clearpole.videoyou.model.WebDavModel
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.utils.SetBarTransparent
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.tencent.mmkv.MMKV
import com.thegrizzlylabs.sardineandroid.DavResource
import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebDavActivity : BaseActivity<ActivityWebDavBinding>() {
    @SuppressLint("ResourceType")
    @Suppress("NAME_SHADOWING")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SetBarTransparent.setBarTransparent(
            binding.root.findViewById(R.id.web_dav_status_bar),
            this,
            resources
        )
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
        val int = this.intent
        if (!int.getStringExtra("webLine").isNullOrEmpty()) {
            if (int.getStringExtra("name")!="/"){
                binding.topAppBar.menu.clear()
            }
            binding.webDavDir.text = int.getStringExtra("dir")!!
                .take(int.getStringExtra("dir")!!.length - int.getStringExtra("name")!!.length)
            binding.topAppBar.title = int.getStringExtra("name")
            val kv = MMKV.mmkvWithID("WebDav")
            kv.decodeString("WebDavIp")
            val username = kv.decodeString("WebDavUser")
            val password = kv.decodeString("WebDavPassword")
            CoroutineScope(Dispatchers.IO).launch {
                val sardine: Sardine = OkHttpSardine()
                val list = try {
                    sardine.list(int.getStringExtra("webLine"))
                }catch (e:Exception){
                    sardine.setCredentials(username, password)
                    sardine.list(int.getStringExtra("webLine"))
                }
                val model = getWebDavData(list)
                launch(Dispatchers.Main) {
                    binding.webDavFilesRv.linear().setup {
                        addType<WebDavModel> { R.layout.webdav_list_item }
                        onBind {
                            val binding = WebdavListItemBinding.bind(itemView)
                            if (layoutPosition == 0) {
                                binding.webDavFilesRoot.layoutParams.height = 0
                            } else {

                                binding.webDavFilesTitle.text =
                                    getModel<WebDavModel>(layoutPosition).title.name
                                binding.webDavFilesPath.text =
                                    getModel<WebDavModel>(layoutPosition).title.path
                                if (getModel<WebDavModel>(layoutPosition).title.contentType.contains(
                                        "directory"
                                    )
                                ) {
                                    binding.wLI.setImageDrawable(
                                        Drawable.createFromXml(
                                            resources,
                                            resources.getXml(R.drawable.twotone_folder_24)
                                        )
                                    )
                                    binding.webDavFilesRoot.setOnClickListener {
                                        val webPath = kv.decodeString("WebDavIpRoot")
                                        val webLine =
                                            webPath + (getModel<WebDavModel>(layoutPosition).title.path).subStringX(
                                                "/",
                                                null
                                            )
                                        val int =
                                            Intent(this@WebDavActivity, WebDavActivity::class.java)
                                        int.putExtra("webLine", webLine)
                                        int.putExtra(
                                            "dir",
                                            getModel<WebDavModel>(layoutPosition).title.path + getModel<WebDavModel>(
                                                layoutPosition
                                            ).title.name
                                        )
                                        int.putExtra(
                                            "name", getModel<WebDavModel>(
                                                layoutPosition
                                            ).title.name
                                        )
                                        startActivity(int)
                                    }
                                } else {
                                    binding.wLI.setImageDrawable(
                                        Drawable.createFromXml(
                                            resources,
                                            resources.getXml(R.drawable.twotone_attach_file_24)
                                        )
                                    )
                                    binding.wRI.visibility = View.GONE
                                    binding.webDavFilesRoot.setOnClickListener {
                                        val webPath = kv.decodeString("WebDavIpRoot") + getModel<WebDavModel>(layoutPosition).title.path.subStringX("/",null)
                                        val int = Intent(this@WebDavActivity,VideoPlayer::class.java)
                                        int.putExtra("webPath",webPath)
                                        int.putExtra("username",username)
                                        int.putExtra("password",password)
                                        VideoPlayObjects.title = getModel<WebDavModel>(layoutPosition).title.name
                                        startActivity(int)
                                    }
                                }

                            }
                        }
                    }.models = model
                }
            }
        }
    }

    private fun String.subStringX(beforeString: String?, afterString: String?): String? {
        return if (beforeString == null && afterString != null) {
            this.substring(0, this.indexOf(afterString))
        } else if (beforeString != null && afterString == null) {
            this.substring(this.indexOf(beforeString) + beforeString.length, this.length)
        } else if (beforeString != null && afterString != null) {
            this.subStringX(beforeString, null)!!.subStringX(null, afterString)
        } else {
            null
        }
    }

    private fun getWebDavData(list: List<DavResource>): MutableList<Any> {
        return mutableListOf<Any>().apply {
            for (index in list.indices) {
                add(WebDavModel(list[index]))
            }
        }
    }
}
