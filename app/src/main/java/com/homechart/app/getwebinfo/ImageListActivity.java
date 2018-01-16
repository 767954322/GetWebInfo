package com.homechart.app.getwebinfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by gumenghao on 18/1/15.
 */

public class ImageListActivity extends BaseActivity implements View.OnClickListener {
    private List<String> imagelist;
    private TextView mTital;
    private ImageButton mBack;
    private ListView mListView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_image_list;
    }

    @Override
    protected void initExtraBundle() {
        super.initExtraBundle();
        imagelist = (List<String>) getIntent().getSerializableExtra("imagelist");
    }

    @Override
    protected void initView() {

        mTital = (TextView) findViewById(R.id.tv_tital_comment);
        mListView = (ListView) findViewById(R.id.lv_listview);
        mBack = (ImageButton) findViewById(R.id.nav_left_imageButton);

    }

    @Override
    protected void initListener() {
        super.initListener();
        mBack.setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mTital.setText("采集到的图片");
        MyImageAdapter mAdapter = new MyImageAdapter(ImageListActivity.this, imagelist);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_left_imageButton:
                ImageListActivity.this.finish();
                break;
        }
    }

}
