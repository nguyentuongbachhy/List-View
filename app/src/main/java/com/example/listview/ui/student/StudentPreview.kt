package com.example.listview.ui.student

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun StudentPreviewScreen(studentInfo: String, navController: NavController) {
    val decodedStudentInfo = URLDecoder.decode(studentInfo, StandardCharsets.UTF_8.toString())

    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("Back")
        }
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true

                    loadUrl("file:///android_asset/index.html")

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            val escapedJson = decodedStudentInfo.replace("'", "\\'")
                            evaluateJavascript("displayStudentInfo('$escapedJson');", null)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}