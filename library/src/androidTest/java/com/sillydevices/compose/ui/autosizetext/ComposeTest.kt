package com.sillydevices.compose.ui.autosizetext

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.sillydevices.compose.ui.AutoSizeText
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ComposeTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun displayTest() {
        composeTestRule.setContent {
            MaterialTheme {
                Column {
                    Text("Test Text", modifier = Modifier.testTag("text"))
                    Spacer(Modifier.height(8.dp))
                    AutoSizeText("Test Text", modifier = Modifier.testTag("autosize"))
                }
            }
        }
        composeTestRule.onNodeWithTag("text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("autosize").assertIsDisplayed()
    }

    @Test
    fun CompareWithTextTest() {
        val textSize =  mutableStateOf(IntSize(-1,  -1))
        val autoTextSize = mutableStateOf(IntSize(-1,  -1))
        lateinit var textLayoutResult: TextLayoutResult
        lateinit var autoSizeTextLayoutResult: TextLayoutResult
        composeTestRule.setContent {
            MaterialTheme {
                val style = MaterialTheme.typography.h5
                Column(Modifier.width(200.dp)) {
                    Text("Sample Text", style = style, onTextLayout = {
                        textSize.value = it.size
                        textLayoutResult = it
                    })
                    AutoSizeText("Sample Text", style = style, onTextLayout = {
                        autoTextSize.value = it.size
                        autoSizeTextLayoutResult = it
                    })
                }
            }
        }
        composeTestRule.waitForIdle()
        Assert.assertEquals(textSize.value, autoTextSize.value)
    }

}