package de.fh.stud.p3;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p1.Knoten;
import de.fh.stud.p1.Position;
import de.fh.stud.p1.WorldHelper;

public class Suche {

	private final Knoten initialKnoten;

	public Suche(PacmanTileType[][] view, Position position) {
		this.initialKnoten = new Knoten(view, position);
	}

	public boolean isFinished(Knoten knoten) {
		final int possiblePoints = WorldHelper.count(knoten.getWorld(),
				Arrays.asList(PacmanTileType.DOT, PacmanTileType.GHOST_AND_DOT));

		return possiblePoints == 0;
	}

	public Knoten breitensuche() {
		return closedListSerach(this.initialKnoten, this::isFinished, Suche::BREADTH_FIRST);
	}

	public Knoten tiefensuche() {
		return closedListSerach(this.initialKnoten, this::isFinished, Suche::DEPTH_FIRST);
	}

	public static <T extends Node> void BREADTH_FIRST(T node, List<T> openList) {
		openList.add(node);
	}

	public static <T extends Node> void DEPTH_FIRST(T node, List<T> openList) {
		openList.add(0, node);
	}

	public static interface Node {

		Stream<? extends Node> expand();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Node> T closedListSerach(T initialNode, Predicate<T> goalTest,
			BiConsumer<T, List<T>> insert) {

		long start = System.currentTimeMillis();
		long lastMessage = System.currentTimeMillis();

		Set<T> closedList = new HashSet<T>();
		LinkedList<T> openList = new LinkedList<>();

		insert.accept(initialNode, openList);

		while (!openList.isEmpty()) {

			long diff = System.currentTimeMillis() - lastMessage;
			if (diff >= 1000) {
				System.out.println(
						(System.currentTimeMillis() - start) + " ms \t " + openList.size() + " \t " + closedList.size());
				lastMessage = System.currentTimeMillis();
			}

			T node = openList.remove();

			if (goalTest.test(node)) {
				System.out.println("Finished " + (System.currentTimeMillis() - start) + " ms \t " + openList.size()
						+ " \t " + closedList.size());
				return node;
			}

			boolean nodeWasSeenBefore = !closedList.add(node);
			if (nodeWasSeenBefore) {
				continue;
			}

			node.expand().filter(x -> !closedList.contains(x)).forEach((child) -> insert.accept((T) child, openList));
		}

		// return failure
		return null;
	}

}
