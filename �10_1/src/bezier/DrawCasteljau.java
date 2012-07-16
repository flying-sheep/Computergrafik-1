package bezier;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static javax.media.opengl.GL2.*;

import java.awt.geom.Point2D;

import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;

@SuppressWarnings("serial")
public class DrawCasteljau extends JFrame {
	GLCanvas canvas;
	SceneView sv;
	
	public DrawCasteljau() {
		sv = new SceneView();
		
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		canvas = new GLCanvas(caps);
		
		canvas.addGLEventListener(sv);
		
		add(canvas);
		
		setTitle("Casteljau");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setVisible(true);
	}
	
	class SceneView implements GLEventListener {
		Casteljau casteljau;
		
		public SceneView() {
			Point2D.Double[] ctrlPoints = new Point2D.Double[] {
				new Point2D.Double(3, -2),
				new Point2D.Double(1, 3),
				new Point2D.Double(10, 4),
				new Point2D.Double(8, -5),
			};
			
			casteljau = new Casteljau(ctrlPoints, 50);
		}
		
		public void init(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			
			gl.glEnable(GL_LINE_SMOOTH);
			gl.glEnable(GL_BLEND);
			gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
			gl.glClearColor(1, 1, 1, 0);
		}
		
		public void display(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glClear(GL_COLOR_BUFFER_BIT);
			
			gl.glColor3d(0, 0, 1);
			gl.glBegin(GL_LINE_STRIP);
			for (int i = 0; i < casteljau.curvePoints.length; i++)
				gl.glVertex3d(casteljau.curvePoints[i].x, casteljau.curvePoints[i].y, 0);
			gl.glEnd();
			
			gl.glColor3d(1, 0, 0);
			gl.glPointSize(5.0f);
			gl.glBegin(GL_POINTS);
			for (int i = 0; i < casteljau.ctrlPoints.length; i++)
				gl.glVertex3d(casteljau.ctrlPoints[i].x, casteljau.ctrlPoints[i].y, 0);
			gl.glEnd();
			
			gl.glBegin(GL_LINE_STRIP);
			for (int i = 0; i < casteljau.ctrlPoints.length; i++)
				gl.glVertex3d(casteljau.ctrlPoints[i].x, casteljau.ctrlPoints[i].y, 0);
			gl.glEnd();
		}
		
		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glViewport(max(0, (w - h) / 2), max(0, (h - w) / 2), min(w, h), min(w, h));
			gl.glMatrixMode(GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrtho(-1, 15, -8, 8, -1, 100);
		}
		
		public void dispose(GLAutoDrawable drawable) {}
	}
	
	public static void main(String args[]) {
		new DrawCasteljau();
	}
}
