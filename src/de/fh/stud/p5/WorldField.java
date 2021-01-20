package de.fh.stud.p5;

import java.util.List;

import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p5.MDP.QActionValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class WorldField {

	public final float qValue;
	public final PacmanAction qAction;
	public final PacmanTileType tileType;
	public final List<QActionValue> allQActionValues;
}
