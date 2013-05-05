package com.bennavetta.appsite2.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles

class EnhanceTask extends DefaultTask
{
	//@Input
	def persistenceUnit
	
	//@Input
	def verbose
	
	@InputFiles
	def classes
	
	@TaskAction
	def enhance()
	{
		if(!persistenceUnit) persistenceUnit = project.jpa.persistenceUnit
		project.javaexec {
			classpath = project.files(classes, project.configurations.jpaEnhance)
			main = 'org.datanucleus.enhancer.DataNucleusEnhancer'
			args '-pu', persistenceUnit, '-api', 'JPA'
			if(verbose) args '-v'
		}
	}
}