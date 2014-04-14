/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import sphere.zbuffer.Lecture;
import window.main.MainSphere;
import window.view.AbstractVueGLCanvas;
import window.view.VueCourbe;
import window.view.VueFinale;
import window.view.VueInformations;
import window.view.VueSpheres;
import window.view.VueSpheresHillClimbing;
import window.view.VueZBuffer;

/**
 * Fenetre
 * @author Tristan
 */
public class Fenetre extends JFrame {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    private final MainSphere _ms;
    private final VueZBuffer vz;
    private final VueInformations vi;
    private final VueCourbe vc;
    private final AbstractVueGLCanvas vs;
    private final AbstractVueGLCanvas vf;
    
    private final int _nbSpheres = 2;
    private int _width;
    private int _height;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /////////////////////////// CONSTRUCTEUR //////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public Fenetre(String path, float[] zBufferTab) {

        super("Reconstruction 3D à partir de sphères");
        this.setBackground(new Color(255,255,255));
        
        // Recuperation de la largeur et de la hauteur de l'image
        this.saveWidthHeight(path);
        
        // Recuperation du tableau de profondeur a deux entrees
        float[][] newZbufferTab = this.convertToDoubleEntryTab(zBufferTab, this._width, this._height);
        
        this._ms = new MainSphere();
        
        // Vue de l'image de profondeur de depart
        vz = new VueZBuffer(this._ms,path);
        this._ms.addObserver(vz);
        
        // Vue de la fentre 3D
        //vs = new VueSpheres(this._ms,newZbufferTab,this._width,this._height,this._nbSpheres);
        vs = new VueSpheresHillClimbing(this._ms,newZbufferTab,this._width,this._height,this._nbSpheres);
        this._ms.addObserver((Observer) vs);
        
        // Vue du meilleur resultat observe
        vf = new VueFinale(this._ms,this._width,this._height);
        this._ms.addObserver((Observer) vf);
        
        // Informations sur le meilleur resultat observe
        vi = new VueInformations(this._ms);
        this._ms.addObserver((Observer) vi);
                     
        // Split entre les vues
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   this.vz, this.vs);
        splitPane1.setEnabled(false);
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   this.vf, this.vi);
        splitPane2.setEnabled(false);
        JSplitPane splitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                   splitPane1, splitPane2);
        splitPane3.setEnabled(false);
        this.add(splitPane3,BorderLayout.NORTH);
        
        // Courbe d'evolution
        vc = new VueCourbe(this._ms,this._width,this._height);
        this._ms.addObserver((Observer)vc);
        //this.add(vc,BorderLayout.SOUTH);
        
        pack();
        this.setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    } // Fenetre()
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Permet de recuperer et de memoriser la largeur et la hauteur de l'image
     * d'origine
     * @param path chemin menant a l'image d'origine 
     */
    public void saveWidthHeight(String path) {
        
        // Recuperation des donnees du z-buffer
        Lecture lecture = new Lecture(path);
        float[][] zBufferImage = lecture.lireImage();
        
        // Mise a jour de la largeur et de la hauteur 
        this._width = zBufferImage.length;
        this._height = zBufferImage[0].length;
        
    } // updateWidthHeight(String path)
    
    
    /**
     * Permet de convertir un tableau a une entree en tableau a double entree
     * @param floatTab tableau initial a une entree
     * @param width largeur de tableau
     * @param height hauteur du tableau
     * @return un tableau a deux entrees
     */
    public float[][] convertToDoubleEntryTab(float[] floatTab, int width, int height) {
        
        float[][] res = new float[width][height];
        for(int x=0;x<width;x++) {
            
            for(int y=0;y<height;y++) {
                
                res[x][y] = floatTab[x+y*width];
                
            }
            
        }
        
        return res;
        
    } // convertToDoubleEntryTab(float[] floatTab, int width, int height)
    
    
} // class Fenetre
