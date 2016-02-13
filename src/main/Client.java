package main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;

/*
 * Client app:
 */

public class Client {

	private static JFrame basicFrame = new JFrame("Client");
	static JPanel panel = new JPanel();
	static JLabel topicLabel = new JLabel("This is client application.");
	static JLabel hostLabel = new JLabel("Set host");
	static JTextField hostField = new JTextField();
	static JButton runButton = new JButton("Run");
	static JLabel portLabel = new JLabel("Set port");
	static JTextField portField = new JTextField();

	private static JLabel scriptPathLabel = new JLabel("Set script");
	private static JTextField scriptPathField = new JTextField("testScript.rpy");

	private static JLabel databaseLabel = new JLabel("Set results folder");
	private static JTextField sourceFolder = new JTextField("testSourceFolder");

	public static void main(String[] args) {

		basicFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel.setLayout(new GridLayout(10, 1));

		panel.add(topicLabel);
		panel.add(hostLabel);
		panel.add(hostField);
		panel.add(portLabel);
		panel.add(portField);
		panel.add(scriptPathLabel);
		panel.add(scriptPathField);
		panel.add(databaseLabel);
		panel.add(sourceFolder);
		panel.add(runButton);

		runButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String host = hostField.getText();
				String script = readScript(scriptPathField.getText());
				Client client = new Client();
				File file = new File(sourceFolder.getText() + "/Results.odb");
				try {
					Registry registry = LocateRegistry.getRegistry(host);
					ServerInterface stub = (ServerInterface) registry
							.lookup("ServerInterface");

					File test = stub.runAbaqusScript(script,
							sourceFolder.getText());

					client.sendFile(stub.getFile(test.getPath()),
							file.getPath());
				} catch (Exception e) {
					System.err.println("Ecteption in client app: "
							+ e.toString());
					e.printStackTrace();
				}
			}
		});

		panel.setPreferredSize(new Dimension(300, 300));
		basicFrame.getRootPane().setDefaultButton(runButton);
		basicFrame.add(panel);
		basicFrame.pack();
		basicFrame.setVisible(true);
	}

	public static String readScript(String fileName) {

		StringBuilder stringBuilder = new StringBuilder();
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
