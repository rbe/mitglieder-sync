@echo off
rem
rem Start BVKDesktop with bundled JRE
rem Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
rem Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
rem
prompt -$G
@echo on
set BASE=%~dp0
set JAVA_HOME=%BASE%jre160_06
set PATH=%JAVA_HOME%\bin;%PATH%
set CP="%BASE%lib\*"
set OPT=-Djava.library.path=%BASE%lib
set MAIN=eu.artofcoding.mitglieder.sync.desktop.BVKDesktopApp
java %OPT% -cp %CP% %MAIN%
pause
