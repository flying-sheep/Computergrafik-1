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
	
	public static void drawCuboid(GL2 gl, double w, double h, double d) {
		gl.glBegin(GL_QUAD_STRIP);
		for (int s=0; s<10; s++)
			gl.glVertex3d(
				(s%2 == 0) ? -w/2 : w/2,
				(s<4 || s>7) ? h/2 : -h/2,
				(s<2 || s>5) ? -d/2 : d/2);
		gl.glEnd();
		
		gl.glBegin(GL_QUADS);
		for (int l=-1; l<2; l+=2) {
			gl.glVertex3d(l*w/2,  h/2, -d/2);
			gl.glVertex3d(l*w/2,  h/2,  d/2);
			gl.glVertex3d(l*w/2, -h/2,  d/2);
			gl.glVertex3d(l*w/2, -h/2, -d/2);
		}
		gl.glEnd();
	}
	
	public static void drawGrid(GL2 gl, int s) {
		drawGrid(gl, s, s);
	}
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
	}
}
