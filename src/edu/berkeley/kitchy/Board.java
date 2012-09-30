package edu.berkeley.kitchy;

import java.util.Vector;

/**
 * Representation of the cuts that have been made thus far
 * 
 * @author Akrolsmir
 * 
 */
public class Board {

	// A cut is an integer array of 4 points, x1, y1, x2 and y2
	public Vector<int[]> cuts = new Vector<int[]>();

	public void addCut(int[] cut) {
		cuts.add(cut);
	}

	public void addCut(int x1, int y1, int x2, int y2) {
		int[] result = { x1, y1, x2, y2 };
		cuts.add(result);
	}

	public Vector<int[]> getCuts() {
		return cuts;
	}

	public void resetCuts() {
		cuts = cuts = new Vector<int[]>();
	}

}
