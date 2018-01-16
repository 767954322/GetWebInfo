package com.homechart.app.getwebinfo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.homechart.app.getwebinfo.glide.GlideImgManager;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by gumenghao on 18/1/15.
 */

public class MyImageAdapter extends BaseAdapter {

    private int mWidth;
    private Context mContext;
    private List<String> imageList;

    public MyImageAdapter(Context mContext, List<String> imageList) {
        this.mContext = mContext;
        this.imageList = imageList;
        mWidth = getScreenWidth(mContext);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int i) {
        return imageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        MyHolder myHolder = null;
        if (convertView == null) {
            myHolder = new MyHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_image, null);
            myHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_image);
            convertView.setTag(myHolder);
        } else {
            myHolder = (MyHolder) convertView.getTag();
        }

//        //获取图片真正的宽高
//        Glide.with(mContext)
//                .load(imageList.get(i))
//                .asBitmap()//强制Glide返回一个Bitmap对象
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
//                        int width = bitmap.getWidth();
//                        int height = bitmap.getHeight();
//                    }
//                });
//
//        //获取图片显示在ImageView后的宽高
//        Glide.with(mContext)
//                .load(imageList.get(i))
//                .asBitmap()//强制Glide返回一个Bitmap对象
//                .listener(new RequestListener<String, Bitmap>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap bitmap, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        int width = bitmap.getWidth();
//                        int height = bitmap.getHeight();
//                        return false;
//                    }
//                }).into(myHolder.imageView);


//        double douBi = loadImageFromNetwork(imageList.get(i));
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myHolder.imageView.getLayoutParams();
//        layoutParams.width = mWidth;
//        layoutParams.height = (int) (mWidth / douBi);
//        myHolder.imageView.setLayoutParams(layoutParams);
//        GlideImgManager.glideLoader(mContext, imageList.get(i), R.mipmap.ic_launcher_round, R.mipmap.ic_launcher_round, myHolder.imageView);
        ImageUtils.displayFilletImage(imageList.get(i), myHolder.imageView);
        return convertView;
    }

    class MyHolder {
        ImageView imageView;
    }


    private double loadImageFromNetwork(String url) {
        try {
            URL m_url = new URL(url);
            HttpURLConnection con = (HttpURLConnection) m_url.openConnection();
            InputStream in = con.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            int height = options.outHeight;
            int width = options.outWidth;
            return div(width, height, 4);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */

    public double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取屏幕的宽度
     *
     * @param context
     * @return
     */
    public int getScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


}
