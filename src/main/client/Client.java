package main.client;

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
import main.server.ServerInterface;

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
        ClientLogic clientLogic = new ClientLogic();

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

        //All RMI actions are under this button action
        panel.add(runButton);
        runButton.addActionListener(clientLogic.runRMIAction(hostField.getText(),
                scriptPathField.getText(), sourceFolder.getText()));

        panel.setPreferredSize(new Dimension(300, 300));
        basicFrame.getRootPane().setDefaultButton(runButton);
        basicFrame.add(panel);
        basicFrame.pack();
        basicFrame.setVisible(true);
    }

}
