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
    private ArrayList<Float> _translations;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /////////////////////////// CONSTRUCTEUR //////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public MainSphere() {
        
    } // MainSphere()
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
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

    public ArrayList<Float> getTranslations() {
        return _translations;
    }

    public void setTranslations(ArrayList<Float> _translations) {
        this._translations = _translations;
    }
    
    
} // class MainSphere
