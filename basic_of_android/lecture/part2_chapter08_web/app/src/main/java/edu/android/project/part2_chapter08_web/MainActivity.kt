package edu.android.project.part2_chapter08_web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val goHomeButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.goHomeButton)
    }

    private val addressBar: EditText by lazy {
        findViewById<EditText>(R.id.addressBar)
    }

    private val goBackButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.goBackButton)
    }

    private val goForwardButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.goForwardButton)
    }

    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
    }

    private val webView: WebView by lazy {
        findViewById<WebView>(R.id.webView)
    }

    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById<ContentLoadingProgressBar>(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        bindViews()
    }

    override fun onBackPressed() {
        if(webView.canGoBack()) {
            // 뒤로갈 내용이 있다면
            webView.goBack()
        } else {
            // 없다면 그냥 오리지날 런
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        webView.apply {
            webViewClient = WebViewClient()             // 이동되지 않게 하기
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)        // 암호화되지 않았어!
        }
    }

    private fun bindViews() {
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        // 액션이 발생했을 때
        addressBar.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                val loadingUrl = v.text.toString()

                // http 없어도 바로 켜질 수 있게 만들기
                if(URLUtil.isNetworkUrl(loadingUrl)) {
                    webView.loadUrl(loadingUrl)
                } else {
                    webView.loadUrl("http://$loadingUrl")
                }
            }

            return@setOnEditorActionListener false
        }

        goBackButton.setOnClickListener {
            webView.goBack()
        }

        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        // 당겼을 때
        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    // inner를 써야지 상위에 접근 가능하다
    inner class WebViewClient: android.webkit.WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            // 이걸 해야지 없어진다
            refreshLayout.isRefreshing = false
            progressBar.hide()
            // history에 따라 못가게 만들기
            goBackButton.isEnabled = webView.canGoBack()
            goForwardButton.isEnabled = webView.canGoForward()

            addressBar.setText(url)
        }
    }

    inner class WebChromeClient: android.webkit.WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }

    companion object {
        private const val DEFAULT_URL = "http://www.google.com"
    }
}
