package com.spradhan.uxcomponentLib

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

data class GradientConfig(
    val shape: Int? = GradientDrawable.RECTANGLE,
    val colors: IntArray? = null,
    @ColorInt val solidColor: Int? = null,
    val orientation: GradientDrawable.Orientation? = GradientDrawable.Orientation.TOP_BOTTOM,
    val angleDegrees: Int? = null,
    @FloatRange(from = 0.0, to = 1.0) val positions: FloatArray? = null,

    val topLeftRadiusDp: Float = 0f,
    val topRightRadiusDp: Float = 0f,
    val bottomRightRadiusDp: Float = 0f,
    val bottomLeftRadiusDp: Float = 0f,

    val cornerRadiusDp: Float = 0f,
    val cornerRadii: FloatArray? = null,
    val strokeWidthDp: Float = 0f,
    @ColorInt val strokeColor: Int = Color.TRANSPARENT,
    val strokeDashWidthDp: Float = 0f,
    val strokeDashGapDp: Float = 0f,
    val gradientType: Int = GradientDrawable.LINEAR_GRADIENT,
    // Radial/sweep extras
    val gradientCenterX: Float = 0.5f,
    val gradientCenterY: Float = 0.5f,
    val gradientRadius: Float = 0.5f
)