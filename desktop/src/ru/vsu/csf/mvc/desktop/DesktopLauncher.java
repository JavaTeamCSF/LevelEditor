package ru.vsu.csf.mvc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.vsu.csf.mvc.LevelEditor;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        /*config.width = 200 + 40*15;
        config.height = 100 + 40*15;*/ /** - если с полями*/
        config.width = 40*16;
        config.height = 40*16;
		new LwjglApplication(new LevelEditor(), config);
	}
}
