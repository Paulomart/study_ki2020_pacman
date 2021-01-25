package de.fh.stud.p3;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
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
		return new BREADTH_FIRST<Knoten>().closedListSerach(initialKnoten, this::noPointsLeft);
	}

	public Knoten tiefensuche() {
		return new DEPTH_FIRST<Knoten>().closedListSerach(this.initialKnoten, this::noPointsLeft);
	}

	public Knoten kostensuche() {
		return new UNIFORM_COST<Knoten>(this.costToReach).closedListSerach(this.initialKnoten, this::noPointsLeft);
	}

	public Knoten greedysuche() {
		return new GREEDY<Knoten>(this.dotsLeft).closedListSerach(this.initialKnoten, this::noPointsLeft);
	}

	public Knoten aStern() {
		return new A_STAR<Knoten>(this.costToReach, this.dotsLeft).closedListSerach(this.initialKnoten,
				this::noPointsLeft);
	}

	public static class BREADTH_FIRST<TNode extends Node> extends ClosedListSerach<TNode, LinkedList<TNode>> {

		@Override
		public LinkedList<TNode> create(Stream<TNode> initialNodes) {
			return new LinkedList<>(initialNodes.collect(Collectors.toList()));
		}

		@Override
		public void insert(LinkedList<TNode> ref, Stream<TNode> nodes) {
			nodes.forEach(x -> ref.add(x));

		}

	}

	public class DEPTH_FIRST<TNode extends Node> extends ClosedListSerach<TNode, LinkedList<TNode>> {

		@Override
		public LinkedList<TNode> create(Stream<TNode> initialNodes) {
			return new LinkedList<>(initialNodes.collect(Collectors.toList()));
		}

		@Override
		public void insert(LinkedList<TNode> ref, Stream<TNode> nodes) {
			nodes.forEach(x -> ref.add(0, x));

		}

	}

	public class UNIFORM_COST<TNode extends Node> extends ClosedListSerach<TNode, Queue<TNode>> {

		private final ToIntFunction<TNode> costFunction;

		public UNIFORM_COST(ToIntFunction<TNode> costFunction) {
			this.costFunction = costFunction;
		}

		@Override
		protected Queue<TNode> create(Stream<TNode> initialNodes) {
			PriorityQueue<TNode> queue = new PriorityQueue<>((a, b) -> {
				return costFunction.applyAsInt(a) - costFunction.applyAsInt(b);
			});

			queue.addAll(initialNodes.collect(Collectors.toList()));
			return queue;
		}

		@Override
		protected void insert(Queue<TNode> ref, Stream<TNode> nodes) {
			nodes.forEach(x -> ref.add(x));
		}

	}

	public class GREEDY<TNode extends Node> extends ClosedListSerach<TNode, Queue<TNode>> {

		private final ToIntFunction<TNode> heuristic;

		public GREEDY(ToIntFunction<TNode> heuristic) {
			this.heuristic = heuristic;
		}

		@Override
		protected Queue<TNode> create(Stream<TNode> initialNodes) {
			PriorityQueue<TNode> queue = new PriorityQueue<>((a, b) -> {
				return heuristic.applyAsInt(a) - heuristic.applyAsInt(b);
			});

			queue.addAll(initialNodes.collect(Collectors.toList()));
			return queue;
		}

		@Override
		protected void insert(Queue<TNode> ref, Stream<TNode> nodes) {
			nodes.forEach(x -> ref.add(x));
		}

	}

	public static class A_STAR<TNode extends Node> extends ClosedListSerach<TNode, Queue<TNode>> {

		private final ToIntFunction<TNode> costFunction;
		private final ToIntFunction<TNode> heuristic;

		public A_STAR(ToIntFunction<TNode> costFunction, ToIntFunction<TNode> heuristic) {
			this.costFunction = costFunction;
			this.heuristic = heuristic;
		}

		@Override
		protected Queue<TNode> create(Stream<TNode> initialNodes) {
			PriorityQueue<TNode> queue = new PriorityQueue<>((a, b) -> {
				int projectedCostA = costFunction.applyAsInt(a) + heuristic.applyAsInt(a);
				int projectedCostB = costFunction.applyAsInt(b) + heuristic.applyAsInt(b);

				return projectedCostA - projectedCostB;
			});

			queue.addAll(initialNodes.collect(Collectors.toList()));
			return queue;
		}

		@Override
		protected void insert(Queue<TNode> ref, Stream<TNode> nodes) {
			nodes.forEach(x -> ref.add(x));
		}

	}

	public static interface Node {

		Stream<? extends Node> expand();

	}

}
