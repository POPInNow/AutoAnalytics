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
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import javax.annotation.CheckReturnValue

@CheckReturnValue
internal fun FunSpec.Builder.generateKDoc(className: ClassName): FunSpec.Builder {
  return this.addKdoc(generateKdoc(className))
}

@CheckReturnValue
private fun generateKdoc(className: ClassName): CodeBlock {
  // Can't use %L here since there is some formatting bug in KotlinPoet
  val explanation =
    "Converts [%T] to a name and payload and delegates it to [%T.${Extension.FUNCTION_NAME}]."
  return CodeBlock.builder()
    .addStatement(explanation, className, EVENT_TRACKER_CLASS)
    .addStatement("")
    .addStatement("This is a generated function. Do not edit.")
    .build()
}

