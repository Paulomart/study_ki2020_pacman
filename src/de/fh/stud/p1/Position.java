package de.fh.stud.p1;

import de.fh.pacman.enums.PacmanAction;
import lombok.ToString;

@ToString
public class Position {

	public final int x;
	public final int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Position north() { return new Position(this.x, this.y - 1); }
	public Position south() { return new Position(this.x, this.y + 1); }
	public Position west() { return new Position(this.x - 1, this.y); }
	public Position east() { return new Position(this.x + 1, this.y); }
	
	public Position mutate(PacmanAction action) {
		/*
		 *            ^ (-y/north)
		 *            |
		 *            |
		 *            |
		 *            |
		 * <----------+----------->
		 *  (-x/west) |        (+x/east)
		 *            |
		 *            |
		 *            | (+y/south)
		 *            V
		 */
		
		switch (action) {

		case GO_EAST:
			return new Position(x + 1, y);
		case GO_WEST:
			return new Position(x - 1, y);
		case GO_SOUTH:
			return new Position(x, y + 1);
		case GO_NORTH:
			return new Position(x, y - 1);

		case QUIT_GAME:
			return this;
		case WAIT:
			return this;

		default:
			throw new IllegalArgumentException("Unexpected value: " + action);
		}
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
