@REM
@REM Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
@REM All rights reserved. This program and the accompanying materials
@REM are made available under the terms of the Eclipse Public License v1.0
@REM which accompanies this distribution, and is available at
@REM http://robocode.sourceforge.net/license/epl-v10.html
@REM


 setLocal EnableDelayedExpansion
 set CLASSPATH=".\libs\robocode.jar
 for /R ./droolslibs %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
 )
 set CLASSPATH=!CLASSPATH!"
 echo !CLASSPATH!
 set ROBOCODE_PATH=%CD%

java -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -Xmx512M -cp %CLASSPATH% -Dsun.io.useCanonCaches=false -DNOSECURITY=true -DWORKINGDIRECTORY=%ROBOCODE_PATH% -Drobot.debug=true robocode.Robocode %*