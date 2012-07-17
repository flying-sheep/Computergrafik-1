package particle;

import static javax.media.opengl.GL2.*;
import gl.Util;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

@SuppressWarnings("serial")
public class ParticleRenderer extends JFrame {
	GLCanvas canvas;
	SceneView sv;
	FPSAnimator animator;
	
	public ParticleRenderer() {
		sv = new SceneView();
		
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		canvas = new GLCanvas(caps);
		
		canvas.addGLEventListener(sv);
		add(canvas);
		
		animator = new FPSAnimator(canvas, 60);
		animator.start();
		
		setTitle("Particles");
		setSize(500, 500);
		setVisible(true);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				animator.stop();
				dispose();
			}
		});
	}
	
	class SceneView implements GLEventListener {
		static final double PARTICLE_SIZE = .1;
		static final int MAX_PARTICLES = 100;
		
		List<Particle> particles;
		GLU glu;
		
		public SceneView() {
			glu = new GLU();
			particles = new ArrayList<>(MAX_PARTICLES);
		}
		
		public void init(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			
			gl.glEnable(GL_DEPTH_TEST);
			gl.glEnable(GL_BLEND);
			gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
			gl.glClearColor(0, 0, 0, 0);
		}
		
		public void display(GLAutoDrawable drawable) {
			update();
			render(drawable);
		}
		
		private void update() {
			final Vector3D origin = new Vector3D(0, 0, 0);
			if (particles.size() < MAX_PARTICLES)
				particles.add(new Particle(origin, randomMomentum(), randomColor()));
			
			for (Particle p : particles)
				p.tick();
		}
		
		private GLColor randomColor() {
			return new GLColor(
				(float) Math.random(),
				(float) Math.random(),
				(float) Math.random());
		}
		
		private Vector3D randomMomentum() {
			return new Vector3D(
				(Math.random() - .5) * .1,
				Math.random() * .1,
				(Math.random() - .5) * .1);
		}
		
		private void render(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			gl.glMatrixMode(GL_MODELVIEW);
			gl.glLoadIdentity();
			glu.gluLookAt(8, 5, 3, 0, 0, 0, 0, 1, 0);
			
			Util.drawGrid(gl, 10);
			
			gl.glColor3d(1, 0, 0);
			
			gl.glPushMatrix();
			gl.glTranslated(0, -1, 0);
			gl.glRotated(-90, 1, 0, 0);
			glu.gluCylinder(glu.gluNewQuadric(), 1, 0, 1, 10, 1);
			gl.glPopMatrix();
			
			double hs = PARTICLE_SIZE / 2.;
			
			for (Particle p : particles) {
				p.color.activate(gl);
				
				gl.glBegin(GL_TRIANGLE_STRIP);
				gl.glVertex3d(p.pos.x, p.pos.y+hs, p.pos.z-hs);
				gl.glVertex3d(p.pos.x, p.pos.y+hs, p.pos.z+hs);
				gl.glVertex3d(p.pos.x, p.pos.y-hs, p.pos.z-hs);
				gl.glVertex3d(p.pos.x, p.pos.y-hs, p.pos.z+hs);
				gl.glEnd();
			}
		}
		
		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glViewport(0, 0, w, h);
			
			gl.glMatrixMode(GL_PROJECTION);
			gl.glLoadIdentity();
			
			glu.gluPerspective(90, (double)w / h, .001, 200);
		}
		
		public void dispose(GLAutoDrawable drawable) {}
	}
	
	public static void main(String args[]) {
		new ParticleRenderer();
	}
}
