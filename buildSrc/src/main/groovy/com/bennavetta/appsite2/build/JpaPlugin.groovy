package com.bennavetta.appsite2.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class JpaPlugin implements Plugin<Project>
{
	public static final String DATANUCLEUS_VERSION = '3.2.1'
	public static final String QUERYDSL_VERSION = '3.1.1'
	
	public void apply(Project project)
	{
		project.extensions.create('jpa', JpaExtension)
		project.configurations {
			jpaEnhance {
				extendsFrom compile
			}
			queryDsl {
				extendsFrom compile
			}
		}
		project.dependencies {
			compile "org.datanucleus:datanucleus-core:$DATANUCLEUS_VERSION"
			compile "org.datanucleus:datanucleus-api-jpa:$DATANUCLEUS_VERSION"
			compile "org.apache.geronimo.specs:geronimo-jpa_2.0_spec:1.1"
			jpaEnhance "javax.jdo:jdo-api:3.0.1"
			
			queryDsl "com.mysema.querydsl:querydsl-apt:$QUERYDSL_VERSION"
			compile "com.mysema.querydsl:querydsl-jpa:$QUERYDSL_VERSION"
		}
		
		QueryDSLConfigurator.setupQueryDSL(project)
		
		project.task('jpaEnhance', type: EnhanceTask)
		project.jpaEnhance.classes = [project.sourceSets.main.output.classesDir, project.sourceSets.main.output.resourcesDir]
		project.jpaEnhance.dependsOn project.compileJava, project.processResources
		project.classes.dependsOn project.jpaEnhance
	}
}
