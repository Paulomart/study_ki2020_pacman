package de.fh.stud.p5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.fh.kiServer.util.Vector2;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p1.Position;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class MDP {

	private final PacmanPercept percept;
	private final List<DeadEnd> deadEnds;
	private final Map<Position, Integer> ghostDistances;
	private final int dotsMax;
	private final int dotsLeft;

	public MDP(PacmanPercept percept, int dotsMax, List<DeadEnd> deadEnds, Map<Position, Integer> ghostDistances) {
		this.percept = percept;
		this.deadEnds = deadEnds;
		this.ghostDistances = ghostDistances;

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
				ghostMultiplier = 0.9;
			} else if (ghostType.equalsIgnoreCase("ghost_random")) {
				ghostMultiplier = 1;
			}
		}

		float positionValue = 0F;

		switch (tile) {
		case DOT:
			positionValue = 900;
//			positionValue = 1200;
//			positionValue = 1800;
			break;

		case EMPTY:
			positionValue = 0;
			break;

		case GHOST:
//			positionValue = (float) (-9000F * pLeft * ghostMultiplier);
			positionValue = (float) (-9000F * turnsPercentLeft);
//			positionValue = (float) (-9000F * turnsPercentLeft * ghostMultiplier);
//			positionValue = (float) (-9000F * dotsPercentLeft);
//			positionValue = -9000F;
			break;

		case GHOST_AND_DOT:
//			positionValue = (float) (-9800F * pLeft * ghostMultiplier);
			positionValue = (float) (-9800F * turnsPercentLeft);
//			positionValue = (float) (-9800F * turnsPercentLeft * ghostMultiplier);
//			positionValue = (float) (-9800F * dotsPercentLeft);
//			positionValue = -9800F;
			break;

		case PACMAN:
			positionValue = 0; // ?
			break;

		case WALL:
//			positionValue = -9999;
			positionValue = 0;
			break;

		default:
			throw new IllegalArgumentException();
		}

		// If an tile is in a dead end and its not a dot, the score is very low
		// This makes the agent walk out of the dead end after eating all dots.
		if (DeadEnd.isEndDeadEnd(deadEnds, p) && tile != PacmanTileType.DOT) {
			return -99999;
		}
		return positionValue;

	}

	public float q_star(WorldField[][] w, Position pos, PacmanAction action) {
		Position newPos = pos.mutate(action);

		if (!WorldHelper.isInBounds(w, newPos)) {
			return 0;
		}

		WorldField moveTargetType = WorldHelper.getTileType(w, newPos);
		WorldField currentType = WorldHelper.getTileType(w, pos);

		if (moveTargetType.tileType == PacmanTileType.WALL || currentType.tileType == PacmanTileType.WALL) {
			return rate(PacmanTileType.WALL, null);
		}

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

					// ignore beginning of dead ends for the beginning
					DeadEnd isDeadEnd = DeadEnd.isStartDeadEnd(deadEnds, newP);
					if (isDeadEnd != null && !isDeadEnd.path.contains(p)) {
						// only calc when needed (cache?)

						int ghostsDistance = this.ghostDistances.get(newP);
						int deadEndLength = isDeadEnd.path.size();

						if (!isDeadEnd.path.stream().map(pathEntry -> WorldHelper.getTileType(w, pathEntry))
								.anyMatch(tt -> tt.tileType == PacmanTileType.DOT)) {
							continue;
						}

						// only works because the if above ensures that there are dots inside the dead end
						boolean onlyTheDotsInTheDeadEnd = isDeadEnd.path.size() >= dotsLeft;
						boolean ghostIsTooClose = ghostsDistance < deadEndLength * 2 + 1;

						if (ghostIsTooClose && !onlyTheDotsInTheDeadEnd) {
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
