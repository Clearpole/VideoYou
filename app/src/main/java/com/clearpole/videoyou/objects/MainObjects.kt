package com.clearpole.videoyou.objects

import android.view.View
import org.json.JSONObject

class MainObjects {
    companion object{
        var allSize : Long = 0
        var isChoose = false
        var chooseList : JSONObject = JSONObject()
        var cardList = ArrayList<List<View>>()
        var count : Int = 0
        var chooseState = false
    }
}