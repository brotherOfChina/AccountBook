package com.example.administrator.accountbook.bill

/**
 * Created by 赵鹏军
 * on 2018/5/19 0019.
 */

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator

import com.bumptech.glide.util.Util
import com.example.administrator.accountbook.extensions.dp2px
import com.example.administrator.accountbook.extensions.dp2px2Int

import java.util.ArrayList

/**
 *
 */
class SelectPieView : View {
    private var mWidth: Int = 0
    private var mCallBack: SelectPieCallBack? = null
    private var initPostion = false
    /**
     * 当前选中的区域
     */
    private var currentDownPostion: Int = 0

    /**
     * 笔宽
     */
    private var mPaintWid: Int = 0
    /**
     * 外边圆半径
     */
    private var mOutRoot: Int = 0
    /**
     * 内边圆半径
     */
    private var mIntRoot: Int = 0
    /**
     * 空白处宽度
     */
    private val emptysize = -1
    /**
     * 点击前的圆和点击后的圆半径差距
     */
    private val betweenSize = 10f
    /**
     * 向限
     */
    private val XIANGXAIN: Int = 0
    /**
     * 开始的角度
     */
    private var start = 360f
    /**
     * 旋转过的角度
     */
    private val haveRoats = ArrayList<startAndRoatData>()
    /**
     *
     */
     var mTitle = "总消费"
    /**
     *
     */
    private var mSubTitle = "00"
    /**
     *
     */
    private var mSubTitleDot = "00"
    /**
     * 是否在运行
     */
    private var isRun = true

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 整数部分
     */
    private val textPaintSubTitle = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 小数部分
     */
    private val textPaintSubTitleDot = Paint(Paint.ANTI_ALIAS_FLAG)

    private var Lxy2: Int = 0

    private val datas = ArrayList<PieData>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width: Int
        var hight: Int

        val widthmode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthsize = View.MeasureSpec.getSize(widthMeasureSpec)

        val hightmode = View.MeasureSpec.getMode(heightMeasureSpec)
        val hightsize = View.MeasureSpec.getSize(heightMeasureSpec)

        if (View.MeasureSpec.EXACTLY == widthmode) {
            width = widthsize
        } else {
            width = 200
            if (View.MeasureSpec.AT_MOST == widthmode) {
                width = Math.min(widthsize, 200)
            }
        }

        if (View.MeasureSpec.EXACTLY == hightmode) {
            hight = hightsize
        } else {
            hight = 200
            if (View.MeasureSpec.AT_MOST == hightmode) {
                hight = Math.min(hightsize, 200)
            }
        }

        setMeasuredDimension(Math.min(width, hight), Math.min(width, hight))
    }

    override fun onDraw(canvas: Canvas) {
        // reSetData();
        if (null == datas || datas.size == 0)
            return
        mWidth = width
        mOutRoot = mWidth / 2
        mPaintWid = 40f.dp2px2Int(context)
        mIntRoot = mOutRoot - mPaintWid

        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = mPaintWid.toFloat()
        val rt = RectF(mPaintWid / 2 + betweenSize, mPaintWid / 2 + betweenSize,
                mWidth.toFloat() - (mPaintWid / 2).toFloat() - betweenSize, mWidth.toFloat() - (mPaintWid / 2).toFloat() - betweenSize)
        val rt2 = RectF((mPaintWid / 2).toFloat(), (mPaintWid / 2).toFloat(), (mWidth - mPaintWid / 2).toFloat(), (mWidth - mPaintWid / 2).toFloat())

        val size = datas.size
        var allValues = 0f
        for (i in datas.indices) {
            allValues += datas[i].valuse
        }
        // allValues = allValues + emptysize * size;
        val sigleSize = (360 - emptysize * datas.size) / (allValues * 1f)

        var end = 0f
        haveRoats.clear()
        for (i in 0 until size) {
            paint.color = resources.getColor(datas[i].color)
            end = datas[i].valuse * sigleSize

            if (!isRun && datas[i].postion == currentDownPostion && datas.size > 1) {
                canvas.drawArc(rt2, start + 3, end - 6, false, paint)
                canvas.drawArc(rt, start + 3, end - 6, false, paint)
            } else {
                canvas.drawArc(rt, start, end, false, paint)
            }
            Log.i(TAG, "first=" + start % 360 + "==" + end + "postion=" + datas[i].postion)

            haveRoats.add(startAndRoatData(datas[i].postion, start % 360, end))

            start = start + end + emptysize.toFloat()

        }

//        textPaint.strokeWidth = Util.dip2px(context, 1)
        textPaint.strokeWidth = 1f.dp2px(context)
        /** 画图片  */
        for (i in haveRoats.indices) {
            val startAndRoatData = haveRoats[i]

            var x = 0f
            var y = 0f

            if (!isRun && currentDownPostion == haveRoats[i].postion && datas.size > 1) {

                x = (Math
                        .cos(Math.PI / 180 * (startAndRoatData.startAng + startAndRoatData.roatAng / 2)) * (mIntRoot + mPaintWid / 2) + mOutRoot).toFloat()
                y = (Math
                        .sin(Math.PI / 180 * (startAndRoatData.startAng + startAndRoatData.roatAng / 2)) * (mIntRoot + mPaintWid / 2) + mOutRoot).toFloat()
            } else {
                x = (Math
                        .cos(Math.PI / 180 * (startAndRoatData.startAng + startAndRoatData.roatAng / 2)) * (mIntRoot + mPaintWid / 2 - betweenSize) + mOutRoot).toFloat()
                y = (Math
                        .sin(Math.PI / 180 * (startAndRoatData.startAng + startAndRoatData.roatAng / 2)) * (mIntRoot + mPaintWid / 2 - betweenSize) + mOutRoot).toFloat()
            }

            val rect = Rect((x - mPaintWid / 3).toInt(), (y - mPaintWid / 3).toInt(), (x + mPaintWid / 3).toInt(),
                    (y + mPaintWid / 3).toInt())

            val width = BitmapFactory.decodeResource(resources, datas[i].icon).width
            // L=n（圆心角度数）× π（圆周率）× r（半径）/180（角度制）
            if (startAndRoatData.roatAng.toDouble() * Math.PI * (mIntRoot + mPaintWid / 2).toDouble() / 180 > width) {
                canvas.drawBitmap(BitmapFactory.decodeResource(resources, datas[i].icon), null, rect, null)
            }
        }

        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 14f.dp2px(context)
        /** 写文字  */
        canvas.drawText(mTitle, mWidth / 2 - textPaint.measureText(mTitle) / 2,
                mWidth / 2 - 8f.dp2px(context), textPaint)

        textPaintSubTitle.textSize = 20f.dp2px(context)
        canvas.drawText(mSubTitle,
                (mWidth / 2).toFloat() - textPaintSubTitle.measureText(mSubTitle) / 2 - textPaintSubTitleDot.measureText(".$mSubTitleDot") / 2,
                mWidth / 2 + 15f.dp2px(context), textPaintSubTitle)

        textPaintSubTitleDot.textSize = 15f.dp2px(context)
        canvas.drawText(".$mSubTitleDot",
                mWidth / 2 + textPaintSubTitle.measureText(mSubTitle) / 2 - textPaintSubTitleDot.measureText(".$mSubTitleDot") / 2,
                mWidth / 2 +15f.dp2px(context), textPaintSubTitleDot)
        //Toast.makeText(getContext(), "=="+textPaint.measureText(mSubTitle), Toast.LENGTH_SHORT).show();
        /** 测试基线  */
        /*
         * paint.setColor(Color.BLACK);
         * paint.setStrokeWidth(Util.dip2px(getContext(), 1));
         *
         * canvas.drawLine(0, width / 2, width, width / 2, paint);
         * canvas.drawLine(width / 2, 0, width / 2, width, paint);
         */
        /**
         * 初始化位置
         */
        if (!initPostion) {
            initPostion = true

            val roatData = haveRoats[0]
            val currentCenterAng = roatData.startAng + roatData.roatAng / 2
            if (currentCenterAng < 90) {
                starRoat(start, start + 90 - (roatData.startAng + roatData.roatAng / 2), false)
            } else if (currentCenterAng > 90 && currentCenterAng < 270) {
                starRoat(start, start - (currentCenterAng - 90), false)
            } else {
                starRoat(start, start + 360f + 90f - (roatData.startAng + roatData.roatAng / 2), false)
            }
            currentDownPostion = roatData.postion
            isRun = false
            invalidate()
        }

    }

    /**
     * 设置显示金额
     *
     * @param number
     */
    fun setNumber(number: String) {
        var number = number
        if (TextUtils.isEmpty(number)) {
            number = "0.00"
        }
        if (number.contains(".")) {
            val split = number.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            mSubTitle = split[0]
            mSubTitleDot = split[1]
        } else {
            mSubTitle = number
            mSubTitleDot = "00"
        }
        if (mSubTitleDot.length > 2) {
            mSubTitleDot = mSubTitleDot.substring(0, 2)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                val atan = Math.atan(((x - mOutRoot) / (mOutRoot - y)).toDouble())
                val currntAngle = atan / Math.PI * 180
                var clickAngle = 0.0
                Lxy2 = Math.pow((x - mOutRoot).toDouble(), 2.0).toInt() + Math.pow((mOutRoot - y).toDouble(), 2.0).toInt()
                if (Math.pow(mIntRoot.toDouble(), 2.0) < Lxy2 && Lxy2 < Math.pow(mOutRoot.toDouble(), 2.0)) {

                    if (x > mWidth / 2 && y > mWidth / 2) {
                        /** currntAngle第四象限是负数  */
                        // starRoat(start, (float) (start - currntAngle), true);
                        clickAngle = currntAngle + 90

                    } else if (x > mWidth / 2 && y < mWidth / 2) {
                        /** currntAngle第一象限是负数  */
                        //starRoat(start, (float) (start + 180 - currntAngle), true);
                        clickAngle = currntAngle + 270
                    } else if (x < mWidth / 2 && y < mWidth / 2) {
                        /** currntAngle第二象限是正数  */
                        //starRoat(start, (float) (start - (180 - Math.abs(currntAngle))), true);
                        clickAngle = currntAngle + 270
                    } else {
                        /** currntAngle第三象限是正数  */
                        //starRoat(start, (float) (start - Math.abs(currntAngle)), true);
                        clickAngle = currntAngle + 90
                    }

                    val i = clickDownPostion(clickAngle)
                    val roatData = haveRoats[i]
                    currentDownPostion = roatData.postion
                    val currentCenterAng = roatData.startAng + roatData.roatAng / 2
                    if (currentCenterAng < 90) {
                        starRoat(start, start + 90 - (roatData.startAng + roatData.roatAng / 2), true)
                    } else if (currentCenterAng > 90 && currentCenterAng < 270) {
                        starRoat(start, start - (currentCenterAng - 90), true)
                    } else {
                        starRoat(start, start + 360f + 90f - (roatData.startAng + roatData.roatAng / 2), true)
                    }

                }

                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun clickDownPostion(clickAngle: Double): Int {
        for (i in haveRoats.indices) {
            val data = haveRoats[i]
            if (data.startAng < clickAngle && data.startAng + data.roatAng > clickAngle || data.startAng + data.roatAng > 360 && (data.startAng + data.roatAng) % 360 > clickAngle) {
                return i
            }
        }
        return 0
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun starRoat(star: Float, end: Float, isSmooth: Boolean) {
        val valueAnimator = ValueAnimator.ofFloat(star, end)
        valueAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            start = animatedValue
            isRun = true
            invalidate()
        }
        valueAnimator.duration = if (isSmooth) 700.toLong() else 10
        valueAnimator.interpolator = AccelerateInterpolator()
        valueAnimator.start()
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {

                if (currentDownPostion == -1) {
                    start++
                    starRoat(start, start++, false)
                } else {
                    isRun = false
                    invalidate()//画突出部分
                    mCallBack!!.currentPostion(currentDownPostion)
                }
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
    }

    fun reSetData(data: List<PieData>) {
        datas.clear()
        datas.addAll(data)
        var all = 0f
        for (da in datas) {
            all += da.valuse
        }
        if (all < 360) {
            for (da in datas) {
                da.valuse = da.valuse * 200
            }
        }
        for (da in datas) {
            all += da.valuse
        }
        /**强制设置最低值 */
        for (da in datas) {
            if (da.valuse / all < 0.03) {
                da.valuse = (all * 0.03).toFloat()
            }
        }

        invalidate()

    }

    /**
     * 判断当前选择的所在区间
     *
     * @return
     */
    private fun findCurrentDownPostion(): Int {
        if (haveRoats == null || haveRoats.size <= 0) {
            return 1
        }

        for (i in haveRoats.indices) {

            val startAng = haveRoats[i].startAng
            val roatAng = haveRoats[i].roatAng
            //Utility.Logi(TAG, "currentpostion=sstar=" + startAng + "===rroat=" + roatAng);
            if (startAng < 90 && startAng <= 90 && startAng + roatAng > 90) {
                // Utility.Logi(TAG, "currentpostion=" + haveRoats.get(i).getPostion());
                return haveRoats[i].postion
            } else if (startAng > 90 && startAng - 360 + roatAng > 90) {
                //Utility.Logi(TAG, "currentpostion=" + haveRoats.get(i).getPostion());
                return haveRoats[i].postion
            }
        }
        return -1
    }

    fun setCallBack(callBack: SelectPieCallBack) {
        this.mCallBack = callBack
    }

    interface SelectPieCallBack {
        fun currentPostion(postion: Int)
    }

    class PieData(var postion: Int, var valuse: Float, var color: Int, var icon: Int)

    internal inner class startAndRoatData(var postion: Int, var startAng: Float, var roatAng: Float) {

        override fun toString(): String {
            return "startAndRoatData{" + "postion=" + postion + ", startAng=" + startAng + ", roatAng=" + roatAng + '}'.toString()
        }
    }

    companion object {

        private val TAG = "CustomPie_tag"
    }
}
