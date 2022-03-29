# javaHttpsTest
HTTPs tester .. for Java JVM.


## Usage

```
java -jar target/httpsTest-1.0.0.jar -h
Usage: prog [-h,--help]
            [-q,--quiet]
            [-u,--url]URL(https://..)
```

.. and to actually connect somewhere:
```
java -jar target/httpsTest-1.0.0.jar -u https://www.dev404.net
```

```
Program: httpsTester
Version: 0.*
Made by: Dev404
===
Jul 06, 2019 9:57:29 PM com.stupica.prog.MainRun main
INFO: main(): Program is starting ..
Checking URL: https://www.dev404.net
        Protocol: https
        Host: www.dev404.net
        Path:
        Port(def): 443  Port: -1
        .. test SSL -> ..
Successfully connected
        .. test HTTP -> ..
Response Code       : 200
Cipher Suite        : TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256
Conn. timeout       : 0
Content encoding    : null
Content type        : text/html
Content length      : 739

Cert Type       : X.509
Cert Hash Code  : 274535925
Cert Public Key Algorithm   : RSA
Cert Public Key Format      : X.509

Cert Type       : X.509
Cert Hash Code  : -1251542849
Cert Public Key Algorithm   : RSA
Cert Public Key Format      : X.509

+ the it comes the Content ..
```


## Ref.

* https://mkyong.com/java/java-https-client-httpsurlconnection-example/
