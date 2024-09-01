# Capturable

![Capturable](art/header.png)

🚀A Kotlin Multiplatform utility library for converting Composable content into Bitmap images 🖼️.  
_Made with ❤️ for developers working across WebAssembly (WASM), Android, iOS, and desktop platforms._

[![Build](https://github.com/PatilShreyas/Capturable/actions/workflows/build.yml/badge.svg)](https://github.com/PatilShreyas/Capturable/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/dev.shreyaspatil/capturable)](https://search.maven.org/artifact/dev.shreyaspatil/capturable)

## 💡Introduction

In the previous View system, drawing a Bitmap Image from a `View` was straightforward. However, with Jetpack Compose's unique characteristics, achieving the same is different. This library provides a simple way to capture Composable content into a Bitmap, making it suitable for Kotlin Multiplatform projects targeting WebAssembly (WASM), Android, iOS, and desktop.

## 🚀 Implementation

You can check the [/app](/app) directory, which includes an example application for demonstration.

### Gradle setup

In the `build.gradle.kts` of the relevant module, include this dependency:

```gradle
dependencies {
    implementation("dev.shreyaspatil:capturable:2.1.0")
}
```

_You can find the latest version and changelogs in the [releases](https://github.com/PatilShreyas/Capturable/releases)_.

### Usage

#### 1. Setup the controller

To capture Composable content, you need an instance of the [`CaptureController`](https://patilshreyas.github.io/Capturable/capturable/dev.shreyaspatil.capturable.controller/-capture-controller/index.html), which allows you to decide when to capture the content. You can obtain the instance as follows:

```kotlin
@Composable
fun TicketScreen() {
    val captureController = rememberCaptureController()
}
```

_[`rememberCaptureController()`](https://patilshreyas.github.io/Capturable/capturable/dev.shreyaspatil.capturable.controller/remember-capture-controller.html) is a Composable function._

#### 2. Add the content

To capture a specific component, apply a `capturable()` Modifier to that @Composable component as shown:

```kotlin
@Composable
fun TicketScreen() {
    val captureController = rememberCaptureController()

    // Composable content to be captured.
    // Here, everything inside the following Column will be captured
    Column(modifier = Modifier.capturable(captureController)) {
        MovieTicketContent(...)
    }
}
```

#### 3. Capture the content

To capture the content, use [`CaptureController#captureAsync()`](https://patilshreyas.github.io/Capturable/capturable/dev.shreyaspatil.capturable.controller/-capture-controller/captureAsync.html) as follows:

```kotlin
// Example: Capture the content when a button is clicked
val scope = rememberCoroutineScope()
Button(onClick = {
    // Capture content
    scope.launch {
        val bitmapAsync = captureController.captureAsync()
        try {
            val bitmap = bitmapAsync.await()
            // Do something with `bitmap`.
        } catch (error: Throwable) {
            // Handle the error
        }
    }
}) { ... }
```

When this method is called, a request to capture the content is sent, and `ImageBitmap` is returned asynchronously. _This method is safe to call from the Main thread._

By default, it captures the Bitmap using [`Bitmap.Config`](https://developer.android.com/reference/android/graphics/Bitmap.Config) **ARGB_8888**. You can modify this by providing a different config from the [`Bitmap.Config` enum](https://developer.android.com/reference/android/graphics/Bitmap.Config).

Example:

```kotlin
captureController.captureAsync(Bitmap.Config.ALPHA_8)
```

That's all you need!

## 📄 API Documentation

[**Visit the API documentation of this library**](https://patilshreyas.github.io/Capturable) for more detailed information.

---

## 🙋‍♂️ Contribute

Read the [contribution guidelines](CONTRIBUTING.md) for more information on how to contribute.

## 💬 Discuss?

Have any questions, doubts, or want to share your opinions? You're always welcome. You can [start discussions](https://github.com/PatilShreyas/Capturable/discussions).

## 📝 License

```
MIT License

Copyright (c) 2022 Shreyas Patil

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
