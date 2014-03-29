/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.media.opengl.GL;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLUquadric;
import window.main.MainSphere;
import sphere.zbuffer.Lecture;

/**
 * VueFinale
 * @author Tristan
 */
public class VueFinale extends AbstractVueGLCanvas implements Observer {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    
    protected MainSphere _ms;

    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public VueFinale(MainSphere ms, String path) {
        
        this._ms = ms;
        
        // Recuperation des donnees du z-buffer
        Lecture lecture = new Lecture(path);
        this._pixels = lecture.lireImage();
        
        // Mise a jour de la largeur et de la hauteur du rendu final
        this._width = this._pixels.length;
        this._height = this._pixels[0].length;

        // Dimension de la fenetre
        this.setPreferredSize(new Dimension(this._width, this._height));
        
    } // VueFinale()
    
    
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
        
        // Creation des spheres
        this._spheres = new ArrayList<>();

        for(int i=0;i<this._spheres.size();i++) {
      
            GLUquadric qobj1 = _glu.gluNewQuadric();
            this._gl.glPushMatrix();
            this._gl.glColor3f(1, 1, 1);
            this._gl.glTranslatef(0.f, 0.f, 0.f);
            _glu.gluSphere(qobj1, 1.f, 100, 100);
            this._gl.glPopMatrix();
            this._spheres.add(qobj1);
        
        }
        
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
        
        // Draw sphere 
        int s = 0;
        for(int i=0;i<this._spheres.size();i++) {
      
            GLUquadric qobj1 = this._spheres.get(s);
            this._gl.glPushMatrix();
            this._gl.glTranslatef(this._translations.get(i).getX(), this._translations.get(i).getY(), this._translations.get(i).getZ());
            _glu.gluSphere(qobj1, 1.f, 100, 100);
            this._gl.glPopMatrix();
            s++;
        
        }

        // Done Drawing 
        this._gl.glEnd();                                             

        // increasing rotation for the next iteration                   
        //this._rotateT += 0.2f; 
       
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

    
    ///////////////////////////// OBSERVER //////////////////////////////
    
    
    @Override
    public void update(Observable o, Object arg) {
        
        this._spheres = this._ms.getSpheres();
        this._translations = this._ms.getTranslations();
        this.display();
        
    } // update(Observable o, Object arg)
    
    
} // class VueFinale