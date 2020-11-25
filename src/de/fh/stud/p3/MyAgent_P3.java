package de.fh.stud.p3;

import java.util.LinkedList;
import java.util.Queue;

import de.fh.kiServer.agents.Agent;
import de.fh.pacman.PacmanAgent_2021;
import de.fh.pacman.PacmanGameResult;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.PacmanStartInfo;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanActionEffect;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p1.Knoten;
import de.fh.stud.p1.Position;

public class MyAgent_P3 extends PacmanAgent_2021 {

	/**
	 * Die aktuelle Wahrnehmung der Spielwelt
	 */
	private PacmanPercept percept;
	/**
	 * Die empfangene Rückmeldung des Servers auf die zuletzt ausgeführte Aktion
	 */
	private PacmanActionEffect actionEffect;
	/**
	 * Das aktuell wahrgenommene 2D Array der Spielwelt
	 */
	private PacmanTileType[][] view;

	/**
	 * Der gefundene Lösungknoten der Suche
	 */
	private Knoten loesungsKnoten;
	private Queue<PacmanAction> actions;
	private PacmanAction nextAction;

	public MyAgent_P3(String name) {
		super(name);
	}

	public static void main(String[] args) {
		MyAgent_P3 agent = new MyAgent_P3("MyAgent");
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

		/*
		 * Die möglichen zurückzugebenden PacmanActions sind: PacmanAction.GO_EAST
		 * PacmanAction.GO_NORTH PacmanAction.GO_SOUTH PacmanAction.GO_WEST
		 * PacmanAction.QUIT_GAME PacmanAction.WAIT
		 */

		// Wenn noch keine Lösung gefunden wurde, dann starte die Suche
		if (loesungsKnoten == null) {

			Suche suche = new Suche(view, new Position(this.percept.getPosX(), this.percept.getPosY()));
//			loesungsKnoten = suche.tiefensuche();
			loesungsKnoten = suche.breitensuche();

			actions = new LinkedList<PacmanAction>(loesungsKnoten.getResultedByActions());
		}

		// Wenn die Suche eine Lösung gefunden hat, dann ermittle die als nächstes
		// auszuführende Aktion
		if (!actions.isEmpty()) {

			nextAction = this.actions.remove();

		} else {
			// Ansonsten wurde keine Lösung gefunden und der Pacman kann das Spiel aufgeben
			nextAction = PacmanAction.QUIT_GAME;
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
