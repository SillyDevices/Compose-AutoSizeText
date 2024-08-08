# AutoSizeText
---
Text Composamble wrapper that automatically adjusts its font size to fit the available space.
Compose replacement for [Autosizing TextView](https://developer.android.com/develop/ui/views/text-and-emoji/autosizing-textview)

## Setup
---
Add the dependency to your `build.gradle.kts` file.
```kotlin
implementation("com.sillydevices:compose.ui.autosizetext:0.1.1")
```

## Usage
---
This configuration only shrinks the text size to fit the available space.
```kotlin
AutoSizeText("Your text")
```

This configuration try to fit the text in the available space.
```kotlin
AutoSizeText("Your text", fontSize = TextUnit.Unspecified)
AutoSizeText("Your text", maxFontSize = TextUnit.Unspecified)
```