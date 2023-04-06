/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.elements

import android.graphics.Matrix
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

class FlowerShape(
    private val amplitudeDp: Dp = 4.dp,
    private val rotationDegrees: Float = 0f,
    private val numberOfPetals: Int = 12
) : Shape {

    private fun getCurveX(
        radius: Float,
        startRadius: Float,
        radian: Float,
        amplitude: Float
    ): Float = (sin(numberOfPetals * radian) * amplitude + startRadius) * cos(radian) + radius

    private fun getCurveY(
        radius: Float,
        startRadius: Float,
        radian: Float,
        amplitude: Float
    ) = (sin(numberOfPetals * radian) * amplitude + startRadius) * sin(radian) + radius

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val amplitudePx = with(density) { amplitudeDp.toPx() / 2 }
        val halfOfWidth = size.width / 2f
        val halfOfHeight = size.height / 2f
        val startRadius = halfOfWidth - amplitudePx
        val path = Path().apply {
            moveTo((size.width - amplitudePx), halfOfHeight)
            repeat(360) {
                lineTo(
                    getCurveX(
                        halfOfWidth,
                        startRadius,
                        Math.toRadians(it.toDouble()).toFloat(),
                        amplitudePx
                    ),
                    getCurveY(
                        halfOfHeight,
                        startRadius,
                        Math.toRadians(it.toDouble()).toFloat(),
                        amplitudePx
                    )
                )
            }
            close()
        }

        val center = path.getBounds().center

        val rotatedPath = path.asAndroidPath().apply {
            transform(Matrix().apply {
                postRotate(rotationDegrees, center.x, center.y)
            })
        }.asComposePath()

        return Outline.Generic(rotatedPath)
    }

}