package main;

import model.DB;
import model.Song;
import view.Log;
import view.View;

public class Main {

	public static void main(String[] args) {
		
		Log.p("Start songs transfer");
		
		String defaultFilename = "songs.txt";
		String fileName = defaultFilename;
		if (args.length == 1) {
			fileName = args[0];
		}
		if (!fileName.endsWith(".txt")) {
			Log.e("file name not in type .txt: " + fileName);
			return;
		}
		DB.getDB().readFile(fileName);
		
		if (DB.getDB().getTitles().length == 0) {
			Log.e("No song found!");
			return;
		}
		Log.p(DB.getDB().getTitles().length + " song(s) found!");
		
		String folder = "output_" + defaultFilename.replaceAll(".txt", "");
		if (!View.createFolderIfNotExists(folder)) {
			return;
		}
		
		for (String title : DB.getDB().getTitles()) {
			Song s = DB.getDB().getSong(title);
			String content = s.getExportFileContent();
			Log.p(s.toString());
			View.writeFile(folder + "/" + title + ".txt", content);
		}

		Log.p("End of transfer");

	}
	
}
