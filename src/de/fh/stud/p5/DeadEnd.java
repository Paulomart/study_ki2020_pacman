package de.fh.stud.p5;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p1.WorldHelper;
import de.fh.stud.p1.Position;

public class DeadEnd {
	
	public Position startPosition;
	public Position endPosition;
	public LinkedList<Position> path = new LinkedList<>();
	
	public DeadEnd(PacmanTileType[][] w, Position p) {
		this.startPosition = p;
		this.endPosition = p;
		
		ArrayList<Position> neighbours = getNeighbours(w, p);
		Position oldPosition = this.startPosition;
		path.add(this.startPosition);
		
		while(neighbours.size() < 3) {
			
			neighbours.remove(oldPosition);
			if (neighbours.size() != 1) {
				throw new IllegalStateException();
			}
			oldPosition = this.startPosition;
			this.startPosition = neighbours.get(0);
			
			path.add(this.startPosition);
			neighbours = getNeighbours(w, this.startPosition);
		}
		
		this.startPosition = this.path.get(this.path.size() - 2);
		this.path.removeLast();
	}
	
	public static ArrayList<DeadEnd> getDeadEnds(PacmanTileType[][] w) {
		
		ArrayList<DeadEnd> deadEnds = new ArrayList<>();
		
		for(int x = 0; x < w.length; x++) {
			for(int y = 0; y < w[x].length; y++) {
				
				if(w[x][y] == PacmanTileType.WALL) {
					continue;
				}
				
				Position p = new Position(x, y);
				ArrayList<Position> neighbours = getNeighbours(w, p);
				
				if (neighbours.size() != 1) {
					continue;
				}
				
				deadEnds.add(new DeadEnd(w, p));
			}
		}
		return deadEnds;
	}
	
	public static boolean isInDeadEnd(List<DeadEnd> deadEnds, Position p) {
	
		for (DeadEnd deadEnd: deadEnds) {
			if (deadEnd.path.contains(p)) {
				return true;
			}
		}
		
		return false;	
	}
	
	public static boolean isStartDeadEnd(List<DeadEnd> deadEnds, Position p) {
		
		for (DeadEnd deadEnd: deadEnds) {
			if (deadEnd.startPosition.equals(p)) {
				return true;
			}
		}
		
		return false;	
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
