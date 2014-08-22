package main;

import java.io.File;

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
		} else {
			fileName = View.startFileChooser();
			if (fileName == null) {
				Log.e("Cancel choosing input file");
				endOfMain();
				return;
			}
		}
		if (!fileName.endsWith(".txt")) {
			Log.e("File name not in type .txt: " + fileName);
			endOfMain();
			return;
		}
		DB.getDB().readFile(fileName);
		
		if (DB.getDB().getTitles().length == 0) {
			Log.e("No song found!");
			endOfMain();
			return;
		}
		Log.p(DB.getDB().getTitles().length + " song(s) found!");
		
		String folder = "output_" + new File(fileName).getName().replaceAll(".txt", "");
		if (!View.createFolderIfNotExists(folder)) {
			endOfMain();
			return;
		}
		
		for (String title : DB.getDB().getTitles()) {
			Song s = DB.getDB().getSong(title);
			String content = s.getExportFileContent();
//			Log.p(s.toString());
			View.writeFile(folder + "/" + title + ".txt", content);
		}

		Log.p("End of transfer");
		endOfMain();
	}
	
	private static void endOfMain() {
		Log.writeLogFile();
	}
	
}
