# idiro-tm
Utility for command line programmes

This library gather some classes for running command line programs.

A task is an independent runnable programms composed of:
* an init phase
* a run phase
* a check phase

A task can take variables in two fashion:
* Arguments - typed at run time
* Preferences - stored on the user or system level

A SuperTask is a task on which you can have several profiles

You can always get the help of a task with the "-h" option. 
* Alone or with a non task first argument, it will list the different actions you can run within the programme or the package
* On a task, it will give you a description, the possible variables, the default etc.

A bare minimal CLI have been created, 
but have been abandoned. It doesn't give any extras compare to a standard terminal environment.

An import/export preference utility is provided inside.
