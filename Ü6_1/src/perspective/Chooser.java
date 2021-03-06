package perspective;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import static javax.media.opengl.GL2.*;

import gl.Util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;

public class Chooser extends JFrame {
	private static final long serialVersionUID = 3759212296619829724L;
	
	static final Perspective DEFAULT_PERSPECTIVE = Perspective.CAVALIER;
	static enum Perspective {
		CAVALIER(135, 1),
		CABINET(30, .5);
		
		private final double[] matrix;
		
		private Perspective(int angle, double ratio) {
			double radA = toRadians(angle);
			double c = -cos(radA) * ratio;
			double s = -sin(radA) * ratio;
			
			this.matrix = new double[] {
				1, 0, c, 0,
				0, 1, s, 0,
				0, 0, 1, 0,
				0, 0, 0, 1,
			};
		}
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
			int r = bgColor.getRed(),
				g = bgColor.getGreen(),
				b = bgColor.getBlue();
			gl.glClearColor(r/255f, g/255f, b/255f, 1);
			
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
			gl.glOrtho(
				-halfWidth * aspect, halfWidth * aspect,
				-halfWidth, halfWidth,
				-halfWidth, halfWidth);
			
			gl.glMultTransposeMatrixd(perspective.matrix, 0);
		}
		
		public void display(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glPolygonMode(GL_FRONT_AND_BACK, (solid) ? GL_FILL : GL_LINE);
			
			gl.glMatrixMode(GL_MODELVIEW);
			gl.glLoadIdentity();
			
			gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			Util.drawCoordinateSystem(gl);
			gl.glColor3d(0, 0, 1);
			Util.drawCuboid(gl, 1, 1, 1);
		}
		
		public void dispose(GLAutoDrawable drawable) {}
	}
	
	public static void main(String[] args) {
		new Chooser();
	}
}
