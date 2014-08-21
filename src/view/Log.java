package view;

public class Log {
	
	private static String logContent;
	
	public static void p(String message) {
		System.out.println(message);
		addToLogFile(message);
	}
	
	public static void e(String message) {
		System.err.println(message);
		addToLogFile(message);
	}
	
	private static void addToLogFile(String message) {
		if (logContent == null) {
			logContent = "";
		}
		logContent += message + "\n";
	}
	
	public static void writeLogFile() {
		String logFileName = "log.txt";
		View.writeFile(logFileName, logContent);
	}
	
}