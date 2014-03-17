/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
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
    
    
    private final VueZBuffer vz;
    private final VueSpheres vs;
    
    private int _nbSpheres = 3;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /////////////////////////// CONSTRUCTEUR //////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public Fenetre(String path) {

        super("Reconstruction 3D à partir de sphères");
        this.setBackground(new Color(255,255,255));
        
        vz = new VueZBuffer(path);
        //this.add(vz,BorderLayout.WEST);
        
        vs = new VueSpheres(path,this._nbSpheres);
        //this.add(vs,BorderLayout.EAST);
                     
        // Split entre les vues
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   this.vz, this.vs);
        splitPane.setEnabled(false);
        this.add(splitPane,BorderLayout.NORTH);
        
        pack();
        this.setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    } // Fenetre()
    
    
} // class Fenetre
