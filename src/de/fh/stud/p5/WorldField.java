package de.fh.stud.p5;

import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class WorldField {

	public final float qValue;
	public final PacmanAction qAction;
	public final PacmanTileType tileType;

}
