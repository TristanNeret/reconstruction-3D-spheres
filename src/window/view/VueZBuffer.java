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
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import window.main.MainSphere;

/**
 * VueZBuffer
 * @author Tristan
 */
public class VueZBuffer extends JPanel implements Observer {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    protected MainSphere _ms;
    private BufferedImage _zbuffer;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public VueZBuffer(MainSphere ms, String path) {
        
        this._ms = ms;
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

    
    ///////////////////////////// OBSERVER //////////////////////////////
    
    
    @Override
    public void update(Observable o, Object arg) {
       
    } // update(Observable o, Object arg)
    
    
} // class VueZBuffer
