package com.ctianjhoey.chat.client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Client extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP = "";
	private Socket connection;
	
	//constructor
	public Client(String host) {
		super("Client chat box!");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
	}
	
	protected void sendMessage(String text) {
		try {
			output.writeObject("CLIENT - " + text);
			output.flush();
			showMessage("\nCLIENT - " + text);
		} catch (IOException e) {
			chatWindow.append("\nShit happened!");
		}
	}

	private void showMessage(final String x) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(x);
			}
		});
	}

	//connect to server
	public void startRunning() {
		try {
			
			connectToServer();
			setupStreams();
			whileChatting();
			
		} catch (EOFException e) {
			showMessage("\nClient terminated the connection");
		} catch (IOException i) {
			i.printStackTrace();
		} finally {
			closeCrap();
		}
	}

	private void closeCrap() {
		showMessage("\nClosing connections...");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				
				message = (String) input.readObject();
				showMessage("\n" + message);
				
			} catch (ClassNotFoundException e) {
				showMessage("\n Idk that object! \n");
			}
		} while (!message.equals("SERVER - END"));
	}

	private void ableToType(final boolean b) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(b);
				
			}
		});
	}

	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nDude your streams are now good to go! \n");
	}

	private void connectToServer() throws IOException {
		showMessage("Attempting connection...\n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to : "+ connection.getInetAddress().getHostName());
	}
	
	
	
}
