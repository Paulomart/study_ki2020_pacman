package de.fh.stud.p5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fh.kiServer.util.Vector2;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p1.Position;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class MDP {

	private final PacmanPercept percept;
	private final int dotsMax;
	private final int dotsLeft;

	public MDP(PacmanPercept percept, int dotsMax) {
		this.percept = percept;

		this.dotsMax = dotsMax;
		this.dotsLeft = de.fh.stud.p1.WorldHelper.count(percept.getView(), Arrays.asList(PacmanTileType.DOT));
	}

	public WorldField[][] compute() {
		WorldField[][] w = convertWorld(this.percept.getView());

		for (int i = 0; i < 50; i++) {
			w = next(w);
		}

		DebugGUI.setW(w);

		return w;
	}

	public float rate(PacmanTileType tile, Position p) {
		double turnsPercentLeft = ((double) (percept.getMaxTurns() - percept.getTurn()))
				/ (double) percept.getMaxTurns();
		double dotsPercentLeft = ((double) dotsLeft) / ((double) dotsMax);

		double ghostMultiplier = 1;
		if (p != null && tile == PacmanTileType.GHOST || tile == PacmanTileType.GHOST_AND_DOT) {
			String ghostType = percept.getGhostTypes().get(new Vector2(p.x, p.y));
			if (ghostType.equalsIgnoreCase("ghost_hunter")) {
				ghostMultiplier = 1;
			} else if (ghostType.equalsIgnoreCase("ghost_random")) {
				ghostMultiplier = 0.7;
			}
		}

		switch (tile) {
		case DOT:
			return 900;
//			return 1200;
//			return 1800;

		case EMPTY:
			return 0;
		case GHOST:
//			return (float) (-9000F * pLeft * ghostMultiplier);
			return (float) (-9000F * turnsPercentLeft);
//			return (float) (-9000F * dotsPercentLeft);

//			return -9000F;

		case GHOST_AND_DOT:
//			return (float) (-9800F * pLeft * ghostMultiplier);
			return (float) (-9800F * turnsPercentLeft);
//			return (float) (-9800F * dotsPercentLeft);

//			return -9800F;
		case PACMAN:
			return 0; // ?
		case WALL:
//			return -9999;
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
			return rate(PacmanTileType.WALL, null);
		}

//		if (moveTargetType.tileType == PacmanTileType.DOT || currentType.tileType == PacmanTileType.DOT) {
//			return rate(PacmanTileType.DOT);
//		}

//		return (moveTargetType.qValue * 0.6F + rate(currentType.tileType) * 0.2F + rate(moveTargetType.tileType) * 0.2F)
//				* 0.9F; // + rate(moveTargetType.tileType);
		float rawScore = moveTargetType.qValue * 0.9F + rate(currentType.tileType, pos);

		return rawScore;
	}

	public WorldField[][] convertWorld(PacmanTileType[][] w) {
		WorldField[][] world = new WorldField[w.length][];

		for (int x = 0; x < w.length; x++) {
			world[x] = new WorldField[w[x].length];

			for (int y = 0; y < w[x].length; y++) {

				PacmanTileType t = w[x][y];

				float baseRating = rate(t, new Position(x, y));
				world[x][y] = new WorldField(baseRating, PacmanAction.WAIT, t, Arrays.asList());

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

					// dont respect wall when calulcating scores
					Position newP = p.mutate(action);
					if (WorldHelper.isInBounds(w, newP)) {
						WorldField f = WorldHelper.getTileType(w, p.mutate(action));
						if (f.tileType == PacmanTileType.WALL) {
							continue;
						}
					}

					float q = q_star(w, p, action);

					values.add(new QActionValue(action, q));

				}

				QActionValue v = values.stream().max((a, b) -> Float.compare(a.qValue, b.qValue)).orElse(null);

				if (v == null) {
					newWorld[x][y] = t;
					continue;
				}

				float avg = (float) values.stream().mapToDouble(a -> a.qValue).average().getAsDouble();
				newWorld[x][y] = new WorldField(avg, v.qAction, t.tileType, values);
			}
		}

		return newWorld;

	}

	@ToString
	@RequiredArgsConstructor
	class QActionValue {

		final PacmanAction qAction;
		final float qValue;

	}

}
