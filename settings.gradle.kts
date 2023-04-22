import org.gradle.kotlin.dsl.support.listFilesOrdered

rootProject.name = "benchmark-spring-application"

fun discoverSubprojects(start: File): List<File> {
    logger.debug("Visiting: $start")
    return start.listFilesOrdered { file -> file.isDirectory && file.resolve("build.gradle.kts").exists() }
        .flatMap { dir -> listOf(dir) + discoverSubprojects(dir) }
}

val rootPath = rootProject.projectDir
discoverSubprojects(rootPath).forEach { subproject ->
    val projectName = ":${subproject.relativeTo(rootPath).toString().replace(File.separator, ":")}"
    logger.debug("Subproject: $subproject ($projectName)")
    include(projectName)
}

