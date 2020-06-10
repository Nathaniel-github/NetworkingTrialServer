import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private ServerSocket server;
	private int count = 0;
	
	public Server(int port) throws IOException {
		
		server = new ServerSocket(port);
		System.out.println("Server up");
		
		while (true) {
			Socket client = server.accept();
			LogHandler.write("Client connected! Client: " + client.getInetAddress());
			count ++;
			
			new Thread(new Runnable() {

				Socket socket = client;
				
				@Override
				public void run() {
					
					try {
						new ClientHandler(socket, count);
					} catch (IOException e) {
						LogHandler.write("Client exited, Client: " + socket.getInetAddress());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				}
				
			}).start();
			
		}
		
	}
	
	public static void main(String[] args) {
		
		try {
			new Server(25565);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
