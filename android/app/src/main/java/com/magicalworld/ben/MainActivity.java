package com.magicalworld.ben;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private static final String GAME_URL = "file:///android_asset/index.html";
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Draw into the display cutout (notch) area so landscape has no black bars.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        hideSystemUI();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Surface WebView JS in logcat and allow remote inspection while debugging.
        try {
            WebView.setWebContentsDebuggingEnabled(true);
        } catch (Throwable ignored) {
        }

        try {
            webView = new WebView(this);
        } catch (Throwable t) {
            // Some devices ship a broken/updating System WebView. Fail loudly.
            showFatal("This device's Android System WebView is unavailable.\n"
                    + "Update \"Android System WebView\" and Chrome in the Play Store, then reopen.");
            return;
        }
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setAllowFileAccess(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // Only replace the page for main-frame failures, not sub-resources.
                if (request != null && request.isForMainFrame()) {
                    CharSequence desc = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && error != null)
                            ? error.getDescription() : "unknown error";
                    showFatal("Couldn't load the game.\n(" + desc + ")");
                }
            }

            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                // The WebView renderer died (OOM/crash). Don't let it take the app down;
                // rebuild the WebView and reload so the player just restarts.
                if (webView != null) {
                    webView.destroy();
                }
                recreate();
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());

        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setBackgroundColor(0xFF000000);

        webView.loadUrl(GAME_URL);
    }

    private void showFatal(String message) {
        String html = "<html><head><meta name='viewport' content='width=device-width,initial-scale=1'>"
                + "<style>html,body{margin:0;height:100%;background:#14082a;color:#f0abfc;"
                + "font-family:sans-serif;display:flex;align-items:center;justify-content:center;text-align:center}"
                + "div{padding:24px;max-width:600px;line-height:1.5}h1{color:#d946ef}</style></head>"
                + "<body><div><h1>The Magical World of Ben</h1><p>"
                + message.replace("\n", "<br>") + "</p></div></body></html>";
        if (webView == null) {
            webView = new WebView(this);
            setContentView(webView);
        }
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.systemBars());
                controller.setSystemBarsBehavior(
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
        hideSystemUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) webView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // Back button does nothing during gameplay - prevents accidental exits
    }
}
