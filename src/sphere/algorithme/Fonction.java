/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sphere.algorithme;

/**
 * Fonction
 * @author Tristan
 */
public class Fonction {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /////////////////////////// CONSTRUCTEUR //////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public Fonction() {
        
    } // Fonction()
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Permet de connaitre la distance euclidienne entre deux images
     * @param pixels1 tableau de pixels original
     * @param pixels2 tableau de pixels a comparer
     * @return la disatance euclidienne entre les deux images
     */
    public float getDistanceEuclidienne(float[][] pixels1, float[][] pixels2) {
        
        int width = pixels1.length;
        int height = pixels1[0].length;
        assert(width == pixels2.length) : "ERREUR: (getDistanceEuclidienne) les deux tableaux " 
                + "de pixels n'ont pas les memes dimensions.";
        
        float distance, dTmp;
        distance = 0;
        
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                
                //dTmp = (float) Math.sqrt(Math.pow(pixels1[i][j], 2)+Math.pow(pixels2[i][j], 2));
                //dTmp = Math.abs(pixels1[i][j]-pixels2[i][j]);
                dTmp = (float) Math.pow(pixels1[i][j]-pixels2[i][j],2);
                distance += dTmp;
                
            }
        }
        
        //return distance;
        return (float) Math.sqrt(distance);
        
    } // getDistanceEuclidienne(float[][] pixels1, float[][] pixels2)
    
    
} // class Fonction
