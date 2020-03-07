package com.fadai.textjumpview

import android.content.Context

/**
 * 作者：miaoyongyong on 2020/3/7 15:01

 * 邮箱：i_fadai@163.com
 */
object SizeUtils {
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.getResources().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }
}