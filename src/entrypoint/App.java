package entrypoint;

public class App {

	public static void main(String[] args) {
		new Thread(() -> {
			de.fh.pacman.gui.PacmanServer_Main.main(args);
		}, "KI_Server_Main").start();
		new Thread(() -> {
			de.fh.stud.p5.MyAgent_P5.main(args);
		}, "MyAgent_P5").start();
	}
}
