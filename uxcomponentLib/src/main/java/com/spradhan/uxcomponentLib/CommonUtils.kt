package com.spradhan.uxcomponentLib

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build

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
}