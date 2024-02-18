package pl.andrzejressel.plugin

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

interface CommonExtension {
  @get:Input val license: Property<License>
  // When Java is too new (Palantir does not support Java 21)
  @get:Input @get:Optional val disableJavaFormatter: Property<Boolean>
  @get:Input @get:Optional val childAndParentInTheSameProject: Property<Boolean>
}
