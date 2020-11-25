package de.fh.stud.p1;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import de.fh.pacman.enums.PacmanTileType;

public class WorldHelper {

	public static boolean isInBounds(PacmanTileType[][] w, Position p) {
		return p.x >= 0 && p.x < w.length && p.y >= 0 && p.y < w[p.x].length;
	}

	public static PacmanTileType getTileType(PacmanTileType[][] w, Position p) {
		if (!isInBounds(w, p)) {
			throw new IllegalArgumentException("Tried to access tile out of bounds: " + p);
		}

		return w[p.x][p.y];
	}

	public static PacmanTileType[][] withTileType(PacmanTileType[][] w, Position p, PacmanTileType newType) {
		if (!isInBounds(w, p)) {
			throw new IllegalArgumentException("Tried to access tile out of bounds: " + p);
		}

		w = clone(w);
		w[p.x][p.y] = newType;

		return w;
	}

	public static void printWorld(PacmanTileType[][] w) {
		printWorld(w, null);
	}

	public static void printWorld(PacmanTileType[][] w, Position marker) {
		String[] rows = new String[Stream.of(w).mapToInt(x -> x.length).max().orElse(0)];
		Arrays.fill(rows, "");

		for (int y = 0; y < w.length; y++) {
			for (int x = 0; x < w[y].length; x++) {
				if (marker != null && marker.x == x && marker.y == y) {
					rows[x] += 'M';
				} else {
					rows[x] += getSymbol(w[y][x]);
				}
			}
		}

		for (String row : rows) {
			System.out.println(row);
		}
	}

	public static int count(PacmanTileType[][] w, Collection<PacmanTileType> types) {
		int count = 0;

		for (int y = 0; y < w.length; y++) {
			for (int x = 0; x < w[y].length; x++) {

				PacmanTileType t = w[y][x];
				if (types.contains(t)) {
					count++;
				}

			}
		}

		return count;
	}

	private static char getSymbol(PacmanTileType tile) {
		switch (tile) {
		case DOT:
			return '.';
		case EMPTY:
			return ' ';
		case GHOST:
			return 'G';
		case GHOST_AND_DOT:
			return 'Ö';
		case PACMAN:
			return 'C';
		case WALL:
			return '#';

		default:
			throw new IllegalArgumentException("Unexpected value: " + tile);
		}
	}

	// https://stackoverflow.com/a/15846160/8504342
	static <T> T[][] clone(T[][] source) {
		Class<? extends T[][]> type = (Class<? extends T[][]>) source.getClass();
		T[][] copy = (T[][]) Array.newInstance(type.getComponentType(), source.length);

		Class<? extends T[]> itemType = (Class<? extends T[]>) source[0].getClass();
		for (int i = 0; i < source.length; i++) {
			copy[i] = (T[]) Array.newInstance(itemType.getComponentType(), source[i].length);
			System.arraycopy(source[i], 0, copy[i], 0, source[i].length);
		}
		return copy;
	}
}
