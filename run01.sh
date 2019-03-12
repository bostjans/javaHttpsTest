#!/bin/sh

# This ..
#

PROG_DIR=.
PROG_DIR=target

java -jar $PROG_DIR/httpsTest-1.0.0-jar-with-dependencies.jar -u https://www.setcce.com
#java -jar $PROG_DIR/httpsTest-1.0.0-jar-with-dependencies.jar -u https://www.setcce.com > httpsTest.log 2>&1

java -Djavax.net.debug=ssl,handshake -jar $PROG_DIR/httpsTest-1.0.0-jar-with-dependencies.jar -u https://www.setcce.com

#java -Djavax.net.debug=ssl:handshake:verbose:keymanager:trustmanager -Djava.security.debug=access:stack -jar $PROG_DIR/httpsTest-1.0.0-jar-with-dependencies.jar -u https://www.setcce.com

echo That_s it.

exit 0
