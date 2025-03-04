package com.plcoding.cryptotracker.crypto.presentation.coin_detail

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

data class ChartStyle(
    val chartLinesColor: Color,
    val unselectedLinesColor: Color,
    val selectedLinesColor: Color,
    val helperLinesThicknessPx: Float,
    val axisLinesThicknessPx: Float,
    val labelFontSize: TextUnit,
    val minYLabelSpacing: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val xAxisLabelSpacing: Dp
)
