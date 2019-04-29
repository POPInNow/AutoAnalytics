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

import com.google.auto.service.AutoService
import com.popinnow.analytics.AutoAnalyticsEvent
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.KotlinMetadataUtils
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.modality
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import javax.annotation.CheckReturnValue
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

@AutoService(Processor::class)
class AnalyticsEventProcessor internal constructor(
) : KotlinAbstractProcessor(), KotlinMetadataUtils {

  override fun getSupportedAnnotationTypes(): Set<String> {
    return setOf(ANNOTATION.canonicalName)
  }

  override fun getSupportedSourceVersion(): SourceVersion {
    return SourceVersion.latestSupported()
  }

  override fun process(
    annotations: Set<TypeElement>,
    roundEnv: RoundEnvironment
  ): Boolean {
    val outputDir = generatedDir
    if (outputDir == null) {
      logError("process", "Cannot find generated output dir.")
      return false
    }

    // Get all elements that has been annotated with our annotation
    val elements = roundEnv.getElementsAnnotatedWith(ANNOTATION)
    for (element in elements) {
      // Check if the annotatedElement is a Kotlin sealed class
      val analyticsElement = getAnalyticsElement(element)
      if (analyticsElement == null) {
        logWarn(
            "process",
            "Could not find any of $supportedAnnotationTypes on element: $element. Skipping"
        )
        continue
      }

      // Get all the declared inner class as our Analytics Event
      val events = getDeclaredEvents(analyticsElement)
      if (events.isEmpty()) {
        // No declared Analytics Event, skip this class.
        logWarn("process", "$analyticsElement has no valid inner class, skipping.")
        continue
      }

      // Generate codes with KotlinPoet
      CodeGenerator.generateCode(analyticsElement, events, outputDir)
    }
    return true
  }

  @CheckReturnValue
  private fun getAnalyticsElement(element: Element): TypeElement? {
    val kotlinMetadata = element.kotlinMetadata
    if (kotlinMetadata !is KotlinClassMetadata || element !is TypeElement) {
      // Not a Kotlin class
      logWarn("getAnalyticsElement", "$element is not a Kotlin class.")
      return null
    }

    val proto = kotlinMetadata.data.classProto
    if (proto.modality != ProtoBuf.Modality.SEALED) {
      // Is not a sealed class
      logWarn("getAnalyticsElement", "$element is not a sealed Kotlin class.")
      return null
    }

    return element
  }

  @CheckReturnValue
  private fun getDeclaredEvents(analyticsElement: TypeElement): Map<ClassName, List<String>> {
    val events = mutableMapOf<ClassName, List<String>>()
    val enclosedElements = analyticsElement.enclosedElements

    val supertype = analyticsElement.asType()
    for (element: Element? in enclosedElements) {
      if (element == null) {
        logDebug("getDeclaredEvents", "skipping null element")
        continue
      }

      val type = element.asType()
      if (!isValidElement(element, type, supertype, analyticsElement)) {
        continue
      }

      val typeElement = element as TypeElement
      val metadata = typeElement.kotlinMetadata as KotlinClassMetadata
      val proto = metadata.data.classProto
      if (proto.constructorCount == 0) {
        logDebug("getDeclaredEvents", "$typeElement has no constructor, skipping")
        continue
      }

      val parameters = getParametersForConstructor(metadata, proto)
      val eventClass = typeElement.asClassName()
      events[eventClass] = parameters
    }
    return events
  }

  @CheckReturnValue
  private fun isValidElement(
    element: Element,
    type: TypeMirror,
    supertype: TypeMirror,
    typeElement: TypeElement
  ): Boolean {
    if (element !is TypeElement) {
      logDebug("isValidElement", "$element is not a Kotlin class, skipping")
      return false
    } else if (!typeUtils.directSupertypes(type).contains(supertype)) {
      logDebug("isValidElement", "$element does not extend $typeElement, skipping")
      return false
    }

    return true
  }

  @CheckReturnValue
  private fun getParametersForConstructor(
    metadata: KotlinClassMetadata,
    proto: ProtoBuf.Class
  ): List<String> {
    val nameResolver = metadata.data.nameResolver
    val mainConstructor = proto.constructorList[0]
    return mainConstructor.valueParameterList.map { nameResolver.getString(it.name) }
  }

  companion object {
    private val ANNOTATION = AutoAnalyticsEvent::class.java
  }

}
