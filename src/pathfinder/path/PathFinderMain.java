package pathfinder.path;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class PathFinderMain {
	private static String toFind;
	private static ArrayList<File> found_matches = new ArrayList<File>();
	private static ArrayList<Integer> invoke_or_not_not_in = new ArrayList<Integer>();
	private static int running_recursives = 0;
	private static int started_threads;

	public static void main(String[] args) {
		toFind = args.length > 0 ? args[0] : JOptionPane
				.showInputDialog("What program should I find?");
		String[] split = System.getenv("PATH").split(
				System.getProperty("path.seperator", ";"));
		recur_path(split, false, false);
		try {
			Thread.sleep(100);
		} catch (InterruptedException ie) {
		}
		while (running_recursives > 0) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
			}
		}
		if (found_matches.size() < 1) {
			System.err.println("Didn't find any matches for " + toFind);
			boolean search_all = JOptionPane.showConfirmDialog(null,
					"Search the entire computer?") == JOptionPane.YES_OPTION;
			if (search_all) {
				recur_path(new File[] { new File("/") }, false, false);
				try {
					Thread.sleep(100);
				} catch (InterruptedException ie) {
				}
				while (running_recursives > 0) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException ie) {
					}
				}
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
		System.err.println("The program started " + started_threads
				+ " in total.");
	}

	private static void recur_path(final String[] split,
			final boolean invnotin, final boolean not_in) {
		Runnable r = new Runnable() {
			int our_recur;
			int we_started = 0;

			public void run() {
				running_recursives++;
				our_recur = started_threads;

				//System.err.println("Started running recursive #" + our_recur);

				for (String path : split) {
					File path_f = new File(path).getAbsoluteFile();
					if (path_f.isDirectory() && path_f.listFiles() != null) {
						// System.err.println("Searching " +
						// path_f.getAbsolutePath());
						we_started++;
						recur_path(path_f.listFiles(), not_in, true);
					} else {
						// System.err.println("Testing " +
						// path_f.getAbsolutePath());
						if (path_f.getPath().endsWith(toFind)) {
							found_matches.add(path_f);
							invoke_or_not_not_in.add((invnotin ? 1 : 0)
									| (not_in ? 2 : 0));
						}
					}
				}

				//System.err.println("Finished running recursive #" + our_recur);

				if (running_recursives == 0) {
					System.err
							.println("WARNING: This runnable would make running_recursives -1!");
				} else {
					running_recursives--;
				}
				System.err.println("#" + our_recur + " started " + we_started
						+ " recursive calls");
			}
		};
		started_threads++;
		new Thread(r).start();
	}

	private static void recur_path(File[] listFiles, boolean invnotin,
			boolean not_in) {
		String[] invoke_with = new String[listFiles.length];
		int i = 0;
		for (File f : listFiles) {
			invoke_with[i++] = f.getAbsolutePath();
		}
		recur_path(invoke_with, invnotin, not_in);
	}
}
