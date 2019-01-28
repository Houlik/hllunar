# hllunar
公历 - 农历    
主要用于将公历年月日转换获取对应的农历年月日    
    通过SubLunarCalendar.Builder来获取    
    
    调用方式如下:    
    1. 创建SubLunarCalendar.Builder 对象    
    2. setContext(当前Activity)    
    3. 通过process() 获取LunarCalendar对象    
    4. 使用LunarCalendar对象调用setOnLunarCalendarListener    
    5. 通过setOnLunarCalendarListener中两个方法分别获取getNumberic数字以及getMandarin中文的公历  
    6. getNumberic 和 getMandarin 当中数组参数的年月日是按照数组下标来获取  
        ints[0]年 ints[1]月 ints[2]日  
        strings[0]年 strings[1]月 strings[2]日
    7. 最后setOnLunarCalendarListener的第二个参数需求就是要查询的公历  
        输入方式 19781203 年月日
