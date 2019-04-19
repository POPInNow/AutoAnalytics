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

package com.popinnow.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

internal val EVENT_TRACKER_CLASS = ClassName("com.popinnow.analytics", "AutoAnalytics")

internal object Extension {

  internal const val FUNCTION_NAME = "track"
  internal const val PARAMETER_NAME = "event"

}

internal object Declaration {

  internal const val LOCAL_NAME_NAME = "name"
  internal const val LOCAL_PAYLOAD_NAME = "payload"
  internal val LOCAL_NAME_TYPE = String::class
  internal val LOCAL_PAYLOAD_TYPE: ParameterizedTypeName

  init {
    // payload: Map<String, Any?>
    val mapType = ClassName("kotlin.collections", "Map")
    val stringType = ClassName("kotlin", "String")
    val anyType = ClassName("kotlin", "Any")
    LOCAL_PAYLOAD_TYPE =
      mapType.parameterizedBy(stringType, anyType.copy(nullable = true))
  }

}

internal object FunctionTypes {

  internal val MAP_OF = ClassName("kotlin.collections", "mapOf")
  internal val EMPTY_MAP = ClassName("kotlin.collections", "emptyMap")
}
