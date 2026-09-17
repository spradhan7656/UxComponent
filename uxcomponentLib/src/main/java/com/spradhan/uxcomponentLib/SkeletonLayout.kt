package com.spradhan.uxcomponentLib

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import com.spradhan.uxcomponentLib.R

/**
 * Drop-in shimmer skeleton loader.
 *
 * Wrap your real content (the layout you'd normally show) inside a
 * SkeletonLayout. Call showSkeleton() while data is loading and
 * hideSkeleton() once it arrives. It automatically draws a rounded
 * placeholder block over every leaf view inside it and animates a
 * shimmer sweep across them — no need to hand-build placeholder XML.
 *
 * XML:
 * <com.skeletonloader.SkeletonLayout
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:skeletonAutoStart="true">
 *
 *     <!-- your real content, e.g. a card with an ImageView + two TextViews -->
 *     <LinearLayout ...>...</LinearLayout>
 *
 * </com.skeletonloader.SkeletonLayout>
 *
 * Kotlin:
 * skeletonLayout.showSkeleton()
 * // ...load data...
 * skeletonLayout.hideSkeleton()
 */
class SkeletonLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var baseColor = Color.parseColor("#E0E0E0")
    private var highlightColor = Color.parseColor("#F5F5F5")
    private var cornerRadiusPx = dp(6f)
    private var shimmerDurationMs = 1200L
    private var autoStart = false

    private var isShimmering = false
    private var shimmerAnimator: ValueAnimator? = null
    private var shimmerTranslate = 0f

    private val skeletonRects = mutableListOf<RectF>()
    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shimmerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var shimmerShader: LinearGradient? = null
    private val shaderMatrix = Matrix()

    private var contentView: View? = null

    init {
        attrs?.let { loadAttrs(it) }
        setWillNotDraw(false)
        basePaint.color = baseColor
    }

    private fun loadAttrs(attrs: AttributeSet) {
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SkeletonLayout)
        try {
            baseColor = ta.getColor(R.styleable.SkeletonLayout_skeletonBaseColor, baseColor)
            highlightColor = ta.getColor(R.styleable.SkeletonLayout_skeletonHighlightColor, highlightColor)
            cornerRadiusPx = ta.getDimension(R.styleable.SkeletonLayout_skeletonCornerRadius, cornerRadiusPx)
            shimmerDurationMs = ta.getInt(
                R.styleable.SkeletonLayout_skeletonShimmerDurationMs,
                shimmerDurationMs.toInt()
            ).toLong()
            autoStart = ta.getBoolean(R.styleable.SkeletonLayout_skeletonAutoStart, autoStart)
        } finally {
            ta.recycle()
        }
        basePaint.color = baseColor
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            contentView = getChildAt(0)
        }
        if (autoStart) {
            post { showSkeleton() }
        }
    }

    private fun updateShader() {
        val w = if (width > 0) width.toFloat() else return
        shimmerShader = LinearGradient(
            0f, 0f, w, 0f,
            intArrayOf(baseColor, highlightColor, baseColor),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        shimmerPaint.shader = shimmerShader
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w <= 0) return
        updateShader()
        // rects depend on layout size, refresh if currently shimmering
        if (isShimmering) {
            skeletonRects.clear()
            contentView?.let { collectRects(it, skeletonRects) }
            startShimmer() // Restart shimmer with the new actual width range
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isShimmering) {
            skeletonRects.clear()
            contentView?.let { collectRects(it, skeletonRects) }
        }
    }

    /** Hides the real content and starts the shimmer animation. */
    fun showSkeleton() {
        if (isShimmering) return
        isShimmering = true
        skeletonRects.clear()
        contentView?.let { collectRects(it, skeletonRects) } // Collect bounds first
        contentView?.visibility = View.INVISIBLE // Then hide content
        startShimmer()
        invalidate()
    }

    /** Stops the shimmer animation and reveals the real content. */
    fun hideSkeleton() {
        if (!isShimmering) return
        isShimmering = false
        stopShimmer()
        contentView?.visibility = View.VISIBLE
        invalidate()
    }

    fun isShowingSkeleton(): Boolean = isShimmering

    fun setBaseColor(color: Int) {
        baseColor = color
        basePaint.color = baseColor
        updateShader()
        invalidate()
    }

    fun setHighlightColor(color: Int) {
        highlightColor = color
        updateShader()
        invalidate()
    }

    fun setCornerRadius(radiusDp: Float) {
        cornerRadiusPx = dp(radiusDp)
        invalidate()
    }

    fun setShimmerDuration(durationMs: Long) {
        shimmerDurationMs = durationMs
        if (isShimmering) {
            startShimmer()
        }
    }

    private fun collectRects(view: View, out: MutableList<RectF>) {
        if (view != contentView && view.visibility != View.VISIBLE) return
        if (view is ViewGroup && view.childCount > 0) {
            for (i in 0 until view.childCount) {
                collectRects(view.getChildAt(i), out)
            }
        } else {
            val loc = IntArray(2)
            view.getLocationInWindow(loc)
            val myLoc = IntArray(2)
            this.getLocationInWindow(myLoc)
            val left = (loc[0] - myLoc[0]).toFloat()
            val top = (loc[1] - myLoc[1]).toFloat()
            if (view.width > 0 && view.height > 0) {
                out.add(RectF(left, top, left + view.width, top + view.height))
            }
        }
    }

    private fun startShimmer() {
        shimmerAnimator?.cancel()
        val w = if (width > 0) width.toFloat() else 1f
        shimmerAnimator = ValueAnimator.ofFloat(-w, w * 2f).apply {
            duration = shimmerDurationMs
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                shimmerTranslate = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun stopShimmer() {
        shimmerAnimator?.cancel()
        shimmerAnimator = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isShimmering) return
        for (rect in skeletonRects) {
            canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, basePaint)
            shimmerShader?.let { shader ->
                shaderMatrix.reset()
                shaderMatrix.setTranslate(shimmerTranslate, 0f)
                shader.setLocalMatrix(shaderMatrix)
                canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, shimmerPaint)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopShimmer()
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density
}
