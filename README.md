# Capturable

![Capturable](art/header.png)

🚀A Jetpack Compose utility library for converting Composable content into Bitmap image 🖼️.  
_Made with ❤️ for Android Developers and Composers_ 

[![Build](https://github.com/PatilShreyas/Capturable/actions/workflows/build.yml/badge.svg)](https://github.com/PatilShreyas/Capturable/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/dev.shreyaspatil/capturable)](https://search.maven.org/artifact/dev.shreyaspatil/capturable)

[![Github Followers](https://img.shields.io/github/followers/PatilShreyas?label=Follow&style=social)](https://github.com/PatilShreyas)
[![GitHub stars](https://img.shields.io/github/stars/PatilShreyas/Capturable?style=social)](https://github.com/PatilShreyas/Capturable/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/PatilShreyas/Capturable?style=social)](https://github.com/PatilShreyas/Capturable/network/members)
[![GitHub watchers](https://img.shields.io/github/watchers/PatilShreyas/Capturable?style=social)](https://github.com/PatilShreyas/Capturable/watchers)
[![Twitter Follow](https://img.shields.io/twitter/follow/imShreyasPatil?label=Follow&style=social)](https://twitter.com/imShreyasPatil)

## 💡Introduction 

In the previous View system, drawing Bitmap Image from `View` was very straightforward. But that's not the case with Jetpack Compose since it's different in many aspects from previous system. This library helps easy way to achieve the same results. 
It's built upon the `ComposeView` and uses `View`'s APIs to draw the Bitmap image.

## 🚀 Implementation

You can check [/app](/app) directory which includes example application for demonstration. 

### Gradle setup

In `build.gradle` of app module, include this dependency

```gradle
dependencies {
    implementation "dev.shreyaspatil:capturable:1.0.1"
}
```

_You can find latest version and changelogs in the [releases](https://github.com/PatilShreyas/Capturable/releases)_.

### Usage

#### 1. Setup the controller

To be able to capture Composable content, you need instance of [`CaptureController`](https://patilshreyas.github.io/Capturable/capturable/dev.shreyaspatil.capturable.controller/-capture-controller/index.html) by which you can decide when to capture the content. You can get the instance as follow.

```kotlin
@Composable
fun TicketScreen() {
    val captureController = rememberCaptureController()
}
```

_[`rememberCaptureController()`](https://patilshreyas.github.io/Capturable/capturable/dev.shreyaspatil.capturable.controller/remember-capture-controller.html) is a Composable function._

#### 2. Add the content

The component which needs to be captured should be placed inside [`Capturable`](https://patilshreyas.github.io/Capturable/capturable/dev.shreyaspatil.capturable/-capturable-kt/-capturable.html) composable as follows.

```kotlin
@Composable
fun TicketScreen() {
    val captureController = rememberCaptureController()
    
    Capturable(
        controller = captureController,
        onCaptured = { bitmap, error ->
           // This is captured bitmap of a content inside Capturable Composable.
           if (bitmap != null) {
               // Bitmap is captured successfully. Do something with it!
           }
            
            if (error != null) {
                // Error occurred. Handle it!
            }
        }
    ) {
        // Composable content to be captured.
        // Here, `MovieTicketContent()` will be get captured
        MovieTicketContent(...)
    }
}
```

#### 3. Capture the content

To capture the content, use [`CaptureController#capture()`](https://patilshreyas.github.io/Capturable/capturable/dev.shreyaspatil.capturable.controller/-capture-controller/capture.html) as follows. 

```kotlin
Button(onClick = { captureController.capture() }) { ... }
```

On calling this method, request for capturing the content will be sent and event will be received in callback `onCaptured` with `ImageBitmap` as a parameter in the `Capturable` function.

By default, it captures the Bitmap using [`Bitmap.Config`](https://developer.android.com/reference/android/graphics/Bitmap.Config) **ARGB_8888**. If you want to modify, you can provide config from [`Bitmap.Config` enum](https://developer.android.com/reference/android/graphics/Bitmap.Config).

Example:

```kotlin
captureController.capture(Bitmap.Config.ALPHA_8)
```

> _Make sure to call this method as a part of **callback function** and **not as a part of the Composable function itself**. Otherwise, it'll lead to capture bitmaps unnecessarily in recompositions which can degrade the performance of the application._

That's all needed!

## 📄 API Documentation

[**Visit the API documentation of this library**](https://patilshreyas.github.io/Capturable) to get more information in detail.

---

## 🙋‍♂️ Contribute 

Read [contribution guidelines](CONTRIBUTING.md) for more information regarding contribution.

## 💬 Discuss? 

Have any questions, doubts or want to present your opinions, views? You're always welcome. You can [start discussions](https://github.com/PatilShreyas/Capturable/discussions).

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
