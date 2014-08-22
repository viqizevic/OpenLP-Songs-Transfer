package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import view.Log;
import view.View;

public class DB {
	
	private static DB db = new DB();

	private HashMap<String, Song> songs;
	
	public DB() {
		songs = new HashMap<String, Song>();
	}
	
	public static DB getDB() {
		return db;
	}

	public void readFile(String fileName) {
		try {
			// create a new file, which is a clean version
			String cleanFileName = fileName.replaceAll(".txt", "_clean.txt");
			createCleanVersionFile(fileName, cleanFileName);
			checkInputFile(cleanFileName);
			
			BufferedReader br = new BufferedReader(new FileReader(cleanFileName));
			String line, preLine, postLine;
			
			// a variable to save a new song
			Song song = null;
			
			// a boolean to check if the titles are numbered 1, 2, 3, ...
			boolean titlesAreNumbered = false;
			int songNr = 0;
			
			boolean titleIsInLine = false;
			boolean authorInfoIsInLine = false;
			
			// a variable to save a paragraph
			String paragraph = null;
			// a boolean which tell if the actual line belongs to paragraph
			boolean inParagraph = false;
			
			line = br.readLine();
			preLine = "";
			
			while (line != null) {
				line = line.trim();
				postLine = br.readLine(); // the next line
				
				titleIsInLine = false;
				authorInfoIsInLine = false;
				boolean emptyPreLine = false, emptyPostLine = false, emptyLine = false;
				if (postLine == null || postLine.trim().equals("")) {
					emptyPostLine = true;
				}
				if (preLine.trim().equals("")) {
					emptyPreLine = true;
				}
				if (line.trim().equals("")) {
					emptyLine = true;
				}
				
				// if line begins with number and . and whitespace (e.g. "7. Title")
				if (line.matches("^\\d+\\.\\s.*")) {
					if (line.startsWith("1.") && db.isEmpty()) { // if the first one found is "1. ___"
						titlesAreNumbered = true; // then we will look for the title by checking these numbers
						song = new Song(line.replaceAll("\\d+\\.", "").trim());
						db.addNewSong(song);
						titleIsInLine = true;
					} else {
						if (titlesAreNumbered && line.startsWith((songNr+1) + ".")) { // the next title
							song = new Song(line.replaceAll("\\d+\\.", "").trim());
							db.addNewSong(song);
							titleIsInLine = true;
						}
					}
				}
				
				if (line.toLowerCase().startsWith("by ")) {
					String[] arr = line.split("\\s+");
					// if the word after "by" start with an upper case letter
					if (!arr[1].equals(arr[1].toLowerCase())) {
						// set composer
						song.setComposer(line.substring(arr[0].length()).trim());
						authorInfoIsInLine = true;
					}
				}
				
				if (!titlesAreNumbered) {
					if ( emptyPreLine && emptyPostLine && !emptyLine) {
						song = new Song(line.trim());
						db.addNewSong(song);
						titleIsInLine = true;
					} else if (emptyPreLine && !emptyLine) {
						inParagraph = true;
						paragraph = "";
					} else if (!emptyLine && emptyPostLine) {
						inParagraph = false;
						paragraph += line + "\n";
						if (song == null) {
							// TODO: If no title found, use the first line of the first vers
							Log.e("Unable to add new part, since no title was found!");
						} else {
							song.addNewPart(paragraph);
						}
					}
				} else {
					if (emptyPreLine && !emptyLine && !(titleIsInLine || authorInfoIsInLine)) {
						inParagraph = true;
						paragraph = "";
					} else if (!emptyLine && emptyPostLine && !(titleIsInLine || authorInfoIsInLine) && (paragraph != null)) {
						inParagraph = false;
						paragraph += line + "\n";
						if (song == null) {
							// TODO: If no title found, use the first line of the first vers
							Log.e("Unable to add new part, since no title was found!");
						} else {
							song.addNewPart(paragraph);
						}
					}
				}
				
				if (titleIsInLine) {
					paragraph = null;
					inParagraph = false;
					songNr++;
				}
				
				if (inParagraph && (paragraph != null)) {
					paragraph += line + "\n";
				}
				preLine = line;
				line = postLine;
			}
			br.close();
			View.deleteFile(cleanFileName);
			db.checkSongs();
		} catch (FileNotFoundException e) {
			Log.e("Cannot find file: " + fileName);
		} catch (IOException e) {
			Log.e("Error while reading the file: " + fileName);
		}
	}

	public void addNewSong(Song song) {
		if (songs.containsKey(song.getTitle())) {
			Log.e("Unable to add new song! "
					+ "Song with the title " + song.getTitle() + " already exists in database!");
			return;
		}
		songs.put(song.getTitle(), song);
	}
	
	public boolean isEmpty() {
		return songs.isEmpty();
	}
	
	public String[] getTitles() {
		String[] titles = new String[songs.size()];
		int i = 0;
		for (String k : songs.keySet()) {
			titles[i++] = k;
		}
		Arrays.sort(titles);
		return titles;
	}
	
	public Song getSong(String title) {
		return songs.get(title);
	}
	
	private void checkInputFile(String filename) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line, maxLine = "";
		// list of chars with code number > 255 we can ignore, use hash map for counting occurrences
		HashMap<Integer, Integer> otherChars = new HashMap<Integer, Integer>();
		while ((line = br.readLine()) != null) {
			// check chars in line
			for (int i=0; i<line.length(); i++) {
				int c = (int) line.charAt(i);
				if (c > 255) {
					if (!otherChars.keySet().contains(c)) {
						otherChars.put(c, 0);
					} else {
						otherChars.put(c, otherChars.get(c)+1);
					}
				}
			}
			// update maxLine
			if (line.length() > maxLine.length()) {
				maxLine = line;
			}
		}
		for (int i : otherChars.keySet()) {
			int x = otherChars.get(i);
			if (x > 0) {
				Log.p("Found char <" + (char) i + "> (#" + i + ") " + x + " time(s)");
			}
		}
		Log.p("Max number of characters in one line: " + maxLine.length());
		Log.p("Max line: " + maxLine);
		br.close();
	}
	
	private void createCleanVersionFile(String filename, String cleanFilename) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		String content = "";
		int c8232 = 0;
		int cChordLines = 0;
		int possibleChordLines = 0;
		while ((line = br.readLine()) != null) {
			// trim and replace two or more white spaces
			String cleanLine = line.replaceAll("\\s+", " ").trim();
			// check chars
			for (int i=0; i<line.length(); i++) {
				int c = (int) line.charAt(i);
				if (c > 255) {
					if (c == 8232) {
						// Replace this char with normal carriage return as line separator
						cleanLine = line.replaceAll(line.charAt(i)+"", "\n");
						c8232++;
					}
				}
			}
			// check if this is a line with chords
			boolean isChordLine = false;
			String notes = "^[CDEFGAB]";
			String accidentals = "[#b]?";
			String chords = "(m|maj|min|sus)?";
			String number = "[2479]?";
			String regex = notes + accidentals + chords + number;
			String[] arr = cleanLine.replace('/', ' ').replace('-', ' ').split("\\s+");
			int match = 0;
			for (String s : arr) {
				if (s.matches(regex)) {
					match++;
				}
			}
			if (match == arr.length) {
				isChordLine = true;
				cChordLines++;
			} else if (match > 1) {
				possibleChordLines++;
			}
			// do not add the line with chords
			if (!isChordLine) {
				content += cleanLine + "\n";
			}
		}
		br.close();
		if (c8232 > 0) {
			Log.p("Replace char #8232 " + c8232 + " time(s)");
		}
		if (cChordLines > 0) {
			Log.p("Remove chord line " + cChordLines + " time(s)");
		}
		if (possibleChordLines > 0) {
			Log.p("Possible chord line not removed found " + possibleChordLines + " time(s)");
		}
		View.writeFile(cleanFilename, content);
		Log.p("Finish creating clean version file: " + cleanFilename);
	}
	
	private void checkSongs() {
		int chAdded = 0;
		LinkedList<String> songTitles = new LinkedList<String>();
		for (String t : songs.keySet()) {
			songTitles.add(t);
		}
		for (String title : songTitles) {
			Song song = songs.get(title);
			if (song.getParts().keySet().size() == 0) {
				Log.e("Found a song with title only, no vers: " + title);
			}
			if (song.hasChorus()) {
				LinkedList<String> s1 = song.getPartsOrder();
				addChorusInBetweenIfNeeded(song);
				LinkedList<String> s2 = song.getPartsOrder();
				if (s1.size() != s2.size()) {
					chAdded++;
				}
			}
			String newTitle = cleanVersionTitle(title);
			if (!newTitle.equals(title)) {
				song.setTitle(newTitle);
				songs.remove(title);
				songs.put(newTitle, song);
			}
		}
		if (chAdded != 0) {
			Log.p("Extra chorus added " + chAdded + " time(s)");
		}
	}
	
	private String cleanVersionTitle(String title) {
		String newTitle = title;
		String t = newTitle.toLowerCase();
		if (t.charAt(0) == newTitle.charAt(0)) {
			Log.e("Please check if this really a title: " + newTitle
					+ ". If it is, please write the first letter in capital case.");
		}
		if (newTitle.contains("=")) {
			newTitle = newTitle.replaceAll("\\s=\\s", "="); // replace "C = 1" to "C=1", this way it will be recognize as one word;
			String[] arr = newTitle.split("\\s");
			String sequence = "";
			for (String s : arr) {
				if (s.contains("=")) {
					sequence = s;
				}
			}
			newTitle = newTitle.replaceAll(sequence, "");
			Log.p("Remove " + sequence + " from title: " + title + " => " + newTitle);
		}
		if (newTitle.matches(".*\\d\\s*+\\s*/\\d+.*")) {
			newTitle = newTitle.replaceAll("\\d\\s*+\\s*/\\d+", "");
			Log.p("Remove N/N from title: " + title + " => " + newTitle);
		}
		if (newTitle.contains("/")) {
			newTitle = newTitle.replace('/', '&');
			Log.p("Replace char / with & in title: " + title + " => " + newTitle);
		}
		return newTitle.trim();
	}

	private void addChorusInBetweenIfNeeded(Song song) {
		if (!song.hasChorus()) {
			return;
		}
		LinkedList<String> order = song.getPartsOrder();
		String v = Song.VERS;
		String c = Song.CHORUS + " ";
		LinkedList<String> newOrder = new LinkedList<String>();
		// determine the number of verses before a chorus
		int nrOfVbeforeC = 0;
		for (String s : order) {
			if (s.contains(v)) {
				nrOfVbeforeC++;
			} else if (s.contains(c)) {
				break;
			}
		}
		if (nrOfVbeforeC == 0) {
			return;
		}
		int i = 0;
		int currC = 0;
		Object[] arr = order.toArray();
		for (int k=0; k<arr.length; k++) {
			String s = (String) arr[k];
			// if this is not chorus and number of verses before is reached, then add chorus
			if (i == nrOfVbeforeC && !s.contains(c)) {
				newOrder.add(c+currC);
				i = 0;
			}
			if (s.contains(v)) {
				newOrder.add(s);
				i++;
				// if this is the last verse and number of verses before is reached, then add chorus
				if (k == arr.length-1 && i == nrOfVbeforeC) {
					newOrder.add(c+currC);
				}
			} else if (s.contains(c)) {
				newOrder.add(s);
				i = 0;
				currC++;
			}
		}
		if (newOrder.size() == 0) {
			newOrder = order;
		}
		song.setPartsOrder(newOrder);
	}

}
