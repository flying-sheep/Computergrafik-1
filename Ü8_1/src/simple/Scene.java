package simple;

import static javax.media.opengl.GL2.*;

import gl.Util;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

class Scene implements GLEventListener {
	static final int FLATTEN = 2;
	static final GLU glu = new GLU();
	
	GL2 gl;
	Texture dice;
	
	public static void main(String[] args) {
		new Scene();
	}
	
	Scene() {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		
		GLCanvas canvas = new GLCanvas(caps);
		
		canvas.addGLEventListener(this);
		
		Frame frame = new Frame("Simple scene");
		frame.add(canvas);
		frame.setSize(500, 500);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
	}
	
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		
		gl.glClearColor(0, 0, 0, 1);
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_LIGHTING);
		gl.glEnable(GL_LIGHT0);
		
		try {
			dice = TextureIO.newTexture(Scene.class.getResource("dice.png"), true, "png");
		} catch (GLException | IOException e) {
			e.printStackTrace();
		}
		dice.bind(gl);
	}
	
	public void display(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		
		glu.gluLookAt(2, 2, 2, 0, 0, 0, 0, 1, 0);
		
		lightSetup();
		
		plasticMaterialSetup();
		
		double step = .1;
		int max = 200;
		for (int v=-max; v<max; v++) {
			gl.glBegin(GL_QUAD_STRIP);
			gl.glNormal3d(0, 1, 0);
			for (int h=-max; h<=max; h++) {
				gl.glVertex3d(h*step, 0, v*step);
				gl.glVertex3d(h*step, 0, v*step+step);
			}
			gl.glEnd();
		}
		
		gl.glPushMatrix();
		gl.glTranslated(1, .5, 0);
		glu.gluSphere(glu.gluNewQuadric(), 0.5f, 200, 200);
		gl.glPopMatrix();
		
		glowMaterialSetup();
		
		gl.glEnable(GL_TEXTURE_2D);
		dice.enable(gl);
		gl.glPushMatrix();
		gl.glTranslated(0, .5, 1);
		Util.drawCuboid(gl, 1, 1, 1);
		gl.glPopMatrix();
		dice.disable(gl);
		gl.glDisable(GL_TEXTURE_2D);
	}
	
	private void lightSetup() {
		gl.glLightfv(GL_LIGHT0, GL_AMBIENT, new float[] { .2f, .2f, .2f, 0 }, 0);
		gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[] { 1, 1, 1, 1 }, 0);
		gl.glLightfv(GL_LIGHT0, GL_SPECULAR, new float[] { 1, 1, 1, 1 }, 0);
		
		gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[] { 3, 3, 3, 1 }, 0);
		gl.glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, new float[] { -1, -2f, -1 }, 0);
		gl.glLightfv(GL_LIGHT0, GL_SPOT_CUTOFF, new float[] { 45 }, 0);
		gl.glLightfv(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, new float[] { .01f }, 0);
	}
	
	private void plasticMaterialSetup() {
		float[] no_mat = { 0.0f, 0.0f, 0.0f, 1.0f };
		
		gl.glMaterialfv(GL_FRONT, GL_AMBIENT, no_mat, 0);
		gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, new float[] { .1f, .5f, .8f, 1 }, 0);
		gl.glMaterialfv(GL_FRONT, GL_SPECULAR, new float[] { 1, 1, 1, 1 }, 0);
		gl.glMaterialfv(GL_FRONT, GL_SHININESS, new float[] { 80 }, 0);
		gl.glMaterialfv(GL_FRONT, GL_EMISSION, no_mat, 0);
	}
	
	private void glowMaterialSetup() {
		float[] no_mat = { 0.0f, 0.0f, 0.0f, 1.0f };
		
		gl.glMaterialfv(GL_FRONT, GL_AMBIENT, new float[] { .2f, .6f, .2f, 1 }, 0);
		gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, new float[] { .1f, .5f, .3f, 1 }, 0);
		gl.glMaterialfv(GL_FRONT, GL_SPECULAR, no_mat, 0);
		gl.glMaterialfv(GL_FRONT, GL_SHININESS, new float[] { 80.0f }, 0);
		gl.glMaterialfv(GL_FRONT, GL_EMISSION, no_mat, 0);
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		gl = drawable.getGL().getGL2();
		
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(90, (double)w / h, .001, 200);
	}
	
	public void dispose(GLAutoDrawable drawable) {}
}

