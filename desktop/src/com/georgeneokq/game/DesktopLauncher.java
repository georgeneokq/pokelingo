package com.georgeneokq.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setResizable(false);
		config.setDecorated(false);
		config.setWindowedMode(1920, 1080);
		// config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

		config.setForegroundFPS(60);
		config.setTitle("PokeLingo");
		new Lwjgl3Application(new Main(), config);
	}
}
