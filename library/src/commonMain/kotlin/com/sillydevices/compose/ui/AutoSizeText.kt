package com.sillydevices.compose.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.takeOrElse
import kotlin.math.min


@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = style.fontSize,
    minFontSize: TextUnit = TextUnit.Unspecified,
    maxFontSize: TextUnit = fontSize,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign = TextAlign.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    adjustLineHeight: Boolean = true,
    lineSpaceRatio: Float = style.lineHeight.value / style.fontSize.value,
) {
    val alignment = when (textAlign) {
        TextAlign.Start -> Alignment.CenterStart
        TextAlign.Center -> Alignment.Center
        TextAlign.End -> Alignment.CenterEnd
        else -> Alignment.CenterStart
    }
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = alignment,
    ) {
        val mergedStyle = style.merge(
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
        )
        val layoutDirection = LocalLayoutDirection.current

        val fontFamilyResolver = LocalFontFamilyResolver.current
        val textMeasurer = rememberTextMeasurer()
        val coercedLineSpaceRatio = lineSpaceRatio.takeIf { it.isFinite() && it >= 1 } ?: 1F
        val density = LocalDensity.current

        val testOverflow: (textSizePx: Int) -> Boolean = { textSizePx ->
            val textSize = with(density) { textSizePx.toSp() }
            hasVisualOverflow(
                text = text,
                textStyle = mergedStyle.merge(
                    fontSize = textSize,
                    lineHeight = if (adjustLineHeight) textSize * coercedLineSpaceRatio else mergedStyle.lineHeight,
                ),
                maxLines = maxLines,
                layoutDirection = layoutDirection,
                softWrap = softWrap,
                density = density,
                fontFamilyResolver = fontFamilyResolver,
                textMeasurer = textMeasurer,
            )
        }

        val choices = remember(density, maxWidth, maxHeight, maxFontSize, minFontSize) {
            createSizeChoices(
                density = density,
                containerSize = DpSize(maxWidth, maxHeight),
                maxFontSize = maxFontSize,
                minFontSize = minFontSize,
        ) }

        val bestFontSize = remember(choices, testOverflow) {
            findBestFontSize(choices, density, testOverflow)
        }

        Text(
            text = text,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = mergedStyle.merge(
                fontSize = bestFontSize,
                lineHeight = if (adjustLineHeight) bestFontSize * coercedLineSpaceRatio else mergedStyle.lineHeight,
            ),
        )
    }
}

private fun BoxWithConstraintsScope.hasVisualOverflow(
    text: String,
    textStyle: TextStyle,
    maxLines: Int,
    layoutDirection: LayoutDirection,
    softWrap: Boolean,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver,
    textMeasurer: TextMeasurer,
) = textMeasurer.measure(
    text = text,
    style = textStyle,
    overflow = TextOverflow.Clip,
    softWrap = softWrap,
    maxLines = maxLines,
    constraints = constraints,
    layoutDirection = layoutDirection,
    density = density,
    fontFamilyResolver = fontFamilyResolver,
).hasVisualOverflow

private fun createSizeChoices(
    density: Density,
    containerSize: DpSize,
    minFontSize: TextUnit = TextUnit.Unspecified,
    maxFontSize: TextUnit = TextUnit.Unspecified,
): IntProgression {
    val intSize = containerSize.takeOrElse { DpSize.Zero }
        .run { with(density) { IntSize(width.roundToPx(), height.roundToPx()) } }
        .run { min(width, height) }
    val max = maxFontSize.takeIf { it.isSp }
        ?.let { with(density) { if (it.isSpecified) it.roundToPx() else 0 } }
        ?.coerceIn(0..intSize)
        ?: intSize
    val min = minFontSize.takeIf { it.isSp }
        ?.let { with(density) { it.roundToPx() } }
        ?.coerceIn(range = 0..max)
        ?: 0
    return min..max step 1
}

private fun findBestFontSize(
    choices: IntProgression,
    density: Density,
    testOverflow: (px: Int) -> Boolean
): TextUnit {
    var low = choices.first
    var high = choices.last
    if (!testOverflow(high))
        return with(density) { high.toSp() }
    while (low <= high) {
        val mid = low + (high - low) / 2
        if (testOverflow((mid)))
            high = mid - 1
        else
            low = mid + 1
    }
    val resultPx = high.coerceAtLeast(choices.first)
    return with(density) { resultPx.toSp() }
}