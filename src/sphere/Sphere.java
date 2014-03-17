/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sphere;

import zbuffer.Creation;
import zbuffer.Lecture;

/**
 * Sphere
 * @author Tristan
 */
public class Sphere {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Creation scene = new Creation("zbuffer.png",0);
        Lecture zbuffer = new Lecture("zbuffer.png");
        
        float[][] pixels = zbuffer.lireImage();
        //zbuffer.affichage(pixels,"zbuffer_test.png");
        
    } // main(String[] args)
    
    
} // class Sphere
