package ersteTests

import java.awt.Frame
import java.awt.event._

import javax.media.opengl.awt.GLCanvas
import javax.media.opengl.fixedfunc.GLMatrixFunc
import javax.media.opengl.glu.GLU
import javax.media.opengl._

import gl.Util

object Uebung2_2 extends App {
	override def main(args: Array[String]) = new Uebung2_2
	
	def primitive(prim: Int)(block: => Unit)(implicit gl: GL2) {
		gl glBegin prim
		block
		gl glEnd
	}
}

class Uebung2_2 extends Frame { frame =>
	val glp = GLProfile.getDefault
	val caps = new GLCapabilities(glp)
	
	val canvas = new GLCanvas(caps)
	
	val sv = new SceneView
	canvas addGLEventListener sv
	canvas addKeyListener sv
	
	frame add canvas
	
	frame setTitle "Frame for Uebung2_2"
	frame addWindowListener new WindowAdapter {
		override def windowClosing(e: WindowEvent) = System exit 0
	}
	frame setSize (500, 500)
	frame setVisible true
	
	import Uebung2_2.primitive
	
	class SceneView extends GLEventListener with KeyListener { scene ⇒
		var view = 4
		var gl: GL2 = _
		
		def init(drawable: GLAutoDrawable) {
			gl = drawable.getGL.getGL2
			gl glClearColor (.5f, .5f, .5f, 1)
			
			gl glMatrixMode GLMatrixFunc.GL_PROJECTION
			gl glLoadIdentity()
			gl glOrthof (-10, 10, -10, 10, 0, 100)
			
			scene display drawable
		}
		
		def display(drawable: GLAutoDrawable) {
			gl glClear(GL.GL_COLOR_BUFFER_BIT) // clear background
			
			Util drawGrid (gl, 10)
			
			Util drawCoordinateSystem gl
			
			scene drawHouse
			
			gl glMatrixMode(GLMatrixFunc.GL_MODELVIEW)
			gl glLoadIdentity
			
			val glu = new GLU
			this.view match {
				case 1 => glu.gluLookAt(0,   0, 10, 0, 0, 0, 0, 1,  0)
				case 2 => glu.gluLookAt(10,  0,  0, 0, 0, 0, 0, 1,  0)
				case 3 => glu.gluLookAt(0,  10,  0, 0, 0, 0, 0, 0, -1)
				case n => glu.gluLookAt(10, 10, 10, 0, 0, 0, 0, 1,  0)
			}
		}
		
		def reshape(drawable: GLAutoDrawable, x: Int, y: Int, w: Int, h: Int) {
			gl glViewport (
				math.max(0, (w - h) / 2),
				math.max(0, (h - w) / 2),
				math.min(w, h),
				math.min(w, h))
		}
		
		def dispose(drawable: GLAutoDrawable) {}
		
		def drawHouse {
			gl glColor3d (0, 1, 0)
			
			for(z ← Array(-2, -5)) {
				primitive(GL.GL_LINE_LOOP) {
					gl glVertex3i (2, 2, z)
					gl glVertex3i (2, 0, z)
					gl glVertex3i (4, 0, z)
					gl glVertex3i (4, 2, z)
					gl glVertex3i (3, 3, z)
					gl glVertex3i (2, 2, z)
					gl glVertex3i (4, 2, z)
				} (scene gl)
			}
			
			primitive(GL.GL_LINES) {
				for (x ← Array(2,4))
					for (y ← Array(2, 0))
						for (z ← Array(-2, -5))
							gl glVertex3i (x, y, z)
				
				gl glVertex3i (3, 3, -2)
				gl glVertex3i (3, 3, -5)
			} (scene gl)
		}
		
		def keyPressed(e: KeyEvent) {
			try {
				scene.view = e.getKeyChar.toString.toInt
				//println(e.getKeyChar)
			} catch {
				case e: NumberFormatException =>
			}
			
			if (scene.view > 4 || scene.view < 1)
				scene.view = 4
			
			canvas.repaint
		}
		
		def keyReleased(e: KeyEvent) {}
		
		def keyTyped(e: KeyEvent) {}
	}
}