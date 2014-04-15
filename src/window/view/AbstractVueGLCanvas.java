/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window.view;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import sphere.algorithme.Fonction;
import sphere.zbuffer.Creation;
import window.main.Coordonnees;

/**
 * AbstractVueGLCanvas
 * @author Tristan
 */
public abstract class AbstractVueGLCanvas extends GLCanvas implements GLEventListener {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    protected float[][] _pixels;
    protected ArrayList<GLUquadric> _spheres;
    protected ArrayList<GLUquadric> _spheresMem;
    protected ArrayList<Coordonnees> _translations;
    protected ArrayList<Coordonnees> _translationsMem;
    protected float _distance;
    protected float _distanceMem;
    protected int _nbSpheres;
    protected Random _rand;
    protected boolean _test;
    
    // Parametres generaux
    protected int _width;
    protected int _height;
    protected int _nbIterations;
    protected int _nbIterationsMem;
    protected float _rotateT;
    protected Fonction _fonction;
    
    // z-buffer
    protected String _path; // Nom de l'image de profondeur
    protected int _sens;    // 1 pour noir-blanc et 0 pour blanc-noir
    protected int _frameBufferID[];
    protected int _depthBufferID[];
    protected FloatBuffer _pixelsBis;
    
    // JOGL
    protected FPSAnimator _FPSAnimator;
    protected GL2 _gl;
    
    /** The GL unit (helper class) **/
    protected GLU _glu;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /////////////////////////// CONSTRUCTEUR //////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public AbstractVueGLCanvas() {
        
        // Nombre de spheres a representer
        this._spheres = new ArrayList<>();
        this._spheresMem = new ArrayList<>();
        this._translations = new ArrayList<>();
        this._translationsMem = new ArrayList<>();
        this._rand = new Random();
        this._test = true;
        
        // Preparation de JOGL
        this._FPSAnimator = new FPSAnimator(this, 30); 
        this.addGLEventListener(this);
    
        // Create GLU.
        _glu = new GLU();
        
        this._rotateT = 0.0f;
        this._fonction = new Fonction();
        this.afficher();
     
    } // AbstractVueGLCanvas
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Cree et affiche la scene
     */
    public final void afficher() {
        
        this._nbIterations = 0;
        this._nbIterationsMem = 0;
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
    
    
    ///////////////////////////// Z-BUFFER //////////////////////////////
    
    
    /**
     * Prepare le tableau de z-buffer
     * @param gl 
     */
    protected void initializeZBuffer(GL2 gl) {

	// Create frame buffer
        _frameBufferID = new int[1];
	gl.glGenFramebuffers(1, _frameBufferID, 0);
	gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _frameBufferID[0]);
	
	// ------------- Depth buffer texture --------------
	_depthBufferID = new int[1];
	gl.glGenTextures(1,_depthBufferID,0);
	gl.glBindTexture(GL2.GL_TEXTURE_2D, _depthBufferID[0]);
	
	gl.glTexImage2D(GL2.GL_TEXTURE_2D,        // target texture type
			0,                        // mipmap LOD level
			GL2.GL_DEPTH_COMPONENT24, // internal pixel format
			_width/1,                 // width of generated image
			_height/1,                // height of generated image
			0,                        // border of image
			GL2.GL_DEPTH_COMPONENT,   // external pixel format 
			GL2.GL_FLOAT,             // datatype for each value
			null);                    // buffer to store the texture in memory
	
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
    
    } // initializeZBuffer(GL2 gl)
    
    
    /**
     * Dessine l'image de profondeur de l'image
     * @param gLDrawable 
     * @return le tableau de profondeur de l'image
     */
    protected float[][] getZBufferTab(GLAutoDrawable gLDrawable) {
        
        this._test = false;
        
        //Render scene into (own, special) Frame buffer first
        this._gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _frameBufferID[0]);
        display(gLDrawable);

        //Read pixels (i.e., a ByteBuffer) from buffer
        this._gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, _frameBufferID[0]);
        
        //Read pixels 
        this._gl.glReadPixels(0, 0, _width/1, _height/1, GL2.GL_DEPTH_COMPONENT , GL2.GL_FLOAT,
                        _pixelsBis);
        
        // Recuperation du tableau de z-buffer
        float[][] res = new float[_width][_height];
        for (int x=0; x<_width/1; x++) {
            for (int y=0; y<_height/1; y++) {
                
                res[x][y] = this._pixelsBis.get(x+y*this._width);
                
            }
        }
        
        return res;
        
    } // getZBufferTab(GLAutoDrawable gLDrawable)
    
    
    ///////////////////////// SAUVEGARDE Z-BUFFER /////////////////////////////
    
    
    /**
     * Permet de reechantilloner les valeurs de z-buffer
     * @return le tableau echantillonne de z-buffer
     */
    protected float[] reechantillonner() {
        
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
     * Enregistre l'image de profondeur de l'image
     * @param pixels tableau de pixels
     */
    public void saveZBufferPNG(float[][] pixels) {

        // Preparation de la sauveagrde de l'image
        BufferedImage bi = new BufferedImage(this._width, this._height, BufferedImage.TYPE_INT_ARGB);

        for (int x=0; x<this._width/1; x++) {
            for (int y=0; y<this._height/1; y++) {
        
                int color = (int)(pixels[x][this._height-1-y]*255);
                
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
        
    } // saveZBufferPNG(float[][] pixels) 
    
    
    /**
     * Dessine l'image de profondeur de l'image cree et recupere le tableau de
     * pixels de cette image
     * (enregistre un fichier image et un fichier texte)
     * @param gLDrawable 
     * @return le tableau des valeurs des valeurs de profondeur de l'image
     */
    public float[] saveCreationZBuffer(GLAutoDrawable gLDrawable) {
        
        this._test = false;
        
        //Render scene into (own, special) Frame buffer first
        this._gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, this._frameBufferID[0]);
        display(gLDrawable);

        //Read pixels (i.e., a ByteBuffer) from buffer
        this._gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, this._frameBufferID[0]);
        
        //Read pixels 
        this._gl.glReadPixels(0, 0, this._width/1, this._height/1, GL2.GL_DEPTH_COMPONENT , GL2.GL_FLOAT, this._pixelsBis);

        // Preparation de la sauveagrde de l'image
        BufferedImage bi = new BufferedImage(this._width, this._height, BufferedImage.TYPE_INT_ARGB);
        
        float[] tab;
        tab = this.reechantillonner();
        for (int x=0; x<this._width/1; x++) {
            for (int y=0; y<this._height/1; y++) {
                
                // Pas de reechantillonnage
                //float color = (float)(this._pixelsBis.get(x+this._width*y)*255);
                // Reechantillonage
                int color = (int)(tab[x+this._width*(this._height-1-y)]*255);
                
                if(this._sens == 0) color = 255 - color;
                int rgb = new Color(color,color,color).getRGB();
                bi.setRGB(x, y, rgb);
                
            }
        }
        
        // Sauveagrde de l'image
        try {
            
            String nomFichier = this._path + ".png";
            ImageIO.write(bi, "PNG", new File(nomFichier));
            System.out.println("Image " + nomFichier + " enregistree !");
            
        } catch (IOException ex) {
            Logger.getLogger(Creation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Recupere le tableau de float et enregistre le fichier texte
        float[] res = null;
        try {
            
            PrintWriter writer = new PrintWriter(this._path + ".txt", "UTF-8");
            res = new float[this._width*this._height];
            int i = 0;
            while (this._pixelsBis.hasRemaining()) {

                res[i] = this._pixelsBis.get();
                writer.println(res[i]);
                i++;

            }
            writer.close();
            System.out.println("Fichier texte " + this._path + ".txt" + " enregistre !");
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(AbstractVueGLCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Retourne le FloatBuffer en tableau d'array
        return res;
        
    } // saveCreationZBuffer(GLAutoDrawable gLDrawable, String nomFichier)  
    
} // AbstractVueGLCanvas
