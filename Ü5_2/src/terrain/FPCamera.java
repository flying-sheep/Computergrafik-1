package terrain;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.FPSAnimator;

public class FPCamera {
	private static final int FORWARD   = KeyEvent.VK_W;
	private static final int BACKWARD  = KeyEvent.VK_S;
	private static final int LEFTWARD  = KeyEvent.VK_A;
	private static final int RIGHTWARD = KeyEvent.VK_D;
	private static final int FAST = KeyEvent.VK_SHIFT;
	private static final int SLOW = KeyEvent.VK_ALT;
	
	private static final double MOUSESPEED = 1;
	private static final double KEYSPEED   = 3;
	private static final float ROTV_MAX =  1;
	private static final float ROTV_MIN = -1;
	
	private final Set<Integer> keys = new HashSet<Integer>();
	private final float[] modelview = new float[16];
	
	private Robot robot;
	private boolean grabbed;
	private float rotX, rotY, rotZ, rotV;
	private float posX, posY, posZ;
	private long lastTime = -1;
	private GLCanvas canvas;
	
	public FPCamera(GLCanvas canvas) {
		// we need the robot to get full control over the mouse
		try {
			robot = new Robot();
		} catch (final AWTException e) {
			e.printStackTrace();
			robot = null;
		}
		
		for (int i=0; i<4; i++)
			modelview[i * 5] = 1;
		
		this.canvas = canvas;
		
		canvas.addKeyListener(new KeyListener());
		canvas.addMouseMotionListener(new MouseMotionListener());
		canvas.addFocusListener(new FocusListener()); //TODO: MouseGrabManager
		
		FPSAnimator animator = new FPSAnimator(canvas, 60);
		animator.start();
	}
	
	private void calcPosition() {
		if (lastTime == -1)
			lastTime = System.nanoTime();
		
		double mul = (keys.contains(SLOW)) ? .1 : (keys.contains(FAST)) ? 3 : 1;
		
		final double speed = KEYSPEED * -((lastTime - (lastTime = System.nanoTime())) / 10E7) * mul;
		
		final double srys = sin(rotY) * speed;
		final double crys = cos(rotY) * speed;
		final double rvs  =     rotV  * speed;
		
		if (keys.contains(FORWARD)) {
			posX -= srys;
			posZ += crys;
			posY += rvs;
		}
		
		if (keys.contains(BACKWARD)) {
			posX += srys;
			posZ -= crys;
			posY -= rvs;
		}
		
		if (keys.contains(LEFTWARD)) {
			posX += crys;
			posZ += srys;
		}
		
		if (keys.contains(RIGHTWARD)) {
			posX -= crys;
			posZ -= srys;
		}
	}
	
	public float[] calcModelView() {
		calcPosition();
		
		final float sinX = (float) sin(rotX);
		final float sinY = (float) sin(rotY);
		final float sinZ = (float) sin(rotZ);
		
		final float cosX = (float) cos(rotX);
		final float cosY = (float) cos(rotY);
		final float cosZ = (float) cos(rotZ);
		
		modelview[0] = cosY * cosZ + sinY * sinX * sinZ;
		modelview[1] = cosX * sinZ;
		modelview[2] = -sinY * cosZ + cosY * sinX * sinZ;
		modelview[4] = -cosY * sinZ + sinY * sinX * cosZ;
		modelview[5] = cosX * cosZ;
		modelview[6] = sinY * sinZ + cosY * sinX * cosZ;
		modelview[8] = sinY * cosX;
		modelview[9] = -sinX;
		modelview[10] = cosY * cosX;
		
		modelview[12] = modelview[0] * posX + modelview[4] * posY + modelview[8] * posZ;
		modelview[13] = modelview[1] * posX + modelview[5] * posY + modelview[9] * posZ;
		modelview[14] = modelview[2] * posX + modelview[6] * posY + modelview[10] * posZ;
		
		return modelview;
	}
	
	public void setPosition(float x, float y, float z) {
		posX = x;
		posY = y;
		posZ = z;
	}
	
	private class MouseMotionListener implements java.awt.event.MouseMotionListener {
		public void mouseMoved(final MouseEvent e) {
			if (grabbed) {
				final Point pos = canvas.getLocationOnScreen();
				final int centerX = pos.x + canvas.getWidth()  / 2;
				final int centerY = pos.y + canvas.getHeight() / 2;
				
				rotY -= (centerX - e.getXOnScreen()) / 1000.0 * MOUSESPEED;
				rotV -= (centerY - e.getYOnScreen()) / 1000.0 * MOUSESPEED;
				
				if (rotV > ROTV_MAX) rotV = ROTV_MAX;
				
				if (rotV < ROTV_MIN) rotV = ROTV_MIN;
				
				rotX = (float) cos(rotY) * rotV;
				rotZ = (float) sin(rotY) * rotV;
				
				if (robot != null)
					robot.mouseMove(centerX, centerY);
			}
		}
		
		public void mouseDragged(final MouseEvent e) {
			mouseMoved(e);
		}
	}
	
	private class KeyListener implements java.awt.event.KeyListener {
		public void keyPressed(final KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_ESCAPE:
				canvas.setFocusable(false);
				canvas.setFocusable(true);
			default:
				keys.add(keyCode);
			}
		}
		
		public void keyReleased(final KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			default:
				keys.remove(keyCode);
			}
		}
		
		public void keyTyped(final KeyEvent e) {}
	}
	
	/** Used to grab and release the mouse according to the focus of the canvas. <br>
	 * Grabbing means setting {@link grabbed} to <code>true</code> and hiding the Cursor.<br>
	 * Defocusing is handled by the {@link KeyListener}. */
	private class FocusListener implements java.awt.event.FocusListener {
		private final BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		private final Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		
		public void focusGained(FocusEvent e) {
			e.getComponent().setCursor(blankCursor);
			grabbed = true;
		}
		
		public void focusLost(FocusEvent e) {
			e.getComponent().setCursor(Cursor.getDefaultCursor());
			grabbed = false;
		}
	}
}