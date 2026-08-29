allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory =
    rootProject.layout.buildDirectory
        .dir("../../build")
        .get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}

// 自动给缺失 namespace 的第三方库插件补齐 namespace（安全的评估状态检查）
subprojects {
    val fixNamespace: Project.() -> Unit = {
        val androidExtension = extensions.findByName("android")
        if (androidExtension != null) {
            val getNamespace = androidExtension.javaClass.getMethod("getNamespace")
            val currentNamespace = getNamespace.invoke(androidExtension)
            if (currentNamespace == null) {
                val setNamespace = androidExtension.javaClass.getMethod("setNamespace", String::class.java)
                val fallbackNamespace = "com.example.${name.replace("-", "_")}"
                setNamespace.invoke(androidExtension, fallbackNamespace)
            }
        }
    }

    if (state.executed) {
        fixNamespace()
    } else {
        afterEvaluate {
            fixNamespace()
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
