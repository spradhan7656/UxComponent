package com.spradhan.uxcomponentLib

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat


object CommonDrawables {

    fun getCurvedGreyBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,
            GradientConfig(
                solidColor = ContextCompat.getColor(mCtx, R.color.light_grey),
                cornerRadiusDp = 5f
            ))
    }

    fun getGreyCurvedWhiteBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,
            GradientConfig(
                solidColor = ContextCompat.getColor(mCtx, R.color.white),
                cornerRadiusDp = 5f,
                strokeWidthDp = 1f,
                strokeColor = ContextCompat.getColor(mCtx, R.color.light_grey)
            ))
    }

    fun getCurvedLightRedBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,
            GradientConfig(
                solidColor = ContextCompat.getColor(mCtx, R.color.light_red),
                cornerRadiusDp = 5f
            ))
    }

    fun getDottedGreyBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,GradientConfig(
            cornerRadiusDp = 5f,
            strokeWidthDp = 1f,
            strokeDashWidthDp = 3f,
            strokeDashGapDp = 2f,
            strokeColor = ContextCompat.getColor(mCtx, R.color.grey),
            solidColor = ContextCompat.getColor(mCtx, R.color.light_grey)
        ))
    }

    fun getDottedYellowBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,GradientConfig(
            cornerRadiusDp = 5f,
            strokeWidthDp = 2f,
            strokeDashWidthDp = 3f,
            strokeDashGapDp = 2f,
            strokeColor = ContextCompat.getColor(mCtx, R.color.yellow_light),
            solidColor = ContextCompat.getColor(mCtx, R.color.yellow_opadcity25)
        ))
    }

    fun getCircleYellowBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,
            GradientConfig(
                solidColor = ContextCompat.getColor(mCtx, R.color.yellow),
                shape = GradientDrawable.OVAL
            ))
    }

    fun getCircleAppThemeBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,
            GradientConfig(
                solidColor = ContextCompat.getColor(mCtx, R.color.app_color_tddcol_uni),
                shape = GradientDrawable.OVAL
            ))
    }

    fun getCurvedYellowBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,
            GradientConfig(
                solidColor = ContextCompat.getColor(mCtx, R.color.yellow),
                cornerRadiusDp = 5f
            ))
    }

    fun getCurvedAppThemeBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,
            GradientConfig(
                solidColor = ContextCompat.getColor(mCtx, R.color.app_color),
                cornerRadiusDp = 5f
            ))
    }

    fun getCurvedGreenBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,
            GradientConfig(
                solidColor = ContextCompat.getColor(mCtx, R.color.text_green),
                cornerRadiusDp = 5f
            ))
    }

    fun getCircleGreenBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,
            GradientConfig(
                shape = GradientDrawable.OVAL,
                solidColor = ContextCompat.getColor(mCtx, R.color.text_green),
                cornerRadiusDp = 5f
            ))
    }

    fun getCurvedLigtGreenBg(mCtx : Context) : GradientDrawable {
        return CommonUtils.createDynamicGradient(mCtx,
            GradientConfig(
                solidColor = ContextCompat.getColor(mCtx, R.color.text_green_opacity10),
                cornerRadiusDp = 5f
            ))
    }
}