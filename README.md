# Mindustry Mod Template
Template for creating multi-module mods for mindustry.

## Dependencies
To use a repository as a dependency, you need to specify its in `modules` as follows: `modules.{module short name} = [{module repository}, {module version}]`.

#### Example:<br>
```groovy
modules.arc = ["com.github.Anuken:Arc", "v143"]
modules.mindustry = ["com.github.Anuken:Mindustry", "v143"]
```

Subproject dependencies are specified as follows `deps.{dependency type}.{subproject name} = [{dependency list}]`.<br>

Dependencies types:
<ul> 
    <li>impl - implementation</li>
    <li>comp - compile only</li>
    <li>proc - annotation processor</li>
</ul>

To specify a dependency in the dependency list, you can use the following methods:
<ul>
    <li>basic method - "com.github.Anuken.Arc:arc-core:v143" </li>
    <li>project dependency project(":{project name}") - project(":core")</li>
    <li>by module module("{module short name}", "{submodule name (nothing if you importing root module) }") - module("arc", "arc-core")</li>
</ul>

#### Example:<br>
```groovy
deps.impl.core = [
        module("ktlStd", ""),
]
deps.comp.core = [
        project(":annotations"),
        module("mindustry", "core"),
        module("arc", "arc-core"),
]
deps.proc.core = [
        module("jabel", ""),
        project(":annotations"),
]
```

You can concat dependencies of different subprojects: <br>
#### Example:<br>
```groovy
// core dependencies
deps.impl.core = [
        module("ktlStd", ""),
]
deps.comp.core = [
        project(":annotations"),
        module("mindustry", "core"),
        module("arc", "arc-core"),
        module("arc", "extensions:flabel"),
        module("arc", "extensions:freetype"),
        module("arc", "extensions:g3d"),
        module("arc", "extensions:fx"),
        module("arc", "discord"),
        module("arc", "extensions:arcnet"),
]
deps.proc.core = [
        module("jabel", ""),
        project(":annotations"),
]
// desktop dependencies
deps.impl.desktop = [
        project(":core")
] + deps.impl.core
deps.comp.desktop = [
] + deps.comp.core
deps.proc.desktop = [
] + deps.proc.core
```

## Building

To build a project, you need to use buildProj - ./gradlew {subproject for build}:buildProj. <br>
#### Example: <br>

```
./gradlew desktop:buildProj
```
To build all subprojects at once, you need to use ./gradlew deploy. <br>
In this case, all artifacts will be moved into /artifacts folder.
