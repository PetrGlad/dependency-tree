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
4. Here are two options put files manually into your installation directory or install into workspace's metadata.
4.1 To install into workspace choose "install into host" and keep suggested filepath (somewhere in workspace/.metadata/) the rest will be done for you.
4.2 To install into host choose to export to directory, specify some directory name. Then put file petrglad.dependencytree_1.0.2.alpha.jar into your eclipse installation directory/dropins/dependency-tree/eclipse/plugins
5. Restart eclipse

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

