package perspective;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.GL2GL3.GL_LINE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.DoubleBuffer;
import java.util.EnumMap;
import java.util.Map;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;

public class Chooser extends JFrame {
	private static final long serialVersionUID = 3759212296619829724L;
	
	static final Perspective DEFAULT_PERSPECTIVE = Perspective.CAVALIER;
	static enum Perspective { CAVALIER, CABINET }
	static final Map<Perspective, DoubleBuffer> perspectiveMatrices = new EnumMap<>(Perspective.class);
	static {
		double radA = toRadians(45);
		perspectiveMatrices.put(Perspective.CAVALIER, DoubleBuffer.wrap(new double[] {
			1, 0, -cos(radA), 0,
			0, 1, -sin(radA), 0,
			0, 0, 1         , 0,
			0, 0, 0         , 1,
		}));
		perspectiveMatrices.put(Perspective.CABINET, DoubleBuffer.wrap(new double[] {
			1, 0, -cos(radA)/2, 0,
			0, 1, -sin(radA)/2, 0,
			0, 0, 1           , 0,
			0, 0, 0           , 1,
		}));
	}
	
	Perspective perspective;
	boolean solid;
	
	public Chooser() {
		super("Perspective Chooser");
		perspective = DEFAULT_PERSPECTIVE;
		solid = true;
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (Exception e) {}
		
		setLayout(new BorderLayout());
		
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		final GLCanvas canvas = new GLCanvas(caps);
		canvas.addGLEventListener(new Renderer());
		add(canvas, BorderLayout.CENTER);
		
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout());
		add(pane, BorderLayout.PAGE_END);
		
		ButtonGroup persChoices = new ButtonGroup();
		
		for (final Perspective pers : Perspective.values()) {
			JRadioButton cavalierRadio = new JRadioButton(pers.toString().toLowerCase(), perspective == DEFAULT_PERSPECTIVE);
			
			persChoices.add(cavalierRadio);
			pane.add(cavalierRadio);
			
			cavalierRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					perspective = pers;
					
					canvas.reshape(0, 0, canvas.getWidth(), canvas.getHeight());
					canvas.display();
				}
			});
		}
		
		final JCheckBox solidCheckbox = new JCheckBox("solid", solid);
		pane.add(solidCheckbox);
		
		solidCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				solid = solidCheckbox.isSelected();
				
				canvas.display();
			}
		});
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		canvas.setSize(500, 500);
		pack();
	}
	
	class Renderer implements GLEventListener {
		public void init(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			
			Color bgColor = UIManager.getColor("Panel.background");
			gl.glClearColor(bgColor.getRed() / 255f, bgColor.getGreen() / 255f, bgColor.getBlue() / 255f, 1);
			
			gl.glEnable(GL_DEPTH_TEST);
			
			gl.glEnable(GL_LINE_SMOOTH);
			gl.glEnable(GL_BLEND);
			gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		}
		
		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			GL2 gl = drawable.getGL().getGL2();
			
			double aspect = (double) width / height;
			
			gl.glMatrixMode(GL_PROJECTION);
			gl.glLoadIdentity();
			
			int halfWidth = 1;
			gl.glOrtho(-halfWidth*aspect, halfWidth*aspect, -halfWidth, halfWidth, -halfWidth, halfWidth);
			
			gl.glMultTransposeMatrixd(perspectiveMatrices.get(perspective));
		}
		
		public void display(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glPolygonMode(GL_FRONT_AND_BACK, (solid) ? GL_FILL : GL_LINE);
			
			gl.glMatrixMode(GL_MODELVIEW);
			gl.glLoadIdentity();
			
			gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			drawCoordinateSystem(gl);
			gl.glColor3d(0, 0, 1);
			drawCuboid(gl, 1, 1, 1);
		}
		
		public void dispose(GLAutoDrawable drawable) {}
		
		void drawCoordinateSystem(GL2 gl) {
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
		
		void drawCuboid(GL2 gl, double w, double h, double d) {
			gl.glBegin(GL_QUAD_STRIP);
			gl.glVertex3d(-w / 2,  h / 2, -d / 2);
			gl.glVertex3d( w / 2,  h / 2, -d / 2);
			gl.glVertex3d(-w / 2,  h / 2,  d / 2);
			gl.glVertex3d( w / 2,  h / 2,  d / 2);
			gl.glVertex3d(-w / 2, -h / 2,  d / 2);
			gl.glVertex3d( w / 2, -h / 2,  d / 2);
			gl.glVertex3d(-w / 2, -h / 2, -d / 2);
			gl.glVertex3d( w / 2, -h / 2, -d / 2);
			gl.glVertex3d(-w / 2,  h / 2, -d / 2);
			gl.glVertex3d( w / 2,  h / 2, -d / 2);
			gl.glEnd();
			
			gl.glBegin(GL_QUADS);
			gl.glVertex3d(-w / 2,  h / 2, -d / 2);
			gl.glVertex3d(-w / 2,  h / 2,  d / 2);
			gl.glVertex3d(-w / 2, -h / 2,  d / 2);
			gl.glVertex3d(-w / 2, -h / 2, -d / 2);
			
			gl.glVertex3d(w / 2,  h / 2, -d / 2);
			gl.glVertex3d(w / 2,  h / 2,  d / 2);
			gl.glVertex3d(w / 2, -h / 2,  d / 2);
			gl.glVertex3d(w / 2, -h / 2, -d / 2);
			gl.glEnd();
		}
	}
	
	public static void main(String[] args) {
		new Chooser();
	}
}
