/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window.main;

import java.util.ArrayList;
import java.util.Observable;
import javax.media.opengl.glu.GLUquadric;

/**
 * MainSphere
 * @author Tristan
 */
public class MainSphere extends Observable {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    private ArrayList<GLUquadric> _spheres;
    private ArrayList<Coordonnees> _translations;
    private String _diff;
    private String _nbIterations;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /////////////////////////// CONSTRUCTEUR //////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public MainSphere() {
        
    } // MainSphere()
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Permet de mettre a jour les informations sur le derouement de 
     * l'algorithme
     * @param diff meilleur difference calculee entre les 2 z-buffer
     * @param nbIterations nombre d'iterations pour y arriver
     */
    public void updateInformations(String diff, String nbIterations) {
        
        this._diff = diff;
        this._nbIterations = nbIterations;
        
    } // updateInformations(String diff, String nbIterations)
    
    
    /**
     * Permet d'effacer
     */
    public void updateView() {
        
        setChanged();
        notifyObservers("1");
        
    } // updateView()
    
    
    //////////////////////////// GETTER/SETTER ///////////////////////////
    

    public ArrayList<GLUquadric> getSpheres() {
        return _spheres;
    }

    public void setSpheres(ArrayList<GLUquadric> _spheres) {
        this._spheres = _spheres;
    }

    public ArrayList<Coordonnees> getTranslations() {
        return _translations;
    }

    public void setTranslations(ArrayList<Coordonnees> _translations) {
        this._translations = _translations;
    }

    public String getDiff() {
        return _diff;
    }

    public void setDiff(String _diff) {
        this._diff = _diff;
    }

    public String getNbIterations() {
        return _nbIterations;
    }

    public void setNbIterations(String _nbIterations) {
        this._nbIterations = _nbIterations;
    }
    
    
} // class MainSphere
