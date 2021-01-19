package de.fh.stud.p5;

import de.fh.kiServer.agents.Agent;
import de.fh.pacman.PacmanAgent_2021;
import de.fh.pacman.PacmanGameResult;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.PacmanStartInfo;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanActionEffect;
import de.fh.stud.p1.Position;

public class MyAgent_P5 extends PacmanAgent_2021 {

	private WorldField[][] policy;

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

		long nsStart = System.nanoTime();

		MDP mdp = new MDP(percept);
		policy = mdp.compute();

//		System.exit(0);

		WorldField f = WorldHelper.getTileType(policy, new Position(percept.getPosX(), percept.getPosY()));

		long nsStop = System.nanoTime();
		double diff = ((double) (nsStop - nsStart)) / 1000D / 1000D;
		System.out.println(diff + "ms");

		return f.qAction;
	}

	@Override
	protected void onGameStart(PacmanStartInfo startInfo) {

	}

	@Override
	protected void onGameover(PacmanGameResult gameResult) {
	}

}
