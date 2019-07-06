package com.stupica.prog;


import com.stupica.ConstGlobal;
import com.stupica.GlobalVar;

import jargs.gnu.CmdLineParser;

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
public class MainRun {
    // Variables
    //
    boolean bIsModeTest = true;

    long    iMaxRows = 100;

    String  sDelimiter = ";";
    String  sDtFormat = "yyyy-MM-dd HH:mm:ss";
    String  sUrl = "https://www.setcce.com/";

    /**
     * Main object instance variable;
     */
    private static MainRun objInstance;

    private static Logger logger = Logger.getLogger(MainRun.class.getName());

    //private SimpleDateFormat objDateFormat01 = new SimpleDateFormat(sDtFormat);


    /**
     * @param a_args    ..
     */
    public static void main(String[] a_args) {
        // Local variables
        int             i_result;
        int             i_return;

        // Initialization
        i_result = ConstGlobal.RETURN_OK;
        //
        i_return = ConstGlobal.PROCESS_EXIT_SUCCESS;
        GlobalVar.getInstance().sProgName = "httpsTester";
        GlobalVar.getInstance().sVersionMax = "0";
        GlobalVar.getInstance().sVersionMin = "1";
        GlobalVar.getInstance().sVersionPatch = "0";
        GlobalVar.getInstance().sVersionBuild = "11";
        GlobalVar.getInstance().sAuthor = "stupica.com - Bostjan Stupica";

        // Generate main program class
        objInstance = new MainRun();

        if (objInstance.bIsModeTest) {
            if (logger != null) {
                //logger.setLevel(java.util.logging.Level.ALL);
                logger.setLevel(Level.FINE);

                ConsoleHandler handler = new ConsoleHandler();
                // PUBLISH this level
                handler.setLevel(Level.FINE);
                logger.addHandler(handler);
                //
                logger.setUseParentHandlers(false);
            }
        }

        // Program parameters
        //
        // Create a CmdLineParser, and add to it the appropriate Options.
        CmdLineParser obj_parser = new CmdLineParser();
        CmdLineParser.Option obj_op_help = obj_parser.addBooleanOption('h', "help");
        CmdLineParser.Option obj_op_quiet = obj_parser.addBooleanOption('q', "quiet");
        CmdLineParser.Option obj_op_url = obj_parser.addStringOption('u', "url");

        try {
            obj_parser.parse(a_args);
        } catch (CmdLineParser.OptionException e) {
            System.err.println(e.getMessage());
            print_usage();
            System.exit(ConstGlobal.PROCESS_EXIT_FAIL_PARAM);
        }

        if (Boolean.TRUE.equals(obj_parser.getOptionValue(obj_op_help))) {
            print_usage();
            System.exit(ConstGlobal.PROCESS_EXIT_SUCCESS);
        }
        if (!Boolean.TRUE.equals(obj_parser.getOptionValue(obj_op_quiet)))
        {
            // Display program info
            System.out.println();
            System.out.println("Program: " + GlobalVar.getInstance().sProgName);
            System.out.println("Version: " + GlobalVar.getInstance().get_version());
            System.out.println("Made by: " + GlobalVar.getInstance().sAuthor);
            System.out.println("===");
            // Check logger
            if (logger != null) {
                logger.info("main(): Program is starting ..");
            }
        } else {
            GlobalVar.bIsModeVerbose = false;
        }

        objInstance.sUrl = (String)obj_parser.getOptionValue(obj_op_url, "");

        // Check previous step
        if (i_return == ConstGlobal.PROCESS_EXIT_SUCCESS) {
            // Run ..
            i_result = objInstance.run();
            // Error
            if (i_result != ConstGlobal.RETURN_OK) {
                logger.severe("main(): Error at run() operation!");
                i_return = ConstGlobal.PROCESS_EXIT_FAILURE;
            }
        }

        // Return
        if (i_return != ConstGlobal.PROCESS_EXIT_SUCCESS)
            System.exit(i_return);
        else
            //System.exit(GlobalVar.getInstance().EXIT_SUCCESS);
            return;
    }


    private static void print_usage() {
        System.err.println("Usage: prog [-h,--help]");
        System.err.println("            [-q,--quiet]");
        System.err.println("            [-u,--url]URL(https://..)");
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
        iResult = ConstGlobal.RETURN_SUCCESS;
        System.out.println("Checking URL: " + sUrl);

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
        int     iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;
        System.out.println("\t.. test HTTP -> .. ");

        try {
            HttpsURLConnection con = (HttpsURLConnection)aobjUrl.openConnection();

            //dumpl all cert info
            print_https_cert(con);

            //dump all the content
            print_content(con);
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
        if (con!=null) {

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

    private void print_content(HttpsURLConnection con) {
        String input;

        if (con!=null) {
            try {
                System.out.println("****** Content of the URL ********");
                BufferedReader br =
                        new BufferedReader(
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
