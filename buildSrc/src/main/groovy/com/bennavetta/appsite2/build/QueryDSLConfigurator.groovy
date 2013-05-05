package com.bennavetta.appsite2.build;

import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class QueryDSLConfigurator
{
	public static void setupQueryDSL(Project project)
	{
		def targetDir = new File(project.buildDir, "querydsl-generated")
		project.task('queryDSL', type: JavaCompile) {
			source = project.sourceSets.main.java
			classpath = project.configurations.queryDsl // extends compile
			options.compilerArgs = [
				"-proc:only",
				"-processor", "com.mysema.query.apt.jpa.JPAAnnotationProcessor"
			]
			destinationDir = targetDir
		}
		
		project.queryDSL.inputs.dir project.sourceSets.main.java
		project.queryDSL.outputs.dir targetDir
		
		project.compileJava.dependsOn project.queryDSL
		project.compileJava.source targetDir
		
		/*
		project.eclipse {
			classpath {
				file {
					withXml { // not sure how else to add an arbitrary source directory
						def node = it.asNode()
						node.appendNode('classpathentry', [kind: 'src', path: targetDir.canonicalPath])
					}
				}
			}
		}
		*/
	}
}