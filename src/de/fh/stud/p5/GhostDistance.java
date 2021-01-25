package de.fh.stud.p5;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p1.Position;
import de.fh.stud.p3.Suche.A_STAR;
import de.fh.stud.p3.Suche.Node;
import lombok.RequiredArgsConstructor;

public class GhostDistance {

	private final PacmanTileType[][] w;
	private final List<Position> ghosts = new ArrayList<Position>(2);

	public GhostDistance(PacmanTileType[][] w) {
		this.w = w;
		for (int x = 0; x < w.length; x++) {
			for (int y = 0; y < w[x].length; y++) {
				PacmanTileType t = w[x][y];
				if (t == PacmanTileType.GHOST || t == PacmanTileType.GHOST_AND_DOT) {
					ghosts.add(new Position(x, y));
				}

			}
		}

	}

	private int manhattanMin(Position p) {
		OptionalInt optMin = ghosts.stream().mapToInt(g -> Math.abs(g.x - p.x) + Math.abs(g.y - p.y)).min();
		return optMin.orElse(99999);
	}

	public int at(Position position, int maxDepth) {
		ToIntFunction<Knoten> cost = (knoten) -> {
			return knoten.distance;
		};

		ToIntFunction<Knoten> heuristic = (knoten) -> {
			return manhattanMin(knoten.position);
		};

		Predicate<Knoten> goal = (knoten) -> {
			PacmanTileType t = de.fh.stud.p1.WorldHelper.getTileType(w, knoten.position);
			return t == PacmanTileType.GHOST || t == PacmanTileType.GHOST_AND_DOT;
		};

		Knoten initialKnoten = new Knoten(position, 0, maxDepth);

		Knoten goalKnoten = new A_STAR<Knoten>(cost, heuristic).closedListSerach(initialKnoten, goal);
		return goalKnoten != null ? goalKnoten.distance : 9999;
	}

	@RequiredArgsConstructor
	class Knoten implements Node {

		private final Position position;
		private final int distance;
		private final int maxDepth;

		@Override
		public Stream<? extends Node> expand() {
			if (distance > maxDepth) {
				return Stream.empty();
			}
			return Stream.of(WorldHelper.DIRS).map(a -> position.mutate(a))
					.filter(x -> de.fh.stud.p1.WorldHelper.isInBounds(w, x))
					.filter(x -> de.fh.stud.p1.WorldHelper.getTileType(w, x) != PacmanTileType.WALL)
					.map(x -> new Knoten(x, distance + 1, maxDepth));
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + ((position == null) ? 0 : position.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Knoten other = (Knoten) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			if (position == null) {
				if (other.position != null)
					return false;
			} else if (!position.equals(other.position))
				return false;
			return true;
		}

		private GhostDistance getEnclosingInstance() {
			return GhostDistance.this;
		}

	}

}
