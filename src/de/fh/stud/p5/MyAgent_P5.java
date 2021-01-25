package de.fh.stud.p5;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fh.kiServer.agents.Agent;
import de.fh.pacman.PacmanAgent_2021;
import de.fh.pacman.PacmanGameResult;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.PacmanStartInfo;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanActionEffect;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p1.Position;

public class MyAgent_P5 extends PacmanAgent_2021 {

	private WorldField[][] policy;
	private int dotsMax = -1;

	public MyAgent_P5(String name) {
		super(name);
	}

	public static void main(String[] args) {
		new DebugGUI().test();

		MyAgent_P5 agent = new MyAgent_P5("MyAgent");
		Agent.start(agent, "127.0.0.1", 5000);
	}

	@Override
	public PacmanAction action(PacmanPercept percept, PacmanActionEffect actionEffect) {
		if (actionEffect == PacmanActionEffect.BUMPED_INTO_WALL) {
//			throw new IllegalStateException("Bumpled into wall. Out of sync?");
		}

		if (dotsMax == -1) {
			dotsMax = de.fh.stud.p1.WorldHelper.count(percept.getView(), Arrays.asList(PacmanTileType.DOT));
		}

		long nsStart = System.nanoTime();

		List<DeadEnd> deadEnds = DeadEnd.getDeadEnds(percept.getView());
		DebugGUI.setDeadEnds(deadEnds);

		GhostDistance gd = new GhostDistance(percept.getView());
		Map<Position, Integer> gdMap = new HashMap<Position, Integer>();
		for (DeadEnd deadEnd : deadEnds) {
			gdMap.put(deadEnd.startPosition, gd.at(deadEnd.startPosition));
		}
		DebugGUI.setGhostDistances(gdMap);

		MDP mdp = new MDP(percept, dotsMax, deadEnds, gdMap);
		policy = mdp.compute();

		WorldField f = WorldHelper.getTileType(policy, new Position(percept.getPosX(), percept.getPosY()));

		long nsStop = System.nanoTime();

		DebugGUI.onTurnEnded(nsStop - nsStart);

		return f.qAction;
	}

	@Override
	protected void onGameStart(PacmanStartInfo startInfo) {
		dotsMax = -1;
	}

	@Override
	protected void onGameover(PacmanGameResult gameResult) {
		DebugGUI.onRunEnded(gameResult);
	}

}
