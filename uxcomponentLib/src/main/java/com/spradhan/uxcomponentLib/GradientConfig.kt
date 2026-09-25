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

    // --- Solid stroke (used when strokeColors is null) ---
    val strokeWidthDp: Float = 0f,
    @ColorInt val strokeColor: Int = Color.TRANSPARENT,
    val strokeDashWidthDp: Float = 0f,
    val strokeDashGapDp: Float = 0f,

    // --- Gradient stroke (used when strokeColors is non-null/non-empty) ---
    val strokeColors: IntArray? = null,
    val strokeGradientType: Int = GradientDrawable.LINEAR_GRADIENT,
    val strokeGradientOrientation: GradientDrawable.Orientation? = GradientDrawable.Orientation.LEFT_RIGHT,
    @FloatRange(from = 0.0, to = 1.0) val strokePositions: FloatArray? = null,

    val gradientType: Int = GradientDrawable.LINEAR_GRADIENT,
    // Radial/sweep extras
    val gradientCenterX: Float = 0.5f,
    val gradientCenterY: Float = 0.5f,
    val gradientRadius: Float = 0.5f,
    // --- Elevation / shadow ---
    val elevationDp: Float = 0f,
    @ColorInt val elevationShadowColor: Int = 0x40000000, // translucent black
    val shadowSpreadDp: Float = 2f,
    val shadowOffsetYDp: Float = 2f
)