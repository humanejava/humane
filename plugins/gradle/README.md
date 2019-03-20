# Humane Code Style Gradle Plugin

Please see the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/com.offbeatmind.humane.gradle) for how to apply this plugin to your project.

The plugin only needs to be applied to the root project. It will add relevant tasks and extensions to all subprojects with supported languages automatically.

The tasks are:

  - `humaneStyleCheck<source-set-name><language-name>` (e.g. humaneStyleCheckMainJava) to check a specified source set.
  - `humaneStyleCheck` to check all the supported code found in all source sets of the project called for and all its subprojects.
  - Not available yet: `humaneStyleFix<source-set-name><language-name>` (e.g. humaneStyleFixMainJava) to fix the formatting of a specified source set.
  - Not available yet: `humaneStyleFix` to fix the formatting of all the supported code found in all source sets of the project called for and all its subprojects.

Note: `check` task is made dependent on `humaneStyleCheck`.  
