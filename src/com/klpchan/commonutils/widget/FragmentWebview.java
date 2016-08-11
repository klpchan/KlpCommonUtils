package com.klpchan.commonutils.widget;

import static android.webkit.WebViewClient.ERROR_AUTHENTICATION;
import static android.webkit.WebViewClient.ERROR_BAD_URL;
import static android.webkit.WebViewClient.ERROR_CONNECT;
import static android.webkit.WebViewClient.ERROR_FAILED_SSL_HANDSHAKE;
import static android.webkit.WebViewClient.ERROR_FILE;
import static android.webkit.WebViewClient.ERROR_FILE_NOT_FOUND;
import static android.webkit.WebViewClient.ERROR_HOST_LOOKUP;
import static android.webkit.WebViewClient.ERROR_IO;
import static android.webkit.WebViewClient.ERROR_PROXY_AUTHENTICATION;
import static android.webkit.WebViewClient.ERROR_REDIRECT_LOOP;
import static android.webkit.WebViewClient.ERROR_TIMEOUT;
import static android.webkit.WebViewClient.ERROR_TOO_MANY_REQUESTS;
import static android.webkit.WebViewClient.ERROR_UNKNOWN;
import static android.webkit.WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME;
import static android.webkit.WebViewClient.ERROR_UNSUPPORTED_SCHEME;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.klpchan.commonutils.LogUtil;
import com.klpchan.commonutils.R;
import com.klpchan.commonutils.demo.WebViewDemoActivity;

/**
 * Created by zyl on 2016/2/22.
 */
public class FragmentWebview extends Fragment {
    public static final String ARGUMENTS_LOAD_URL = "arguments_load_url";
//    private static final String DEFAULT_LOAD_URL = "file://" + Environment.getExternalStorageDirectory() + "/banner.html";
    private static final String DEFAULT_LOAD_URL = "file:///android_asset/banner.html";

    @Bind(R.id.sed)WebView mWebView;
    @Bind(R.id.progress)View mProgressView;
    @Bind(R.id.retry)View mRetryButton;
    @Bind(R.id.retryLayout)View mRetryLayout;
    @Bind(R.id.message)TextView mMessage;
    @Bind(R.id.icon)ImageView mIcon;
    @Bind(R.id.refresh)Button mRefresh;

    private String mUrl;
    private boolean mAvailable;
    private boolean bLoaded = false;

    @OnClick(R.id.refresh)
    void refresh() {
        mWebView.reload();
    }

    //@OnClick(R.id.retry)
    void retry() {
        mWebView.reload();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_demo_webview, container, false);
        ButterKnife.bind(this, root);

        mWebView = (WebView) root.findViewById(R.id.sed);
        mProgressView = root.findViewById(R.id.progress);
        mRetryButton = root.findViewById(R.id.retry);
        mRetryLayout = root.findViewById(R.id.retryLayout);
        mMessage = (TextView) root.findViewById(R.id.message);
        mIcon = (ImageView) root.findViewById(R.id.icon);
        mRefresh = (Button) root.findViewById(R.id.refresh);
        
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });
        
        
        return root;
    }


    private static final String BUNDLE_KEY_URL = "url";
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY_URL, mUrl);
    }

    public void setRetryButtonOnClickListener(View.OnClickListener listener) {
        mRetryButton.setOnClickListener(listener);
    }

    public WebView getWebView() {
        return mWebView;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUrl = arguments.getString(ARGUMENTS_LOAD_URL);
            if (TextUtils.isEmpty(mUrl)) {
                mUrl = DEFAULT_LOAD_URL;
            }
        } else {
            mUrl = DEFAULT_LOAD_URL;
        }

/*        if (TextUtils.isEmpty(mUrl)) {
            mProgressView.setVisibility(View.VISIBLE);
        }*/

        if (savedInstanceState != null) {
            mUrl = savedInstanceState.getString(BUNDLE_KEY_URL, mUrl);
        }

        Log.v(TAG, "mUrl = " + mUrl);

        mWebView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings localWebSettings = mWebView.getSettings();
        localWebSettings.setJavaScriptEnabled(true);
        localWebSettings.setGeolocationEnabled(true);
//        localWebSettings.setGeolocationDatabasePath(getContext().getFilesDir().getPath());
        localWebSettings.setDomStorageEnabled(true);
        localWebSettings.setDatabaseEnabled(true);
        localWebSettings.setAppCacheEnabled(true);
        localWebSettings.setAllowContentAccess(true);
        localWebSettings.setAllowFileAccess(true);
        localWebSettings.setAllowFileAccessFromFileURLs(true);
        localWebSettings.setAllowUniversalAccessFromFileURLs(true);
        localWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        localWebSettings.setLoadWithOverviewMode(true);
        localWebSettings.setTextSize(WebSettings.TextSize.NORMAL);
        localWebSettings.setUseWideViewPort(true);
        localWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
            localWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        String userAgent;
        String version = "";
        try {
            PackageManager packageManager = getContext().getPackageManager();
            version = packageManager.getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        userAgent = localWebSettings.getUserAgentString() + " SRoaming/" + version;
        localWebSettings.setUserAgentString(userAgent);
        mWebView.addJavascriptInterface(this, "SebHandlerInterface");
        WebViewClient client = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.v(TAG, "shouldInterceptRequest 1.url = " + request.getUrl());
                Log.v(TAG, "shouldInterceptRequest 2.headers = " + request.getRequestHeaders());
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.v(TAG, "onReceivedError = " + errorCode);
                //onError(errorCode);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.v(TAG, "onReceivedError 1.headers = " + request.getRequestHeaders());
                Log.v(TAG, "onReceivedError 2.url = " + request.getUrl());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.v(TAG, "onReceivedError 3.errorCode = " + error.getErrorCode());
                    Log.v(TAG, "onReceivedError 4.description = " + error.getDescription());
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.v(TAG, "onReceivedSslError = " + error);
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
                //onError(ERROR_BAD_URL);
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                Log.v(TAG, "onReceivedHttpAuthRequest = " + host);
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
                onError(ERROR_BAD_URL);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.v(TAG, "onReceivedHttpError = " + errorResponse.getEncoding() + " \n" +
                    errorResponse.getMimeType() + "\n"+errorResponse.getReasonPhrase() + "\n" +
                    errorResponse.getStatusCode());
                super.onReceivedHttpError(view, request, errorResponse);
                int statusCode = errorResponse.getStatusCode();
                if (statusCode >= 500) {
                    onError(ERROR_BAD_URL);
                }
                //onError(ERROR_BAD_URL);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.v(TAG, "onPageFinished");
                // we can get the button value, then check the available
            }
        };

        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                notifyProgressChanged(newProgress);
            }

        };

        mWebView.setWebViewClient(client);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        loadUrl();
    }

    private ProgressDialog mProgressDialog;
    private void dismissInProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        dismissInProgressDialog();
        mProgressDialog = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void notifyProgressChanged(int newProgress) {
        if (!isAdded() || isDetached()) {
            return;
        }

        if (mAvailable) {
            if (newProgress > 40) {
                mRetryLayout.setVisibility(View.GONE);
                mWebView.onResume();
                mWebView.setVisibility(View.VISIBLE);
                mProgressView.setVisibility(View.GONE);
                bLoaded = true;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        start();
    }

    @Override
    public void onStop() {
        super.onStop();
        stop();
    }

    public void start() {
        loadUrl();
    }

    public void stop() {
        pause();
    }

    void loadUrl() {
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }

        mWebView.onResume();
        if (bLoaded) {
            return;
        }

        mAvailable = isNetworkConnected();
        if (mAvailable) {
//            showProgressBar();
            mProgressView.setVisibility(View.GONE);
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            mWebView.loadUrl(mUrl, getExtraHeaders());
        } else {
            if (mWebView.getSettings().getCacheMode() != WebSettings.LOAD_CACHE_ONLY) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            }

            mProgressView.setVisibility(View.GONE);
            mWebView.loadUrl(mUrl, getExtraHeaders());
            bLoaded = true;
        }
    }

    public void enableCache() {
        /*mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);*/

    }

    public void clearCache() {
        /*//娓呯悊Webview缂撳瓨鏁版嵁搴?
        File file = getContext().getCacheDir();
        deleteDirectory(file);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);*/
    }

    public void loadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        mUrl = url;
        bLoaded = false;
        mWebView.onResume();
        mAvailable = isNetworkConnected();

        if (mAvailable) {
//            showProgressBar();
            mProgressView.setVisibility(View.GONE);
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            mWebView.loadUrl(mUrl, getExtraHeaders());
            Log.v(TAG, "loadUrl " + mUrl);
        } else {
            mProgressView.setVisibility(View.GONE);
            if (mWebView.getSettings().getCacheMode() != WebSettings.LOAD_CACHE_ONLY) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            }
            mWebView.loadUrl(mUrl, getExtraHeaders());
            bLoaded = true;
        }
    }

    private Map<String, String> getExtraHeaders() {
        Map<String, String> extraHeaders = new HashMap<>();
        //extraHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        return extraHeaders;
    }

    void pause() {
        mWebView.onPause();
    }

    boolean isNetworkConnected() {
        ConnectivityManager manager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onError(int errorCode) {
        Log.d(TAG, "FragmentWebView onError , error code : " + errorCode);
        hideWebView(getErrorMessage(errorCode));
    }

    private int getErrorMessage(int errorCode) {
        switch (errorCode) {
            case ERROR_AUTHENTICATION:
            case ERROR_BAD_URL:
            case ERROR_CONNECT:
            case ERROR_FAILED_SSL_HANDSHAKE:
            case ERROR_FILE:
            case ERROR_FILE_NOT_FOUND:
            case ERROR_HOST_LOOKUP:
            case ERROR_IO:
            case ERROR_PROXY_AUTHENTICATION:
            case ERROR_REDIRECT_LOOP:
            case ERROR_TIMEOUT:
            case ERROR_TOO_MANY_REQUESTS:
            case ERROR_UNSUPPORTED_AUTH_SCHEME:
            case ERROR_UNSUPPORTED_SCHEME:
            case ERROR_UNKNOWN:
            default:
                return R.string.network_error_retry;
        }
    }

    public void hideWebView(int messageResId) {
        if (!isAdded() || isDetached()) {
            return;
        }

        mWebView.stopLoading();
        bLoaded = false;
        mAvailable = false;
        mRetryLayout.setVisibility(View.VISIBLE);
        mMessage.setText(messageResId);
        mIcon.setImageResource(R.drawable.ic_no_network);
        mWebView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
    }

    private static final String TAG = LogUtil.customTagPrefix + ":FragmentWebview";
    @JavascriptInterface
    public void tapBanner(String url, String title) {
        Log.v(TAG, "tap Banner");
        Log.v(TAG, "title : " + title + " URL : " + url);

        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.playSoundEffect(SoundEffectConstants.CLICK);
            }
        });

        Intent intent = new Intent(mWebView.getContext(), WebViewDemoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        url = mUrl.substring(0,mUrl.lastIndexOf("/")) + "/" + url;

        Log.v(TAG, "tapBanner, targetUrl = " + url);
        intent.putExtra(WebViewDemoActivity.WEBVIEWACTIVITY_EXTRA_URL, url);
        intent.putExtra(WebViewDemoActivity.WEBVIEWACTIVITY_EXTRA_TITLE, title);
        mWebView.getContext().startActivity(intent);
    }
}

