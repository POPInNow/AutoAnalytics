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

// Set Target as class since we want this annotation to be added to a class element
@Target(AnnotationTarget.CLASS)
// Set retention as Source since we only need this annotation
// during annotation processing process and
// we don't need this class at runtime.
@Retention(AnnotationRetention.SOURCE)
annotation class AutoAnalyticsEvent
