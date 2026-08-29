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

// 👇 新增：自动给缺失 namespace 的第三方库插件补齐 namespace，兼容 AGP 8.0+
subprojects {
    afterEvaluate {
        val androidExtension = extensions.findByName("android")
        if (androidExtension != null) {
            val getNamespace = androidExtension.javaClass.getMethod("getNamespace")
            val currentNamespace = getNamespace.invoke(androidExtension)
            if (currentNamespace == null) {
                val setNamespace = androidExtension.javaClass.getMethod("setNamespace", String::class.java)
                // 使用子项目的 group 或名称作为默认 namespace
                val fallbackNamespace = "com.example.${project.name.replace("-", "_")}"
                setNamespace.invoke(androidExtension, fallbackNamespace)
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
