package model;

import java.util.HashMap;
import java.util.LinkedList;

import view.Log;

public class Song {
	
	private String title;
	
	private HashMap<String, String> parts;
	
	private LinkedList<String> partsOrder;

	private boolean hasChorus;
	
	private int totalVersNumber;
	
	private int totalChorusNumber;

	private String ccliNumber;
	
	private String composer;
	
	private String copyright;
	
	private String comment;
	
	private String ccliLicenseNumber;
	
	public Song(String title) {
		this.title = title;
		parts = new HashMap<String, String>();
		partsOrder = new LinkedList<String>();
		hasChorus = false;
		totalVersNumber = 0;
		totalChorusNumber = 0;
		ccliNumber = "CCLI-Liednummer NNN";
		ccliLicenseNumber = "2175389";
		comment = "www.ccli.de";
	}
	
	public void addPart(String partName, String text) {
		if (parts.containsKey(partName)) {
			Log.e("Please choose other part name, not: " + partName);
			return;
		}
		parts.put(partName, text);
	}
	
	public void addNewPart(String text) {
		boolean isChorus = false;
		boolean needChorusAsNext = false;
		if (text.toLowerCase().contains("ref") || text.toLowerCase().contains("chor")) {
			if (text.toLowerCase().startsWith("ref") || text.toLowerCase().startsWith("chor")) {
				// This is a refrain
				isChorus = true;
				hasChorus = true;
				// Remove the string refer to refrain or chorus
				String[] arr = text.split("\n")[0].split(" ");
				text = text.replaceAll(arr[0], "").trim();
			} else {
				// Not a refrain, just show that a refrain should follow
				needChorusAsNext = true;
				String[] arr = text.split("\n");
				arr = arr[arr.length-1].split("\\s");
				String s = arr[arr.length-1];
				if (s.toLowerCase().contains("ref") || s.toLowerCase().contains("chor")) {
					text = text.replaceAll(s, "").trim();
				}
			}
		}
		// Remove last carriage return if exists
		if (text.charAt(text.length()-1) == '\n') {
			text = text.substring(0, text.length()-1);
		}
		String name = "Vers ";
		int i = totalVersNumber+1;
		if (!isChorus) {
			totalVersNumber++;
		} else {
			name = "Chorus ";
			i = totalChorusNumber+1;
			totalChorusNumber++;
		}
		String partName = name+i;
		addPart(partName, text);
		partsOrder.add(partName);
		if (needChorusAsNext) {
			name = "Chorus ";
			i = totalChorusNumber;
			partName = name+i;
			partsOrder.add(partName);
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public String getComposer() {
		return composer;
	}

	public void setComposer(String composer) {
		this.composer = composer;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public boolean hasChorus() {
		return hasChorus;
	}

	public HashMap<String, String> getParts() {
		return parts;
	}
	
	public LinkedList<String> getPartsOrder() {
		return partsOrder;
	}

	public void setPartsOrder(LinkedList<String> partsOrder) {
		this.partsOrder = partsOrder;
	}

	public String getExportFileContent() {
		String s = title + "\n\n\n";
		for (String k : partsOrder) {
			String text = parts.get(k);
			String[] arr = k.split(" ");
			if (arr[0].equals("Vers")) {
				text += " (" + arr[1] + "/" + totalVersNumber + ")";
			}
			s += k + "\n" + text + "\n\n";
		}
		s += ccliNumber + "\n";
//		if (composer != null && !composer.equals("")) {
//			s += composer + "\n";
//		}
//		s += copyright + "\n";
		s += ccliLicenseNumber + "\n";
		s += comment + "\n";
		return s;
	}
	
	public String toString() {
		String s = title + "\n";
		for (String k : partsOrder) {
			s += k + " ";
		}
		s += "\n";
		if (composer != null && !composer.equals("")) {
			s += composer + "\n";
		}
		return s;
	}

}
