/*
 * Copyright (C) 2019 POP Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.popinnow.analytics

import android.app.Activity
import android.os.Bundle
import android.widget.Button

class SampleActivity : Activity() {

  private val tracker = AnalyticsTracker()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Generated track() method
    tracker.track(LifecycleAnalytics.ActivityCreated)

    val button = findViewById<Button>(R.id.button)
    button.setOnClickListener {

      // Generated track() method
      tracker.track(InteractiveAnalytics.ButtonClicked(button.text.toString()))
    }
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)

    // On the fly tracking, generally not recommended - but you do you.
    tracker.track(
      "Activity save instance failed", mapOf(
        "bundle" to outState
      )
    )
  }

  override fun onDestroy() {
    super.onDestroy()

    // Generated track() method
    tracker.track(LifecycleAnalytics.ActivityDestroyed)
  }

}
