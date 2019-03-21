package com.offbeatmind.humane.gradle;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.SourceSet;

import com.offbeatmind.humane.java.JavaSourceTree
import com.offbeatmind.humane.java.JavaLanguageProcessor

public class HumaneCodeGradlePlugin implements Plugin<Project> {
    private static final String TASK_NAME_PREFIX = "humaneStyle";
    
    void apply(Project project) {
        if (hasSupportedLanguages(project)) {
            throw new GradleException("Cannot apply this plugin before supported language plugins.");
        }
        project.afterEvaluate {
            applyHumaneCodeStyle(project);
        }
    }
    
    private boolean hasSupportedLanguages(Project project) {
        if (project.plugins.findPlugin('java') != null) return true;
        
        for (Project childProject: project.getChildProjects().values()) {
            if (hasSupportedLanguages(childProject)) return true;
        }
        
        return false;
    }

    void applyHumaneCodeStyle(Project project) {
        if (project.extensions.findByName('humaneCode') != null) return;

        def extension = project.extensions.create('humaneCode', HumaneCodeExtension);
        
        Task thisCheckTask = project.task(TASK_NAME_PREFIX + 'Check') {
            group        'Verification'
            description  'Checks all code for humane formatting.'
        }
        Task checkTask = project.tasks.findByName('check');
        if (checkTask != null) checkTask.dependsOn thisCheckTask

        Task thisFixTask = project.task(TASK_NAME_PREFIX + 'Fix') {
            group        'Style'
            description  'Fixes code style/formatting errors.'
        }
        
        // TODO Currently only Java is supported, want more.
        if (project.plugins.findPlugin('java') != null) createJavaTasks(project, thisCheckTask, thisFixTask);
        
        // Apply to relevant subprojects
        final HumaneCodeGradlePlugin thisPlugin = this;
        final Project thisProject = project;

        for (Project childProject: project.getChildProjects().values()) {
            childProject.afterEvaluate {
                if (thisPlugin.hasSupportedLanguages(childProject)) {
                    applyHumaneCodeStyle(childProject);
                    relateTasks(thisProject, childProject);
                }
            }
        }
    }
    
    private void createJavaTasks(Project project, Task combinedCheckTask, Task combinedFixTask) {
        project.sourceSets.each { sourceSet ->
            String name = sourceSet.name
            String capitalizedName = name.capitalize()
            
            Task checkTask = project.task(TASK_NAME_PREFIX + 'Check' + capitalizedName + 'Java') {
                group        'Verification'
                description  "Checks' ${name}' code for humane formatting."
            }
            configureJavaTask(checkTask, sourceSet, false);
            combinedCheckTask.dependsOn checkTask
            
            Task fixTask = project.task(TASK_NAME_PREFIX + 'Fix' + capitalizedName + 'Java') {
                group        'Style'
                description  "Fixes '${name}' code style/formatting errors."
            }
            configureJavaTask(checkTask, sourceSet, true);
            combinedFixTask.dependsOn fixTask
        }
    }
    
    private void configureJavaTask(Task javaTask, SourceSet sourceSet, boolean fixErrors) {
        javaTask.configure {
            inputs.files sourceSet.allJava
            
            doLast {
                Set<File> srcDirs = sourceSet.allJava.srcDirs;
                ArrayList<File> sourceTrees = new ArrayList<File>(srcDirs.size());
                
                for (File dir: sourceSet.allJava.srcDirs) {
                    if (dir.exists()) {
                        sourceTrees.add(new JavaSourceTree(dir));
                    }
                }
                JavaLanguageProcessor.INSTANCE.process(fixErrors, sourceTrees);
            }
        }
    }
    
    private void relateTasks(Project parentProject, Project childProject) {
        for (Task childTask: childProject.getTasks()) {
            final String taskName = childTask.getName();
            
            if (taskName.startsWith(TASK_NAME_PREFIX)) {
                Task parentTask = parentProject.getTasks().findByName(taskName);
                
                if (parentTask == null) {
                    parentTask = parentProject.task(taskName) {
                        group       childTask.group
                        description childTask.description
                    }
                }
                parentTask.dependsOn childTask
            }
        }
    }
}
