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
internal fun FunSpec.Builder.generateWhenStatement(analyticEvents: Map<ClassName, List<String>>): FunSpec.Builder {
  return this
      .beginControlFlow("when (%L)", Extension.PARAMETER_NAME)
      .generateWhenCases(analyticEvents)
      .endControlFlow()
}

@CheckReturnValue
private fun FunSpec.Builder.generateWhenCases(analyticEvents: Map<ClassName, List<String>>): FunSpec.Builder {
  var builder = this
  for ((caseName, parameterList) in analyticEvents) {
    val whenCase = generateWhenCase(caseName, parameterList)
    builder = builder.addCode(whenCase)
  }
  return builder
}

@CheckReturnValue
private fun generateWhenCase(
  caseName: ClassName,
  parameterList: List<String>
): CodeBlock {
  return CodeBlock.builder()
      .beginControlFlow("is %T ->", caseName)
      .addName(caseName)
      .addPayload(parameterList)
      .endControlFlow()
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
private fun CodeBlock.Builder.addPayload(parameterList: List<String>): CodeBlock.Builder {
  return if (parameterList.isNotEmpty()) {
    addEventPayload(parameterList)
  } else {
    addEmptyPayload()
  }
}

@CheckReturnValue
private fun CodeBlock.Builder.addEventPayload(parameterList: List<String>): CodeBlock.Builder {
  return this.addStatement("%L = %T(", Declaration.LOCAL_PAYLOAD_NAME, FunctionTypes.MAP_OF)
      .indent()
      .apply {
        for (parameter in parameterList) {
          addStatement(
              "%S to %L.%L%L",
              parameter.toSnakeCase(CaseFormat.LOWER_CAMEL),
              Extension.PARAMETER_NAME,
              parameter,
              if (parameter == parameterList.last()) "" else ","
          )
        }
      }
      .unindent()
      .addStatement(")")
}

@CheckReturnValue
private fun CodeBlock.Builder.addEmptyPayload(): CodeBlock.Builder {
  return this.addStatement("%L = %T()", Declaration.LOCAL_PAYLOAD_NAME, FunctionTypes.EMPTY_MAP)
}

/**
 * Convert ClassName names to lower_underscore_case
 */
@CheckReturnValue
private fun ClassName.asPayloadKey(fromCase: CaseFormat): String {
  return simpleName.convertCase(fromCase, LOWER_UNDERSCORE)
}

/**
 * Convert strings to lower_underscore_case
 */
@CheckReturnValue
private fun String.toSnakeCase(fromCase: CaseFormat): String {
  return convertCase(fromCase, LOWER_UNDERSCORE)
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
