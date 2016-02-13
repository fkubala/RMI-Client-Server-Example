package main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;

/*
 * Server side of app
 */
public class Server implements ServerInterface {

	private static final String abaqusRunFile = "C:/Temp/abq_cae_open.bat";
	private static final String homePathForOperations = "C:/Temp/test4/";

	public static void main(String[] args) {
		JFrame frame = new JFrame("Server");
		JLabel label = new JLabel("Server is Running!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(label);
		try {
			System.setSecurityManager(new RMISecurityManager());
			Server obj = new Server();
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

		frame.setBounds(500, 0, 100, 100);
		frame.pack();
		frame.setVisible(true);
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
				process = runtime.exec(new String[] { abaqusRunFile,
						homePathForOperations + scriptPath });
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (file == null) {
				file = findResultFile(homePathForOperations);
			}
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
			throws FileNotFoundException, IOException {
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
