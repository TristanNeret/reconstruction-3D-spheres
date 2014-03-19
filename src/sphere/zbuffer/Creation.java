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
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public Creation(String path, int sens, int width, int height) {
        
        this._path = path;
        this._sens = sens;
        this._width = width;
        this._height = height;
        
        // Permet d'afficher l'image
        Frame frame = new Frame("JOGL");
        frame.add(this);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setSize(new Dimension(width,height));
        frame.setVisible(true);
        
    } // Creation(String path, int sens, int width, int height)
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
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
        
        // Gestion de l'eclairage
        this.setLight();
        
        // Rotate on the three axis
        /*this._gl.glRotatef(this._rotateT, 1.0f, 0.0f, 0.0f);
        this._gl.glRotatef(this._rotateT, 0.0f, 1.0f, 0.0f);
        this._gl.glRotatef(this._rotateT, 0.0f, 0.0f, 1.0f);*/

        // Draw sphere 
        GLUquadric qobj0 = this._glu.gluNewQuadric();
    	this._gl.glPushMatrix();
        this._gl.glColor3f(1, 1, 1);
    	this._glu.gluSphere(qobj0, 1.f, 100, 100);
    	this._gl.glPopMatrix();
        
        GLUquadric qobj1 = this._glu.gluNewQuadric();
    	this._gl.glPushMatrix();
        this._gl.glColor3f(1, 1, 1);
        this._gl.glTranslatef(2.5f, 2.5f, -5.f);
    	this._glu.gluSphere(qobj1, 1.f, 100, 100);
    	this._gl.glPopMatrix();

        // Done Drawing 
        this._gl.glEnd();                                             

        // increasing rotation for the next iteration                   
        this._rotateT += 0.2f; 
        
        // Dessine l'image de z-buffer
        if (this._test) {
         
            this.saveCreationZBufferPNG(gLDrawable);
            
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
