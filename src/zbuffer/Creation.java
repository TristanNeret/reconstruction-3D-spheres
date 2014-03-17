/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zbuffer;

import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.*;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * Creation
 * @author Tristan
 */
public final class Creation implements GLEventListener {
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    // Parametres generaux
    static int _width = 500;
    static int _height = 400;
    private float _rotateT = 0.0f;
    private boolean _test = true;
    
    // z-buffer
    protected String _path; // Nom de l'image de profondeur
    protected int _sens;    // 1 pour noir-blanc et 0 pour blanc-noir
    static int _frameBufferID[];
    static int _depthBufferID[];
    static private FloatBuffer _pixels;
    
    // JOGL
    protected final GLCanvas _canvas;
    protected final Frame _frame;
    protected final Animator _animator;
    protected GL2 _gl;
    
    /** The GL unit (helper class). */
    static private GLU _glu;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public Creation(String path, int sens) {
        
        this._path = path;
        this._sens = sens;
        
        // Preparation de JOGL
        this._canvas = new GLCanvas();
        this._frame = new Frame("JOGL");
        this._animator = new Animator(this._canvas);
        this._canvas.addGLEventListener(this);
        
        // Create GLU.
        Creation._glu = new GLU();
        
        this.afficher();
        
    } // Creation(String path, int sens)
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Cree et affiche la scene
     */
    public void afficher() {
        
        this._frame.add(this._canvas);
        this._frame.setSize(Creation._width, Creation._height);
        this._frame.setResizable(false);
        this._frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                        _animator.stop();
                        _frame.dispose();
                        System.exit(0);
                }
        });
        this._frame.setVisible(true);
        this._animator.start();
        this._canvas.requestFocus();
        
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
    
    
    ///////////////////////////// Z-BUFFER //////////////////////////////
    
    
    /**
     * Prepare el tableau de z-buffer
     * @param gl 
     */
    static private void initializeFBO3(GL2 gl) {

	// Create frame buffer
        Creation._frameBufferID = new int[1];
	gl.glGenFramebuffers(1, Creation._frameBufferID, 0);
	gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, Creation._frameBufferID[0]);
	
	// ------------- Depth buffer texture --------------
	Creation._depthBufferID = new int[1];
	gl.glGenTextures(1,Creation._depthBufferID,0);
	gl.glBindTexture(GL2.GL_TEXTURE_2D, Creation._depthBufferID[0]);
	
	gl.glTexImage2D(GL2.GL_TEXTURE_2D,          // target texture type
			0,                          // mipmap LOD level
			GL2.GL_DEPTH_COMPONENT24,   // internal pixel format
			//GL2.GL_DEPTH_COMPONENT
			Creation._width/1,           // width of generated image
			Creation._height/1,          // height of generated image
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
				  Creation._depthBufferID[0],0);
	
	gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	
	// Disable color buffer
	gl.glDrawBuffer(GL2.GL_NONE);
	gl.glReadBuffer(GL2.GL_NONE);
	
	// Set pixels ((width*2)* (height*2))
	// It has to have twice the size of shadowmap size
	Creation._pixels = GLBuffers.newDirectFloatBuffer(Creation._width/1*Creation._height/1);
	
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
        
        float[] tab = new float[Creation._width*Creation._height];
        for(int i=0;i<Creation._width*Creation._height;i++) {
            tab[i] = 1;
        }
        float min = (float)Creation._pixels.get(0);
        float max = (float)Creation._pixels.get(0);
        for(int i=0;i<Creation._width;i++) {
            
            for(int j=0;j<Creation._height;j++) {
                
                if(Creation._pixels.get(i+j*Creation._width) < min) min = (float)Creation._pixels.get(i+j*Creation._width);
                if(Creation._pixels.get(i+j*Creation._width) > max) max = (float)Creation._pixels.get(i+j*Creation._width);
                
            }
            
        }
        
        // Remplissage du tableau echantillonne
        for(int i=0;i<Creation._width;i++) {
            
            for(int j=0;j<Creation._height;j++) {
                
                //tab[i+j*Creation._width] = (255*(Creation._pixels.get(i+j*Creation._width)-min)/(max-min))/255;
                tab[i+j*Creation._width] = (255*(Creation._pixels.get(i+j*Creation._width)-min)/(max-min))/255;
                
            }
        }
        
        return tab;
        
    } // reechantillonner()
    
    
    /**
     * Dessine l'image de profondeur de l'image
     * @param gLDrawable 
     */
    public void dessinerCreation(GLAutoDrawable gLDrawable) {
        
        //Render scene into (own, special) Frame buffer first
        this._gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, Creation._frameBufferID[0]);
        display(gLDrawable);

        //Read pixels (i.e., a ByteBuffer) from buffer
        this._gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, Creation._frameBufferID[0]);
        
        //Read pixels 
        this._gl.glReadPixels(0, 0, Creation._width/1, Creation._height/1, GL2.GL_DEPTH_COMPONENT , GL2.GL_FLOAT,
                        Creation._pixels);

        // Preparation de la sauveagrde de l'image
        BufferedImage bi = new BufferedImage(Creation._width, Creation._height, BufferedImage.TYPE_INT_ARGB);
        
        float[] tab;
        tab = this.reechantillonner();
        for (int x=0; x<Creation._width/1; x++) {
            for (int y=0; y<Creation._height/1; y++) {
                
                // Pas de reechantillonnage
                //int color = (int)(Creation._pixels.get(x+Creation._width*y)*255);
                // Reechantillonage
                //int color = (int)(tab[x+Creation._width*y]*255);
                int color = (int)(tab[x+Creation._width*(Creation._height-1-y)]*255);
                
                if(this._sens == 0) color = 255 - color;
                int rgb = new Color(color,color,color).getRGB();
                bi.setRGB(x, y, rgb);
                
            }
        }
        
        // Sauveagrde de l'image
        try {
            
            ImageIO.write(bi, "PNG", new File(this._path));
            System.out.println("Image " + this._path + " enregistree !");
            
        } catch (IOException ex) {
            Logger.getLogger(Creation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    } // dessinerCreation(GLAutoDrawable gLDrawable) 
    
    
    ///////////////////////////// OPEN-GL ///////////////////////////////
    
    
    @Override
    public void display(GLAutoDrawable gLDrawable) {
        
        this._gl = gLDrawable.getGL().getGL2();
        this._gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        this._gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        this._gl.glLoadIdentity();
        this._gl.glTranslatef(0.0f, 0.0f, -5.0f);
        
        // Dessine l'image de z-buffer
        if (this._test) {
            
	    this._test = false;
            this.dessinerCreation(gLDrawable);
            
        }
        
        // Gestion de l'eclairage
        this.setLight();
        
        // rotate on the three axis
        this._gl.glRotatef(this._rotateT, 1.0f, 0.0f, 0.0f);
        this._gl.glRotatef(this._rotateT, 0.0f, 1.0f, 0.0f);
        this._gl.glRotatef(this._rotateT, 0.0f, 0.0f, 1.0f);
        
        // Draw sphere 
        GLUquadric qobj0 = Creation._glu.gluNewQuadric();
    	this._gl.glPushMatrix();
        this._gl.glColor3f(1, 1, 1);
    	Creation._glu.gluSphere(qobj0, 1.f, 100, 100);
    	this._gl.glPopMatrix();
        
        GLUquadric qobj1 = Creation._glu.gluNewQuadric();
    	this._gl.glPushMatrix();
        this._gl.glColor3f(1, 1, 1);
        this._gl.glTranslatef(2.5f, 2.5f, -5.f);
    	Creation._glu.gluSphere(qobj1, 1.f, 100, 100);
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
    
    
} // class Creation
