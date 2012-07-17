package particle;

import java.awt.Color;

import javax.media.opengl.GL2;

public class GLColor implements Cloneable {
	public float r, g, b, a;
	
	public GLColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public GLColor(float r, float g, float b) {
		this(r, g, b, 1f);
	}
	
	public GLColor(Color color) {
		this(
			color.getRed() / 256f,
			color.getGreen() / 256f,
			color.getBlue() / 256f);
	}
	
	public void activate(GL2 gl) {
		gl.glColor4f(r, g, b, a);
	}
	
	public GLColor clone() {
		return new GLColor(r, g, b, a);
	}
}
