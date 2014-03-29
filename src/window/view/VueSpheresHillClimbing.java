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
 * VueSpheresHillClimbing
 * @author Tristan
 */
public class VueSpheresHillClimbing extends AbstractVueGLCanvas implements Observer {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
  
    
    protected MainSphere _ms;
    protected float _v_x, _v_y, _v_z;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public VueSpheresHillClimbing(MainSphere ms, String path, int nbSpheres) {
        
        this._ms = ms;
        
        // Nombre de spheres a representer
        this._nbSpheres = nbSpheres;
        
        // Recuperation des donnees du z-buffer
        Lecture lecture = new Lecture(path);
        this._pixels = lecture.lireImage();
        
        // Mise a jour de la largeur et de la hauteur du rendu final
        this._width = this._pixels.length;
        this._height = this._pixels[0].length;
        
        // Dimension de la fenetre
        this.setPreferredSize(new Dimension(this._width, this._height));
        
    } // VueSpheresHillClimbing()

    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
   
    
    ///////////////////////////// OPEN-GL ///////////////////////////////
    
    
    @Override
    public void init(GLAutoDrawable glDrawable) {
        
        this._FPSAnimator.start();
        
        this._gl = glDrawable.getGL().getGL2();
        this._gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        this._gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this._gl.glClearDepth(1.0f);
        this._gl.glEnable(GL.GL_DEPTH_TEST);
        this._gl.glDepthFunc(GL.GL_LEQUAL);
        this._gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        
        // Creation des spheres
        this._spheres = new ArrayList<>();

        for(int i=0;i<this._nbSpheres;i++) {
      
            GLUquadric qobj1 = _glu.gluNewQuadric();
            this._gl.glPushMatrix();
            this._gl.glColor3f(1, 1, 1);
            this._gl.glTranslatef(0.f, 0.f, 0.f);
            _glu.gluSphere(qobj1, 1.f, 100, 100);
            this._gl.glPopMatrix();
            this._spheres.add(qobj1);
        
        }
        this._spheresMem = this._spheres;
        this._distanceMem = Float.POSITIVE_INFINITY;
        
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
        /*float rotate_x = (float)this._rand.nextInt(101)/100;
        float rotate_y = (float)this._rand.nextInt(101)/100;
        float rotate_z = (float)this._rand.nextInt(101)/100;
        this._gl.glRotatef(this._rotateT, rotate_x, rotate_y, rotate_z);*/
        
        // Draw sphere 
        if(this._test) {
            this._translations = new ArrayList<>();
            for(int i=0;i<this._spheres.size();i++) {

                this._v_x = (((float)this._rand.nextInt(401)/100)-(float)2);
                this._v_y = (((float)this._rand.nextInt(401)/100)-(float)2);
                this._v_z = (((float)this._rand.nextInt(1001)/100)-(float)8);
                this._translations.add(this._v_x);
                this._translations.add(this._v_y);
                this._translations.add(this._v_z);

            }
        }
        
        int s = 0;
        for(int i=0;i<this._spheres.size()*3;i+=3) {
      
            GLUquadric qobj1 = this._spheres.get(s);
            this._gl.glPushMatrix();
            this._gl.glTranslatef(this._translations.get(i), this._translations.get(i+1), this._translations.get(i+2));
            _glu.gluSphere(qobj1, 1.f, 100, 100);
            this._gl.glPopMatrix();
            s++;
        
        }

        // Done Drawing 
        this._gl.glEnd();                                             

        // increasing rotation for the next iteration                   
        this._rotateT += 0.2f; 
        
        // Affichage de la distance euclidienne
        float[][] distance;
        if(_test) { 
            
            distance = this.getZBufferTab(gLDrawable);
            float res = this._fonction.getDistanceEuclidienne(this._pixels,distance);
            this._nbIterations++;
            this._test = true;
            
            // Memorisation du meilleur resultat
            if(res < this._distanceMem) {
                
                this._distanceMem = res;
                this._spheresMem = this._spheres;
                this._translationsMem = this._translations;
                
                this._ms.setSpheres(this._spheresMem);
                this._ms.setTranslations(this._translationsMem);
                this._ms.updateInformations(res + "", this._nbIterations + "");
                this._ms.updateView();
                
                System.out.println("Distance euclidienne: " + res + " (" 
                        + this._nbIterations + " iterations)");
                
            }
            
        } else {
            
            //this._test = !this._test;
            
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

    
    ///////////////////////////// OBSERVER //////////////////////////////
    
    
    @Override
    public void update(Observable o, Object arg) {
       
    } // update(Observable o, Object arg)
    
    
} // class VueSpheresHillClimbing
