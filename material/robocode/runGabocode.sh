
#!/bin/bash
export CLASSPATH=".:$PWD/libs/robocode.jar:$CLASSPATH" 
for file in $PWD/droolslibs/*.jar; do 
    export CLASSPATH=".:$file:$CLASSPATH" 
done 
echo $CLASSPATH 
set ROBOCODE_PATH=$PWD

java -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -Xmx512M -cp $CLASSPATH -Dsun.io.useCanonCaches=false -DNOSECURITY=true -DWORKINGDIRECTORY=$ROBOCODE_PATH -Drobot.debug=true robocode.Robocode $@
