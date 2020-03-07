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

/**
 * 作者：miaoyongyong on 2020/3/7 10:33

 * 邮箱：i_fadai@163.com
 */
class TextJumpView : View {

    val TAG = this.javaClass.simpleName

    // 文字列表
    private var mTextList: MutableList<TextBean> = ArrayList()
    // 文字大小
    private var mTextSize = 0F

    // 当前第几个文字
    private var mCurrentTextIndex = 0
    // 最大旋转角度（绝对值
    private var mMaxRotateAngle = 360F
    // 当前最大旋转角度
    private var mCurrentMaxRotateAngle = 360F
    // 当前旋转到多少度
    private var mCurrentRotateAngle = 0F
    // 文字可活动的最大高度
    private var mTextOffsetMaxH = 0F
    // 当前距离顶部的高度
    private var mCurrentOffsetH = 0F
    // 当前文字区域的宽高
    private var mTextAreaHeight = 0
    private var mTextAreaWidth = 0

    // 阴影最大宽
    private var mShadowMaxWidth = SizeUtils.dp2px(context, 28F).toFloat()
    // 阴影最小宽
    private var mShadowMinWidth = SizeUtils.dp2px(context, 10F).toFloat()
    // 阴影高度
    private val mShadowHeight = SizeUtils.dp2px(context, 8F).toFloat()
    // 阴影当前宽度
    private var mCurrentShadowWidth = 0F

    // 文字画笔
    private var mTextPaint: Paint = Paint()
    // 阴影画笔
    private var mShadowPaint: Paint = Paint()

    private var mUpAnimator: ValueAnimator? = null
    private var mDownAnimator: ValueAnimator? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        mTextSize = SizeUtils.dp2px(context, 16F).toFloat()
        mTextPaint.isAntiAlias = true
        mTextPaint.isFakeBoldText = true
        mTextPaint.textSize = mTextSize

        mShadowPaint.isAntiAlias = true
        mShadowPaint.color = Color.GRAY
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawText(canvas)
        drawShadow(canvas)
    }

    fun drawText(canvas: Canvas) {
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

    fun drawShadow(canvas: Canvas) {
        var textBean = mTextList[mCurrentTextIndex]
        mShadowPaint.color = textBean.color

        var shadowX = width / 2F - mCurrentShadowWidth / 2F
        var shadowY = height - mShadowHeight
        var rectF = RectF(shadowX, shadowY, shadowX + mCurrentShadowWidth, shadowY + mShadowHeight)
        canvas.drawOval(rectF, mShadowPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mTextOffsetMaxH = h - mShadowHeight - mTextSize
    }

    fun setTextList(textList: List<TextBean>) {
        mTextList.clear()
        mTextList.addAll(textList)
    }

    fun setTextSize(textSize: Float) {
        mTextSize = textSize
        mTextPaint.textSize = mTextSize
        mTextOffsetMaxH = height - mShadowHeight - mTextSize
    }

    fun start() {
        if (mTextList.size == 0) {
            return
        }
        mCurrentTextIndex = 0

        mUpAnimator = ValueAnimator.ofFloat(0F, 1F)
        mUpAnimator?.duration = 900
        mUpAnimator?.interpolator = AccelerateInterpolator()
        mUpAnimator?.addUpdateListener {
            var value = it.animatedValue as Float
            Log.d(TAG, "value=$value")
            mCurrentRotateAngle = mCurrentMaxRotateAngle * value
            mCurrentOffsetH = mTextOffsetMaxH * (1 - value)
            mCurrentShadowWidth =
                mShadowMinWidth + (mShadowMaxWidth - mShadowMinWidth) * value
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
        mDownAnimator?.duration = 800
        mDownAnimator?.interpolator = AccelerateInterpolator()
        mDownAnimator?.startDelay = 100
        mDownAnimator?.addUpdateListener {
            var value = it.animatedValue as Float
            mCurrentRotateAngle = 0F
//            mCurrentRotateAngle = mCurrentMaxRotateAngle * (1 - value)
            mCurrentOffsetH = mTextOffsetMaxH * value
            mCurrentShadowWidth =
                mShadowMinWidth + (mShadowMaxWidth - mShadowMinWidth) * (1 - value)
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

    private fun startAnimForInit() {
        getTextAreaSize()
        if (mCurrentTextIndex % 2 == 0) {// 偶数
            mCurrentMaxRotateAngle = mMaxRotateAngle
        } else {// 奇数
            mCurrentMaxRotateAngle = -mMaxRotateAngle
        }
    }

    fun getTextAreaSize() {
        var rect = getTextArea(mTextList.get(mCurrentTextIndex).text)
        mTextAreaWidth = rect.width()
        mTextAreaHeight = rect.height()
    }

    fun getTextArea(text: String): Rect {
        val rect = Rect()
        mTextPaint.getTextBounds(text, 0, text.length, rect)
        return rect
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

}