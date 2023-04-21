pluginManagement {
        repositories {
                mavenLocal()
                jcenter()
                mavenCentral()
        }
}

include ("ee-common_java", "ee-common", "ee-lang_item", "ee-lang_gen", "ee-lang",
        "ee-design_gen", "ee-design", "ee-design_swagger", "ee-design_xsd", "ee-design_json")