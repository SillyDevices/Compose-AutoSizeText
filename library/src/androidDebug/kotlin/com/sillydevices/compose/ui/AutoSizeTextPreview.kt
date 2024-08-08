package com.sillydevices.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Preview(name = "Compare with Text by Density", showBackground = false)
@Composable
private fun CompareWithTextPreview() {
    MaterialTheme {
        val density = LocalDensity.current
        Column {
            Row {
                Surface {
                    CompositionLocalProvider(LocalDensity provides Density(density.density, 0.8f)) {
                        Column {
                            Text("Sample Text")
                            AutoSizeText("Sample Text")
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Surface {
                    Column {
                        Text("Sample Text")
                        AutoSizeText("Sample Text")
                    }
                }
                Spacer(Modifier.width(8.dp))
                Surface {
                    CompositionLocalProvider(LocalDensity provides Density(density.density, 1.2f)) {
                        Column {
                            Text("Sample Text")
                            AutoSizeText("Sample Text")
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row {
                Surface {
                    CompositionLocalProvider(LocalDensity provides Density(density.density, 0.8f)) {
                        Column(Modifier.width(50.dp)) {
                            Text("Sample Text")
                            AutoSizeText("Sample Text")
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Surface {
                    Column(Modifier.width(60.dp)) {
                        Text("Sample Text")
                        AutoSizeText("Sample Text")
                    }
                }
                Spacer(Modifier.width(8.dp))
                Surface {
                    CompositionLocalProvider(LocalDensity provides Density(density.density, 1.2f)) {
                        Column(Modifier.width(70.dp)) {
                            Text("Sample Text")
                            AutoSizeText("Sample Text")
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Surface {
                    val style = MaterialTheme.typography.h5
                    Column(Modifier.width(100.dp)) {
                        Text("Sample Text", style = style)
                        AutoSizeText("Sample Text", style = style)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row {
                Surface {
                    val style = MaterialTheme.typography.h5.copy(color = Color.DarkGray)
                    Column(Modifier.width(200.dp)) {
                        Text("Sample Text", style = style)
                        AutoSizeText("Sample Text", style = style)
                    }
                }
            }
        }
    }
}

@Preview(name = "Preview Shrink and Expand", showBackground = false)
@Composable
private fun ShrinkPreview() {
    MaterialTheme {
        Row {
            Surface {
                Column {
                    val sizes = remember { 40..140 step 20 }
                    sizes.reversed().forEach {
                        Box(modifier = Modifier.width(it.dp).background(Color.LightGray)) {
                            AutoSizeText("Sample Text", maxLines = 1)
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Surface {
                Column {
                    val sizes = remember { 40..140 step 20 }
                    sizes.reversed().forEach {
                        Box(modifier = Modifier.width(it.dp).background(Color.LightGray)) {
                            AutoSizeText("Sample Text", maxLines = 1, fontSize = TextUnit.Unspecified)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Preview Shrink and Expand Multiline", showBackground = false)
@Composable
private fun ShrinkMultilinePreview() {
    MaterialTheme {
        Row {
            Surface {
                Column {
                    val sizes = remember { 20..140 step 20 }
                    sizes.reversed().forEach {
                        Box(modifier = Modifier.width(it.dp).background(Color.LightGray)) {
                            AutoSizeText("Sample Text", maxLines = 2)
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Surface {
                Column {
                    val sizes = remember { 20..140 step 20 }
                    sizes.reversed().forEach {
                        Box(modifier = Modifier.width(it.dp).background(Color.LightGray)) {
                            AutoSizeText("Sample Text", maxLines = 2, fontSize = TextUnit.Unspecified)
                        }
                    }
                }
            }
        }
    }
}