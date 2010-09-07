Intro
-----

_dependency-tree_ is (very) simple plugin that shows recursive dependencies (and dependency cycles) 
of given Java project in Eclipse. List of (directly or indirectly) required projects is written to a text file. 

This plugin can be helpful to resolve circular dependencies of a project. You may want to use it because 
standard means of Eclipse are too tedious for this task and for large workspaces manual finding all  
project dependencies can be daunting. 


Installation
------------

This plugin is currently distributed in source form only. To yse it you need Eclipse with Plugin Development Environment
(for example Eclipse "classic"). 

### Quick&Dirty way to run it immediately:
1. Create new (temporary) workspace
2. Import this plugin project into it
3. Create new runtime configuration "eclipse application", and point it's workspace location to workspace 
with projects you want to examine
4. Run it  

### To compile and install:
1. Import the plugin project into your workspace
2. Export/Plugin development/Deployable plugins and fragments
3. Choose the plugin project
4. Choose to export to directory, specify some directory name (You may instead choose to "install into host" and the rest will be done for you.)
5. In your eclipse installation directory put file petrglad.dependencytree_1.0.2.alpha.jar into /dropins/dependency-tree/eclipse/plugins
6. Restart eclipse


Usage
-----

In Java's package explorer open Java project's context menu and choose "Dependencies/Show dependencies", 
look into file dependencies*.txt.

