package fish.hms.survivalclash.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fish.hms.survivalclash.SurvivalClash;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Survival Clash";
		config.width = 384;
		config.height = 384;
		new LwjglApplication(new SurvivalClash(), config);
	}
}
