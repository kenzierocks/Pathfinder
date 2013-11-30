package pathfinder.path.print;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class PathFinderNonThreaded {

	private static String toFind;
	private static boolean not_in_path;
	private static ArrayList<File> found_matches = new ArrayList<File>();
	private static ArrayList<Integer> invoke_or_not_not_in = new ArrayList<Integer>();
	private static boolean inovke_not_in;

	public static void main(String[] args) {
		toFind = args.length > 0 ? args[0] : JOptionPane
				.showInputDialog("What program should I find?");
		toFind = toFind.toLowerCase();
		String[] split = System.getenv("PATH").split(
				System.getProperty("path.seperator", ";"));
		recur_path(split);
		if (found_matches.size() < 1) {
			System.err.println("Didn't find any matches for " + toFind);
			boolean search_all = JOptionPane.showConfirmDialog(null,
					"Search the entire computer?") == JOptionPane.YES_OPTION;
			if (search_all) {
				not_in_path = true;
				recur_path(new File[] { new File("/") });
				if (found_matches.size() < 1) {
					System.err.println("Didn't find any matches for " + toFind);
				} else {
					for (File match : found_matches) {
						System.err
								.println("This program can be found at "
										+ match.getParentFile()
												.getAbsolutePath()
										+ (invoke_or_not_not_in
												.get(found_matches
														.indexOf(match)) == (1 | 2) ? ", but it is not in your path. Add the folder to it!"
												: "."));
					}
				}
			}
		} else {
			for (File match : found_matches) {
				System.err
						.println("This program can be found at "
								+ match.getParentFile().getAbsolutePath()
								+ (invoke_or_not_not_in.get(found_matches
										.indexOf(match)) == (1 | 2) ? ", but it is not in your path. Add the folder to it!"
										: "."));
			}
		}
	}

	private static void recur_path(String[] split) {
		for (String path : split) {
			File path_f = new File(path).getAbsoluteFile();
			if (path_f.isDirectory() && path_f.listFiles() != null) {
				System.err.println("Searching " + path_f.getAbsolutePath());
				inovke_not_in = not_in_path;
				not_in_path = true;
				recur_path(path_f.listFiles());
				not_in_path = false;
			} else {
				System.err.println("Testing " + path_f.getAbsolutePath());
				if (path_f.getPath().toLowerCase().endsWith(toFind)) {
					found_matches.add(path_f);
					System.err.println("It appears " + path_f.getParent()
							+ " is" + (inovke_not_in ? "not" : "")
							+ " on the path.");
					invoke_or_not_not_in.add((inovke_not_in ? 1 : 0)
							| (not_in_path ? 2 : 0));
				}
			}
		}
	}

	private static void recur_path(File[] listFiles) {
		String[] invoke_with = new String[listFiles.length];
		int i = 0;
		for (File f : listFiles) {
			invoke_with[i++] = f.getAbsolutePath();
		}
		recur_path(invoke_with);
	}
}
