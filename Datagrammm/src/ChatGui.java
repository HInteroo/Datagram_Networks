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
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ChatGui extends JFrame{
	private ChatPanel chatPanel;
	
	public ChatGui(InetAddress serverName, int serverPort,String destinationIP,int destinationPort, Socket socket) throws UnknownHostException{
		chatPanel = new ChatPanel(serverName, serverPort,destinationIP,destinationPort, socket);
		add(chatPanel);
	}
	public void DisplayMessage(String message) {
		chatPanel.updateDisplay(message);
	}
	
	public static class ChatPanel extends JPanel implements ActionListener{
		private Socket socket = null;
		boolean done = false;
		private InetAddress _ServerName,_destinationIP;
		private int _serverPort,_destinationPort;						
		private JTextArea displayArea;				
		private JTextField tInput;
		private String[] jbtnNames = {"SEND", "DISCONNECT"};
		private JButton [] jbtns = new JButton[jbtnNames.length];
		private final int BTN_INDEX_SEND = 0;
		private final int BTN_INDEX_DISCONNECT = 1;

		
		public ChatPanel(InetAddress serverName, int serverPort, String destinationIP, int destinationPort, Socket MGSocket) throws UnknownHostException{
			JLabel MsgLabel = new JLabel("Message: ");
			_ServerName = serverName;
			_serverPort = serverPort;
			_destinationIP = InetAddress.getByName(destinationIP);
			_destinationPort = destinationPort;
			tInput = new JTextField (10);
			setLayout(new BorderLayout());
			displayArea = new JTextArea();
			add(displayArea, BorderLayout.CENTER);
			
			//-----------------------------------------------//
			
			JPanel bottomPanel = new JPanel();
			bottomPanel.setLayout(new GridLayout(2,1));
			JPanel btm1Panel = new JPanel();
			btm1Panel.setLayout(new GridLayout(1,3));
			btm1Panel.add(MsgLabel,BorderLayout.WEST);
			btm1Panel.add(tInput,BorderLayout.CENTER);
			
			//-----------------------------------------------//
			
			JPanel btn2Panel = new JPanel();
			btn2Panel.setLayout(new GridLayout(3,2));
			for(int i=0; i<jbtns.length; i++){
				jbtns[i] = new JButton(jbtnNames[i]);
				jbtns[i].addActionListener(this);
				btn2Panel.add(jbtns[i]);
			}

			//-----------------------------------------------//
			bottomPanel.add(btm1Panel);
			bottomPanel.add(btn2Panel);
			add(bottomPanel, BorderLayout.SOUTH);

			//-----------------------------------------------//
			socket = MGSocket;
			connect();
		}

		public void connect(){	
			String conStatus="Connected to server "+_ServerName + " on port: "+ _serverPort+"\n";
			updateDisplay(conStatus);
//			new Thread(this).start();
		}
//		
//		@Override
//		public void run() {
//			while (!done){
//				DatagramPacket packet;
//				do {
//					packet = socket.receive();
//					if (packet != null) {
//						String msg = new String(packet.getData());
//						updateDisplay("Them: "+msg);
//					}
//				} while(packet != null);
//			}
//		}
	
		public void disconnect() {
			done=true;
			System.exit(-1);
		}

		public void send(String msg){
			socket.send(msg, _destinationIP, _destinationPort);
			updateDisplay("You sent: "+ msg);
		}

		public void updateDisplay(String text){
				displayArea.append("\n"+text); 
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btnClicked = (JButton) e.getSource();			
			if(btnClicked.equals(jbtns[BTN_INDEX_SEND])){
				String msg = tInput.getText();
				send(msg);
			}
			else if(btnClicked.equals(jbtns[BTN_INDEX_DISCONNECT])){
				disconnect();
			}

		}
	}
//	public static void main(String[] args) throws UnknownHostException {
//		
//		InetAddress myAddress = null;
//		
//		try {
//			myAddress = InetAddress.getLocalHost();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(-1);
//		}
//		Socket socket = new Socket(63000);
//		ChatGui gui = new ChatGui(myAddress, 63000, "192.168.1.11", 64000, socket);
//		gui.setSize(500, 500);
//		gui.setVisible(true);	
//	}
}

