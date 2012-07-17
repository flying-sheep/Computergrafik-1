package bezier;

import static java.lang.Math.PI;
import static javax.media.opengl.GL2.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import gl.Util;

@SuppressWarnings("serial")
public class AnimationRenderer extends JFrame {
	GLCanvas canvas;
	SceneView sv;
	FPSAnimator animator;
	
	public AnimationRenderer() {
		sv = new SceneView();
		
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		canvas = new GLCanvas(caps);
		
		canvas.addGLEventListener(sv);
		add(canvas);
		
		animator = new FPSAnimator(canvas, 60);
		animator.start();
		
		setTitle("Casteljau");
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
		Casteljau c;
		GLU glu;
		int s;
		
		public SceneView() {
			glu = new GLU();
			s = 0;
			
			Point2D.Double[] ctrlPoints = new Point2D.Double[] {
				new Point2D.Double(3, -2),
				new Point2D.Double(1, 3),
				new Point2D.Double(10, 4),
				new Point2D.Double(8, -5),
			};
			
			c = new Casteljau(ctrlPoints, 100);
		}
		
		public void init(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			
			gl.glEnable(GL_DEPTH_TEST);
			
			gl.glClearColor(1, 1, 1, 0);
		}
		
		public void display(GLAutoDrawable drawable) {
			update();
			render(drawable);
		}
		
		private void update() {
			s++;
			s %= c.steps;
		}
		
		private void render(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			gl.glMatrixMode(GL_MODELVIEW);
			gl.glLoadIdentity();
			glu.gluLookAt(6, 3, -5, 6, 0, -3, 0, 1, 0);
			
			gl.glColor3d(0, 0, 1);
			gl.glBegin(GL_LINE_STRIP);
			for (int i = 0; i < c.curvePoints.length; i++)
				gl.glVertex3d(c.curvePoints[i].x, 0, c.curvePoints[i].y);
			gl.glEnd();
			
			gl.glColor3d(1, 0, 0);
			
			gl.glPushMatrix();
			Point2D.Double subtracted = new Point2D.Double(
				c.curvePoints[(s + 1) % c.steps].x - c.curvePoints[s].x,
				c.curvePoints[(s + 1) % c.steps].y - c.curvePoints[s].y);
			
			gl.glTranslated(c.curvePoints[s].x, 0, c.curvePoints[s].y);
			gl.glRotated(Math.atan2(subtracted.x, subtracted.y) * 180 / PI, 0, 1, 0);
			
			Util.drawCuboid(gl, 1, 1, 1);
			gl.glPopMatrix();
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
		new AnimationRenderer();
	}
}
