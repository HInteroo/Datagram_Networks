import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")

public class MainGUI extends JFrame{
	public MainGUI(InetAddress serverName, int serverPort){
		ChatPanel chatPanel = new ChatPanel(serverName, serverPort);
		add(chatPanel);
	}
	
	public static class ChatPanel extends JPanel implements Runnable,ActionListener{
		private ChatClientArray clients;
		private Socket socket = null;
		boolean done = false;
		private InetAddress _ServerName;
		private int _ServerPort;				
		private JTextField ipInput, portInput;
		private String[] jbtnNames = {"CONNECT","DISCONNECT"};
		private JButton [] jbtns = new JButton[jbtnNames.length];
		private final int BTN_INDEX_CONNECT = 0;
		private final int BTN_INDEX_DISCONNECT = 1;
		
		public ChatPanel(InetAddress serverName, int serverPort){
			_ServerName = serverName;
			_ServerPort = serverPort;
			clients = new ChatClientArray();
			JLabel MsgLabel = new JLabel("IP Address: (1.2.3.4)");
			JLabel Msg2Label = new JLabel("Port Number (Int):");
			MsgLabel.setHorizontalAlignment(JLabel.CENTER);
			Msg2Label.setHorizontalAlignment(JLabel.CENTER);
			ipInput = new JTextField (10);
			portInput = new JTextField (10);
			setLayout(new BorderLayout());
			
			//--------------- Creating the main panel --------------------------------//
			
			JPanel MainPanel = new JPanel();
			MainPanel.setLayout(new GridLayout(2,1));
			JPanel topPanel = new JPanel();
			topPanel.setLayout(new GridLayout(2,2));
			topPanel.add(MsgLabel,BorderLayout.CENTER);
			topPanel.add(ipInput,BorderLayout.CENTER);
			topPanel.add(Msg2Label);
			topPanel.add(portInput);
			
			//---------------   Buttons --------------------------------//
			
			JPanel btnPanel = new JPanel();
			btnPanel.setLayout(new GridLayout(3,1));
			for(int i=0; i<jbtns.length; i++){
				jbtns[i] = new JButton(jbtnNames[i]);
				jbtns[i].addActionListener(this);
				btnPanel.add(jbtns[i]);
			}

			//----------------- Adding the panels to main panel ------------------------------//
			MainPanel.add(topPanel);
			MainPanel.add(btnPanel);
			add(MainPanel, BorderLayout.CENTER);

			//----------------- Connecting to a socket and receives thread------------------------------//
			socket = new Socket(_ServerPort);
			new Thread(this).start();

		}
		public void connect() throws UnknownHostException{ //Opening on a gui once i'm connected to someone
			try {
				open();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void open() throws UnknownHostException{
			try {
				ChatGui gui = new ChatGui(_ServerName, _ServerPort, ipInput.getText(), Integer.parseInt(portInput.getText()),socket);
				gui.setSize(500, 500);
				gui.setTitle(_ServerName+"    "+ _ServerPort);
				gui.setVisible(true);
				clients.add(Integer.parseInt(portInput.getText()),gui);
				} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//Opens up a ChatGUI
		public void disconnect(){
			done=true;
			System.exit(1);
		}
		
//Disconnects the chatgui/ closes it.
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btnClicked = (JButton) e.getSource();
			if(btnClicked.equals(jbtns[BTN_INDEX_CONNECT])){
				try {
					connect();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
			}
			else if(btnClicked.equals(jbtns[BTN_INDEX_DISCONNECT])){
				disconnect();
			}
		}
		
//Actions taken by pressing the send/disconnect button(s)
		
		@Override
		public void run() {
			int port;
			String address;
			DatagramPacket packet; 
			ChatGui gui;
			String message;
			while (!done) {
				
				packet = socket.receive();
				
				
				if(packet!=null) { // if packet is not null
					
					
					port = packet.getPort();
					address = packet.getAddress().getHostAddress();
					if(clients.contains(port)) {				
						

							gui = clients.getClient(port);
							
							
							do {
								if (packet != null) {
									message = new String(packet.getData());
									gui.DisplayMessage("Them: "+message);
								}
								
								packet = socket.receive();
								
								if (packet != null) {
									message = new String(packet.getData());
									gui.DisplayMessage("Them: "+message);
								}
								
								
							} while(packet != null);
						}			
					
					
					else if (!clients.contains(port)){		
						try {
							gui = new ChatGui(_ServerName, _ServerPort, address, port, socket);
							gui.setSize(500, 500);
							gui.setTitle(_ServerName+"    "+ _ServerPort);
							gui.setVisible(true);	
							message = new String(packet.getData());
							gui.DisplayMessage("Them: "+message);
							clients.add(port,gui);
						}catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}		
						}	
					}
				} // while the program is not done.
			}
	}
	
//method runs when it detects a thread. Runs until program is finished/disconnected
	
	public static void main(String[] args) throws UnknownHostException {
		InetAddress myAddress = null;
		
		try {
			myAddress = InetAddress.getLocalHost();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		int port = 63000;
		MainGUI gui = new MainGUI(myAddress, port);
		gui.setSize(500, 250);
		gui.setVisible(true);	
		gui.setTitle("IP: "+ myAddress+"    "+"Port: "+port);
	}
}

//How it works (Note to self): 
//	Step 1: Make a gui with a port -> 64000. Done on main()
// 	Step 2: Connect with someone by specifying there Port and IPAddress
//	Step 2.5: Entering nothing on IP/port or non integers on IP/port will result in a crush
//	Step 3: click send to send a message to the connected person
//	Step 3.5: Can receive messages from other messengers by opening a new window with their messages automatically when
//			 packet on run() is not null
// 