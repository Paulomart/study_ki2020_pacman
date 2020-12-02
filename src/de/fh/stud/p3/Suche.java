package de.fh.stud.p3;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p1.Knoten;
import de.fh.stud.p1.Position;
import de.fh.stud.p1.WorldHelper;

public class Suche {

	private final Knoten initialKnoten;
	private final ToIntFunction<Knoten> dotsLeft;
	private final ToIntFunction<Knoten> costToReach;

	public Suche(PacmanTileType[][] view, Position position) {
		this.initialKnoten = new Knoten(view, position);

		int maxDots = WorldHelper.count(initialKnoten.getWorld(),
				Arrays.asList(PacmanTileType.DOT, PacmanTileType.GHOST_AND_DOT));
		this.dotsLeft = (x) -> maxDots - x.getDotsEaten();

		this.costToReach = Knoten::getCostToReach;

	}

	public boolean noPointsLeft(Knoten knoten) {
		final int possiblePoints = WorldHelper.count(knoten.getWorld(),
				Arrays.asList(PacmanTileType.DOT, PacmanTileType.GHOST_AND_DOT));

		return possiblePoints == 0;
	}

	public Knoten breitensuche() {
		return closedListSerach(this.initialKnoten, this::noPointsLeft, Suche::BREADTH_FIRST);
	}

	public Knoten tiefensuche() {
		return closedListSerach(this.initialKnoten, this::noPointsLeft, Suche::DEPTH_FIRST);
	}

	public Knoten kostensuche() {
		return closedListSerach(this.initialKnoten, this::noPointsLeft, Suche.make_UNIFORM_COST(this.costToReach));
	}

	public Knoten greedysuche() {
		return closedListSerach(this.initialKnoten, this::noPointsLeft, Suche.make_GREEDY(this.dotsLeft));
	}

	public Knoten aStern() {
		return closedListSerach(this.initialKnoten, this::noPointsLeft,
				Suche.make_A_STAR(this.costToReach, this.dotsLeft));
	}

	public static <T extends Node> LinkedList<T> BREADTH_FIRST(Stream<T> nodes, /* LinkedList */ Queue<T> openList) {
		if (openList == null) {
			return new LinkedList<T>(nodes.collect(Collectors.toList()));
		}

		LinkedList<T> list = (LinkedList<T>) openList;
		nodes.forEach(x -> list.add(x));

		return list;
	}

	public static <T extends Node> LinkedList<T> DEPTH_FIRST(Stream<T> nodes, /* LinkedList */ Queue<T> openList) {
		if (openList == null) {
			return new LinkedList<T>(nodes.collect(Collectors.toList()));
		}

		LinkedList<T> list = (LinkedList<T>) openList;
		nodes.forEach(x -> list.add(0, x));

		return list;
	}

	public static <T extends Node> BiFunction<Stream<T>, Queue<T>, Queue<T>> make_UNIFORM_COST(
			ToIntFunction<T> costFunction) {
		return (Stream<T> nodes, Queue<T> openList) -> {
			if (openList == null) {
				PriorityQueue<T> queue = new PriorityQueue<T>((a, b) -> {
					return costFunction.applyAsInt(a) - costFunction.applyAsInt(b);
				});

				queue.addAll(nodes.collect(Collectors.toList()));
				return queue;
			}

			PriorityQueue<T> queue = (PriorityQueue<T>) openList;
			nodes.forEach(x -> queue.add(x));

			return queue;
		};
	}

	public static <T extends Node> BiFunction<Stream<T>, Queue<T>, Queue<T>> make_GREEDY(ToIntFunction<T> heuristic) {
		return (Stream<T> nodes, Queue<T> openList) -> {
			if (openList == null) {
				PriorityQueue<T> queue = new PriorityQueue<T>((a, b) -> {
					return heuristic.applyAsInt(a) - heuristic.applyAsInt(b);
				});

				queue.addAll(nodes.collect(Collectors.toList()));
				return queue;
			}

			PriorityQueue<T> queue = (PriorityQueue<T>) openList;
			nodes.forEach(x -> queue.add(x));

			return queue;
		};
	}

	public static <T extends Node> BiFunction<Stream<T>, Queue<T>, Queue<T>> make_A_STAR(ToIntFunction<T> costFunction,
			ToIntFunction<T> heuristic) {
		return (Stream<T> nodes, Queue<T> openList) -> {
			if (openList == null) {
				PriorityQueue<T> queue = new PriorityQueue<T>((a, b) -> {
					int projectedCostA = costFunction.applyAsInt(a) + heuristic.applyAsInt(a);
					int projectedCostB = costFunction.applyAsInt(b) + heuristic.applyAsInt(b);

					return projectedCostA - projectedCostB;
				});

				queue.addAll(nodes.collect(Collectors.toList()));
				return queue;
			}

			PriorityQueue<T> queue = (PriorityQueue<T>) openList;
			nodes.forEach(x -> queue.add(x));

			return queue;
		};
	}

	public static interface Node {

		Stream<? extends Node> expand();

	}

	@SuppressWarnings("unchecked")
	public static <T extends Node, OpenListType extends Queue<T>> T closedListSerach(T initialNode,
			Predicate<T> goalTest, BiFunction<Stream<T>, OpenListType, OpenListType> insert) {

		long start = System.currentTimeMillis();
		long lastMessage = System.currentTimeMillis();

		System.out.println("    time     #open   #closed   used mb   mem%");

		Set<T> closedList = new HashSet<T>(10_000_000, 0.9F);
		OpenListType openList = null;

		openList = insert.apply(Stream.of(initialNode), openList);

		BiConsumer<OpenListType, Set<T>> printStatus = (pOpenList, pClosedList) -> {
			Runtime rt = Runtime.getRuntime();

			long maxMB = rt.maxMemory() / 1024 / 1024;
			long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
			long percent = (long) (((double) usedMB / (double) maxMB) * 100D);

			String log = String.format("%7ds   %7d   %7d   %7d   %3d%%", (System.currentTimeMillis() - start) / 1000,
					pOpenList.size(), pClosedList.size(), usedMB, percent);
			System.out.println(log);
		};
		
		while (!openList.isEmpty()) {

			long diff = System.currentTimeMillis() - lastMessage;
			if (diff >= 1000) {
				printStatus.accept(openList, closedList);
				lastMessage = System.currentTimeMillis();
			}

			T node = openList.remove();

			if (goalTest.test(node)) {
				printStatus.accept(openList, closedList);
				System.out.println("Fininshed: "  + openList.size() + "\t" + closedList.size() + "\t" + ((Knoten) node).getResultedByActions().size());
				return node;
			}

			boolean nodeWasSeenBefore = !closedList.add(node);
			if (nodeWasSeenBefore) {
				continue;
			}

			insert.apply((Stream<T>) node.expand().filter(x -> !closedList.contains(x)), openList);
		}

		// return failure
		return null;
	}

}
