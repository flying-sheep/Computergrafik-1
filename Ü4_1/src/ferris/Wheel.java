package ferris;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import gl.Util;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.FPSAnimator;

class Wheel implements GLEventListener {
	static GLU glu = new GLU();
	GL2 gl;
	int angle;
	
	static class Measures {
		static int CARRIAGES = 7;
		static double CARRIAGE_WIDTH = 1;
		static double RADIUS = 5;
		static double HEIGHT = RADIUS + CARRIAGE_WIDTH;
		static double DISTANCE = RADIUS * 2;
		static double SPOKE_THICKNESS = .2;
		static double LEG_WIDTH = 1;
	}
	
	public static void main(String[] args) {
		new Wheel();
	}
	
	Wheel() {
		angle = 0;
		
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		caps.setSampleBuffers(true);
		caps.setNumSamples(4);
		
		GLCanvas canvas = new GLCanvas(caps);
		
		canvas.addGLEventListener(this);
		
		Frame frame = new Frame("Ferris Wheel");
		frame.add(canvas);
		frame.setSize(500, 500);
		frame.setVisible(true);
		
		final FPSAnimator animator = new FPSAnimator(canvas, 60);
		animator.start();
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						animator.stop();
						System.exit(0);
					}
				}).start();
			}
		});
	}
	
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		
		gl.glClearColor(0, 0, 0, 1);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
	}
	
	public void display(GLAutoDrawable drawable) {
		update();
		render(drawable);
	}
	
	void update() {
		angle++;
		angle %= 360;
	}
	
	void render(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		Util.drawGrid(gl, 10);
		Util.drawCoordinateSystem(gl);
		drawWheel();
		drawPiles();
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		final double ang = toRadians(angle);
		glu.gluLookAt(Measures.DISTANCE * cos(ang), Measures.HEIGHT / 2, Measures.DISTANCE * sin(ang), 0, Measures.HEIGHT/3*2, 0, 0, 1, 0);
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		gl = drawable.getGL().getGL2();
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(90, (double)w / h, .001, 100);
	}
	
	public void dispose(GLAutoDrawable drawable) {}
	
	void drawWheel() {
		gl.glPushMatrix();
		
		gl.glTranslated(0, Measures.HEIGHT, 0);
		
		for (int n = 0; n < Measures.CARRIAGES; n++) {
			gl.glPushMatrix();
			
			final double ang = toRadians((angle + 360 * n / Measures.CARRIAGES) % 360);
			gl.glTranslated(cos(ang) * Measures.RADIUS, sin(ang) * Measures.RADIUS - Measures.CARRIAGE_WIDTH / 2, 0);
			
			drawCarriage();
			
			gl.glPopMatrix();
		}
		
		final double zoff = (Measures.CARRIAGE_WIDTH + Measures.SPOKE_THICKNESS) / 2;
		
		for (int f : new int[] { -1, 1 }) {
			gl.glPushMatrix();
			
			gl.glTranslated(0, 0, f * zoff);
			
			for (int n = 0; n < Measures.CARRIAGES; n++) {
				gl.glPushMatrix();
				
				gl.glRotated((angle + 90 + 360 * n / Measures.CARRIAGES) % 360, 0, 0, 1);
				gl.glTranslated(0, -Measures.RADIUS / 2, 0);
				drawSpoke();
				
				gl.glPopMatrix();
			}
			
			gl.glPopMatrix();
		}
		
		gl.glPopMatrix();
	}
	
	void drawCarriage() {
		gl.glColor3d(1, 0, 0);
		Util.drawCuboid(gl, Measures.CARRIAGE_WIDTH, Measures.CARRIAGE_WIDTH, Measures.CARRIAGE_WIDTH);
	}
	
	void drawSpoke() {
		gl.glColor3d(1, 1, 0);
		Util.drawCuboid(gl, Measures.SPOKE_THICKNESS, Measures.RADIUS, Measures.SPOKE_THICKNESS);
	}
	
	void drawPiles() {
		final double zoff = (Measures.CARRIAGE_WIDTH + Measures.LEG_WIDTH) / 2 + Measures.SPOKE_THICKNESS;
		
		for (int f : new int[] { -1, 1 }) {
			gl.glPushMatrix();
			
			gl.glTranslated(0, 0, f * zoff);
			drawPilePair();
			
			gl.glPopMatrix();
		}
	}
	
	void drawPilePair() {
		for (double s : new double[] { -.5, .5 }) {
			gl.glPushMatrix();
			
			gl.glTranslated(0, Measures.HEIGHT, 0);
			gl.glMultMatrixd(new double[] {
				1, 0, 0, 0,
				s, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1,
			}, 0);
			gl.glTranslated(0, -Measures.HEIGHT / 2, 0);
			drawPile();
			
			gl.glPopMatrix();
		}
	}
	
	void drawPile() {
		gl.glColor3d(0, 1, 0);
		Util.drawCuboid(gl, Measures.LEG_WIDTH, Measures.HEIGHT, Measures.LEG_WIDTH);
	}
}
