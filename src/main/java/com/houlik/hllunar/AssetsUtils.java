package com.houlik.hllunar;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Assets 资源工具
 * Created by Houlik on 24/05/2017.
 */

public class AssetsUtils {

    private AssetManager assetManager;

    private static AssetsUtils assetsUtils = new AssetsUtils();

    private AssetsUtils(){}

    public static AssetsUtils getInstance(){
        if(assetsUtils == null){
            assetsUtils = new AssetsUtils();
        }
        return assetsUtils;
    }

    /**
     * 读取Assets文件夹中的图片资源
     * @param context
     * @param fileName 图片名称
     * @return 返回位图
     **/
    public Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        assetManager = context.getResources().getAssets();
        try {
            InputStream is = assetManager.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 得到Assets文件夹内的全部图片
     *
     * @param activity
     * @param folderName
     * @return 返回集合
     * @throws IOException
     */
    public List getAllImageFromAssetsFolder(Activity activity, String folderName) throws IOException {
        String[] resource = activity.getResources().getAssets().list(folderName);
        Bitmap[] bitmap;
        List list_Bitmap = new ArrayList();
        if (resource.length > 0) {
            bitmap = new Bitmap[resource.length];
            for (int i = 0; i < resource.length; i++) {
                bitmap[i] = getImageFromAssetsFile(activity, folderName + "/" + resource[i]);
                list_Bitmap.add(bitmap);
            }
        } else {
            bitmap = new Bitmap[0];
        }
        return list_Bitmap;
    }

    /**
     * 从Assets文件夹中读取数据流保存到数组中
     * @param context 上下文
     * @param assetsFile 文件名.格式
     * @param regex 正则表达式 - 字符串分割保存
     * @return 返回数组
     */
    public String[] readAssets(Context context, String assetsFile, String regex){
        try {
            InputStream is = context.getAssets().open(assetsFile);
            int size = is.available();
            byte[] b = new byte[size];
            is.read(b);
            is.close();
            //赋予给字符串
            String str = new String(b, "UTF-8");
            String[] arrStr = new String[size];
            //分割保存到数组
            arrStr = str.split("\n");
            return arrStr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
