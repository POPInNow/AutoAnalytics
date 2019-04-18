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
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName
import java.io.File
import javax.lang.model.element.TypeElement

internal object CodeGenerator {

  @JvmStatic
  fun generateCode(
    analyticsElement: TypeElement,
    analyticEvents: Map<ClassName, List<String>>,
    outputDir: File
  ) {
    val className = analyticsElement.asClassName()

    val extensionFunction = FunSpec
      .builder(Extension.FUNCTION_NAME)
      .generateKDoc(className)
      .generateDeclaration(className)
      .generateLocalVariableDeclarations()
      .generateWhenStatement(analyticEvents)
      .generateDelegatedMethodCall()
      .build()

    FileSpec.builder(className.packageName, "AutoAnalytics_${className.simpleName}")
      .addFunction(extensionFunction)
      .build()
      .writeTo(outputDir)
  }
}

