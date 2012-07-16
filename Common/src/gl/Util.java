package gl;

import static javax.media.opengl.GL2.*;

import javax.media.opengl.GL2;

public class Util {
	public static void drawCoordinateSystem(GL2 gl) {
		gl.glBegin(GL_LINES);
		gl.glColor3d(1, 0, 0);
		
		gl.glVertex3i(0, 0, 0);
		gl.glVertex3i(1, 0, 0);
		
		gl.glColor3d(1, 1, 0);
		
		gl.glVertex3i(0, 0, 0);
		gl.glVertex3i(0, 1, 0);
		
		gl.glColor3d(0, 1, 0);
		
		gl.glVertex3i(0, 0, 0);
		gl.glVertex3i(0, 0, 1);
		gl.glEnd();
	}
	
	public static void drawCuboid(GL2 gl, double width, double height, double depth) {
		final int[] np = new int[] { -1, 1};
		final double wh = width/2;
		final double hh = height/2;
		final double dh = depth/2;
		
		gl.glBegin(GL_QUAD_STRIP);
		//left & right
		for (int u : np) {
			gl.glNormal3d(u, 0, 0);
			for (int v : np) for (int w : np) {
				gl.glTexCoord2d((v+1)/2, (w+1)/2);
				gl.glVertex3d(u*wh, v*hh, w*dh);
			}
		}
		
		//bottom & top
		for (int v : np) {
			gl.glNormal3d(0, v, 0);
			for (int w : np) for (int u : np) {
				gl.glTexCoord2d((w+1)/2, (u+1)/2);
				gl.glVertex3d(u*wh, v*hh, w*dh);
			}
		}
		
		//back & front
		for (int w: np) {
			gl.glNormal3d(0, 0, w);
			for (int u : np) for (int v : np) {
				gl.glTexCoord2d((u+1)/2, (v+1)/2);
				gl.glVertex3d(u*wh, v*hh, w*dh);
			}
		}
		gl.glEnd();
	}
	
	/** Draws a centered grid with uniform width and height
	 * @param gl OpenGL context
	 * @param s side length */
	public static void drawGrid(GL2 gl, int s) {
		drawGrid(gl, s, s);
	}
	
	/** Draws a centered grid with given dimensions
	 * @param gl OpenGL context
	 * @param h grid height
	 * @param v grid width */
	public static void drawGrid(GL2 gl, int h, int v) {
		gl.glBegin(GL_LINES);
		gl.glColor3d(.3, .3, .3);
		
		for (int x=-h/2; x<=h/2; x++) {
			gl.glVertex3i(x, 0,-v/2);
			gl.glVertex3i(x, 0, v/2);
		}
		for (int z=-v/2; z<=v/2; z++) {
			gl.glVertex3i(-h/2, 0, z);
			gl.glVertex3i( h/2, 0, z);
		}
		
		gl.glEnd();
	}
}
