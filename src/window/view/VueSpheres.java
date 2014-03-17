/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window.view;

import com.jogamp.opengl.util.Animator;
import java.awt.Dimension;
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
import zbuffer.Lecture;

/**
 * VueSpheres
 * @author Tristan
 */
public class VueSpheres extends GLCanvas implements GLEventListener {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    float[][] _pixels;
    
    // Parametres generaux
    static int _width;
    static int _height;
    private float _rotateT = 0.0f;
    
    // JOGL
    protected final Animator _animator;
    protected GL2 _gl;
    
    /** The GL unit (helper class) **/
    static private GLU _glu;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public VueSpheres(String path) {
        
        Lecture lecture = new Lecture(path);
        this._pixels = lecture.lireImage();
        
        _width = this._pixels.length;
        _height = this._pixels[0].length;
        
        // Preparation de JOGL
        this._animator = new Animator(this);
        this.addGLEventListener(this);
        
        // Create GLU.
        VueSpheres._glu = new GLU();
        
        this.afficher();
        
        // Dimension de la fenetre
        this.setPreferredSize(new Dimension(_width, _height));
        
    } // VueSpheres()

    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Cree et affiche la scene
     */
    public void afficher() {
        
        this._animator.start();
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
        
        // rotate on the three axis
        this._gl.glRotatef(this._rotateT, 1.0f, 0.0f, 0.0f);
        this._gl.glRotatef(this._rotateT, 0.0f, 1.0f, 0.0f);
        this._gl.glRotatef(this._rotateT, 0.0f, 0.0f, 1.0f);
        
        // Draw sphere 
        GLUquadric qobj0 = _glu.gluNewQuadric();
    	this._gl.glPushMatrix();
        this._gl.glColor3f(1, 1, 1);
    	_glu.gluSphere(qobj0, 1.f, 100, 100);
    	this._gl.glPopMatrix();
        
        GLUquadric qobj1 = _glu.gluNewQuadric();
    	this._gl.glPushMatrix();
        this._gl.glColor3f(1, 1, 1);
        this._gl.glTranslatef(2.5f, 2.5f, -5.f);
    	_glu.gluSphere(qobj1, 1.f, 100, 100);
    	this._gl.glPopMatrix();

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
    
    
} // class VueSpheres
