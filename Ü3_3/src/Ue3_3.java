import java.awt.Frame;
import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;


public class Ue3_3 extends Frame {
	public static void main(String[] args) {
		new Ue3_3();
	}
	
	public Ue3_3() {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		
		GLCanvas canvas = new GLCanvas(caps);
		
		SceneView sv = new SceneView();
		canvas.addGLEventListener(sv);
		canvas.addKeyListener(sv);
		
		add(canvas);
		
		setTitle("Frame for Uebung3_3");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		setSize(500, 500);
		setVisible(true);
	}
	
	class SceneView implements GLEventListener, KeyListener {
		private GL2 gl;
		
		public void init(GLAutoDrawable drawable) {
			gl = drawable.getGL().getGL2();
			
			gl.glClearColor(0, 0, 0, 1);
			
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(-10, 10, -10, 10, 0, 100);
			
			gl.glEnable(GL2.GL_LINE_SMOOTH);
			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			
			display(drawable);
		}
		
		public void display(GLAutoDrawable drawable) {
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
			
			drawGrid();
			drawCoordinateSystem();
			drawRobot(0, 0, 0);
			
			GLU glu = new GLU();
			glu.gluLookAt(4, 5, 6, 0, 0, 0, 0, 1,  0);
		}

		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
			gl.glViewport(
				Math.max(0, (w-h)/2),
				Math.max(0, (h-w)/2),
				Math.min(w, h),
				Math.min(w, h));
		}
		
		public void dispose(GLAutoDrawable drawable) {}
		
		public void keyPressed(KeyEvent arg0) {}
		
		public void keyReleased(KeyEvent arg0) {}
		
		public void keyTyped(KeyEvent arg0) {}
		
		void drawCoordinateSystem() {
			gl.glBegin(GL2.GL_LINES);
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
		
		void drawGrid() {
			gl.glBegin(GL2.GL_LINES);
			
			gl.glColor3d(.3f, .3f, .3f);
			
			for (int c=-5; c<=5; c++) {
				gl.glVertex3i( c, 0, -5);
				gl.glVertex3i( c, 0,  5);
				gl.glVertex3i(-5, 0,  c);
				gl.glVertex3i( 5, 0,  c);
			}
			
			gl.glEnd();
		}
		
		void drawRobot(int x, int y, int z) {
			gl.glPushMatrix();
			gl.glTranslated(x, y, z);
			new RobotDrawer(gl, 0, 45).draw();
			gl.glPopMatrix();
		}
	}
}
