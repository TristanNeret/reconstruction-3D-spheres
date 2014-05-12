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
import window.main.Coordonnees;

/**
 * VueSpheresHillClimbing
 * @author Tristan
 */
public class VueSpheresMultiZoom extends AbstractVueGLCanvas implements Observer {
 
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    protected MainSphere _ms;
    protected float _v_x, _v_y, _v_z;
    float[][] distance;
    float[][] dist;
    float res1;
    float res2;
    float res3;
    boolean init;
    float pasCourant;
    float pasOriginal;
    float seuil;
    

    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public VueSpheresMultiZoom(MainSphere ms, float[][] zBufferTab, int width, int height, int nbSpheres) {

        init = false;
        this._ms = ms;
        this._pixels = zBufferTab;
        this._width = width;
        this._height = height;
        dist = new float[_width][_height];
        pasOriginal= (float) 0.1;
        pasCourant = pasOriginal;
        
        // Nombre de spheres a representer
        this._nbSpheres = nbSpheres;
        
        // Dimension de la fenetre
        this.setPreferredSize(new Dimension(this._width, this._height));

    } // VueSpheresMultiZoom(MainSphere ms, float[][] zBufferTab, int width, int height, int nbSpheres)

    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    ////////////////////////////// OPEN-GL ///////////////////////////////////
    
    
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

        this._translations = new ArrayList<>();
        // Creation des spheres
        this._spheres = new ArrayList<>();
        this.seuil = this._fonction.getDistanceEuclidienne(_pixels, new float[this._width][this._height]);
        System.out.println("SEUIL: " + this.seuil);

        for (int i = 0; i < this._nbSpheres; i++) {

            GLUquadric qobj1 = _glu.gluNewQuadric();
            this._gl.glPushMatrix();
            this._gl.glColor3f(1, 1, 1);
            this._gl.glTranslatef(0.f, 0.f, 0.f);
            _glu.gluSphere(qobj1, 1.f, 100, 100);
            this._gl.glPopMatrix();
            
            this._spheres.add(qobj1);
            this._translations.add(new Coordonnees((float) 0, (float) 0, (float) 0, 0));
            
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
        
        // Draw sphere 
        if (this._test) {

            // On incremente le nombre d'iterations effectuees
            this._nbIterations++;
            
            //for (int i = 0; i < this._spheres.size(); i++) {
            // On travaille uniquement sur la derniere sphere ajoutee
            int i = this._spheres.size()-1;
            
                // Des que la distance euclidienne est correct, on utilise un
                // algorithme de Hill-Climbing pour ameliorer le resultat
                Coordonnees c = this._translations.get(i);
                
                if (!init) {

                    this._v_x = ((float) 0);
                    this._v_y = ((float) 0);
                    this._v_z = ((float) 0);

                    c.setX(_v_x);
                    c.setY(_v_y);
                    c.setZ(_v_z);
                    c.setR(1);
                    this._translations.set(i, c);
                    
                    // Calcul initial de la distance euclidienne
                    dist = this.getZBufferTab(gLDrawable);
                    float res = this._fonction.getDistanceEuclidienne(this._pixels, dist);
                    this._distance = res;
                    
                    this._test = true;
                    init = true;
                    this.memo();
                    
                } else {
                    
                    // Hill-Climbing
                    c = this._translations.get(i);
                    float x = c.getX();
                    float y = c.getY();
                    float z = c.getZ();
                    float r = c.getR();
                    
                    boolean testPas = true;
                    
                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                    
                    ////////// Test sur x

                    // Sens positif (x)
                    c.setX(x + pasCourant);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);  
                    res1 = this._fonction.getDistanceEuclidienne(this._pixels, dist);

                    // Sens negatif (x)
                    c.setX(x - pasCourant);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);
                    res2 = this._fonction.getDistanceEuclidienne(this._pixels, dist);
                    
                    // Position originale (x)
                    c.setX(x);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);
                    res3 = this._fonction.getDistanceEuclidienne(this._pixels, dist);
                    
                    //if(this._translations.size() > 1) {
                        System.out.println("RES: " + res1 + "|" + res2 + "|" + res3 + "(" + x + "|" + pasCourant + ")");
                    //}
                    
                    if (res1 < res2 && res1 < res3) {
                        
                        // Le sens positif est le meilleur (x)
                        this._distance = res1;
                        c.setX(x + pasCourant);
                        this._translations.set(i, c);
                        this.memo();
                        testPas = false;
           
                    } else if (res2 < res3) {
                        
                        // Le sens negatif est le meilleur (x)
                        this._distance = res2;
                        c.setX(x - pasCourant);
                        this._translations.set(i, c);
                        this.memo();
                        testPas = false;
                        
                    } else {
                        
                        // Pas d'amelioration (x)
                        this._distance = res3;
                        c.setX(x);
                        this._translations.set(i, c);
                        this.memo();
                        
                    }
                    this._distanceMem = this._distance;

                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                    
                    ////////// Test sur y

                    // Sens positif (y)
                    c.setY(y + pasCourant);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);
                    res1 = this._fonction.getDistanceEuclidienne(this._pixels, dist);

                    // Sens negatif (y)
                    c.setY(y - pasCourant);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);
                    res2 = this._fonction.getDistanceEuclidienne(this._pixels, dist);
                    
                    // Position originale (y)
                    c.setY(y);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);
                    res3 = this._fonction.getDistanceEuclidienne(this._pixels, dist);
                    
                    if (res1 < res2 && res1 < res3) {
                        
                        // Le sens positif est le meilleur (y)
                        this._distance = res1;
                        c.setY(y + pasCourant);
                        this._translations.set(i, c);
                        this.memo();
                        testPas = false;
               
                    } else if (res2 < res3) {
                        
                        // Le sens negatif est le meilleur (y)
                        this._distance = res2;
                        c.setY(y - pasCourant);
                        this._translations.set(i, c);
                        this.memo();
                        testPas = false;
                       
                    } else {
                        
                        // Pas d'amelioration (y)
                        this._distance = res3;
                        c.setY(y);
                        this._translations.set(i, c);
                        this.memo();
                        
                    }
                    this._distanceMem = this._distance;

                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                    
                    ////////// Test sur z

                    // Sens positif (z)
                    c.setZ(z + pasCourant);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);
                    res1 = this._fonction.getDistanceEuclidienne(this._pixels, dist);

                    // Sens negatif (z)
                    c.setZ(z - pasCourant);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);
                    res2 = this._fonction.getDistanceEuclidienne(this._pixels, dist);
                    
                    // Position originale (z)
                    c.setZ(z);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);
                    res3 = this._fonction.getDistanceEuclidienne(this._pixels, dist);
                    
                    if (res1 < res2 && res1 < res3) {
                        
                        // Le sens positif est le meilleur (z)
                        this._distance = res1;
                        c.setZ(z + pasCourant);
                        this._translations.set(i, c);
                        this.memo();
                        testPas = false;
         
                    } else if (res2 < res3) {
                        
                        // Le sens negatif est le meilleur (z)
                        this._distance = res2;
                        c.setZ(z - pasCourant);
                        this._translations.set(i, c);
                        this.memo();
                        testPas = false;
                        
                    } else {
                        
                        // Pas d'amelioration (z)
                        this._distance = res3;
                        c.setZ(z);
                        this._translations.set(i, c);
                        this.memo();
                        
                    }
                    this._distanceMem = this._distance;
                    
                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                    
                    ////////// Test sur r

                    // Sens positif (r)
                    c.setR(r + pasCourant);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);
                    res1 = this._fonction.getDistanceEuclidienne(this._pixels, dist);

                    // Sens negatif (r)
                    c.setR(r - pasCourant);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);
                    res2 = this._fonction.getDistanceEuclidienne(this._pixels, dist);
                    
                    // Pas d'amelioration (r)
                    c.setR(r);
                    this._translations.set(i, c);
                    dist = this.getZBufferTab(gLDrawable);
                    res3 = this._fonction.getDistanceEuclidienne(this._pixels, dist);
                    
                    if (res1 < res2 && res1 < res3) {
                        
                        // Le sens positif est le meilleur (r)
                        this._distance = res1;
                        c.setR(r + pasCourant);
                        this._translations.set(i, c);
                        this.memo();
                        testPas = false;
                     
                    } else if (res2 < res3) {
                        
                        // Le sens negatif est le meilleur (r)
                        this._distance = res2;
                        c.setR(r - pasCourant);
                        this._translations.set(i, c);
                        this.memo();
                        testPas = false;
                    
                    } else {
                        
                        // Pas d'amelioration (r)
                        this._distance = res3;
                        c.setR(r);
                        this._translations.set(i, c);
                        this.memo();
                        
                    }
                    this._distanceMem = this._distance;

                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                   
                    // S'il n'y a eu aucune amelioration, on diminue le pas
                    if(testPas) {
                        
                        pasCourant = (float) (pasCourant /1.01);
                        
                        // Si le pas est trop petit on ajoute une sphere et on
                        // le reinitisalise
                        if(pasCourant < pasOriginal/1.8) {
                        
                            pasCourant = pasOriginal;
                            this.ajouterSphere();
                            
                            // On met a jour la distance euclidienne par celle avec la nouvelle sphere
                           /* dist = this.getZBufferTab(gLDrawable);
                            float res = this._fonction.getDistanceEuclidienne(this._pixels, dist);
                            this._distanceMem = res;
                            this._distance = res;*/
                            System.out.println("______ NEW ______");
                        }
                        
                    }
                    
                    this._test = true;
                    
                //} 

            }
            
        } else {
            
            this.draw();
            
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
    
    
    //////////////////////////// ALGORITHME /////////////////////////////
    

    /**
     * Permet de mettre a jour les informations sur le resultat
     */
    private void memo() {
        
        // Memorisation du meilleur resultat
        //if (this._distance < this._distanceMem) {

            // Pour l'effet crenaux
            if (this._distanceMem < Float.POSITIVE_INFINITY) {
                this._ms.updateInformations(this._distanceMem + "", this._nbIterations + "");
            }

            this._distanceMem = this._distance;
            this._spheresMem = this._spheres;
            this._translationsMem = this._translations;

            this._ms.setSpheres(this._spheresMem);
            this._ms.setTranslations(this._translationsMem);
            this._ms.updateInformations(this._distance + "", this._nbIterations + "");
            // Mise a jour de la vue finale et de la courbe
            this._ms.updateView(1);
            this._ms.updateView(2);
            this._nbIterationsMem = this._nbIterations;

            System.out.println("Distance euclidienne: " + this._distance + " ("
                    + this._nbIterations + " iterations)");

        /*} else if (this._nbIterations % 25 == 0) {

            // Met a jour les infos toutes les 50 iterations meme s'il n'y a 
            // pas de meilleur score
            this._ms.updateInformations(this._distanceMem + "", this._nbIterations + "");
            this._nbIterationsMem = this._nbIterations;
            // Mise a jour de la courbe
            this._ms.updateView(2);

        }*/
            
    } // memo()
    
    
    /**
     * Permet d'ajouter une sphere a la scene
     */
    public void ajouterSphere() {
 
        GLUquadric qobj1 = _glu.gluNewQuadric();
        this._gl.glPushMatrix();
        this._gl.glColor3f(1, 1, 1);
        this._gl.glTranslatef(0.f, 0.f, 2.f);
        _glu.gluSphere(qobj1, 1.f, 100, 100);
        this._gl.glPopMatrix();
        this._spheres.add(qobj1);            
        this._translations.add(new Coordonnees((float) 0, (float) 0, (float) 0, 0));
        
    } // ajouterSphere()

    
    /**
     * Dessine la scene 3D
     */
    private void draw() {
        
        for (int i = 0; i < this._spheres.size(); i++) {

            GLUquadric qobj1 = this._spheres.get(i);
            this._gl.glPushMatrix();
            this._gl.glTranslatef(this._translations.get(i).getX(), this._translations.get(i).getY(), this._translations.get(i).getZ());
            _glu.gluSphere(qobj1, this._translations.get(i).getR(), 100, 100);
            this._gl.glPopMatrix();

        }

        // Done Drawing 
        this._gl.glEnd();
        
    } // draw()

    
    ///////////////////////////// OBSERVER //////////////////////////////
    
    
    @Override
    public void update(Observable o, Object arg) {

    } // update(Observable o, Object arg)
    

} // class VueSpheresMultiZoom
