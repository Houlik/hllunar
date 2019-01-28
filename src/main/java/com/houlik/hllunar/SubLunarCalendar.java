package com.houlik.hllunar;

import android.content.Context;

/**
 * Created by houlik on 2018/11/14.
 */

public class SubLunarCalendar extends LunarCalendar {

    private SubLunarCalendar(String[] config_calendar) {
        super(config_calendar);
    }

    public static class Builder{
        private Context context;
        //这个文件已经保存在asset文件夹中
        private String assetsFile = "hllunar_config.txt";

        public Builder setContext(Context context){
            this.context = context;
            return this;
        }

        /**
         * 如果设置将覆盖当前assetsFile
         * @param assetsFile 资源文件名
         * @return
         */
        public Builder setAssetsFile(String assetsFile){
            this.assetsFile = assetsFile;
            return this;
        }

        /**
         * 开始执行
         * @return
         */
        public SubLunarCalendar process(){
            String[] config_calendar = AssetsUtils.getInstance().readAssets(context,assetsFile,"\n");
            return new SubLunarCalendar(config_calendar);
        }
    }
}
