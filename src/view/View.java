package view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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

	public static String startFileChooser() {
		String fileName = null;
		JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.doc, *.txt", "doc", "txt");
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fileName = fc.getSelectedFile().getAbsolutePath();
		}
		return fileName;
	}
}
