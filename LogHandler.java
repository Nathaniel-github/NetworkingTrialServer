import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

public class LogHandler {
	
	private static FileWriter fileWriter;
	
	public static void write(String str) {

		try {
			if (fileWriter == null) {
				
				fileWriter = new FileWriter(new File("ServerLog.txt"), true);
				
			}
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(str);
			sb.append(" (");
			sb.append(LocalDateTime.now());
			sb.append(")\n");
		
			fileWriter.write(sb.toString());
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void writeClient(String str, Socket client) {
		
		try {
			if (fileWriter == null) {
				
				fileWriter = new FileWriter(new File("ServerLog.txt"), true);
				
			}
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(client.getInetAddress());
			sb.append(": ");
			sb.append(str);
			sb.append(" (");
			sb.append(LocalDateTime.now());
			sb.append(")\n");
		
			fileWriter.write(sb.toString());
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
