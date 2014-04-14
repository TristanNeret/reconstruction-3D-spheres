/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sphere.zbuffer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Lecture
 * @author Tristan
 */
public class Lecture {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    protected String _path;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public Lecture(String path) {
        
        this._path = path;
        
    } // Lecture(String path)
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Permet de recuperer la valeur de chaque pixel d'une image de profondeur
     * stocke dans une image
     * @return un tableau contenant chaque pixel de l'image
     */
    public float[][] lireImage() {
        
        try {
            
            // Chargement de l'image
            BufferedImage zbuffer = ImageIO.read(new File(this._path));
            int width = zbuffer.getWidth();
            int height = zbuffer.getHeight();

            float[][] res = new float[width][height];
            for(int i=0; i<width;i++) {
                
                for(int j=0; j<height;j++) {
                 
                    res[i][j] = (float)(zbuffer.getRGB(i, j)&0x0000FF)/255;
                    
                }
                
            }
            
            return res;
            
        } catch (IOException ex) {
            Logger.getLogger(Lecture.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
        
    } // lireImage()  
    
    
    /**
     * Permet de recuperer la valeur de chaque pixel d'une image de profondeur
     * stocke dans un fichier texte
     * @return un tableau contenant chaque pixel de l'image
     */
    public float[] lireTexte() {
        
        ArrayList<String> liste = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(this._path)))
        {

            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                
                liste.add(currentLine);
                    
            }

        } catch (IOException e) {}
        
        float[] res = new float[liste.size()];
        for(int i=0;i<res.length;i++) {
            
            res[i] = Float.parseFloat(liste.get(i));
            
        }
        
        return res;
        
    } // lireTexte()
    
    
    /**
     * Permet de creer une image a partir d'un tableau de pixels
     * @param pixels tableau de pixels
     * @param file nom de l'image en sortie
     */
    public void affichage(float[][] pixels, String file) {
        
        // Preparation de la sauveagrde de l'image
        BufferedImage bi = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_ARGB);
        
        for (int x=0; x<pixels.length; x++) {
            for (int y=0; y<pixels[0].length/1; y++) {
 
                int color = (int)(pixels[x][y]*255);
                int rgb = new Color(color,color,color).getRGB();
                bi.setRGB(x, y, rgb);
                
            }
        }
        
        // Sauveagrde de l'image
        try {
            
            ImageIO.write(bi, "PNG", new File(file));
            System.out.println("Image " + file + " enregistree !");
            
        } catch (IOException ex) {
            Logger.getLogger(Creation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    } // affichage(float[][] pixels, String file)
    
    
} // class Lecture
