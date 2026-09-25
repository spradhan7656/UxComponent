package com.spradhan.uxcomponentLib

import android.R.attr.strokeColor
import android.R.attr.strokeWidth
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.View.LAYER_TYPE_SOFTWARE

object CommonUtils {
    fun createDynamicGradient(
        context: Context, config: GradientConfig
    ): Drawable {

        val density = context.resources.displayMetrics.density

        fun buildCornerRadii(): FloatArray? = when {
            config.cornerRadii != null && config.cornerRadii.size == 8 ->
                config.cornerRadii.map { it * density }.toFloatArray()

            config.topLeftRadiusDp > 0f || config.topRightRadiusDp > 0f ||
                    config.bottomRightRadiusDp > 0f || config.bottomLeftRadiusDp > 0f ->
                floatArrayOf(
                    config.topLeftRadiusDp * density, config.topLeftRadiusDp * density,
                    config.topRightRadiusDp * density, config.topRightRadiusDp * density,
                    config.bottomRightRadiusDp * density, config.bottomRightRadiusDp * density,
                    config.bottomLeftRadiusDp * density, config.bottomLeftRadiusDp * density
                )

            else -> null
        }

        fun applyCorners(drawable: GradientDrawable, radii: FloatArray?) {
            when {
                radii != null -> drawable.cornerRadii = radii
                config.cornerRadiusDp > 0f -> drawable.cornerRadius = config.cornerRadiusDp * density
            }
        }

        val cornerRadii = buildCornerRadii()
        val strokeWidthPx = (config.strokeWidthDp * density).toInt()
        val hasGradientStroke = config.strokeColors != null && config.strokeColors.isNotEmpty() && strokeWidthPx > 0

        // ---------- Main fill shape ----------
        val mainDrawable = GradientDrawable().apply {
            shape = config.shape ?: GradientDrawable.RECTANGLE

            config.solidColor?.let { setColor(it) }

            config.colors?.let { colorsArray ->
                if (colorsArray.isNotEmpty()) {
                    if (config.positions != null && config.positions.size == colorsArray.size && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        setColors(colorsArray, config.positions)
                    } else {
                        setColors(colorsArray)
                    }

                    gradientType = config.gradientType
                    when (config.gradientType) {
                        GradientDrawable.LINEAR_GRADIENT -> {
                            orientation = config.orientation ?: GradientDrawable.Orientation.TOP_BOTTOM
                        }
                        GradientDrawable.RADIAL_GRADIENT -> {
                            setGradientCenter(config.gradientCenterX, config.gradientCenterY)
                            gradientRadius = config.gradientRadius * density * 100
                        }
                        GradientDrawable.SWEEP_GRADIENT -> {
                            setGradientCenter(config.gradientCenterX, config.gradientCenterY)
                        }
                    }
                }
            }

            applyCorners(this, cornerRadii)

            // Plain solid stroke — only when no gradient stroke was requested
            if (!hasGradientStroke && strokeWidthPx > 0) {
                val dashWidth = config.strokeDashWidthDp * density
                val dashGap = config.strokeDashGapDp * density
                setStroke(strokeWidthPx, config.strokeColor, dashWidth, dashGap)
            }
        }

        // ---------- Gradient stroke (border) ----------
        val contentDrawable: Drawable = if (hasGradientStroke) {
            val strokeDrawable = GradientStrokeDrawable(
                strokeWidth = config.strokeWidthDp * density,
                strokeColors = config.strokeColors!!,
                strokePositions = config.strokePositions,
                cornerRadius = config.cornerRadiusDp * density,
                cornerRadii = if (cornerRadii != null) cornerRadii else null,
                orientation = config.strokeGradientOrientation ?: GradientDrawable.Orientation.LEFT_RIGHT,
                dashWidth = config.strokeDashWidthDp * density,
                dashGap = config.strokeDashGapDp * density
            )

            LayerDrawable(arrayOf(mainDrawable, strokeDrawable))
        } else {
            mainDrawable
        }

        // If no elevation requested, return the (possibly bordered) shape as-is
        if (config.elevationDp <= 0f) return contentDrawable

        // ---------- Fake soft shadow using stacked, inset, fading layers ----------
        val shadowLayers = 6
        val spreadPx = config.shadowSpreadDp * density
        val offsetYPx = config.shadowOffsetYDp * density
        val baseAlpha = Color.alpha(config.elevationShadowColor)

        val shadowCornerRadius = when {
            cornerRadii != null -> cornerRadii.maxOrNull() ?: 0f
            config.cornerRadiusDp > 0f -> config.cornerRadiusDp * density
            else -> 0f
        }

        val layers = arrayOfNulls<Drawable>(shadowLayers + 1)

        for (i in 0 until shadowLayers) {
            val fraction = i / shadowLayers.toFloat()
            val layerAlpha = (baseAlpha * (1f - fraction)).toInt().coerceIn(0, 255)

            val shadowColor = Color.argb(
                layerAlpha,
                Color.red(config.elevationShadowColor),
                Color.green(config.elevationShadowColor),
                Color.blue(config.elevationShadowColor)
            )

            layers[i] = GradientDrawable().apply {
                shape = config.shape ?: GradientDrawable.RECTANGLE
                setColor(shadowColor)
                cornerRadius = shadowCornerRadius
            }
        }

        layers[shadowLayers] = contentDrawable

        return LayerDrawable(layers).apply {
            for (i in 0 until shadowLayers) {
                val inset = (spreadPx * (shadowLayers - i) / shadowLayers).toInt()
                setLayerInset(
                    i,
                    inset,
                    inset + offsetYPx.toInt() / 2,
                    inset,
                    inset - offsetYPx.toInt() / 2
                )
            }
            setLayerInset(shadowLayers, 0, 0, 0, 0)
        }
    }

    fun createGlowDrawable(
        background: Drawable,
        glowColor: Int,
        glowSize: Float,
        cornerRadius: Float
    ): Drawable {

        return object : Drawable() {

            private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            }

            override fun draw(canvas: Canvas) {

                val boundsF = RectF(
                    bounds.left.toFloat(),
                    bounds.top.toFloat(),
                    bounds.right.toFloat(),
                    bounds.bottom.toFloat()
                )

                val effectiveGlow = glowSize.coerceAtMost(
                    Math.min(boundsF.width(), boundsF.height()) / 3f
                )

                val glowRect = RectF(
                    boundsF.left + effectiveGlow,
                    boundsF.top + effectiveGlow,
                    boundsF.right - effectiveGlow,
                    boundsF.bottom - effectiveGlow
                )

                if (glowRect.width() <= 0f || glowRect.height() <= 0f) {
                    background.bounds = bounds
                    background.draw(canvas)
                    return
                }

                val saveCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.saveLayer(boundsF, null)
                } else {
                    canvas.saveLayer(boundsF, null, Canvas.ALL_SAVE_FLAG)
                }

                paint.apply {
                    style = Paint.Style.FILL
                    isAntiAlias = true
                    color = Color.BLACK // Base shape for shadow layer

                    setShadowLayer(
                        effectiveGlow,
                        0f,
                        0f,
                        glowColor
                    )
                }

                val effectiveRadius = cornerRadius.coerceAtMost(
                    Math.min(glowRect.width(), glowRect.height()) / 2f
                )

                canvas.drawRoundRect(
                    glowRect,
                    effectiveRadius,
                    effectiveRadius,
                    paint
                )

                paint.clearShadowLayer()

                // Punch a hole in the middle so only the outer glow halo remains
                canvas.drawRoundRect(
                    glowRect,
                    effectiveRadius,
                    effectiveRadius,
                    clearPaint
                )

                canvas.restoreToCount(saveCount)

                // Draw actual background inside glowRect
                background.bounds = Rect(
                    glowRect.left.toInt(),
                    glowRect.top.toInt(),
                    glowRect.right.toInt(),
                    glowRect.bottom.toInt()
                )
                background.draw(canvas)
            }

            override fun setAlpha(alpha: Int) {
                paint.alpha = alpha
                background.alpha = alpha
            }

            override fun setColorFilter(
                colorFilter: android.graphics.ColorFilter?
            ) {
                paint.colorFilter = colorFilter
                background.colorFilter = colorFilter
            }

            override fun getOpacity(): Int =
                android.graphics.PixelFormat.TRANSLUCENT
        }
    }
}

private class GradientStrokeDrawable(
    private val strokeWidth: Float,
    private val strokeColors: IntArray,
    private val strokePositions: FloatArray?,
    private val cornerRadius: Float,
    private val cornerRadii: FloatArray?,
    private val orientation: GradientDrawable.Orientation,
    private val dashWidth: Float,
    private val dashGap: Float
) : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@GradientStrokeDrawable.strokeWidth
        if (dashWidth > 0 && dashGap > 0) {
            pathEffect = DashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
        }
    }

    override fun draw(canvas: Canvas) {
        val rect = RectF(bounds).apply {
            val halfWidth = strokeWidth / 2f
            inset(halfWidth, halfWidth)
        }

        paint.shader = createShader(bounds)

        if (cornerRadii != null) {
            val path = Path().apply {
                addRoundRect(rect, cornerRadii, Path.Direction.CW)
            }
            canvas.drawPath(path, paint)
        } else {
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        }
    }

    private fun createShader(bounds: Rect): Shader {
        val x0: Float
        val y0: Float
        val x1: Float
        val y1: Float

        when (orientation) {
            GradientDrawable.Orientation.TOP_BOTTOM -> {
                x0 = 0f; y0 = 0f; x1 = 0f; y1 = bounds.height().toFloat()
            }
            GradientDrawable.Orientation.BOTTOM_TOP -> {
                x0 = 0f; y0 = bounds.height().toFloat(); x1 = 0f; y1 = 0f
            }
            GradientDrawable.Orientation.LEFT_RIGHT -> {
                x0 = 0f; y0 = 0f; x1 = bounds.width().toFloat(); y1 = 0f
            }
            GradientDrawable.Orientation.RIGHT_LEFT -> {
                x0 = bounds.width().toFloat(); y0 = 0f; x1 = 0f; y1 = 0f
            }
            GradientDrawable.Orientation.TL_BR -> {
                x0 = 0f; y0 = 0f; x1 = bounds.width().toFloat(); y1 = bounds.height().toFloat()
            }
            GradientDrawable.Orientation.TR_BL -> {
                x0 = bounds.width().toFloat(); y0 = 0f; x1 = 0f; y1 = bounds.height().toFloat()
            }
            GradientDrawable.Orientation.BL_TR -> {
                x0 = 0f; y0 = bounds.height().toFloat(); x1 = bounds.width().toFloat(); y1 = 0f
            }
            GradientDrawable.Orientation.BR_TL -> {
                x0 = bounds.width().toFloat(); y0 = bounds.height().toFloat(); x1 = 0f; y1 = 0f
            }
            else -> {
                x0 = 0f; y0 = 0f; x1 = bounds.width().toFloat(); y1 = 0f
            }
        }
        return LinearGradient(x0, y0, x1, y1, strokeColors, strokePositions, Shader.TileMode.CLAMP)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}