package view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class View {

	public static void writeFile(String filename, String content) {
		try {
			FileWriter fw = new FileWriter(filename);
			fw.write(content);
			fw.flush();
			fw.close();
			Log.p("Write file: " + filename);
		} catch (IOException e) {
			Log.e("Unable to write file: " + filename);
		}
	}
	
	public static boolean deleteFile(String fileName) {
		boolean deleted = new File(fileName).delete();
		if (deleted) {
			Log.p("Delete file " + fileName);
		} else {
			Log.e("Unable to delete the file " + fileName);
		}
		return deleted;
	}
	
	public static boolean createFolderIfNotExists(String folderName) {
		File f = new File(folderName);
		if (f.exists()) {
			return true;
		}
		boolean created = f.mkdir();
		if (created) {
			Log.p("Created new folder: " + folderName);
		} else {
			Log.e("Unable to create new folder: " + folderName);
		}
		return created;
	}
}
