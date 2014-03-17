/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sphere;

import window.Fenetre;
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
        
        // Permet de creer une image de profondeur
        //Creation scene = new Creation("zbuffer.png",0);
        
        // Permet de recuperer le tableau de pixels d'une image de profondeur
        //Lecture zbuffer = new Lecture("zbuffer.png");
        //float[][] pixels = zbuffer.lireImage();
        
        // Affichage de la fenetre
        Fenetre app = new Fenetre("zbuffer.png");
        
    } // main(String[] args)
    
    
} // class Sphere
