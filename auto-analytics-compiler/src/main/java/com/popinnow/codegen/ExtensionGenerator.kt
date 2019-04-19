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

import com.google.common.base.CaseFormat
import com.google.common.base.CaseFormat.LOWER_UNDERSCORE
import com.google.common.base.CaseFormat.UPPER_CAMEL
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import javax.annotation.CheckReturnValue

@CheckReturnValue
internal fun FunSpec.Builder.generateDeclaration(className: ClassName): FunSpec.Builder {
  val parameter = ParameterSpec.builder(Extension.PARAMETER_NAME, className)
      .build()

  return this
      .receiver(EVENT_TRACKER_CLASS)
      .addParameter(parameter)
}

@CheckReturnValue
internal fun FunSpec.Builder.generateLocalVariableDeclarations(): FunSpec.Builder {
  return this
      .addStatement("val %L: %T", Declaration.LOCAL_NAME_NAME, Declaration.LOCAL_NAME_TYPE)
      .addStatement("val %L: %T", Declaration.LOCAL_PAYLOAD_NAME, Declaration.LOCAL_PAYLOAD_TYPE)
}

@CheckReturnValue
internal fun FunSpec.Builder.generateDelegatedMethodCall(): FunSpec.Builder {
  return this.addStatement(
      "%L(%L, %L)",
      Extension.FUNCTION_NAME,
      Declaration.LOCAL_NAME_NAME,
      Declaration.LOCAL_PAYLOAD_NAME
  )
}

@CheckReturnValue
internal fun FunSpec.Builder.generateWhenStatement(
  analyticEvents: Map<ClassName, List<String>>
): FunSpec.Builder {
  val functionMapOf = ClassName("kotlin.collections", "mapOf")
  val functionEmptyMap = ClassName("kotlin.collections", "emptyMap")

  return this
      .beginControlFlow("when (%L)", Extension.PARAMETER_NAME)
      .generateWhenCases(analyticEvents, functionMapOf, functionEmptyMap)
      .endControlFlow()
}

@CheckReturnValue
private fun FunSpec.Builder.generateWhenCases(
  analyticEvents: Map<ClassName, List<String>>,
  functionMapOf: ClassName,
  functionEmptyMap: ClassName
): FunSpec.Builder {
  var builder = this
  for ((caseName, parameterList) in analyticEvents) {
    val whenCase = generateWhenCase(caseName, parameterList, functionMapOf, functionEmptyMap)
    builder = builder.addCode(whenCase)
  }
  return builder
}

@CheckReturnValue
private fun generateWhenCase(
  caseName: ClassName,
  parameterList: List<String>,
  functionMapOf: ClassName,
  functionEmptyMap: ClassName
): CodeBlock {
  return CodeBlock.builder()
      .addStatement("is %T -> {", caseName)
      .indent()
      .addName(caseName)
      .addPayload(parameterList, functionMapOf, functionEmptyMap)
      .unindent()
      .addStatement("}")
      .build()
}

@CheckReturnValue
private fun CodeBlock.Builder.addName(caseName: ClassName): CodeBlock.Builder {
  return this.addStatement(
      "%L = %S",
      Declaration.LOCAL_NAME_NAME,
      caseName.asPayloadKey(UPPER_CAMEL)
  )
}

@CheckReturnValue
private fun CodeBlock.Builder.addPayload(
  parameterList: List<String>,
  functionMapOf: ClassName,
  functionEmptyMap: ClassName
): CodeBlock.Builder {
  return if (parameterList.isNotEmpty()) {
    addEventPayload(parameterList, functionMapOf)
  } else {
    addEmptyPayload(functionEmptyMap)
  }
}

@CheckReturnValue
private fun CodeBlock.Builder.addEventPayload(
  parameterList: List<String>,
  functionMapOf: ClassName
): CodeBlock.Builder {
  return this.addStatement("%L = %T(", Declaration.LOCAL_PAYLOAD_NAME, functionMapOf)
      .indent()
      .apply {
        for ((index, parameter) in parameterList.withIndex()) {
          val size = parameterList.size
          addStatement(
              "%S to %L.%L%L",
              parameter.toSnakeCase(CaseFormat.LOWER_CAMEL),
              Extension.PARAMETER_NAME,
              parameter,
              if (index == size - 1) "" else ","
          )
        }
      }
      .unindent()
      .addStatement(")")
}

@CheckReturnValue
private fun CodeBlock.Builder.addEmptyPayload(functionEmptyMap: ClassName): CodeBlock.Builder {
  return this.addStatement("%L = %T()", Declaration.LOCAL_PAYLOAD_NAME, functionEmptyMap)
}

@CheckReturnValue
private fun String.toSnakeCase(fromCase: CaseFormat): String {
  return convertCase(fromCase, LOWER_UNDERSCORE)
}

@CheckReturnValue
private fun ClassName.asPayloadKey(fromCase: CaseFormat): String {
  return simpleName.convertCase(fromCase, LOWER_UNDERSCORE)
}

/**
 * Helper function to convert String case
 * using guava's [CaseFormat].
 */
@CheckReturnValue
private fun String.convertCase(
  fromCase: CaseFormat,
  toCase: CaseFormat
): String {
  return fromCase.to(toCase, this)
}
