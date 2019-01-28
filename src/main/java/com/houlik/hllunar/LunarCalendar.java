package com.houlik.hllunar;

/**
 * 公历转农历
 * 将1800.1.1 至 2100.10.1 的任意公历日期转为农历
 * 1800 到 2100年, 共300年
 * 配合config_calendar使用
 * Created by houlik on 2018/8/15.
 */

public class LunarCalendar {

    //从外部资源文件读取数据赋值到此数组中
    private String[] config_calendar;
    //公历年
    private String gcYearStr;
    private int gcYearInt = -1;
    //公历月
    private String gcMonthStr;
    private int gcMonthInt = -1;
    //公历日
    private String gcDayStr;
    private int gcDayInt = -1;
    //公历年月日
    private String gcDateStr;

    //公历分开年月日保存数组
    private String[] gcResultStr = new String[3];
    private int[] gcResultInt = new int[3];

    private String[] new_config_calendar;

    private String[] dataTopInit;

    private int FIRST_YEAR = -1;
    private int LAST_YEAR = -1;

    public LunarCalendar(String[] config_calendar) {
        this.config_calendar = config_calendar;
        if (config_calendar.length != 0) {
            dataTopInit = getConfigYear();
        }
    }

    /**
     * 计算得到的月份是经过了多少天
     *
     * @param month
     * @return
     */
    private int addDays(int month) {
        switch (month) {
            //31
            case 1:
                return 0;
            //28 - 29
            case 2:
                return 31;
            //31
            case 3:
                return 59;
            //30
            case 4:
                return 90;
            //31
            case 5:
                return 120;
            //30
            case 6:
                return 151;
            //31
            case 7:
                return 181;
            //31
            case 8:
                return 212;
            //30
            case 9:
                return 243;
            //31
            case 10:
                return 273;
            //30
            case 11:
                return 304;
            //31
            case 12:
                return 334;
            default:
                throw new RuntimeException("错误");
        }
    }

    /**
     * 判断是否闰年 366天
     * 普通闰年 - 能被4整除 但是不能被100整除的年份
     * 世纪闰年 - 能被400整除
     *
     * @param year
     * @return
     */
    private boolean isLeapYear(int year) {
        if (year % 172800 == 0 || year % 400 == 0 && year % 3200 != 0 || year % 4 == 0 && year % 100 != 0) {
            return true;
        }
        return false;
    }

    /**
     * 从ccYearStart 分开提取年月日以便通过算法获取天数
     *
     * @param ccYearStart
     * @return
     */
    private int getDays(String ccYearStart) {
        //从ccYearStart 8位数中取得年
        int year = Integer.parseInt(ccYearStart.substring(0, 4));
        //从ccYearStart 8位数中取得月
        int month = Integer.parseInt(ccYearStart.substring(4, 6));
        //从ccYearStart 8位数中取得日
        int day = Integer.parseInt(ccYearStart.substring(6, 8));
        //得到从春节到月的天数再加上从config得到天数
        int sum = addDays(month) + day;
        //如果是闰年 以及 大于 2月
        if (isLeapYear(year) && month > 2) {
            //就多加一天
            sum++;
        }
        //获取天数返回
        return sum;
    }

    /**
     * 阿拉伯年数转成农历年数
     *
     * @param year
     * @return
     */
    private String formatYear(int year) {
        String table = "零一二三四五六七八九";
        StringBuilder result = new StringBuilder("");
        int year2 = year;
        while (year2 != 0) {
            int every = year2 % 10;
            result.append(table.substring(every, every + 1));
            year2 /= 10;
        }
        return result.reverse().toString();
    }

    /**
     * 阿拉伯月数转成农历月数
     *
     * @param month
     * @return
     */
    private String formatMonth(int month) {
        String table = "正二三四五六七八九十冬腊";
        if (month > 12) {
            return "闰" + table.substring(month - 13, month - 12);
        }
        return table.substring(month - 1, month);
    }

    /**
     * 阿拉伯天数转成农历天数
     *
     * @param day
     * @return
     */
    private String formatDay(int day) {
        String table = "十一二三四五六七八九";
        int day1 = day / 10;
        int day2 = day % 10;
        day1 -= day2 == 0 ? 1 : 0;
        String result = table.substring(day2, day2 + 1);
        if (day == 30) {
            return "三十";
        } else if (day == 20) {
            return "二十";
        } else {
            if (day1 == 0) {
                return "初" + result;
            } else if (day1 == 1) {
                return "十" + result;
            } else if (day1 == 2) {
                return "廿" + result;
            } else {
                throw new RuntimeException("错误");
            }
        }
    }

    /**
     * 得到最新的config_calendar 数组
     *
     * @param data
     * @return
     */
    private String[] newestConfigCalendar(String[] data) {
        String[] dataTemp = new String[data.length - 1];
        for (int i = 1; i < data.length; i++) {
            //通过今年和去年的数据得到当前最新的数据保存到数组中
            dataTemp[i - 1] = add2Digit(data[i], data[i - 1]);
        }
        return dataTemp;
    }

    /**
     * 通过lastYear得到的二位数添加到thisYear
     *
     * @param thisYear
     * @param lastYear
     * @return 返回新添加的新数据
     */
    private String add2Digit(String thisYear, String lastYear) {
        //提取lastYear最后五位数
        String last = lastYear.substring(19);
        //提取通过判断得到的二位数
        String mid = cast2Digit(last);
        StringBuilder sb = new StringBuilder(thisYear);
        //通过StringBuilder在第九位数开始加上得到的二位数
        String result = sb.insert(9, mid).toString();
        return result;
    }

    /**
     * 公式算法
     * 获取农历年月日
     *
     * @param ccYearStart
     * @param gcDateStr
     * @param bigOrLitter
     * @param leap
     * @return 返回字符串类型
     */
    private int[] getLunarCalendar(String ccYearStart, String gcDateStr, int[] bigOrLitter, int leap) {
        //通过getDays方法获取config天数
        int numStart = getDays(ccYearStart);
        //通过getDays方法获取公历的天数
        int numNow = getDays(gcDateStr);
        //公历的天数减去config的天数
        int dif = numNow - numStart;

        //公式算法
        int sum = 0 - bigOrLitter[0] - bigOrLitter[1] - 29 - 29;

        int i = 0;
        //当dif 大于等于 公式算法
        while (dif >= sum) {
            //sum自身加上数组通过下标自增加的数据再加上 29
            sum += (bigOrLitter[i++] + 29);
        }
        //年数类型转换
        int year = Integer.parseInt(gcDateStr.substring(0, 4));
        //初始化新的数组用于保存得到的农历年月日
        int[] result = new int[3];
        //保存年 - 如果dif小于零返回 减少一年 否则不改变
        result[0] = dif < 0 ? year - 1 : year;
        //保存月 - 如果 i 减 2 小于等于0 i自身多加10 否则 i自身减去2
        result[1] = i - 2 <= 0 ? i + 10 : i - 2;

        //如果dif大于等于零
        if (dif >= 0) {
            //如果data_Config_Calendar最后两位数转换成整数类型不等于零
            if (leap != 0) {
                //如果月 等于 data_Config_Calendar最后两位数转换成整数类型加 1
                if (result[1] == leap + 1) {
                    //就把自身多加 11
                    result[1] += 11;
                    //如果月 大于 data_Config_Calendar最后两位数转换成整数类型加 1
                } else if (result[1] > leap + 1) {
                    //月自减
                    result[1]--;
                }
            }
            //否则
        } else {
            int numYear = year;
            int startYear = FIRST_YEAR;
            String[] dataInit = dataTopInit;
            String data = dataInit[numYear - startYear];
            String leapStr = data.substring(23, 25);
            int lastLeap = Integer.parseInt(leapStr);
            if (lastLeap != 0) {
                if (result[1] == lastLeap) {
                    if (lastLeap == 11) {
                        result[1] = 23;
                    } else if (lastLeap == 12) {
                        result[1] = 24;
                    } else {
                        throw new RuntimeException("闰年错误,请联系开发者改正");
                    }
                } else {
                    if (lastLeap == 11 && result[1] == 12 || lastLeap == 12 && result[1] == 11) {
                        result[1] = 12;
                    } else {
                        throw new RuntimeException("闰年错误,请联系开发者改正");
                    }
                }
            }
        }
        result[2] = dif - sum + bigOrLitter[i - 1] + 29 + 1;
        return result;
    }

    /**
     * 根据最后两位数来判断应该提取哪两位数返回
     *
     * @param last5Digit
     * @return
     */
    private String cast2Digit(String last5Digit) {
        //如果最后两位数是00
        if (last5Digit.substring(4).trim().equals("00")) {
            //返回last的前两位数字
            return last5Digit.substring(0, 2);
        } else {
            //返回last的第二位到第三位数字
            return last5Digit.substring(1, 3);
        }
    }

    /**
     * 转换查询农历之前所需的信息传递给getLunarCalendar计算
     *
     * @return 返回农历年月日数组
     */
    private int[] castLunarCalendarInformation() {
        //得到最新的config_calendar
        new_config_calendar = newestConfigCalendar(dataTopInit);

        //得到公历的年
        String year = gcDateStr.substring(0, 4);
        //转换成整数
        int numYear = Integer.parseInt(year);
        //通过初始年减去输入的年得到 config_calendar数据
        String data_Config_calendar = new_config_calendar[numYear - FIRST_YEAR];
        //取得data_Config_Calendar 的春节年月日
        String ccYearStart = data_Config_calendar.substring(0, 8);
        //取得data_config_Calendar 的大小月记录
        String bigOrLitterMonth = data_Config_calendar.substring(9, 24);
        //取得data_Config_Calendar 的最后两位数
        String leapStr = data_Config_calendar.substring(25, 27);
        //初始化数组用于单个保存大小月
        int[] bigOrLitter = new int[15];
        for (int i = 0; i < bigOrLitter.length; i++) {
            bigOrLitter[i] = Integer.parseInt(bigOrLitterMonth.substring(i, i + 1));
        }
        //把data_Config_Calendar最后两位数转换成整数类型
        int leap = Integer.parseInt(leapStr);
        return getLunarCalendar(ccYearStart, gcDateStr, bigOrLitter, leap);
    }

    /**
     * 使用正则表达式判断日期格式
     * 解析各种输入的日期格式为统一的8位数字格式
     *
     * @param date
     * @return 返回整数数组
     */
    private int[] useRegexJudgeDate(String date) {
        //如果是八位数字 19781203
        if (date.matches("\\d{8}")) {
            gcYearInt = Integer.parseInt(date.substring(0, 4));
            gcMonthInt = Integer.parseInt(date.substring(4, 6));
            gcDayInt = Integer.parseInt(date.substring(6, 8));
            // 1978-12-03
        } else if (date.matches("\\d+-\\d{1,2}-\\d{1,2}")) {
            String[] dateArray = date.split("-");
            gcYearInt = Integer.parseInt(dateArray[0]);
            gcMonthInt = Integer.parseInt(dateArray[1]);
            gcDayInt = Integer.parseInt(dateArray[2]);
            // 1978.12.03
        } else if (date.matches("\\d+\\.\\d{1,2}\\.\\d{1,2}")) {
            String[] dateArray = date.split("\\.");
            gcYearInt = Integer.parseInt(dateArray[0]);
            gcMonthInt = Integer.parseInt(dateArray[1]);
            gcDayInt = Integer.parseInt(dateArray[2]);
            // 1978/12/03
        } else if (date.matches("\\d+/\\d{1,2}/\\d{1,2}")) {
            String[] dateArray = date.split("/");
            gcYearInt = Integer.parseInt(dateArray[0]);
            gcMonthInt = Integer.parseInt(dateArray[1]);
            gcDayInt = Integer.parseInt(dateArray[2]);
            // 1978年12月03日
        } else if (date.matches("\\d+年\\d{1,2}月\\d{1,2}日")) {
            String[] dateArray = date.split("年|月|日");
            gcYearInt = Integer.parseInt(dateArray[0]);
            gcMonthInt = Integer.parseInt(dateArray[1]);
            gcDayInt = Integer.parseInt(dateArray[2]);
        } else {
            return null;
        }
        gcResultInt[0] = gcYearInt;
        gcResultInt[1] = gcMonthInt;
        gcResultInt[2] = gcDayInt;

        return gcResultInt;
    }

    /**
     * 判断日期是否正常
     *
     * @return
     */
    private boolean judgeDate() {
        //如果月小于1以及大于12返回错误
        if (gcMonthInt > 12 || gcMonthInt < 1) {
            return false;
            //如果日小于1大于31返回错误
        } else if (gcDayInt > 31 || gcDayInt < 1) {
            return false;
            //如果天数是31但是月数是小月返回错误 - 小月是小于31天 如 30 或者 二月28 或者 二月29
        } else if (gcDayInt == 31 && (gcMonthInt == 2 || gcMonthInt == 4 || gcMonthInt == 6 || gcMonthInt == 9 || gcMonthInt == 11)) {
            return false;
            //如果是二月份
        } else if (gcMonthInt == 2) {
            //如果是大于29返回错误
            if (gcDayInt > 29) {
                return false;
                //如果是闰年以及天数大于26返回错误
            } else if (!isLeapYear(gcYearInt) && gcDayInt > 28) {
                return false;
            } else {
                //否则返回真
                return true;
            }
        } else {
            //否则返回真
            return true;
        }
    }

    /**
     * 转换成字符串
     *
     * @return
     */
    private String cast2String() {
        gcYearStr = addZero(gcYearInt, 4);
        gcMonthStr = addZero(gcMonthInt, 2);
        gcDayStr = addZero(gcDayInt, 2);
        String result = gcYearStr + gcMonthStr + gcDayStr;
        return result;
    }

    /**
     * 如果num小于size自动在前方加上零
     *
     * @param num
     * @param size
     * @return
     */
    private String addZero(int num, int size) {
        //得到num长度
        int len = (num + "").length();
        //如果num长度小于size
        if (len < size) {
            //得到总共相差多少
            char[] chs = new char[size - len];
            for (int i = 0; i < chs.length; i++) {
                //相差多少就在前方加上多少
                chs[i] = '0';
            }
            //相加后返回字符串
            return new String(chs) + num;
        } else {
            return num + "";
        }
    }

    /**
     * 获取转换后的农历年月日
     */
    private void gregorian2LunarCalendar() {
        int[] result = null;
        result = castLunarCalendarInformation();

        onLunarCalendarListener.getNumberic(result);
        onLunarCalendarListener.getMandarin(new String[]{formatYear(result[0]), formatMonth(result[1]), formatDay(result[2])});
    }

    /**
     * 公历日期经过合法性检查后转农历日期
     *
     * @param date 公历日期
     */
    private void getDate(String date) {
        //使用正则表达式判断
        useRegexJudgeDate(date);
        //如果数组是空的
        if (gcResultInt == null) {
            throw new RuntimeException("-输入的日期不合法-");
            //如果是正常日期
        } else if (judgeDate()) {
            int year = gcResultInt[0];
            //如果该年是小于初始年或者大于config里的最大年
            if (year < FIRST_YEAR || year > LAST_YEAR) {
                //抛出异常
                throw new RuntimeException("-输入的日期年份超出范围,年份必须在" + FIRST_YEAR + "与" + LAST_YEAR + "之间-");
            } else {
                //得到转换后的字符串
                gcDateStr = cast2String();
                gregorian2LunarCalendar();
            }
        } else {
            //抛出异常
            throw new RuntimeException("-输入的日期不合法-");
        }
    }

    /**
     * 从config取得开始年 和 结束年
     *
     * @return
     */
    private String[] getConfigYear() {
        //取得config_calendar 第二行前四位数字
        FIRST_YEAR = Integer.parseInt(config_calendar[1].substring(0, 4));
        //取得config_calendar 最后一行前四位数字
        LAST_YEAR = Integer.parseInt(config_calendar[config_calendar.length - 1].substring(0, 4));
        return config_calendar;
    }

    private OnLunarCalendarListener onLunarCalendarListener;

    public interface OnLunarCalendarListener {
        /**
         * 得到阿拉伯数字的农历
         * 下标 0为年 1为月 2为日
         */
        void getNumberic(int[] numberic);

        /**
         * 得到中文字符串的农历
         * 下标 0为年 1为月 2为日
         */
        void getMandarin(String[] mandarin);
    }

    /**
     * 输入年月日字符串，从回调模式得到相对应的日期
     * @param onLunarCalendarListener
     * @param date 公历 格式 为8个数字，例如19781203 年月日
     */
    public void setOnLunarCalendarListener(OnLunarCalendarListener onLunarCalendarListener, String date) {
        this.onLunarCalendarListener = onLunarCalendarListener;
        getDate(date);
    }
}
