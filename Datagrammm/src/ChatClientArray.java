import java.util.ArrayList;

public class ChatClientArray{
	public ArrayList<ChatGui> clients;
	public ArrayList<Integer> GUIPorts;
	
		public ChatClientArray() {
			clients = new ArrayList<ChatGui> (5);
			GUIPorts = new ArrayList<Integer>(5);
		}
		public void add(int port, ChatGui gui){//same as previous version
			GUIPorts.add(port);
			clients.add(gui);
		}
		public void remove(int port) {
			clients.remove(GUIPorts.indexOf(port));
		}
		public boolean contains(int port) {
			return GUIPorts.contains(port);
			
		}
		public ChatGui getClient(int WithPort) {
			return clients.get(GUIPorts.indexOf(WithPort)) ;
			
		}
}