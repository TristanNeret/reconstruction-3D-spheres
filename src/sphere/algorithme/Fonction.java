/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sphere.algorithme;

import java.util.HashMap;
import window.main.Coordonnees;

/**
 * Fonction
 * @author Tristan
 */
public class Fonction {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * 1 - on travaille sur x
     * 2 - on travaille sur y
     * 3 - on travaille sur z
     * 4 - on travaille sur r
     */
    protected HashMap<Integer,Integer> _coordTest;
    /**
     * TRUE = on avance dans le sens positif
     * FALSE = on avance dans le sens negatif
     */
    protected HashMap<Integer,Boolean> _coordSens;
    protected HashMap<Integer,Coordonnees> _coordMem;
    protected double _pas;
    protected int _sphere;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /////////////////////////// CONSTRUCTEUR //////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public Fonction() {
        
        this._coordTest = new HashMap<>();
        this._coordSens = new HashMap<>();
        this._coordMem = new HashMap<>();
        this._pas = 0.1;

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
    
    
    /**
     * Permet de recuperer les nouvelles coordonnees a tester pour la sphere
     * @param prec coordonnees precedentes de la sphere
     * @param numSphere numero de la sphere
     * @param distPrec distance euclidienne precedente
     * @param dist distance euclidienne actuelle
     * @return les nouvelles coordonnees de la sphere
     */
    public Coordonnees getNewCoordonnees(Coordonnees prec, int numSphere, float distPrec, float dist) {
        
        // On memorise les informations de chaque sphere
        if (this._coordMem.isEmpty()) this._sphere = numSphere;
        if(!this._coordMem.containsKey(numSphere)) {
            this._coordMem.put(numSphere, prec);
            this._coordTest.put(numSphere, 1);
            this._coordSens.put(numSphere, false);
        }

        Coordonnees result = prec; 
        if(this._sphere == numSphere) {
     
            numSphere = this._sphere;
            if(distPrec < dist && this._coordSens.get(numSphere)) {
                this._coordTest.put(numSphere, this._coordTest.get(numSphere)+1);
                this._coordSens.put(numSphere,false);
                result = this._coordMem.get(numSphere);
            } else if(distPrec < dist && !this._coordSens.get(numSphere)) {
                this._coordSens.put(numSphere,true);
                result = this._coordMem.get(numSphere);
            } else {
                this._coordMem.put(numSphere, result);
            }

            switch (this._coordTest.get(numSphere)) {

                // Travaille sur x
                case 1:
                    // Effectue les modifications sur les coordonnees
                    if(this._coordSens.get(numSphere)) {
                        result.setX((float) (prec.getX()+this._pas));
                    } else {
                        result.setX((float) (prec.getX()-this._pas));
                    }
                    // Change le sens de lecture si necessaire
                    if(result.getX() < -2) {
                        this._coordSens.put(numSphere,true);
                        result.setX(this._coordMem.get(numSphere).getX());
                    } else if(result.getX() > 2) {
                        this._coordSens.put(numSphere,false);
                        this._coordTest.put(numSphere,this._coordTest.get(numSphere)+1);
                    }
                    break;

                // Travaille sur y
                case 2:
                    // Effectue les modifications sur les coordonnees
                    if(this._coordSens.get(numSphere)) {
                        result.setY((float) (prec.getY()+this._pas));
                    } else {
                        result.setY((float) (prec.getY()-this._pas));
                    }
                    // Change le sens de lecture si necessaire
                    if(result.getY() < -2) {
                        this._coordSens.put(numSphere,true);
                        result.setY(this._coordMem.get(numSphere).getY());
                    } else if(result.getY() > 2) {
                        this._coordSens.put(numSphere,false);
                        this._coordTest.put(numSphere,this._coordTest.get(numSphere)+1);
                    }
                    break;

                // Travaille sur z
                case 3:
                    // Effectue les modifications sur les coordonnees
                    if(this._coordSens.get(numSphere)) {
                        result.setZ((float) (prec.getZ()+this._pas));
                    } else {
                        result.setZ((float) (prec.getZ()-this._pas));
                    }
                    // Change le sens de lecture si necessaire
                    if(result.getZ() < -5) {
                        this._coordSens.put(numSphere,true);
                        result.setZ(this._coordMem.get(numSphere).getZ());
                    } else if(result.getZ() > 5) {
                        this._coordSens.put(numSphere,false);
                        this._coordTest.put(numSphere,this._coordTest.get(numSphere)+1);
                    }
                    break;

                // Travaille sur r
                case 4:
                    // Effectue les modifications sur les coordonnees
                    if(this._coordSens.get(numSphere)) {
                        result.setR((float) (prec.getR()+this._pas));
                    } else {
                        result.setR((float) (prec.getR()-this._pas));
                    }
                    // Change le sens de lecture si necessaire
                    if(result.getR() < -5) {
                        this._coordSens.put(numSphere,true);
                        result.setR(this._coordMem.get(numSphere).getR());
                    } else if(result.getR() > 5) {
                        this._coordSens.put(numSphere,false);
                        this._coordTest.put(numSphere,this._coordTest.get(numSphere)+1);
                    }
                    break;

                default:
                    /*this._coordTest.put(numSphere,1);
                    this._coordSens.put(numSphere,false);
                    this._pas = this._pas/1.001;*/
                    this._sphere = (this._sphere+1)%this._coordMem.size();
                    break;

            }
        
        }
        
        return result;
        
    } // getNewCoordonnees(Coordonnees prec, float distPrec, float dist)
    
    
} // class Fonction
