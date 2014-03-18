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
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import window.main.MainSphere;
import zbuffer.Lecture;

/**
 * VueFinale
 * @author Tristan
 */
public class VueFinale extends GLCanvas implements GLEventListener, Observer {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    private float[][] _pixels;
    private ArrayList<GLUquadric> _spheres;
    private ArrayList<Float> _translations;
    
    // Parametres generaux
    protected MainSphere _ms;
    static int _width;
    static int _height;
    private float _rotateT = 0.0f;
    
    // z-buffer
    protected String _path; // Nom de l'image de profondeur
    
    // JOGL
    protected GL2 _gl;
    
    /** The GL unit (helper class) **/
    static private GLU _glu;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public VueFinale(String path, MainSphere ms) {
        
        this._ms = ms;
        this._spheres = new ArrayList<GLUquadric>();
        this._translations = new ArrayList<Float>();
        
        // Recuperation des donnees du z-buffer
        Lecture lecture = new Lecture(path);
        this._pixels = lecture.lireImage();
        
        // Mise a jour de la largeur et de la hauteur du rendu final
        _width = this._pixels.length;
        _height = this._pixels[0].length;
        
        // Preparation de JOGL
        this.addGLEventListener(this);
        
        // Create GLU.
        VueFinale._glu = new GLU();
        
        this.afficher();
        // Dimension de la fenetre
        this.setPreferredSize(new Dimension(_width, _height));
        
    } // VueFinale()
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Cree et affiche la scene
     */
    public void afficher() {
        
        this.requestFocus();
        
    } // afficher()
    
    
    /**
     * Permet de modifier l'eclairage de la scene
     */
    public void setLight() {
        
        float[] no_mat = { 0.0F,0.0F,0.0F,1.0F };
        float[] mat_ambient = { 0.7F,0.4F,0.7F,1.0F };
        float[] mat_ambient_color = { 0.8F,0.5F,0.2F,1.0F };
        float[] mat_diffuse = { 0.1F,0.5F,0.8F,1.0F };
        float[] mat_specular = { 1.0F,0.7F,1.0F,1.0F };
        float[] no_shininess = { 0.0F };
        float[] low_shininess = { 5.0F };
        float[] high_shininess = { 100.0F };
        float[] mat_emission = {0.3F,0.2F,0.2F,0.0F};
        
        float SHINE_ALL_DIRECTIONS = 1;
        float[] lightPos = {0, 10, 10, SHINE_ALL_DIRECTIONS};
        float[] lightColorAmbient = {0.2f, 0.2f, 0.2f, 1f};
        float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};

        // Set light parameters.
        this._gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
        this._gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
        this._gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);

        // Enable lighting in GL.
        this._gl.glEnable(GL2.GL_LIGHT1);
        this._gl.glEnable(GL2.GL_LIGHTING);

        // Set material properties.
        this._gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_AMBIENT,mat_ambient,0);
        this._gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_DIFFUSE,mat_diffuse,0);
        this._gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_SPECULAR,mat_specular,0);
        this._gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_SHININESS,low_shininess,0);
        this._gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_EMISSION,no_mat,0);
        
    } // setLight()
    

    ///////////////////////////// OPEN-GL ///////////////////////////////
    

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
        for(int i=0;i<this._spheres.size();i+=3) {
      
            GLUquadric qobj1 = this._spheres.get(i);
            this._gl.glPushMatrix();
            this._gl.glTranslatef(this._translations.get(i), this._translations.get(i+1), this._translations.get(i+2));
            _glu.gluSphere(qobj1, 1.f, 100, 100);
            this._gl.glPopMatrix();
        
        }

        // Done Drawing 
        this._gl.glEnd();                                             

        // increasing rotation for the next iteration                   
        this._rotateT += 0.2f; 
       
    } // display(GLAutoDrawable glad)

    
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
        this._spheres = new ArrayList<GLUquadric>();

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