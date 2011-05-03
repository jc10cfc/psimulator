@echo off


IF "%1"=="" (
	echo Specify a port!
	call:napoveda
	exit
)

IF "%1"=="-h" (
	call:napoveda
	exit
)


cygwin\bin\rlwrap.exe -f "doplnovani_linux.txt" "cygwin/bin/telnet.exe" localhost %1

exit


:: Deklarace funkci =============================

:napoveda
	echo usage: linux.bat ^<port of requested virtual computer^>
EXIT /b

