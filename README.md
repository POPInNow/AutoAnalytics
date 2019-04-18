# auto-analytics

Analytics suck.

## Install

In your `build.gradle`

```gradle
dependencies {
  def latestVersion = "0.0.1"

  implementation "com.popinnow.analytics.auto-analytics:$latestVersion"
  kapt "com.popinnow.analytics.auto-analytics-compiler:$latestVersion"

}
```

## What is it

Analytics are a required part of any decently sized modern application.

And it drives all of us insane.

Analytics always seems to be the forgotten afterthought in a project, and ignoring  
the proper planning can lead to a project which is difficult to maintain, and  
analytics which are difficult to consume.

This library is a 'ready to build' implementation of the technique described in
[this](https://medium.com/wantedly-engineering/better-analytics-in-android-with-annotation-processing-and-kotlinpoet-bffca3f24c37)
blog post  
above with some slight optimizations. I just think the idea is really cool.

## How does it work

Create a model and annotate it with `@AutoAnalyticsEvent`

```kotlin
InteractiveAnalytics.kt

@AutoAnalyticsEvent
sealed class InteractiveAnalytics {
  data class ButtonClicked(val buttonText: String) : InteractiveAnalytics()
  data class LinkShared(val link: String) : InteractiveAnalytics()
}
```

Implement the `AutoAnalytics` interface in your tracker class.

```kotlin

class LoggingAnalyticsTracker() : AutoAnalytics {

  override fun track(name: String, payload: Map<String, Any?>) {
    Log.d("LoggingAnalyticsTracker", "Tracking[$name]: $payload")
  }
}

class GoogleOrMixpanelOrSomebodyAnalyticsTracker(
  private val impl: GoogleOrMixpanel
) : AutoAnalytics {

  override fun track(name: String, payload: Map<String, Any?>) {
    impl.track(name, payload)
  }
}

```

When you compile the project, it will generate the following code into a file  
prefixed with `AutoAnalytics_`

```kotlin
AutoAnalytics_InteractiveAnalytics.kt

/**
 * Converts [InteractiveAnalytics] to a name and payload and delegates it to [AutoAnalytics.track].
 *
 * This is a generated function. Do not edit.
 */
fun AutoAnalytics.track(event: InteractiveAnalytics) {
    val name: String
    val payload: Map<String, Any?>
    when (event) {
        is InteractiveAnalytics.ButtonClicked -> {
            name = "button_clicked"
            payload = mapOf(
                "button_text" to event.buttonText
            )
        }
        is InteractiveAnalytics.LinkShared -> {
            name = "link_shared"
            payload = mapOf(
                "link" to event.link
            )
        }
    }
    track(name, payload)
}

```

You can now call this generated extension function from your code to fire  
off a specific analytics event. No more analytics as an afterthought!

```kotlin
  fun onButtonClicked(link: String) {

    LoggingAnalyticsTracker().track(InteractiveAnalytics.ButtonClicked(button.text.toString()))

    GoogleOrMixpanelOrSomebodyAnalyticsTracker().track(InteractiveAnalytics.LinkShared(link))
  }
```

See the sample project for an example.

The success of this model follows the same kind of idea as an MVI designed  
architecture, the model of the Analytics payload maps 1-to-1 with an actual  
Analytics event. Maybe your code base was already doing this, just with massive  
amounts of boilerplate.

Hope this makes your life easier.

## Community

The `AutoAnalytics` library welcomes contributions of all kinds - it does not claim to be perfect code.  
Any improvements that can be made to the usability or the efficiency of the project will be greatly  
appreciated.

## Credits

This library is primarily built and maintained by [Peter Yamanaka](https://github.com/pyamsoft)
at [POPin](https://github.com/POPinNow).  

# Support

Please feel free to make an issue on GitHub, leave as much detail as possible regarding  
the question or the problem you may be experiencing.

# Contributions

Contributions are welcome and encouraged. The project is written entirely in Kotlin and  
follows the [Square Code Style](https://github.com/square/java-code-styles) for `SquareAndroid`.

## License

Apache 2

```
Copyright (C) 2019 POP Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
