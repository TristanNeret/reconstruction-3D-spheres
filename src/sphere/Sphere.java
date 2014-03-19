/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sphere;

import sphere.zbuffer.Creation;
import sphere.zbuffer.Lecture;
import window.Fenetre;
import window.view.AbstractVueGLCanvas;

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
        //AbstractVueGLCanvas scene = new Creation("zbuffer.png",0,200,200);
        
        // Permet de recuperer le tableau de pixels d'une image de profondeur
        //Lecture zbuffer = new Lecture("zbuffer.png");
        //float[][] pixels = zbuffer.lireImage();
        //zbuffer.affichage(pixels, "zbuffer_test.png");
        
        // Affichage de la fenetre
        Fenetre app = new Fenetre("zbuffer.png");
        
    } // main(String[] args)
    
    
} // class Sphere
