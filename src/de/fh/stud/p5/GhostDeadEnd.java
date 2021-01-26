package de.fh.stud.p5;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.fh.kiServer.gridBasedGame.gui.NewLevelView;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p1.Position;
import de.fh.stud.p1.WorldHelper;

public class GhostDeadEnd {
	
	public Position startPosition;
	private Position endPosition;
	public LinkedList<Position> endPositions = new LinkedList<>();
	public Position fieldInFrontOfGhostDeadEnd;
	public LinkedList<Position> path = new LinkedList<>();
	
	private LinkedList<GhostDeadEnd> children = new LinkedList<>();
	
	public GhostDeadEnd(GhostDeadEnd g1, GhostDeadEnd g2) {
		this.children.add(g1);
		this.children.add(g2);
		this.startPosition = g1.fieldInFrontOfGhostDeadEnd;
		this.endPositions.add(g1.endPosition);
		this.endPositions.add(g2.endPosition);
		this.path.addAll(g1.path);
		this.path.addAll(g2.path);
	}
	
	public GhostDeadEnd(PacmanTileType[][] w, Position ghostPosition, Position startPosition) {
		this.startPosition = startPosition;
		this.endPosition = startPosition;
		this.fieldInFrontOfGhostDeadEnd = startPosition;
		
		ArrayList<Position> neighbours = getNeighbours(w, startPosition);
		Position oldPosition = ghostPosition;
		path.add(this.startPosition);
		
		if (neighbours.size() != 2) {
			return;
		}
		
		while(neighbours.size() < 3) {
			
			neighbours.remove(oldPosition);

			if (neighbours.size() != 1) {
				if (neighbours.size() == 0) {
					return;
				}
				for (Position position : neighbours) {
					System.out.println(position);
				}
				System.out.println("test");
				throw new IllegalStateException();
			}
			oldPosition = this.startPosition;
			this.startPosition = neighbours.get(0);
			
			path.add(this.startPosition);
			neighbours = getNeighbours(w, this.startPosition);
		}
		

		this.startPosition = this.path.get(this.path.size() - 2);
		this.fieldInFrontOfGhostDeadEnd = this.path.get(this.path.size() - 1);
		this.path.removeLast();
	}
	
	public static boolean isInGhostDeadEnd(List<GhostDeadEnd> ghostDeadEnds, Position p) {
		
		for (GhostDeadEnd ghostDeadEnd : ghostDeadEnds) {
			if (ghostDeadEnd.path.contains(p)) {
				return true;
			}
		}

		return false;
	}

	public static List<GhostDeadEnd> getGhostDeadEnds(PacmanTileType[][] w) {
		
		ArrayList<GhostDeadEnd> ghostDeadEnds = new ArrayList<>();
		HashMap<Position, ArrayList<GhostDeadEnd>> singleGhostDeadEnds = new HashMap<>();
		
		for(int x = 0; x < w.length; x++) {
			for(int y = 0; y < w[x].length; y++) {
				
				if(!(w[x][y] == PacmanTileType.GHOST || w[x][y] == PacmanTileType.GHOST_AND_DOT)) {
					continue;
				}
				
				Position p = new Position(x, y);
				ArrayList<Position> neighbours = getNeighbours(w, p);
				
				singleGhostDeadEnds.put(p, new ArrayList<>());
				
				for (Position neighbour : neighbours) {
					singleGhostDeadEnds.get(p).add(new GhostDeadEnd(w, p, neighbour));
				}
			}
		}
	
		LinkedList<Position> usedGhosts = new LinkedList<>();
		
		System.out.println(singleGhostDeadEnds.size());
		
		for (Position ghost : singleGhostDeadEnds.keySet()) {
			for (GhostDeadEnd ghostDeadEnd : singleGhostDeadEnds.get(ghost)) {
				HashSet<Position> otherGhosts = new HashSet<>(singleGhostDeadEnds.keySet());
				otherGhosts.remove(ghost);
				
				for (Position otherGhost : otherGhosts) {
					
					if (usedGhosts.contains(otherGhost)) {
						continue;
					}
					for (GhostDeadEnd otherGhostDeadEnd : singleGhostDeadEnds.get(otherGhost)) {
						if (ghostDeadEnd.fieldInFrontOfGhostDeadEnd.equals(otherGhostDeadEnd.fieldInFrontOfGhostDeadEnd)) {
							ghostDeadEnds.add(new GhostDeadEnd(ghostDeadEnd, otherGhostDeadEnd));
						}
					}
				}
			}
			
		}
		
		return ghostDeadEnds;
	}

	private static ArrayList<Position> getNeighbours(PacmanTileType[][] w, Position p) {
		
		ArrayList<Position> neighbours = new ArrayList<>();
		Position north = p.north();
		Position south = p.south();
		Position east = p.east();
		Position west = p.west();
		
		if (WorldHelper.isInBounds(w, north)) {
			if(WorldHelper.getTileType(w, north) != PacmanTileType.WALL) {
				neighbours.add(north);
			}
		}
		if (WorldHelper.isInBounds(w, east)) {
			if(WorldHelper.getTileType(w, east) != PacmanTileType.WALL) {
				neighbours.add(east);
			}
		}
		if (WorldHelper.isInBounds(w, west)) {
			if(WorldHelper.getTileType(w, west) != PacmanTileType.WALL) {
				neighbours.add(west);
			}
		}
		if (WorldHelper.isInBounds(w, south)) {
			if(WorldHelper.getTileType(w, south) != PacmanTileType.WALL) {
				neighbours.add(south);
			}
		}
		
		return neighbours;
	}
}
