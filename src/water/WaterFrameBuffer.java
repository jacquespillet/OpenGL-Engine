package water;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class WaterFrameBuffer {
	
	protected static final int REFLECTION_WIDTH = 320;
	protected static final int REFLECTION_HEIGHT = 180;
	
	protected static final int REFRACTION_WIDTH = 1280;
	protected static final int REFRACTION_HEIGHT = 720;
	
	private int reflectionFramebuffer;
	private int reflectionTexture;
	private int reflectionDepthBuffer;
	
	private int refractionFramebuffer;
	private int refractionTexture;
	private int refractionDepthTexture;
	
	public WaterFrameBuffer() {
		initializeReflectionFramebuffer();
		initializeRefractionFramebuffer();
	}
	
	public void bindReflectionFramebuffer() {
		bindFramebuffer(reflectionFramebuffer, REFLECTION_WIDTH, REFLECTION_HEIGHT);
	}
	
	public void bindRefractionFramebuffer() {
		bindFramebuffer(refractionFramebuffer, REFRACTION_WIDTH, REFRACTION_HEIGHT);
	}
	
	public int getReflectionTexture() {
		return reflectionTexture;
	}
	
    public int getRefractionDepthTexture(){//get the resulting depth texture
        return refractionDepthTexture;
    }
 
	public int getRefractionTexture() {
		return refractionTexture;
	}


	private void initializeReflectionFramebuffer() {
		reflectionFramebuffer = createFramebuffer();
		reflectionTexture = createTextureAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT);
		reflectionDepthBuffer = createDepthBufferAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT);
		unbindCurrentFramebuffer();
	}
	
	private void initializeRefractionFramebuffer() {
		refractionFramebuffer = createFramebuffer();
		refractionTexture = createTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT);
		refractionDepthTexture = createDepthTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT);
		unbindCurrentFramebuffer();
	}
		

	private void bindFramebuffer(int framebuffer, int width, int height) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		GL11.glViewport(0,  0, width, height);
	}
	
	private int createFramebuffer() {
		int framebuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		return framebuffer;
	}
	
	private int createTextureAttachment(int width, int height) {
		int texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texture, 0);
		return texture;
	}
	
	private int createDepthTextureAttachment(int width, int height) {
		int texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, texture, 0);
		return texture;		
	}
	
	private int createDepthBufferAttachment(int width, int height) {
		int depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
		return depthBuffer;
	}
	
	public void unbindCurrentFramebuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0,  0,  Display.getWidth(), Display.getHeight());
	}
	
	public void clean() {
		GL30.glDeleteFramebuffers(reflectionFramebuffer);
		GL11.glDeleteTextures(reflectionTexture);
		GL30.glDeleteRenderbuffers(reflectionDepthBuffer);
		GL30.glDeleteFramebuffers(refractionFramebuffer);
		GL11.glDeleteTextures(refractionTexture);
		GL30.glDeleteRenderbuffers(refractionDepthTexture);
	}
}
