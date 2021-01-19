package de.fh.stud.p5;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.fh.kiServer.util.Vector2;
import de.fh.pacman.enums.PacmanTileType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class DebugGUI {
	static WorldField[][] w;

	public void test() {
		JFrame frame = new JFrame("Top Level Demo");
		frame.setSize(300, 250);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(new World());
		frame.setVisible(true);
	}

	@NoArgsConstructor
	@AllArgsConstructor
	class Area {
		int x1, x2;
		int y1, y2;
		int height, width;
	}

	class World extends JPanel {

		private int[] sections(int num, int area) {
			double perSection = ((double) area) / ((double) num);
			int[] sections = new int[num];
			for (int i = 0; i < num; i++) {
				sections[i] = (int) (perSection * i);
			}

			return sections;
		}

		private Area getCoordsBlock(int x, int y, Dimension size, int[] linesX, int[] linesY) {
			double perSectionX = ((double) size.width) / ((double) linesX.length);
			double perSectionY = ((double) size.height) / ((double) linesY.length);

			Area a = new Area();
			a.x1 = linesX[x];
			a.y1 = linesY[y];

			a.width = (int) perSectionX;
			a.height = (int) perSectionY;

			a.x2 = linesX[x] + a.width;
			a.y2 = linesY[y] + a.height;

			return a;
		}

		@Override
		public void paint(Graphics g) {
			if (w == null) {
				return;
			}

			Dimension size = getSize();
			g.drawLine(0, 0, size.width, size.height);

			int[] linesX = sections(w.length, size.width);
			int[] linesY = sections(w[0].length, size.height);

			for (int x = 0; x < linesX.length; x++) {
				g.drawLine(linesX[x], 0, linesX[x], size.height);
			}

			for (int y = 0; y < linesY.length; y++) {
				g.drawLine(0, linesY[y], size.width, linesY[y]);
			}

			for (int x = 0; x < w.length; x++) {
				for (int y = 0; y < w[x].length; y++) {
					WorldField f = w[x][y];

					Area a = getCoordsBlock(x, y, size, linesX, linesY);

					if (f.tileType == PacmanTileType.WALL) {
						g.setColor(Color.black);
						g.fillRect(a.x1, a.y1, a.width, a.height);
						g.setColor(Color.white);
						float offset = g.getFontMetrics().getLineMetrics("test", g).getHeight();
						g.drawString("test", a.x1, (int) (a.y1 + offset));
					}

				}
			}

		}

	}
}
