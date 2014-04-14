/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sphere;

import java.util.logging.Level;
import java.util.logging.Logger;
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
        
        // Permet de creer une image de profondeur et de recuperer le tableau
        // de z-buffer
        /*Creation scene = new Creation("zbuffer_sphere",0,200,200);
        // Attend que scene soit dessinee pour recuperer les donnees z-buffer
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Sphere.class.getName()).log(Level.SEVERE, null, ex);
        }
        scene.afficherCreation(false);
        float[] newZBufferTab = scene.getCreationBufferTab();*/
        
        // Permet de recuperer le tableau de pixels d'une image de profondeur a
        // partir d'un fichier texte
        Lecture zbuffer = new Lecture("zbuffer_sphere.txt");
        float[] pixels = zbuffer.lireTexte();
        
        // Affichage de la fenetre
        Fenetre app = new Fenetre("zbuffer_sphere.png", pixels);
        
    }    
    
} // class Sphere
