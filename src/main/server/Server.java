package main.server;

import javax.swing.*;

/*
 * Server side of app
 */
public class Server {


	public static void main(String[] args) {
		JFrame frame = new JFrame("Server");
		JLabel label = new JLabel("Server is Running!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(label);

		ServerLogic.startServer();

		frame.setBounds(500, 0, 100, 100);
		frame.pack();
		frame.setVisible(true);
	}

}
