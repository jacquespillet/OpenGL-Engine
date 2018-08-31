package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS = 120;
	
	private static long lastFrameTime;
	private static float deltaTime;
	
	public static void createDisplay() {	
		
		ContextAttribs contextAttribs = new ContextAttribs(3,  3);
		contextAttribs.withForwardCompatible(true);
		contextAttribs.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new org.lwjgl.opengl.DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(), contextAttribs);
			Display.setTitle("Engine");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0,  0,  WIDTH,  HEIGHT);
		lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay() {
		Display.sync(FPS);
		Display.update();
		long currentFrameTime = getCurrentTime();
		deltaTime = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}
	
	public static float getFrameTimeSeconds() {
		return deltaTime;
	}
	
	
	public static void closeDisplay() {
		Display.destroy();
	}
	
	private static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
}
