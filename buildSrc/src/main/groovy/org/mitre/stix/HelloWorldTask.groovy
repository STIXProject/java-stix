package org.mitre.stix

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class HelloWorldTask extends DefaultTask {
    @TaskAction
    def sayHelloWorld() {
        println "Hello World!"
    }
}

