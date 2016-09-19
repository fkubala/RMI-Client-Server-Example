package main.server;

import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;

import java.io.*;
import java.net.InetAddress;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Filip on 2016-09-19.
 */
public class ServerLogic implements ServerInterface {

    //Location of Abaqus on server
    private static final String abaqusRunFile = "C:/Temp/abq_cae_open.bat";
    //Location of homepath on server
    private static final String homePathForOperations = "C:/Temp/test4/";

    public static void startServer() {
        try {
            System.setSecurityManager(new RMISecurityManager());
            ServerLogic obj = new ServerLogic();
            ServerInterface stub = (ServerInterface) UnicastRemoteObject
                    .exportObject(obj, 0);
            System.out.println(InetAddress.getLocalHost().getHostAddress());

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("ServerInterface", stub);
            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Exteption while server running: "
                    + e.toString());
            e.printStackTrace();
        }
    }

    public File runAbaqusScript(String script, String database)
            throws IOException {
        File file = null;

        Runtime runtime = Runtime.getRuntime();
        findAndDelete(homePathForOperations);
        String scriptPath = createTemporaryScript(script);
        if (createTemporaryScript(script) != null) {
            Process process = null;
            try {
                process = runtime.exec(new String[]{abaqusRunFile,
                        homePathForOperations + scriptPath});
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (file == null)
                file = findResultFile(homePathForOperations);

            getFile(file.getPath());
        }
        return file;
    }

    private String createTemporaryScript(String script) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(homePathForOperations + "tempScript.rpy");
        } catch (FileNotFoundException e) {
            System.out.println("No file");
            e.printStackTrace();
        }
        writer.write(script);
        writer.close();
        if (writer.equals(null)) {
            System.out.println("Script not created!");
            return "Error";
        } else
            return "tempScript.rpy";
    }

    /*
     * If there was some old result file, delete it
     */
    private void findAndDelete(String path) {
        String extension = "odb";
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.getName().endsWith(extension)) {
                System.out.println(file.getName());
                file.delete();
            }
        }
    }

    private File findResultFile(String path) {
        String extention = "odb";
        File folder = new File(path);
        File fileToReturn = null;
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            System.out.println(file.getName());
            if (file.getName().endsWith(extention)) {
                fileToReturn = file;
            }
        }
        return fileToReturn;
    }

    /*
     * Method available for client
     */
    public RemoteInputStream getFile(String fileName)
            throws IOException {
        RemoteInputStreamServer istream = null;
        try {
            istream = new GZIPRemoteInputStream(new BufferedInputStream(
                    new FileInputStream(fileName)));
            RemoteInputStream result = istream.export();

            istream = null;
            return result;
        } finally {
            if (istream != null)
                istream.close();
        }
    }
}
