Intro
-----

_dependency-tree_ is (very) simple plug-in that shows recursive dependencies (and dependency cycles) of given Java project in Eclipse. List of (directly or indirectly) required projects is written to a text file.
This plug-in can be helpful to resolve circular dependencies of a project. You may want to use it because standard means of Eclipse are too tedious for this task and for large workspaces manual finding all project dependencies can be daunting.


Installation
------------

This plug-in is currently distributed in source form only. To use it you need Eclipse with Plug-in Development Environment
(for example Eclipse "classic"). 

### To compile and install
1. Import the plug-in project into your workspace
2. Export/Plug-in development/Deployable plug-ins and fragments
3. Choose the plug-in project
4. Choose to export to directory, specify some directory name (You may instead choose to "install into host" and the rest will be done for you.)
5. In your eclipse installation directory put file petrglad.dependencytree_1.0.2.alpha.jar into /dropins/dependency-tree/eclipse/plugins
6. Restart eclipse

### Quick&Dirty way to run it immediately without installing
1. Create new (temporary) workspace
2. Import this plug-in project into it
3. Create new runtime configuration "eclipse application", and point it's workspace location to workspace 
with projects you want to examine
4. Run this configuration.

Usage
-----

In Java's package explorer open Java project's context menu and choose "Dependencies/Show dependencies", 
look into file dependencies*.txt.

