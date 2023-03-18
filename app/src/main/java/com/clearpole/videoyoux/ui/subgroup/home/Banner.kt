package com.clearpole.videoyoux.ui.subgroup.home

import android.app.Activity
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.clearpole.videoyoux.ui.utils.SmoothRoundedCornerShape
import com.kyant.monet.n2
import com.kyant.monet.rangeTo

class Banner {
    companion object {
        @Composable
        fun View(activity: Activity) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(start = 25.dp, end = 25.dp, top = 25.dp),
                colors = CardDefaults.cardColors(95.n2..10.n2),
                shape = SmoothRoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.75f)
                ) {
                    AndroidView(factory = {
                        ImageView(it).apply {
                            this.layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            this.scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                    })
                    Card(
                        shape = SmoothRoundedCornerShape(3.dp), colors = CardDefaults.cardColors(
                            Color.Black.copy(alpha = 0.3f)
                        ), modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 10.dp, end = 15.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                            text = "27:23",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.4f)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        colors = ButtonDefaults.buttonColors(Color.White),
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "查看近期播放历史",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}