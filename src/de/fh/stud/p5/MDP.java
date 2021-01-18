package de.fh.stud.p5;

import java.util.ArrayList;
import java.util.List;

import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p1.Position;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class MDP {

	private final PacmanTileType[][] w;

	public MDP(PacmanTileType[][] w) {
		this.w = w;
	}

	public WorldField[][] compute() {
		WorldField[][] w = convertWorld(this.w);

		for (int i = 0; i < 1000; i++) {
			w = next(w);

		}

		WorldHelper.printWorld(w);

		return w;
	}

	public float rate(PacmanTileType tile) {
		switch (tile) {
		case DOT:
			return 1;
		case EMPTY:
			return 0;
		case GHOST:
			return -200;
		case GHOST_AND_DOT:
			return -180;
		case PACMAN:
			return 0; // ?
		case WALL:
			return 0;
		default:
			throw new IllegalArgumentException();
		}

	}

	public float q_star(WorldField[][] w, Position pos, PacmanAction action) {
		Position newPos = pos.mutate(action);

		if (!WorldHelper.isInBounds(w, newPos)) {
			return 0;
		}

		WorldField moveTargetType = WorldHelper.getTileType(w, newPos);

		WorldField currentType = WorldHelper.getTileType(w, pos);

//		if (moveTargetType.tileType == PacmanTileType.GHOST || currentType.tileType == PacmanTileType.GHOST) {
//			return rate(PacmanTileType.GHOST);
//		}
//
//		if (moveTargetType.tileType == PacmanTileType.GHOST_AND_DOT
//				|| currentType.tileType == PacmanTileType.GHOST_AND_DOT) {
//			return rate(PacmanTileType.GHOST_AND_DOT);
//		}
//
		if (moveTargetType.tileType == PacmanTileType.WALL || currentType.tileType == PacmanTileType.WALL) {
			return rate(PacmanTileType.WALL);
		}

//		if (moveTargetType.tileType == PacmanTileType.DOT || currentType.tileType == PacmanTileType.DOT) {
//			return rate(PacmanTileType.DOT);
//		}

//		return (moveTargetType.qValue * 0.6F + rate(currentType.tileType) * 0.2F + rate(moveTargetType.tileType) * 0.2F)
//				* 0.9F; // + rate(moveTargetType.tileType);
		return moveTargetType.qValue * 0.9F + rate(moveTargetType.tileType);
	}

	public WorldField[][] convertWorld(PacmanTileType[][] w) {
		WorldField[][] world = new WorldField[w.length][];

		for (int x = 0; x < w.length; x++) {
			world[x] = new WorldField[w[x].length];

			for (int y = 0; y < w[x].length; y++) {

				PacmanTileType t = w[x][y];

				float baseRating = rate(t);
				world[x][y] = new WorldField(baseRating, PacmanAction.WAIT, t);

			}
		}

		return world;
	}

	public WorldField[][] duplicate(WorldField[][] w) {
		WorldField[][] newWorld = new WorldField[w.length][];

		for (int x = 0; x < w.length; x++) {
			newWorld[x] = new WorldField[w[x].length];

			for (int y = 0; y < w[x].length; y++) {
				WorldField t = w[x][y];
			}
		}

		return newWorld;

	}

	public WorldField[][] next(WorldField[][] w) {
		WorldField[][] newWorld = duplicate(w);

		for (int x = 0; x < w.length; x++) {
			for (int y = 0; y < w[x].length; y++) {
				WorldField t = w[x][y];

				Position p = new Position(x, y);

				List<QActionValue> values = new ArrayList<>();

				for (PacmanAction action : PacmanAction.values()) {
					if (action == PacmanAction.QUIT_GAME || action == PacmanAction.WAIT) {
						continue;
					}

					float q = q_star(w, p, action);

					values.add(new QActionValue(action, q));

				}

				QActionValue v = values.stream().max((a, b) -> Float.compare(a.qValue, b.qValue)).get();

				newWorld[x][y] = new WorldField(v.qValue, v.qAction, t.tileType);

			}
		}

		return newWorld;

	}

	@ToString
	@RequiredArgsConstructor
	class QActionValue {

		private final PacmanAction qAction;
		private final float qValue;

	}

}
