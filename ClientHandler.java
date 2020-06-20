import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.Timer;

public class ClientHandler {
	
	private ObjectOutputStream objectOut = null;
	private DataOutputStream dataOut = null;
	private DataInputStream dataIn = null;
	private Socket client = null;
	private Player player;
	private final int MOVELENGTH = 5;
	private LinkedList<Player> sendQueue = new LinkedList<Player>();
	
	private Timer leftMove = new Timer(1000/60, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (player != null) {
				player.shiftX(-MOVELENGTH);
				sendQueue.add(player.clone());
				StringBuilder sb = new StringBuilder();
				sb.append("Moving left: ");
				sb.append(player);
				LogHandler.writeClient(sb.toString(), client);
				DataHandler.notifyWait();
			}
			
		}
		
	});
	
	private Timer rightMove = new Timer(1000/60, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (player != null) {
				player.shiftX(MOVELENGTH);
				sendQueue.add(player.clone());
				StringBuilder sb = new StringBuilder();
				sb.append("Moving right: ");
				sb.append(player);
				LogHandler.writeClient(sb.toString(), client);
				DataHandler.notifyWait();
			}
			
		}
		
	});
	
	private Timer upMove = new Timer(1000/60, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (player != null) {
				player.shiftY(-MOVELENGTH);
				sendQueue.add(player.clone());
				StringBuilder sb = new StringBuilder();
				sb.append("Moving up: ");
				sb.append(player);
				LogHandler.writeClient(sb.toString(), client);
				DataHandler.notifyWait();
			}
			
		}
		
	});
	
	private Timer downMove = new Timer(1000/60, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (player != null) {
				player.shiftY(MOVELENGTH);
				sendQueue.add(player.clone());
				StringBuilder sb = new StringBuilder();
				sb.append("Moving down: ");
				sb.append(player);
				LogHandler.writeClient(sb.toString(), client);
				DataHandler.notifyWait();
			}
			
		}
		
	});

	
	private Thread update = new Thread(new Runnable() {

		@Override
		public void run() {
			
			while(true) {
				try {
					
					if (sendQueue.isEmpty()) {
						
						synchronized(DataHandler.syncObj) {
							
							try {
								DataHandler.syncObj.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
						}
						
						updateData();
						
						sendData();
						
					} else {
						
						updateData();
						
						sendData();
						
					}
					
				} catch (Exception e1) {
					DataHandler.removePlayer(player);
					closeAll();
					LogHandler.writeClient("Client disconnected", client);
					break;
				}
			}
		}
		
	});
	
	public ClientHandler(Socket client, int pNum) throws IOException, ClassNotFoundException {
		
		dataOut = new DataOutputStream(client.getOutputStream());
		objectOut = new ObjectOutputStream(client.getOutputStream());
		dataIn = new DataInputStream(client.getInputStream());
		
		this.client = client;
		
		player = new Player(pNum, ThreadLocalRandom.current().nextInt(0, 1400), ThreadLocalRandom.current().nextInt(0, 800));
		
		objectOut.writeObject(player.clone());
		
		DataHandler.addPlayer(player);
		
		update.start();
		
		try {
			while (true) {
				
				String data = dataIn.readUTF();
				StringBuilder sb = new StringBuilder();
				sb.append("Request received: ");
				sb.append(data);
				LogHandler.writeClient(sb.toString(), client);
				readData(data);
				DataHandler.notifyWait();
				
			}
		} catch(Exception e) {
			DataHandler.removePlayer(player);
			closeAll();
			LogHandler.writeClient("Player disconnected, Player: " + player, client);
		}
		
	}
	
	private void updateData() {
		
		if (twoTimersRunning()) {
			
			while (sendQueue.size() < 2) {
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		if (sendQueue.size() > 0) {
			try {
				DataHandler.updatePlayer(sendQueue.removeLast());
				sendQueue.clear();
			} catch (Exception e) {
				System.out.println(sendQueue.size());
				e.printStackTrace();
				sendQueue.clear();
			}
		}
		
	}
	
	private void readData(String data) {
		
		if (data.equalsIgnoreCase("up")) {
			
			upMove.start();

		} else if (data.equalsIgnoreCase("down")) {

			downMove.start();

		} else if (data.equalsIgnoreCase("right")) {

			rightMove.start();

		} else if (data.equalsIgnoreCase("left")) {

			leftMove.start();

		} else if (data.equalsIgnoreCase("up_released")) {

			upMove.stop();

		} else if (data.equalsIgnoreCase("down_released")) {

			downMove.stop();

		} else if (data.equalsIgnoreCase("right_released")) {

			rightMove.stop();

		} else if (data.equalsIgnoreCase("left_released")) {

			leftMove.stop();

		} else {

			player.setData(data);

		}

	}
	
	private boolean twoTimersRunning() {
		
		return ((leftMove.isRunning() && (upMove.isRunning() || downMove.isRunning())) || (rightMove.isRunning() && (upMove.isRunning() || downMove.isRunning())));
		
	}
	
	private void sendData() {
		
		try {
			
			dataOut.writeInt(DataHandler.getAllPlayers().size());
			
			for (int i : DataHandler.getAllPlayers().keySet()) {
				
				StringBuilder sb = new StringBuilder();
				sb.append("Sending X coordinate: ");
				sb.append(DataHandler.getPlayer(i).getX());
				LogHandler.writeClient(sb.toString(), client);
	
				dataOut.writeInt(DataHandler.getPlayer(i).getX());
	
				StringBuilder sb2 = new StringBuilder();
				sb2.append("Sending Y coordinate: ");
				sb2.append(DataHandler.getPlayer(i).getY());
				LogHandler.writeClient(sb2.toString(), client);
	
				dataOut.writeInt(DataHandler.getPlayer(i).getY());
				
			}
		
		} catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	private void closeAll() {
		
		if (!client.isClosed()) {
			try {
				dataIn.close();
				dataOut.close();
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
