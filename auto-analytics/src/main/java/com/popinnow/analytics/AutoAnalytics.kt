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

/**
 * [AutoAnalytics] is the target interface which generated code will extend.
 *
 * Generated extension methods will delegate their calls to the [track] method
 * defined in this interface
 */
interface AutoAnalytics {

  /**
   * The actual track method
   *
   * Use this to call through to your real Analytics implementation, such as Mixpanel, Google
   * or any other third party
   */
  fun track(
    name: String,
    payload: Map<String, Any?>
  )
}

