package com.spradhan.uxcomponentLib

import android.R.attr.strokeColor
import android.R.attr.strokeWidth
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View.LAYER_TYPE_SOFTWARE

object CommonUtils {
    fun createDynamicGradient(
        context: Context,
        config: GradientConfig
    ): GradientDrawable {

        val density = context.resources.displayMetrics.density

        return GradientDrawable().apply {

            shape = config.shape ?: GradientDrawable.RECTANGLE

            config.solidColor?.let {
                setColor(config.solidColor ?: Color.TRANSPARENT)
            }

            config.colors?.let { colorsArray ->

                if (colorsArray.isNotEmpty()) {

                    if (config.positions != null &&
                        config.positions.size == colorsArray.size &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    ) {
                        setColors(colorsArray, config.positions)
                    } else {
                        setColors(colorsArray)
                    }

                    val type = config.gradientType ?: GradientDrawable.LINEAR_GRADIENT
                    gradientType = type

                    when (type) {

                        GradientDrawable.LINEAR_GRADIENT -> {
                            orientation =
                                config.orientation
                                    ?: GradientDrawable.Orientation.TOP_BOTTOM
                        }

                        GradientDrawable.RADIAL_GRADIENT -> {
                            setGradientCenter(
                                config.gradientCenterX ?: 0.5f,
                                config.gradientCenterY ?: 0.5f
                            )
                            gradientRadius =
                                (config.gradientRadius ?: 0.5f) * density * 100
                        }

                        GradientDrawable.SWEEP_GRADIENT -> {
                            setGradientCenter(
                                config.gradientCenterX ?: 0.5f,
                                config.gradientCenterY ?: 0.5f
                            )
                        }
                    }
                }
            }
            // Corner radius
            when {
                config.cornerRadii != null && config.cornerRadii.size == 8 -> {
                    cornerRadii = config.cornerRadii.map { it * density }.toFloatArray()
                }

                config.topLeftRadiusDp > 0f ||
                        config.topRightRadiusDp > 0f ||
                        config.bottomRightRadiusDp > 0f ||
                        config.bottomLeftRadiusDp > 0f -> {

                    cornerRadii = floatArrayOf(
                        config.topLeftRadiusDp * density,
                        config.topLeftRadiusDp * density,

                        config.topRightRadiusDp * density,
                        config.topRightRadiusDp * density,

                        config.bottomRightRadiusDp * density,
                        config.bottomRightRadiusDp * density,

                        config.bottomLeftRadiusDp * density,
                        config.bottomLeftRadiusDp * density
                    )
                }

                config.cornerRadiusDp > 0f -> {
                    cornerRadius = config.cornerRadiusDp * density
                }
            }

            // Stroke
            config.strokeWidthDp?.let { widthDp ->

                val widthPx = (widthDp * density).toInt()
                val dashWidth = (config.strokeDashWidthDp ?: 0f) * density
                val dashGap = (config.strokeDashGapDp ?: 0f) * density
                val strokeColor = config.strokeColor ?: 0xFF000000.toInt()

                setStroke(widthPx, strokeColor, dashWidth, dashGap)
            }
        }
    }
    fun createGlowDrawable(
        background: Drawable,
        glowColor: Int,
        glowSize: Float,
        cornerRadius: Float
    ): Drawable {

        val glowDrawable = object : Drawable() {

            private val paint =
                Paint(Paint.ANTI_ALIAS_FLAG)

            override fun draw(canvas: Canvas) {

                val rect = RectF(
                    bounds.left.toFloat(),
                    bounds.top.toFloat(),
                    bounds.right.toFloat(),
                    bounds.bottom.toFloat()
                )

                paint.apply {
                    style = Paint.Style.FILL
                    isAntiAlias = true
                    color = Color.TRANSPARENT

                    setShadowLayer(
                        glowSize,
                        0f,
                        0f,
                        glowColor
                    )
                }

                canvas.drawRoundRect(
                    rect,
                    cornerRadius,
                    cornerRadius,
                    paint
                )

                paint.clearShadowLayer()

                background.bounds = bounds
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

        return android.graphics.drawable.LayerDrawable(
            arrayOf(glowDrawable)
        ).apply {

            val inset = glowSize.toInt()

            setLayerInset(
                0,
                inset,
                inset,
                inset,
                inset
            )
        }
    }
}