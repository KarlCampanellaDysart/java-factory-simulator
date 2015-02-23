import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerThread extends Thread{
	ServerSocket ss = null;
	FactoryGUI fg;
	ArrayList<UpdateRequestsThreadPanel> allRequests = new ArrayList<UpdateRequestsThreadPanel>();
	int id=0;
	
	public ServerThread(FactoryGUI fg) {
		this.fg = fg;
	}
	public void run(){
		try {
			ServerSocket ss = new ServerSocket(5001);
			while(true){
				Socket s = ss.accept();
				UpdateRequestsThreadPanel utp = new UpdateRequestsThreadPanel(s, fg, this, id++);
				allRequests.add(utp);
				new Thread(utp).start();
			}
		} 
		catch (IOException e) {}
	}
}
