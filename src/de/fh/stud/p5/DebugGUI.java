package de.fh.stud.p5;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p5.MDP.QActionValue;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class DebugGUI {
	private static WorldField[][] w;
	private static JFrame frame;

	public void test() {
		frame = new JFrame("Top Level Demo");
		frame.setSize(800, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(new World());
		frame.setVisible(true);
	}

	public static void setW(WorldField[][] w) {
		SwingUtilities.invokeLater(() -> {
			DebugGUI.w = w;
			frame.getComponents()[0].repaint();
			frame.validate();
			frame.repaint();
		});
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

		private void drawStuff(Area a, Graphics g, WorldField f, float vMin, float vMax) {
			int innerWidth = a.width / 3;
			int innerHeight = a.height / 3;

			int innerX = innerWidth + a.x1;
			int innerY = innerHeight + a.y1;

			g.setColor(getValueColor(f.qValue, vMin, vMax));
			g.fillRect(innerX, innerY, innerWidth, innerHeight);

			if (f.tileType == PacmanTileType.PACMAN) {
				g.setColor(Color.black);
				g.drawOval(a.x1 + innerWidth, a.y1 + innerHeight, innerWidth, innerHeight);
			} else if (f.tileType == PacmanTileType.GHOST || f.tileType == PacmanTileType.GHOST_AND_DOT) {
				g.setColor(Color.black);

				g.drawLine(a.x1 + innerWidth, a.y1 + innerHeight, a.x1 + innerWidth * 2, a.y1 + innerHeight * 2);
				g.drawLine(a.x1 + innerWidth, a.y1 + 2 * innerHeight, a.x1 + innerWidth * 2, a.y1 + innerHeight);

				if (f.tileType == PacmanTileType.GHOST_AND_DOT) {
					int xPad = (int) (innerWidth / 3F);
					int yPad = (int) (innerHeight / 3F);

					g.drawOval(a.x1 + innerWidth + xPad, a.y1 + innerHeight + yPad, innerWidth - 2 * xPad,
							innerHeight - 2 * yPad);
				}

			}

			for (QActionValue v : f.allQActionValues) {
				int[] pointsX = {};
				int[] pointsY = {};

				switch (v.qAction) {
				case GO_NORTH:
					pointsX = new int[] { a.x1, a.x2, a.x1 + innerWidth * 2, a.x1 + innerWidth };
					pointsY = new int[] { a.y1, a.y1, a.y1 + innerHeight, a.y1 + innerHeight };
					break;
				case GO_EAST:
					pointsX = new int[] { a.x2, a.x2, a.x1 + innerWidth * 2, a.x1 + innerWidth * 2 };
					pointsY = new int[] { a.y1, a.y2, a.y1 + innerHeight * 2, a.y1 + innerHeight };
					break;
				case GO_SOUTH:
					pointsX = new int[] { a.x2, a.x1, a.x1 + innerWidth, a.x1 + innerWidth * 2 };
					pointsY = new int[] { a.y2, a.y2, a.y1 + innerHeight * 2, a.y1 + innerHeight * 2 };
					break;
				case GO_WEST:
					pointsX = new int[] { a.x1, a.x1, a.x1 + innerWidth, a.x1 + innerWidth };
					pointsY = new int[] { a.y2, a.y1, a.y1 + innerHeight, a.y1 + innerHeight * 2 };
					// TODO WAIT
					break;
				default:
					break;
				}

				g.setColor(getValueColor(v.qValue, vMin, vMax));
				g.fillPolygon(pointsX, pointsY, pointsX.length);

			}

			int[] pointsX = {};
			int[] pointsY = {};

			switch (f.qAction) {
			case GO_NORTH:
				pointsX = new int[] { a.x1 + innerWidth, a.x1 + a.width / 2, a.x1 + innerWidth * 2 };
				pointsY = new int[] { a.y1 + innerHeight, a.y1, a.y1 + innerHeight };
				break;
			case GO_EAST:
				pointsX = new int[] { a.x1 + innerWidth * 2, a.x2, a.x1 + innerWidth * 2 };
				pointsY = new int[] { a.y1 + innerHeight, a.y1 + a.height / 2, a.y1 + innerHeight * 2 };
				break;
			case GO_SOUTH:
				pointsX = new int[] { a.x1 + innerWidth, a.x1 + a.width / 2, a.x1 + innerWidth * 2 };
				pointsY = new int[] { a.y1 + innerHeight * 2, a.y2, a.y1 + innerHeight * 2 };
				break;
			case GO_WEST:
				pointsX = new int[] { a.x1 + innerWidth, a.x1, a.x1 + innerWidth };
				pointsY = new int[] { a.y1 + innerHeight, a.y1 + a.height / 2, a.y1 + innerHeight * 2 };
				break;
			// TODO WAIT
			default:
				break;
			}

			g.setColor(Color.black);
			g.drawPolygon(pointsX, pointsY, pointsX.length);

		}

		public Color getValueColor(float value, float min, float max) {
			double hue = javafx.scene.paint.Color.BLUE.getHue()
					+ (javafx.scene.paint.Color.RED.getHue() - javafx.scene.paint.Color.BLUE.getHue()) * (value - min)
							/ (max - min);

			javafx.scene.paint.Color fx = javafx.scene.paint.Color.hsb(hue, 1.0, 1.0);

			return new Color((float) fx.getRed(), (float) fx.getGreen(), (float) fx.getBlue(), (float) fx.getOpacity());
		}

		public String fmtNum(float num) {
			return String.format("%+7.1f", num);
		}

		float vMin = Float.MAX_VALUE;
		float vMax = Float.MIN_VALUE;

		@Override
		public void paint(Graphics g) {
			if (w == null) {
				return;
			}

			Dimension size = getSize();

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
					vMin = Math.min(f.qValue, vMin);
					vMax = Math.max(f.qValue, vMax);

					for (QActionValue v : f.allQActionValues) {
						vMin = Math.min(v.qValue, vMin);
						vMax = Math.max(v.qValue, vMax);
					}
				}
			}

			for (int x = 0; x < w.length; x++) {
				for (int y = 0; y < w[x].length; y++) {
					WorldField f = w[x][y];

					Area a = getCoordsBlock(x, y, size, linesX, linesY);

					if (f.tileType == PacmanTileType.WALL) {
						g.setColor(Color.black);
						g.fillRect(a.x1, a.y1, a.width, a.height);

					} else {
						g.setColor(Color.black);
						drawStuff(a, g, f, vMin, vMax);
					}

				}
			}

		}

	}
}
