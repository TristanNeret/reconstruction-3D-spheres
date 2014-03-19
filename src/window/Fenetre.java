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
import window.main.MainSphere;
import window.view.AbstractVueGLCanvas;
import window.view.VueFinale;
import window.view.VueInformations;
import window.view.VueSpheres;
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
    private final AbstractVueGLCanvas vs;
    private final AbstractVueGLCanvas vf;
    
    private final int _nbSpheres = 2;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /////////////////////////// CONSTRUCTEUR //////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public Fenetre(String path) {

        super("Reconstruction 3D à partir de sphères");
        this.setBackground(new Color(255,255,255));
        
        this._ms = new MainSphere();
        
        vz = new VueZBuffer(this._ms,path);
        this._ms.addObserver(vz);
        //this.add(vz,BorderLayout.WEST);
        
        vs = new VueSpheres(this._ms,path,this._nbSpheres);
        this._ms.addObserver((Observer) vs);
        //this.add(vs,BorderLayout.EAST);
        
        vf = new VueFinale(this._ms,path);
        this._ms.addObserver((Observer) vf);
        //this.add(vf,BorderLayout.SOUTH);
        
        vi = new VueInformations(this._ms);
        this._ms.addObserver((Observer) vi);
        //this.add(vf,BorderLayout.SOUTH);
                     
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
        
        pack();
        this.setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    } // Fenetre()
    
    
} // class Fenetre
