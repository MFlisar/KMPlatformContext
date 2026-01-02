[![Maven Central](https://img.shields.io/maven-central/v/io.github.mflisar.kmpplatformcontext/library?style=for-the-badge&color=blue)](https://central.sonatype.com/artifact/io.github.mflisar.kmpplatformcontext/library) ![API](https://img.shields.io/badge/api-23%2B-brightgreen.svg?style=for-the-badge) ![Kotlin](https://img.shields.io/github/languages/top/MFlisar/KMPPlatformContext.svg?style=for-the-badge&amp;color=blueviolet) ![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin_Multiplatform-blue?style=for-the-badge&amp;label=Kotlin) [![License](https://img.shields.io/github/license/MFlisar/KMPPlatformContext?style=for-the-badge)](https://github.com/MFlisar/KMPPlatformContext/blob/main/LICENSE)
# KMPPlatformContext
![Android](https://img.shields.io/badge/android-3DDC84?style=for-the-badge) ![iOS](https://img.shields.io/badge/ios-A2AAAD?style=for-the-badge) ![Windows](https://img.shields.io/badge/windows-5382A1?style=for-the-badge) ![macOS](https://img.shields.io/badge/macos-B0B0B0?style=for-the-badge) ![WebAssembly](https://img.shields.io/badge/wasm-624DE7?style=for-the-badge)

This library provides a multiplatform abstraction for platform-specific context handling and IO dispatching.

This library provides the following 3 things:

- a `PlatformContext` class that maps to `android.content.Context` on Android and to an empty implementation on all other platforms
- a `PlatformContextProvider` that allows you to set and get the current `PlatformContext` instance
- a `PlatformIOContext()` function that will provide `Dispatchers.IO` on all platforms but WASM where it provides `Dispatchers.Default`

Additionally the `initializer` module allows you to initialize the `PlatformContext` on Android with the application context automatically via `androidx.startup.Initializer`.

# :information_source: Table of Contents

- [Supported Platforms](#computer-supported-platforms)
- [Setup](#wrench-setup)
- [Usage](#page_facing_up-usage)
- [Demo](#sparkles-demo)
- [Other Libraries](#bulb-other-libraries)

# :computer: Supported Platforms

| Module | android | iOS | windows | macOS | wasm |
|---|---|---|---|---|---|
| Core | ✅ | ✅ | ✅ | ✅ | ✅ |
| Initializer | ✅ | ❌ | ❌ | ❌ | ❌ |

# :wrench: Setup

<details>

<summary>Dependencies</summary>

<br>

Simply add the dependencies inside your **build.gradle.kts** file.

```kotlin
val kmpplatformcontext = "<LATEST-VERSION>"

implementation("io.github.mflisar.kmpplatformcontext:core:${kmpplatformcontext}")
implementation("io.github.mflisar.kmpplatformcontext:initializer:${kmpplatformcontext}")
```

</details>

<details>

<summary>Version Catalogue</summary>

<br>

Define the dependencies inside your **libs.versions.toml** file.

```toml
[versions]

kmpplatformcontext = "<LATEST-VERSION>"

[libraries]

core = { module = "io.github.mflisar.kmpplatformcontext:core", version.ref = "kmpplatformcontext" }
initializer = { module = "io.github.mflisar.kmpplatformcontext:initializer", version.ref = "kmpplatformcontext" }
```

And then use the definitions in your projects **build.gradle.kts** file like following:

```shell
implementation(libs.core)
implementation(libs.initializer)
```

</details>

# :page_facing_up: Usage

This library is used like following:

On all platforms but android, you only need to add the `core` module to your dependencies and you are done

On android you have 2 options:

### Option 1

Add the `initializer` module to your dependencies and you are done

### Option 2

Manually initialize the library inside your `Application` class like following:

<!-- snippet: PlatformContextProvider::init -->
<!-- endSnippet -->

Afterwards you have access to the `PlatformContext`, `PlatformContextProvider.get()` and `PlatformIOContext()` everywhere in your code like following:

```kotlin

// imports
import com.michaelflisar.kmp.platformcontext.PlatformContext
import com.michaelflisar.kmp.platformcontext.PlatformContextProvider
import com.michaelflisar.kmp.platformcontext.PlatformIOContext

// usage
val platformContext = PlatformContextProvider.get()
val ioContext = PlatformIOContext()

// or use the PlatformContext class for functions (no need for the widely used doSomething(context: Any?) pattern)
expect fun doSomething()

actual fun doSomething() {
    // on android use this, on other platforms this is just a no-op
    val platformContext = PlatformContext.current()
}

```

# :sparkles: Demo

A full [demo](/demo) is included inside the demo module, it shows nearly every usage with working examples.

# :bulb: Other Libraries

You can find more libraries (all multiplatform) of mine that all do work together nicely [here](https://github.com/MFlisar/MyLibraries).
