package com.stupica.prog;


import com.stupica.ConstGlobal;
import com.stupica.GlobalVar;

import com.stupica.core.UtilString;
import com.stupica.mainRunner.MainRunBase;
import jargs.gnu.CmdLineParser;

import java.net.HttpURLConnection;
import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by bostjans on 07/09/16.
 */
public class MainRun extends MainRunBase {
    // Variables
    //
    //boolean bIsModeTest = true;

    //long    iMaxRows = 100;

    String  sUrl = "https://www.setcce.com/";

    /**
     * Main object instance variable;
     */
    private static MainRun objInstance;

    CmdLineParser.Option obj_op_url;

    private static Logger logger = Logger.getLogger(MainRun.class.getName());


    /**
     * @param a_args    ..
     */
    public static void main(String[] a_args) {
        // Initialization
        GlobalVar.getInstance().sProgName = "httpsTester";
        GlobalVar.getInstance().sVersionBuild = "021";

        // Generate main program class
        objInstance = new MainRun();

        iReturnCode = objInstance.invokeApp(a_args);

        // Return
        if (iReturnCode != ConstGlobal.PROCESS_EXIT_SUCCESS)
            System.exit(iReturnCode);
    }


    protected void printUsage() {
        super.printUsage();
        System.err.println("            [-u,--url]URL(https://..)");
    }


    protected void initialize() {
        super.initialize();
        bShouldReadConfig = false;
        //bIsProcessInLoop = false;
    }


    /**
     * Method: defineArguments
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    protected int defineArguments() {
        // Local variables
        int         iResult;

        // Initialization
        iResult = super.defineArguments();

        obj_op_url = obj_parser.addStringOption('u', "url");
        return iResult;
    }

    /**
     * Method: readArguments
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    protected int readArguments() {
        // Local variables
        int         iResult;

        // Initialization
        iResult = super.readArguments();

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            // Set program parameter
            objInstance.sUrl = (String)obj_parser.getOptionValue(obj_op_url, "");
        }
        return iResult;
    }


    /**
     * Method: run
     *
     * Run ..
     *
     * @return int	1 = AllOK;
     */
    public int run() {
        // Local variables
        int         iResult;
        URL         objUrl = null;

        // Initialization
        iResult = super.run();

        if (UtilString.isEmptyTrim(sUrl)) {
            System.out.println("URL not provided!");
            return iResult;
        } else {
            System.out.println("Checking URL: " + sUrl);
        }

        try {
            objUrl = new URL(sUrl);
        } catch (MalformedURLException e) {
            iResult = ConstGlobal.RETURN_ERROR;
            System.err.println("URL is not correct: " + sUrl);
            e.printStackTrace();
        }
        System.out.println("\tProtocol: " + objUrl.getProtocol());
        System.out.println("\tHost: " + objUrl.getHost());
        System.out.println("\tPath: " + objUrl.getPath());
        System.out.println("\tPort(def): " + objUrl.getDefaultPort()
                + "\tPort: " + objUrl.getPort());

        // Do ..
        //
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            if (objUrl.getDefaultPort() == 443) {
                try {
                    // Run ..
                    iResult = testSsl(objUrl);
                    // Error
                    if (iResult != ConstGlobal.RETURN_OK) {
                        logger.severe("run(): Error at testSsl() operation!");
                    }
                } catch(Exception ex) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    logger.severe("run(): Error at testSsl() operation!");
                }
            }
        }
        // Do ..
        //
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            try {
                // Run ..
                iResult = testHttp(objUrl);
                // Error
                if (iResult != ConstGlobal.RETURN_OK) {
                    logger.severe("run(): Error at testHttp() operation!");
                }
            } catch(Exception ex) {
                iResult = ConstGlobal.RETURN_ERROR;
                logger.severe("run(): Error at testHttp() operation!");
            }
        }
        return iResult;
    }


    private int testSsl(URL aobjUrl) {
        // Local variables
        int                 iResult;
        SSLSocketFactory    sslsocketfactory = null;
        SSLSocket           sslsocket = null;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;
        System.out.println("\t.. test SSL -> .. ");

        try {
            sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslsocket = (SSLSocket) sslsocketfactory.createSocket(aobjUrl.getHost(), aobjUrl.getDefaultPort());

            InputStream in = sslsocket.getInputStream();
            OutputStream out = sslsocket.getOutputStream();

            // Write a test byte to get a reaction :)
            out.write(1);

            while (in.available() > 0) {
                System.out.print(in.read());
            }
            System.out.println("Successfully connected");
        } catch (SSLHandshakeException e) {
            iResult = ConstGlobal.RETURN_ERROR;
            e.printStackTrace();
            System.out.println("UnSuccessfully connected!! \n\tTry: -Djavax.net.debug=ssl,handshake"
                    + " \n\tTry: -Djavax.net.debug=ssl:handshake:verbose:keymanager:trustmanager -Djava.security.debug=access:stack"
                    + " \n\tTry: -Djavax.net.debug=all");
            throw new RuntimeException(e);
        } catch (Exception exception) {
            iResult = ConstGlobal.RETURN_ERROR;
            exception.printStackTrace();
        }
        return iResult;
    }


    private int testHttp(URL aobjUrl) {
        // Local variables
        int                 iResult;
        HttpsURLConnection  conHttps = null;
        HttpURLConnection   conHttp = null;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;
        System.out.println("\t.. test HTTP -> .. ");

        try {
            if (aobjUrl.getDefaultPort() == 443) {
                conHttps = (HttpsURLConnection)aobjUrl.openConnection();
                conHttp = conHttps;
            } else
                conHttp = (HttpURLConnection)aobjUrl.openConnection();

            if (conHttps != null) {
                // .. dump all cert info
                print_https_cert(conHttps);
            }
            // .. dump all the content
            print_content(conHttp);
        } catch (MalformedURLException e) {
            iResult = ConstGlobal.RETURN_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            iResult = ConstGlobal.RETURN_ERROR;
            e.printStackTrace();
        }
        return iResult;
    }


    private void print_https_cert(HttpsURLConnection con) {
        if (con != null) {
            try {
                System.out.println("Response Code       : " + con.getResponseCode());
                System.out.println("Cipher Suite        : " + con.getCipherSuite());
                System.out.println("Conn. timeout       : " + con.getConnectTimeout());
                System.out.println("Content encoding    : " + con.getContentEncoding());
                System.out.println("Content type        : " + con.getContentType());
                System.out.println("Content length      : " + con.getContentLength());

                System.out.println("\n");

                Certificate[] certs = con.getServerCertificates();
                for(Certificate cert : certs){
                    System.out.println("Cert Type       : " + cert.getType());
                    System.out.println("Cert Hash Code  : " + cert.hashCode());
                    System.out.println("Cert Public Key Algorithm   : "
                            + cert.getPublicKey().getAlgorithm());
                    System.out.println("Cert Public Key Format      : "
                            + cert.getPublicKey().getFormat());
                    System.out.println("\n");
                }
            } catch (SSLHandshakeException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (SSLPeerUnverifiedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void print_content(HttpURLConnection con) {
        String input;

        if (con != null) {
            try {
                System.out.println("****** Content of the URL ********");
                BufferedReader br = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));

                while ((input = br.readLine()) != null) {
                    System.out.println(input);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
