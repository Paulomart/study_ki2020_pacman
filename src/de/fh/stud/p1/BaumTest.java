package de.fh.stud.p1;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

import de.fh.pacman.enums.PacmanTileType;

public class BaumTest {

	public static void main(String[] args) {
		// Anfangszustand nach Aufgabe
		PacmanTileType[][] view = {
				{ PacmanTileType.WALL, PacmanTileType.WALL, PacmanTileType.WALL, PacmanTileType.WALL },
				{ PacmanTileType.WALL, PacmanTileType.EMPTY, PacmanTileType.DOT, PacmanTileType.WALL },
				{ PacmanTileType.WALL, PacmanTileType.DOT, PacmanTileType.WALL, PacmanTileType.WALL },
				{ PacmanTileType.WALL, PacmanTileType.WALL, PacmanTileType.WALL, PacmanTileType.WALL } };
		// Startposition des Pacman
		int posX = 1, posY = 1;
		Position initialPos = new Position(posX, posY);
		/*
		 * TODO Praktikum 1 [3]: Baut hier basierend auf dem gegebenen Anfangszustand
		 * (siehe view, posX und posY) den Suchbaum auf.
		 */

		WorldHelper.printWorld(view, initialPos);
		
		Knoten initial = new Knoten(view, initialPos);

		
		int depth = 0;
		
		Queue<Knoten> toVistQueue = new LinkedList<Knoten>();
		
		toVistQueue.add(initial);
		
		while (!toVistQueue.isEmpty() && depth < 10) {
			depth++;
			
			Knoten next = toVistQueue.remove();
			System.out.println("depth: " + depth + "--------------------");
			next.print();
			
			toVistQueue.addAll(next.expand().collect(Collectors.toList()));
			
		}
	}
}
