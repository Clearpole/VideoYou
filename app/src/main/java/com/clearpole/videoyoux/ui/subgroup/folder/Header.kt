package com.clearpole.videoyoux.ui.subgroup.folder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Header {
    companion object{
        @Composable
        fun Header(title:String){
            Column(Modifier.padding(20.dp)) {
                Text(text = title, fontSize = 31.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Videos Folder List", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(20.dp))
                Divider()
            }
        }
    }
}