/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sphere.zbuffer;

import java.awt.Dimension;
import java.awt.Frame;
import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.*;
import javax.media.opengl.glu.GLUquadric;
import window.view.AbstractVueGLCanvas;

/**
 * Creation
 * @author Tristan
 */
public final class Creation extends AbstractVueGLCanvas implements GLEventListener {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    private Frame _frame;
    private float[] _bufferTab;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public Creation(String path, int sens, int width, int height) {
        
        this._path = path;
        this._sens = sens;
        this._width = width;
        this._height = height;
        
        // Permet d'afficher l'image
        this._frame = new Frame("JOGL");
        this._frame.add(this);
        this._frame.setResizable(false);
        this._frame.setLocationRelativeTo(null);
        this._frame.setSize(new Dimension(width,height));
        this._frame.setVisible(true);
        
    } // Creation(String path, int sens, int width, int height)
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Permet de recuperer le tableau de profondeur de l'image 3D cree
     * @return le tableau des valeurs de profondeur de l'image 3D
     */
    public float[] getCreationBufferTab() {
        
        return this._bufferTab;
        
    } // getCreationBufferTab()
    
    
    /**
     * Permet d'afficher ou non l'image 3D cree
     * @param visible TRUE si on veut afficher l'image, FALSE sinon
     */
    public void afficherCreation(boolean visible) {
        
        if(visible) {
            
            this._frame.setVisible(true);
            
        } else {
            
            this._frame.dispose();
            
        }
        
    } // afficherCreation(boolean visible)
    
    
    ///////////////////////////// OPEN-GL ///////////////////////////////
    
    
     @Override
    public void init(GLAutoDrawable glDrawable) {
        
        this._gl = glDrawable.getGL().getGL2();
        this._gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        this._gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this._gl.glClearDepth(1.0f);
        this._gl.glEnable(GL.GL_DEPTH_TEST);
        this._gl.glDepthFunc(GL.GL_LEQUAL);
        this._gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        
        // Preparation du z-buffer
        this.initializeZBuffer(this._gl);
        
    } // init(GLAutoDrawable glad)
    
    
    @Override
    public void display(GLAutoDrawable gLDrawable) {
        
        this._gl = gLDrawable.getGL().getGL2();
        this._gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        this._gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        this._gl.glLoadIdentity();
        this._gl.glTranslatef(0.0f, 0.0f, -5.0f);
        
        // Rotate on the three axis
        /*this._gl.glRotatef(this._rotateT, 1.0f, 0.0f, 0.0f);
        this._gl.glRotatef(this._rotateT, 0.0f, 1.0f, 0.0f);
        this._gl.glRotatef(this._rotateT, 0.0f, 0.0f, 1.0f);*/
        
        // Gestion de l'eclairage
        this.setLight();

        // Draw sphere 
        /*GLUquadric qobj0 = this._glu.gluNewQuadric();
    	this._gl.glPushMatrix();
        this._gl.glColor3f(1, 1, 1);
        this._gl.glTranslatef(1f, 0f, 0f);
    	this._glu.gluSphere(qobj0, 1.f, 100, 100);
    	this._gl.glPopMatrix();
        
        GLUquadric qobj1 = this._glu.gluNewQuadric();
    	this._gl.glPushMatrix();
        this._gl.glColor3f(1, 1, 1);
        this._gl.glTranslatef(1.5f, 1.5f, -1.f);
    	this._glu.gluSphere(qobj1, 1.f, 100, 100);
    	this._gl.glPopMatrix();
        
        GLUquadric qobj2 = this._glu.gluNewQuadric();
    	this._gl.glPushMatrix();
        this._gl.glColor3f(1, 1, 1);
        this._gl.glTranslatef(0f, 0f, -3.f);
    	this._glu.gluSphere(qobj2, 3.f, 100, 100);
    	this._gl.glPopMatrix();*/
        
        // Draw triangle
        /*GLUquadric qobj2 = this._glu.gluNewQuadric();
        this._gl.glPushMatrix();
        this._gl.glColor3f(1, 1, 1);
        this._gl.glBegin(GL.GL_TRIANGLE_FAN);
        this._gl.glVertex3f(-1, -1, 0);
        this._gl.glVertex3f(1, -1, -1);
        this._gl.glVertex3f(0, 1, 0);*/
        
        // Draw cube
        this._gl.glBegin(GL2.GL_QUADS);
        // front : blue
        this._gl.glColor3f(0.0f, 0.0f, 1.0f);
        this._gl.glVertex3f(-1f, +1f, +1f);
        this._gl.glVertex3f(-1f, -1f, +1f);
        this._gl.glVertex3f(+1f, -1f, +1f);
        this._gl.glVertex3f(+1f, +1f, +1f);
        // back : green
        this._gl.glColor3f(0.0f, 1.0f, 0.0f);
        this._gl.glVertex3f(+1f, -1f, -1f);
        this._gl.glVertex3f(+1f, +1f, -1f);
        this._gl.glVertex3f(-1f, +1f, -1f);
        this._gl.glVertex3f(-1f, -1f, -1f);
        // left : red
        this._gl.glColor3f(1.0f, 0.0f, 0.0f);
        this._gl.glVertex3f(-1f, +1f, +1f);
        this._gl.glVertex3f(-1f, +1f, -1f);
        this._gl.glVertex3f(-1f, -1f, -1f);
        this._gl.glVertex3f(-1f, -1f, +1f);
        // right : orange
        this._gl.glColor3f(1.0f, 0.4f, 0.0f);
        this._gl.glVertex3f(+1f, +1f, -1f);
        this._gl.glVertex3f(+1f, +1f, +1f);
        this._gl.glVertex3f(+1f, -1f, +1f);
        this._gl.glVertex3f(+1f, -1f, -1f);
        // top : white
        this._gl.glColor3f(1.0f, 1.0f, 1.0f);
        this._gl.glVertex3f(+1f, +1f, +1f);
        this._gl.glVertex3f(-1f, +1f, +1f);
        this._gl.glVertex3f(-1f, +1f, -1f);
        this._gl.glVertex3f(+1f, +1f, -1f);
        // bottom : yellow
        this._gl.glColor3f(1.0f, 1.0f, 0.0f);
        this._gl.glVertex3f(+1f, -1f, +1f);
        this._gl.glVertex3f(+1f, -1f, -1f);
        this._gl.glVertex3f(-1f, -1f, -1f);
        this._gl.glVertex3f(-1f, -1f, +1f);
        
        // Done Drawing 
        this._gl.glEnd();                                             

        // increasing rotation for the next iteration                   
        //this._rotateT += 0.2f; 
        
        // Dessine l'image de z-buffer
        if (this._test) {
            
            this._bufferTab = this.saveCreationZBuffer(gLDrawable);
            
        }
       
    } // display(GLAutoDrawable glad)

    
    @Override
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
        
        this._gl = gLDrawable.getGL().getGL2();
        final float aspect = (float) width / (float) height;
        this._gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        this._gl.glLoadIdentity();
        final float fh = 0.5f;
        final float fw = fh * aspect;
        this._gl.glFrustumf(-fw, fw, -fh, fh, 1.0f, 1000.0f);
        this._gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        this._gl.glLoadIdentity();
      
    } // reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3)
    

    @Override
    public void dispose(GLAutoDrawable gLDrawable) {
       
    } // dispose(GLAutoDrawable glad)
    
    
} // class Creation
