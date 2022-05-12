# danger-kotlin JaCoCo plugin

Plugin for [danger-kotlin](https://github.com/danger/kotlin) that can parse JaCoCo code coverage reports
and post results as PR comments.

## Usage

To use this plugin, first put
```kotlin
@file:DependsOn("net.appsynth.danger:danger-kotlin-jacoco:X.Y.Z")
```
and
```kotlin
register plugin JaCoCoPlugin
```
in your `Dangerfile.df.kts`.

After this, you can use `JaCoCoPlugin` class, which provides 2 methods:
```kotlin
JaCoCoPlugin.parse(vararg reportFiles: File)
JaCoCoPlugin.report(filePaths: List<String>)
```

Alternatively you can use `jacoco {}` to keep danger file more organized:
```kotlin
jacoco {
    parse(File("app/build/reports/jacoco/jacoco.xml"))
    report(git.modifiedFiles + git.createdFiles)
}
```

For additional control, there are some useful configuration options you can set:
```kotlin
jacoco {
    // exclude files matching any of listed regular expressions
    // if not set, the list is empty
    excludePatterns = listOf(
        Regex(".*Test\\.(kt|java)")
    )

    // to reduce the size of the summary, there is a limit of 20 reported files
    // you can override this value with below parameter
    maxReportedFiles = 10

    // files that don't have any entries in JaCoCo report, are marked as not covered
    // by default there will be a warning added about this
    // to turn it off, set as below
    noCoverageWarning = false
}
```

## Example
```kotlin
@file:DependsOn("net.appsynth.danger:danger-kotlin-jacoco:X.Y.Z")

import net.appsynth.danger.JaCoCoPlugin
import net.appsynth.danger.jacoco
import systems.danger.kotlin.*
import java.io.File
import kotlin.io.walk

register plugin JaCoCoPlugin

danger(args) {
    val changedFiles =  git.modifiedFiles + git.createdFiles

    jacoco {
        val coverageReports = File(".")
            .walk()
            .maxDepth(10)
            .filter { it.name == "jacoco.xml" }
            .toList()

        parse(*coverageReports.toTypedArray())
        report(changedFiles.filter { it.endsWith(".kt") || it.endsWith(".java") })
    }
}
```

This will try to find all jacoco.xml files by traversing directory structure up to 10 levels deep.
Then it will parse all found reports into internal format. At the end it will post summary for modified
or added Kotlin/Java source files as PR comment.
