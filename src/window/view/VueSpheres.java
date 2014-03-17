/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window.view;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;
import java.awt.Color;
import java.awt.Dimension;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;
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
    
    
    private float[][] _pixels;
    private ArrayList<GLUquadric> _spheres;
    private ArrayList<GLUquadric> _spheresMem;
    private float _distanceMem;
    private int _nbSpheres;
    private Random _rand;
    private boolean _test;
    
    // Parametres generaux
    static int _width;
    static int _height;
    private float _rotateT = 0.0f;
    
    // z-buffer
    protected String _path; // Nom de l'image de profondeur
    protected int _sens;    // 1 pour noir-blanc et 0 pour blanc-noir
    static int _frameBufferID[];
    static int _depthBufferID[];
    static private FloatBuffer _pixelsBis;
    
    // JOGL
    protected final FPSAnimator _FPSAnimator;
    protected GL2 _gl;
    
    /** The GL unit (helper class) **/
    static private GLU _glu;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public VueSpheres(String path, int nbSpheres) {
        
        // Nombre de spheres a representer
        this._nbSpheres = nbSpheres;
        this._rand = new Random();
        this._test = true;
        
        // Recuperation des donnees du z-buffer
        Lecture lecture = new Lecture(path);
        this._pixels = lecture.lireImage();
        
        // Mise a jour de la largeur et de la hauteur du rendu final
        _width = this._pixels.length;
        _height = this._pixels[0].length;
        
        // Preparation de JOGL
        this._FPSAnimator = new FPSAnimator(this, 5); 
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
        
        this._FPSAnimator.start();
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
        float rotate_x = this._rand.nextFloat();
        float rotate_y = this._rand.nextFloat();
        float rotate_z = this._rand.nextFloat();
        this._gl.glRotatef(this._rotateT, rotate_x, rotate_y, rotate_z);
        //this._gl.glRotatef(this._rotateT, 0.0f, 1.0f, 0.0f);
        //this._gl.glRotatef(this._rotateT, 0.0f, 0.0f, 1.0f);
        
        // Draw sphere 
        float v_x, v_y, v_z;
        for(int i=0;i<this._spheres.size();i++) {
      
            GLUquadric qobj1 = this._spheres.get(i);
            this._gl.glPushMatrix();
            this._gl.glColor3f(1, 1, 1);
            
            v_x = this._rand.nextInt(6)-3;
            v_y = this._rand.nextInt(6)-3;
            v_z = this._rand.nextInt(6)-3;
            this._gl.glTranslatef(v_x, v_y, v_z);
            
            _glu.gluSphere(qobj1, 1.f, 100, 100);
            this._gl.glPopMatrix();
        
        }

        // Done Drawing 
        this._gl.glEnd();                                             

        // increasing rotation for the next iteration                   
        this._rotateT += 0.2f; 
        
        // Affichage de la distance euclidienne
        float[][] distance = null;
        if(_test) { 
            
            distance = this.creationZBuffer(gLDrawable);
            float res = this.getDistanceEuclidienne(distance);
            
            // Memorisation du meilleur resultat
            if(res < this._distanceMem) {
                
                this._distanceMem = res;
                this._spheresMem = this._spheres;
                System.out.println("Distance euclidienne: " + this.getDistanceEuclidienne(distance));
                
            }
            
        } else {
            
            this._test = !this._test;
            
        }
       
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
        
        // Initialize FBO3
        initializeFBO3(this._gl);
        
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
    
    
    ///////////////////////////// Z-BUFFER //////////////////////////////
    
    
    /**
     * Prepare el tableau de z-buffer
     * @param gl 
     */
    static private void initializeFBO3(GL2 gl) {

	// Create frame buffer
        _frameBufferID = new int[1];
	gl.glGenFramebuffers(1, _frameBufferID, 0);
	gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _frameBufferID[0]);
	
	// ------------- Depth buffer texture --------------
	_depthBufferID = new int[1];
	gl.glGenTextures(1,_depthBufferID,0);
	gl.glBindTexture(GL2.GL_TEXTURE_2D, _depthBufferID[0]);
	
	gl.glTexImage2D(GL2.GL_TEXTURE_2D,          // target texture type
			0,                          // mipmap LOD level
			GL2.GL_DEPTH_COMPONENT24,   // internal pixel format
			//GL2.GL_DEPTH_COMPONENT
			_width/1,           // width of generated image
			_height/1,          // height of generated image
			0,                          // border of image
			GL2.GL_DEPTH_COMPONENT,     // external pixel format 
			GL2.GL_FLOAT,        // datatype for each value
			null);  // buffer to store the texture in memory
	
	// Some parameters
	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);     
	
	// Attach 2D texture to this FBO
	gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER,
				  GL2.GL_DEPTH_ATTACHMENT,
				  GL2.GL_TEXTURE_2D,
				  _depthBufferID[0],0);
	
	gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	
	// Disable color buffer
	gl.glDrawBuffer(GL2.GL_NONE);
	gl.glReadBuffer(GL2.GL_NONE);
	
	// Set pixels ((width*2)* (height*2))
	// It has to have twice the size of shadowmap size
	_pixelsBis = GLBuffers.newDirectFloatBuffer(_width/1*_height/1);
	
	// Set default frame buffer before doing the check
	gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
	
	int status = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
	
	// Always check that our framebuffer is ok
	if(gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER) != GL2.GL_FRAMEBUFFER_COMPLETE) {
            
		System.err.println("Can not use FBO! Status error:" + status);
                
	} 
    
    } // initializeFBO3(GL2 gl)
    
    
    /**
     * Permet de reechantilloner les valeurs de z-buffer
     * @return le tableau echantillonne de z-buffer
     */
    public float[] reechantillonner() {
        
        float[] tab = new float[_width*_height];
        for(int i=0;i<_width*_height;i++) {
            tab[i] = 1;
        }
        float min = (float)_pixelsBis.get(0);
        float max = (float)_pixelsBis.get(0);
        for(int i=0;i<_width;i++) {
            
            for(int j=0;j<_height;j++) {
                
                if(_pixelsBis.get(i+j*_width) < min) min = (float)_pixelsBis.get(i+j*_width);
                if(_pixelsBis.get(i+j*_width) > max) max = (float)_pixelsBis.get(i+j*_width);
                
            }
            
        }
        
        // Remplissage du tableau echantillonne
        for(int i=0;i<_width;i++) {
            
            for(int j=0;j<_height;j++) {
                
                //tab[i+j*_width] = (255*(_pixels.get(i+j*_width)-min)/(max-min))/255;
                tab[i+j*_width] = (255*(_pixelsBis.get(i+j*_width)-min)/(max-min))/255;
                
            }
        }
        
        return tab;
        
    } // reechantillonner()
    
    
    /**
     * Dessine l'image de profondeur de l'image
     * @param gLDrawable 
     * @return le tableau de profondeur de l'image
     */
    public float[][] creationZBuffer(GLAutoDrawable gLDrawable) {
        
        this._test = !this._test;
        
        //Render scene into (own, special) Frame buffer first
        this._gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _frameBufferID[0]);
        display(gLDrawable);

        //Read pixels (i.e., a ByteBuffer) from buffer
        this._gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, _frameBufferID[0]);
        
        //Read pixels 
        this._gl.glReadPixels(0, 0, _width/1, _height/1, GL2.GL_DEPTH_COMPONENT , GL2.GL_FLOAT,
                        _pixelsBis);

        float[] tab;
        float[][] res = new float[_width][_height];
        tab = this.reechantillonner();
        for (int x=0; x<_width/1; x++) {
            for (int y=0; y<_height/1; y++) {
                
                // Pas de reechantillonnage
                //int color = (int)(_pixelsBis.get(x+_width*y)*255);
                // Reechantillonage
                int color = (int)(tab[x+_width*(_height-1-y)]*255);
                
                if(this._sens == 0) color = 255 - color;
                res[x][y] = color/255;
                
            }
        }
        
        return res;
        
    } // dessinerCreation(GLAutoDrawable gLDrawable) 
    
    
    /**
     * Permet de connaitre la distance euclidienne entre les deux images
     * @param bis tableau de pixels a comparer
     * @return la disatance euclidienne entre les deux images
     */
    protected float getDistanceEuclidienne(float[][] bis) {
        
        float distance, dTmp;
        distance = dTmp = 0;
        
        for(int i=0;i<_width;i++) {
            for(int j=0;j<_height;j++) {
                
                dTmp = (float) Math.sqrt(Math.pow(this._pixels[i][j], 2)+Math.pow(bis[i][j], 2));
                distance += dTmp;
                
            }
        }
        
        return distance;
        
    } // getDistanceEuclidienne(float[][] bis)
    
    
} // class VueSpheres
