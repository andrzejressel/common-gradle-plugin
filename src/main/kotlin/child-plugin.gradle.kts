import org.gradle.kotlin.dsl.*
import pl.andrzejressel.plugin.CommonExtension
import pl.andrzejressel.plugin.License

plugins {
  `java-library`
  jacoco
  com.vanniktech.maven.publish
  com.diffplug.spotless
}

repositories { mavenCentral() }

val licenseExt = extensions.create("childPlugin", CommonExtension::class.java)

val mvnGroupId = rootProject.group.toString()
val mvnArtifactId = name
val mvnVersion = rootProject.version.toString()

version = mvnVersion

tasks.jacocoTestReport {
  dependsOn("test")

  executionData.setFrom(fileTree(layout.buildDirectory).include("/jacoco/*.exec"))
  reports {
    xml.required = true
    html.required = true
  }
}

tasks.named("check") { dependsOn("jacocoTestReport") }

afterEvaluate {
  val license = licenseExt.license.get()

  spotless {
    kotlin {
      target("src/**/*.kt")
      ktfmt()
      licenseHeader("// SPDX-License-Identifier: ${license.spdxId}")
      endWithNewline()
    }
    groovyGradle {
      target("*.gradle") // default target of groovyGradle
      greclipse()
      endWithNewline()
    }
    if (licenseExt.childAndParentInTheSameProject.orNull != true) {
      kotlinGradle {
        target("*.gradle.kts", "src/**/*.gradle.kts") // default target for kotlinGradle
        ktfmt() // or ktfmt() or prettier()
        endWithNewline()
      }
    }
    if (licenseExt.disableJavaFormatter.orNull != true) {
      java {
        importOrder()
        removeUnusedImports()
        cleanthat()
        palantirJavaFormat().style("GOOGLE")
        target("src/**/*.java")
        licenseHeader("// SPDX-License-Identifier: ${license.spdxId}")
        endWithNewline()
      }
    }
  }

  mavenPublishing {
    coordinates(mvnGroupId, mvnArtifactId, mvnVersion)

    pom {
      licenses {
        when (license) {
          License.GPL ->
              license {
                name = "The GNU General Public License"
                url = "https://www.gnu.org/licenses/gpl-3.0.txt"
                distribution = "https://www.gnu.org/licenses/gpl-3.0.txt"
              }
          License.LGPL ->
              license {
                name = "Gnu Lesser General Public License"
                url = "https://www.gnu.org/licenses/lgpl-3.0.txt"
                distribution = "https://www.gnu.org/licenses/lgpl-3.0.txt"
              }
        }
      }
    }
  }
}
