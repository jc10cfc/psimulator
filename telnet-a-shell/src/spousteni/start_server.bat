@echo off

::call:help

if "%1"=="-h" (
	call:help
)

if "%2"=="" ( :: zadano malo parametru
	echo Specify config file and port!
	echo.
	call:help
	exit
)

java -jar psimulator.jar %*

exit


:help
    echo This is help for starting a server:
    echo First arg is configuration file.
    echo Second arg is a port.
    echo For reading only skelet of computers and interfaces (without settings) use optional arg '-n'
exit \b
