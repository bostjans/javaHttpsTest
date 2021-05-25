#!/bin/sh

# This ..
#

PROG_DIR=.
PROG_DIR=target

java -jar $PROG_DIR/httpsTest-1.0.0.jar -u https://www.setcce.com
#java -jar $PROG_DIR/httpsTest-1.0.0.jar -u https://www.setcce.com > httpsTest.log 2>&1

java -Djavax.net.debug=ssl,handshake -jar $PROG_DIR/httpsTest-1.0.0.jar -u https://www.setcce.com

#java -Djavax.net.debug=ssl:handshake:verbose:keymanager:trustmanager -Djava.security.debug=access:stack -jar $PROG_DIR/httpsTest-1.0.0.jar -u https://www.setcce.com

#java -Djavax.net.debug=all \
#  -Djavax.net.ssl.keyStore=$HOME/devel/project/017_nijz/nijz.vs.11/svn_nijz.vs2/trunk/src/VS.postarCaScheduler/zzzs/zzzs.keystore.jks \
#  -Djavax.net.ssl.keyStorePassword=VSpass_11_22 \
#  -Djavax.net.ssl.trustStoreType=jks \
#  -Djavax.net.ssl.trustStore=$HOME/devel/project/017_nijz/nijz.vs.11/svn_nijz.vs2/trunk/src/VS.postarCaScheduler/zzzs/cacerts.jks \
#  -Djavax.net.ssl.trustStorePassword=VSpass_11_22 \
#  -jar httpsTest-1.0.0.jar -u https://localhost:11443/NOZ4_Web/services/zzzsPaketiServicePort

echo That_s it.

exit 0
