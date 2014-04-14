/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window.view;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import window.main.MainSphere;

/**
 * VueInformations
 * @author Tristan
 */
public class VueInformations extends JPanel implements Observer {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    protected MainSphere _ms;
    protected GridLayout _gl;
    protected JLabel _titre;
    protected JLabel _distance;
    protected JLabel _distance_value;
    protected JLabel _iterations;
    protected JLabel _iterations_value;
    protected final String _distance_texte = "Distance euclidienne: ";
    protected final String _iterations_texte = "Nombre d'iterations: "; 
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public VueInformations(MainSphere ms) {
        
        this._ms = ms;
        this._gl = new GridLayout(5,1);
        this.setLayout(this._gl);
        
        this._titre = new JLabel("Informations");
        this._titre.setHorizontalAlignment(JLabel.CENTER);
        this._titre.setFont(new Font("helvetica", Font.BOLD, 16));
        this.add(this._titre);
        
        this._distance = new JLabel(this._distance_texte);
        this._distance.setHorizontalAlignment(JLabel.CENTER);
        this.add(this._distance);
        this._distance_value = new JLabel("-");
        this._distance_value.setHorizontalAlignment(JLabel.CENTER);
        this._distance_value.setFont(new Font("helvetica", Font.BOLD, 16));
        this.add(this._distance_value);
        
        this._iterations = new JLabel(this._iterations_texte);
        this._iterations.setHorizontalAlignment(JLabel.CENTER);
        this.add(this._iterations);
        this._iterations_value = new JLabel("-");
        this._iterations_value.setHorizontalAlignment(JLabel.CENTER);
        this._iterations_value.setFont(new Font("helvetica", Font.BOLD, 16));
        this.add(this._iterations_value);
       
      
    } // VueZBuffer(String path)
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    
    ///////////////////////////// OBSERVER //////////////////////////////
    
    
    @Override
    public void update(Observable o, Object arg) {
        
        if(arg.toString().equals("1")) {
        
            this._distance_value.setText(this._ms.getDiff());
            this._iterations_value.setText(this._ms.getNbIterations());
        
        }
       
    } // update(Observable o, Object arg)
    
    
} // class VueZBuffer
