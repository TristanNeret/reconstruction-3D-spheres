/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * VueZBuffer
 * @author Tristan
 */
public class VueZBuffer extends JPanel {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    private BufferedImage _zbuffer;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public VueZBuffer(String path) {
        
        try {
            
            this._zbuffer = ImageIO.read(new File(path));
            
            // Dimension de la fenetre
            this.setPreferredSize(new Dimension(this._zbuffer.getWidth(), this._zbuffer.getHeight()));
            
        } catch (IOException ex) {
            Logger.getLogger(VueZBuffer.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    } // VueZBuffer(String path)
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        g.drawImage(this._zbuffer, 0, 0, null);  
        
    } // paintComponent(Graphics g)
    
    
} // class VueZBuffer
