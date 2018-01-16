package com.homechart.app.getwebinfo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private WebView mWeb;
    private EditText mEdit;
    public String pageTitle;
    private String pageDescription = "";
    private TextView mTVLoad;
    private Button mButton;
    private List<String> imgs;
    private String[] urlStr;
    private List<String> listCan;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mWeb = (WebView) findViewById(R.id.wb_webview);
        mEdit = (EditText) findViewById(R.id.et_edittext);
        mTVLoad = (TextView) findViewById(R.id.tv_search);
        mButton = (Button) findViewById(R.id.bt_image);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        listCan = new ArrayList<>();
        initWebView();
        mEdit.setText(PublicUtils.SHOP_DETAILS);
        mEdit.setSelection(PublicUtils.SHOP_DETAILS.length());
        mWeb.loadUrl(PublicUtils.SHOP_DETAILS);
    }

    @Override
    protected void initListener() {
        mTVLoad.setOnClickListener(this);
        mButton.setOnClickListener(this);
    }

    private void initWebView() {
        mWeb.removeJavascriptInterface("searchBoxJavaBredge_");//禁止远程代码执行
        WebSettings settings = mWeb.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setSupportMultipleWindows(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
//        settings.setSupportZoom(true);          //支持缩放
        settings.setBuiltInZoomControls(false);  //不启用内置缩放装置
        settings.setJavaScriptEnabled(true);    //启用JS脚本
        mWeb.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        settings.setUserAgentString(PublicUtils.USER_AGENT);

        mWeb.setWebChromeClient(new WebChromeClient() {
                                    @Override
                                    public void onReceivedTitle(WebView view, String title) {
                                        super.onReceivedTitle(view, title);
                                        try {
                                            pageTitle = title;
                                        } catch (Exception e) {
                                        }
                                    }
                                }
        );
        mWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl(PublicUtils.FINISH_URL);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_search:
                listCan.clear();
                String strUrl = mEdit.getText().toString();
                urlStr = strUrl.split("//");
                if (!strUrl.trim().equals("")) {
                    mButton.setVisibility(View.GONE);
                    mWeb.loadUrl(strUrl);
                } else {
                    Toast.makeText(this, "链接不能为空", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.bt_image:

                Intent intent = new Intent(MainActivity.this, ImageListActivity.class);
                intent.putExtra("imagelist", (Serializable) listCan);
                startActivity(intent);

                break;
        }

    }

    /**
     * js接口
     */
    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            getHtmlContent(html);
        }
    }

    /**
     * 获取内容
     *
     * @param html
     */
    protected void getHtmlContent(final String html) {
//        html页面内容
        Document document = Jsoup.parse(html);
        //通过类名获取到一组Elements，获取一组中第一个element并设置其html
//                Elements elements = document.getElementsByClass("loadDesc");
//                elements.get(0).html("<p>test</p>");
        //通过ID获取到element并设置其src属性
//                Element element = document.getElementById("imageView");
//                element.attr("src","file:///test/dragon.jpg");
        imgs = new ArrayList<>();
        Elements links = document.select("img[src]");
        for (Element element : links) {
            String url = element.attr("src");
            imgs.add(url);
        }
        if (imgs.size() > 0) {
            mHandler.sendEmptyMessage(0);
        } else {
            mHandler.sendEmptyMessage(1);
        }
//        pageDescription = document.select("meta[name=description]").get(0).attr("content");
//        Log.d("LOGCAT", "description:" + pageDescription + imgs.toString());
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 0:
                    for (int i = 0; i < imgs.size(); i++) {
                        String url = imgs.get(i).toString();
                        if (urlStr != null && urlStr.length > 1 && url.substring(0, 2).equals("//")) {
                            url = urlStr[0] + url;
                            imgs.set(i, url);
                        }
                    }

                    for (int i = 0; i < imgs.size(); i++) {
                        if (isConnect(imgs.get(i)) && isConnectImage(imgs.get(i))) {
                            listCan.add(imgs.get(i));
                        }
                    }
                    if (listCan.size() > 0) {
                        mButton.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "无可用图片链接", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "页面无图片", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public boolean isConnect(String urlStr) {
        if (Patterns.WEB_URL.matcher(urlStr).matches()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isConnectImage(String urlStr) {
        if (urlStr.contains("gif") ||
                urlStr.contains("jpg") ||
                urlStr.contains("png") ||
                urlStr.contains("jpeg") ||
                urlStr.contains("GIF") ||
                urlStr.contains("JPG") ||
                urlStr.contains("PNG") ||
                urlStr.contains("JPEG")) {
            return true;
        } else {
            return false;
        }
    }
}
