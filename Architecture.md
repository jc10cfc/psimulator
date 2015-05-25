# Introduction #

Simple description of communication subsystem with a idea of process architecture


# Details #

The main advantage of this architecture is a recursion. One application can be executed inside another one.

For example, CommandShell can run another CommandShell with a different user and history.

Also, there is minimal dependency with telnetd2 library(practicaly just BasicTerminalIO interface)



![http://psimulator.googlecode.com/svn/bp_psi/images/Architecture.png](http://psimulator.googlecode.com/svn/bp_psi/images/Architecture.png)