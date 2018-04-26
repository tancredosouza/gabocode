@echo off
 setLocal EnableDelayedExpansion
 set CLASSPATH="
 for /R ./target/dependencies %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
 )
 set CLASSPATH=!CLASSPATH!"
 echo !CLASSPATH!