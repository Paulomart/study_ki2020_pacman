package de.fh.stud.p1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p3.Suche.Node;

public class Knoten implements Node {

	private final PacmanTileType[][] world;
	private final Position position;
	// attributes related to path finding and optimizing the way.
	private final List<PacmanAction> resultedByActions;
	private final int dotsEaten;

	public Knoten(PacmanTileType[][] world, Position position) {
		this(world, position, Arrays.asList(), 0);
	}

	public Knoten(PacmanTileType[][] world, Position position, List<PacmanAction> resultedByActions, int dotsEaten) {
		this.world = world;
		this.position = position;
		this.resultedByActions = resultedByActions;
		this.dotsEaten = dotsEaten;
	}

	/**
	 * Returns the new node that would result when performing the given action.
	 * 
	 * @param action
	 * @return
	 */
	private Knoten mutate(PacmanAction action) {
		Position newPosition = this.position.mutate(action);

		// check if new position is still in bounds
		boolean isInBounds = WorldHelper.isInBounds(world, newPosition);
		if (!isInBounds) {
			return null;
		}

		PacmanTileType newTile = WorldHelper.getTileType(world, newPosition);
		if (newTile == PacmanTileType.WALL) {
			return null;
		}

		int newDotsEaten = this.dotsEaten;
		if (newTile == PacmanTileType.DOT || newTile == PacmanTileType.GHOST_AND_DOT) {
			newDotsEaten = this.dotsEaten + 1;
		}

		
		List<PacmanAction> newResultedByActions = new ArrayList<PacmanAction>(this.resultedByActions.size() + 1); /// new LinkedList<PacmanAction>(this.resultedByActions);
		newResultedByActions.addAll(resultedByActions);
		newResultedByActions.add(action);

		PacmanTileType[][] modifiedWorld = WorldHelper.withTileType(world, newPosition, PacmanTileType.EMPTY);

		return new Knoten(modifiedWorld, newPosition, Collections.unmodifiableList(newResultedByActions), newDotsEaten);
	}

	public Stream<Knoten> expand() {
		List<PacmanAction> actions = new ArrayList<>(Arrays.asList(PacmanAction.GO_EAST, PacmanAction.GO_NORTH,
				PacmanAction.GO_SOUTH, PacmanAction.GO_WEST));

		return actions.stream().map(x -> this.mutate(x)).filter(x -> x != this && x != null);
//				.collect(Collectors.toList());
	}

	public void print() {
		WorldHelper.printWorld(world, position);
	}

	public int getDotsEaten() {
		return dotsEaten;
	}

	public PacmanTileType[][] getWorld() {
		return world;
	}

	public List<PacmanAction> getResultedByActions() {
		return resultedByActions;
	}

	public int getCostToReach() {
		return this.getResultedByActions().size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + Arrays.deepHashCode(world);
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
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (!Arrays.deepEquals(world, other.world))
			return false;
		return true;
	}

}
