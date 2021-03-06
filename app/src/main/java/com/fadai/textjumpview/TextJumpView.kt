package com.fadai.textjumpview

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.addListener
import com.gz.goodneighbor.widget.loading.TextBean
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


/**
 * 作者：miaoyongyong on 2020/3/7 10:33

 * 邮箱：i_fadai@163.com
 */
class TextJumpView : View {

    val TAG = this.javaClass.simpleName

    // 文字列表
    private var mTextList: MutableList<TextBean> = ArrayList()
    // 文字大小
    private var mTextSize = SizeUtils.dp2px(context, 16F).toFloat()

    // 当前第几个文字
    private var mCurrentTextIndex = 0
    // 当前最大旋转角度
    private var mCurrentMaxRotateAngle = 360F
    // 当前旋转到多少度
    private var mCurrentRotateAngle = 0F
    // 文字可活动的最大高度
    private var mTextOffsetMaxH = 0F
    // 文字当前距离顶部的高度
    private var mCurrentOffsetH = 0F
    // 当前文字区域的宽高
    private var mTextAreaHeight = 0F
    private var mTextAreaWidth = 0F
    // 阴影当前宽度
    private var mCurrentShadowWidth = 0F

    // 阴影最大宽
    private val SHADOW_MAX_WIDTH = SizeUtils.dp2px(context, 28F).toFloat()
    // 阴影最小宽
    private val SHADOW_MIN_WIDTH = SizeUtils.dp2px(context, 8F).toFloat()
    // 阴影高度
    private val SHADOW_HEIGHT = SizeUtils.dp2px(context, 6F).toFloat()

    // 文字画笔
    private var mTextPaint: Paint = Paint()
    // 阴影画笔
    private var mShadowPaint: Paint = Paint()

    // 文字上升动画
    private var mUpAnimator: ValueAnimator? = null
    // 文字下降动画
    private var mDownAnimator: ValueAnimator? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        mTextPaint.isAntiAlias = true
        mTextPaint.isFakeBoldText = true
        mTextPaint.textSize = mTextSize

        mShadowPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawText(canvas)
        drawShadow(canvas)
    }

    // 绘制文字
    private fun drawText(canvas: Canvas) {
        if (mCurrentTextIndex >= mTextList.size)
            return
        var textBean = mTextList[mCurrentTextIndex]
        mTextPaint.color = textBean.color

        // 旋转中心
        var rotateCenterX = width / 2
        var rotateCenterY = mCurrentOffsetH + mTextAreaHeight / 2

        // 旋转画布
        canvas.rotate(mCurrentRotateAngle, rotateCenterX.toFloat(), rotateCenterY.toFloat())

        // 绘制文字
        var textX = width / 2F - mTextAreaWidth / 2F
        var textY = mCurrentOffsetH + mTextAreaHeight / 2 + getBaseline(mTextPaint)
        canvas.drawText(textBean.text, textX, textY, mTextPaint)

        // 复原状态
        canvas.rotate(-mCurrentRotateAngle, rotateCenterX.toFloat(), rotateCenterY.toFloat())
    }

    // 绘制阴影
    private fun drawShadow(canvas: Canvas) {
        var textBean = mTextList[mCurrentTextIndex]
        mShadowPaint.color = textBean.color

        // 起始点xy
        var shadowX = width / 2F - mCurrentShadowWidth / 2F
        var shadowY = height - SHADOW_HEIGHT

        var rectF = RectF(shadowX, shadowY, shadowX + mCurrentShadowWidth, shadowY + SHADOW_HEIGHT)
        canvas.drawOval(rectF, mShadowPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mTextOffsetMaxH = h - SHADOW_HEIGHT - mTextSize
    }

    // 设置文字列表
    fun setTextList(textList: List<TextBean>) {
        mTextList.clear()
        mTextList.addAll(textList)
    }

    // 设置文字大小
    fun setTextSize(textSize: Float) {
        mTextSize = textSize
        mTextPaint.textSize = mTextSize
        mTextOffsetMaxH = height - SHADOW_HEIGHT - mTextSize
    }

    // 开始动画
    fun start() {
        if (mTextList.size == 0) {
            return
        }
        mCurrentTextIndex = 0

        mUpAnimator = ValueAnimator.ofFloat(0F, 1F)
        mUpAnimator?.duration = 600
//        mUpAnimator?.interpolator = AccelerateInterpolator()
        mUpAnimator?.addUpdateListener {
            var value = it.animatedValue as Float
            Log.d(TAG, "value=$value")
            // 旋转角度
            mCurrentRotateAngle = mCurrentMaxRotateAngle * value
            // 当前偏移量
            mCurrentOffsetH = mTextOffsetMaxH * (1 - value)
            // 阴影宽度
            mCurrentShadowWidth =
                SHADOW_MIN_WIDTH + (SHADOW_MAX_WIDTH - SHADOW_MIN_WIDTH) * value
            postInvalidate()
        }
        mUpAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                mDownAnimator?.start()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })

        mDownAnimator = ValueAnimator.ofFloat(0F, 1F)
        mDownAnimator?.duration = 500
        mDownAnimator?.interpolator = AccelerateInterpolator()
        mDownAnimator?.startDelay = 100
        mDownAnimator?.addUpdateListener {
            var value = it.animatedValue as Float

            // 旋转角度
            mCurrentRotateAngle = 0F
            // 当前偏移量
            mCurrentOffsetH = mTextOffsetMaxH * value
            // 阴影宽度
            mCurrentShadowWidth =
                SHADOW_MIN_WIDTH + (SHADOW_MAX_WIDTH - SHADOW_MIN_WIDTH) * (1 - value)
            postInvalidate()
        }
        mDownAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                mCurrentTextIndex++
                if (mCurrentTextIndex >= mTextList.size) {
                    mCurrentTextIndex = 0
                }
                startAnimForInit()
                mUpAnimator?.start()
            }


            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })

        startAnimForInit()
        mUpAnimator?.start()

    }

    // 动画开始前的初始化
    private fun startAnimForInit() {
        getTextAreaSize()
        if (mCurrentTextIndex % 2 == 0) {// 偶数
            mCurrentMaxRotateAngle = 360F
        } else {// 奇数
            mCurrentMaxRotateAngle = -360F
        }
    }

    // 获取文字区域的大小，并赋值
    fun getTextAreaSize() {
        var w = mTextPaint.measureText(mTextList.get(mCurrentTextIndex).text);
        val fm = mTextPaint.getFontMetrics()
        //文字基准线的下部距离-文字基准线的上部距离 = 文字高度
        var h = fm.descent - fm.ascent

        mTextAreaWidth = w
        mTextAreaHeight = h
    }

    /**
     * 计算绘制文字时的基线到中轴线的距离
     *
     * @param p
     * @param centerY
     * @return 基线和centerY的距离
     */
    fun getBaseline(p: Paint): Float {
        val fontMetrics = p.fontMetrics
        return (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mUpAnimator?.isRunning == true) mUpAnimator?.cancel()
        if (mDownAnimator?.isRunning == true) mDownAnimator?.cancel()
    }
}