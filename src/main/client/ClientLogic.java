package main.client;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import main.server.ServerInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Created by Filip on 2016-09-19.
 */
public class ClientLogic {
    public ActionListener runRMIAction(final String host, final String scriptPath, final String sourceFolder) {
        return new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                File file = new File(sourceFolder + "/Results.odb");
                String script = readScript(scriptPath);
                try {
                    Registry registry = LocateRegistry.getRegistry(host);
                    ServerInterface stub = (ServerInterface) registry
                            .lookup("ServerInterface");
                    File test = stub.runAbaqusScript(script, sourceFolder);
                    sendFile(stub.getFile(test.getPath()), file.getPath());
                } catch (Exception e) {
                    System.err.println("Exception in client app: "
                            + e.toString());
                    e.printStackTrace();
                }
            }
        };
    }

    public static String readScript(String fileName) {
        String line = "";
        try {
            line = new Scanner(new File(fileName)).useDelimiter("\\A").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(line);
        return line;
    }

    public void sendFile(RemoteInputStream inFile, String fileName)
            throws IOException {

        OutputStream outputStream = null;
        InputStream istream = RemoteInputStreamClient.wrap(inFile);
        try {
            int content;
            outputStream = new FileOutputStream(new File(fileName));
            byte[] bytes = new byte[1024];

            while ((content = istream.read()) != -1) {
                outputStream.write(bytes, 0, content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (istream != null)
                    istream.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
