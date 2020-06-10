
public class DataHandler {
	
	private static PlayerMap allPlayers = new PlayerMap();
	public static Object syncObj = new Object();
	
	public static void addPlayer(Player player) {
		
		allPlayers.put(player.getPNum(), player);
		notifyWait();
		
	}
	
	public static void removePlayer(Player player) {
		
		allPlayers.remove(player.getPNum());
		notifyWait();
		
	}
	
	public static void updatePlayer(Player player) {
		
		allPlayers.put(player.getPNum(), player);
		notifyWait();
		
	}
	
	public static Player getPlayer(int i) {
		
		return allPlayers.get(i);
		
	}
	
	public static void notifyWait() {
		
		synchronized(syncObj) {
			syncObj.notifyAll();
		}	
	}
	
	public static PlayerMap getAllPlayers() {
		
		return allPlayers;
		
	}

}
