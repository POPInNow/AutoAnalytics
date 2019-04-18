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

import me.eugeniomarletti.kotlin.processing.KotlinProcessingEnvironment
import javax.tools.Diagnostic.Kind
import javax.tools.Diagnostic.Kind.ERROR
import javax.tools.Diagnostic.Kind.NOTE
import javax.tools.Diagnostic.Kind.WARNING

internal fun KotlinProcessingEnvironment.logError(scope: String, message: String) {
  log(ERROR, scope, message)
}

internal fun KotlinProcessingEnvironment.logWarn(scope: String, message: String) {
  log(WARNING, scope, message)
}

internal fun KotlinProcessingEnvironment.logDebug(scope: String, message: String) {
  log(NOTE, scope, message)
}

internal fun KotlinProcessingEnvironment.log(kind: Kind, scope: String, message: String) {
  messager.printMessage(kind, "AutoAnalytics[$scope] $message")
}
