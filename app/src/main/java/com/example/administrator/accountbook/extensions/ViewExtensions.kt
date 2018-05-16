package com.example.administrator.accountbook.extensions

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.administrator.accountbook.R


/**
 * Created by Administrator on 2017/8/27 0027.
 * view 的工具类
 */
val View.ctx: Context get() = context


fun ImageView.show(url: String?) {
    Glide.with(this.ctx).load(url)
            .placeholder(R.drawable.space)
            .error(R.drawable.space)
            .centerCrop()
            .crossFade()
            .dontAnimate().into(this)
}

fun ImageView.show(id: Int?) {
    Glide.with(this.ctx)
            .load(id)
            .placeholder(R.drawable.space)
            .error(R.drawable.space)
            .centerCrop()
            .crossFade()
            .dontAnimate().into(this)
}


fun Float.dp2px2Int(context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            this, context.resources.displayMetrics).toInt()
}

fun Float.dp2px(context: Context): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            this, context.resources.displayMetrics)
}

fun Float.sp2px(context: Context): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            this, context.resources.displayMetrics)
}

fun View.visible() {
    val a: Animation = AnimationUtils.loadAnimation(this.ctx, R.anim.animate_visible)
    this.visibility = View.VISIBLE
    this.animation = a
    a.start()

}

fun View.invisible() {
    val a: Animation = AnimationUtils.loadAnimation(this.ctx, R.anim.animate_invisible)
    this.visibility = View.GONE
    this.animation = a
    a.start()

}