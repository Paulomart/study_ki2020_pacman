package de.fh.stud.p2;

import de.fh.kiServer.agents.Agent;
import de.fh.pacman.PacmanAgent_2021;
import de.fh.pacman.PacmanGameResult;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.PacmanStartInfo;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanActionEffect;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p1.WorldHelper;

public class MyAgent_P2 extends PacmanAgent_2021 {

	/**
	 * Die letzte Wahrnehmung der Spielwelt
	 */
	private PacmanPercept percept;

	/**
	 * Das aktuell wahrgenommene 2D Array der Spielwelt
	 */
	private PacmanTileType[][] view;

	/**
	 * Die zuletzt empfangene Rückmeldung des Servers auf die ausgeführte Aktion
	 */
	private PacmanActionEffect actionEffect;

	/**
	 * Die als nächstes auszuführende Aktion
	 */
	private PacmanAction nextAction;

	public MyAgent_P2(String name) {
		super(name);
	}

	public static void main(String[] args) {
		MyAgent_P2 agent = new MyAgent_P2("MyAgent");
		Agent.start(agent, "127.0.0.1", 5000);
	}

	@Override
	public PacmanAction action(PacmanPercept percept, PacmanActionEffect actionEffect) {
		/*
		 * Aktuelle Wahrnehmung des Agenten, bspw. Position der Geister und Zustand
		 * aller Felder der Welt.
		 */
		this.percept = percept;

		/*
		 * Aktuelle Rückmeldung des Server auf die letzte übermittelte Aktion.
		 * 
		 * Alle möglichen Serverrückmeldungen: PacmanActionEffect.GAME_INITIALIZED
		 * PacmanActionEffect.GAME_OVER PacmanActionEffect.BUMPED_INTO_WALL
		 * PacmanActionEffect.MOVEMENT_SUCCESSFUL PacmanActionEffect.DOT_EATEN
		 */
		this.actionEffect = actionEffect;

		/*
		 * percept.getView() enthält die aktuelle Felderbelegung in einem 2D Array
		 * 
		 * Folgende Felderbelegungen sind möglich: PacmanTileType.WALL;
		 * PacmanTileType.DOT PacmanTileType.EMPTY PacmanTileType.PACMAN
		 * PacmanTileType.GHOST PacmanTileType.GHOST_AND_DOT
		 */
		this.view = percept.getView();

		WorldHelper.printWorld(view);

		/*
		 * Die möglichen zurückzugebenden PacmanActions sind: PacmanAction.GO_EAST
		 * PacmanAction.GO_NORTH PacmanAction.GO_SOUTH PacmanAction.GO_WEST
		 * PacmanAction.QUIT_GAME PacmanAction.WAIT
		 */

		// Nachdem das Spiel gestartet wurde, geht der Agent nach Osten
		if (actionEffect == PacmanActionEffect.GAME_INITIALIZED) {
			nextAction = PacmanAction.GO_EAST;
		}

		if (actionEffect != PacmanActionEffect.BUMPED_INTO_WALL && Math.random() < 0.2) {
			int random = (int) (Math.random() * 3);
			nextAction = PacmanAction.values()[random];
		}

		if (actionEffect == PacmanActionEffect.BUMPED_INTO_WALL && nextAction == PacmanAction.GO_WEST) {
			nextAction = PacmanAction.GO_SOUTH;
		} else if (actionEffect == PacmanActionEffect.BUMPED_INTO_WALL && nextAction == PacmanAction.GO_SOUTH) {
			nextAction = PacmanAction.GO_EAST;
		} else if (actionEffect == PacmanActionEffect.BUMPED_INTO_WALL && nextAction == PacmanAction.GO_EAST) {
			nextAction = PacmanAction.GO_NORTH;
		} else if (actionEffect == PacmanActionEffect.BUMPED_INTO_WALL && nextAction == PacmanAction.GO_NORTH) {
			nextAction = PacmanAction.GO_WEST;
		}

		return nextAction;
	}

	@Override
	protected void onGameStart(PacmanStartInfo startInfo) {

	}

	@Override
	protected void onGameover(PacmanGameResult gameResult) {

	}
}
