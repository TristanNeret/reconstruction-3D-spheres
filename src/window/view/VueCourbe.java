/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window.view;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import window.main.MainSphere;

/**
 * VueCourbe
 * @author Tristan
 */
public class VueCourbe extends JFrame implements Observer {
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// VARIABLES ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    protected MainSphere _ms;
    protected ArrayList<String> _listeDiff;
    protected ArrayList<String> _listeIterations;
    protected ChartPanel _chartPanel;
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////// CONSTRUCTEUR /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    public VueCourbe(MainSphere ms, int width, int height) {
        
        this._ms = ms;
        this._listeDiff = new ArrayList<>();
        this._listeIterations = new ArrayList<>();
     
        // This will create the dataset 
        DefaultXYDataset dataset = getData();
        // based on the dataset we create the chart
        JFreeChart chart = createChart(dataset, "Courbe d'évolution des résultats");
        // we put the chart into a panel
        this._chartPanel = new ChartPanel(chart);
        // default size
        this._chartPanel.setPreferredSize(new java.awt.Dimension(width*2, height));
        // add it to our application
        this.add(this._chartPanel);
      
        pack();
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    } // VueZBuffer(String path)
    
    
    //////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////////////////////// FONCTIONS ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Met a jour le graph des resultats
     */
    protected void afficherGraph() {
        
        // This will create the dataset 
        DefaultXYDataset dataset = getData();
        // based on the dataset we create the chart
        JFreeChart chart = createChart(dataset, "Courbe d'évolution des résultats");
        this._chartPanel.setChart(chart);
        this.repaint();
        
    } // paintComponent(Graphics g)
    
    
    /**
     * Cree les donnee de la courbe
     * @return le tableau de donnees de la courbe
     */
    protected DefaultXYDataset getData() {
        
        double[][] yValues = new double[2][this._listeDiff.size()];
        double[][] xValues = new double[2][this._listeDiff.size()];

        // X values
        for (int i=0; i<this._listeDiff.size(); i++)
        {
           yValues[0][i] = Double.valueOf(this._listeIterations.get(i));
           xValues[0][i] = Double.valueOf(this._listeIterations.get(i));
        }

        // Y values
        for (int i=0; i<this._listeDiff.size(); i++)
        {
           yValues[1][i] = Double.valueOf(this._listeDiff.get(i));
           xValues[1][i] = Double.valueOf(this._listeDiff.get(i));
        }

        DefaultXYDataset dataset = new DefaultXYDataset();
        dataset.addSeries("Dist. euclidienne", yValues);
        dataset.addSeries("Nb itérations", xValues);
        
        return dataset;
        
    } // updateData()
    
    
    /**
     * Cree la courbe
     * @param dataset donnees de la courbe
     * @param title titre de la courbe
     * @return la courbe correspondant aux donnees
     */
    private JFreeChart createChart(DefaultXYDataset dataset, String title) {
        
      // Create the chart
      JFreeChart chart = ChartFactory.createXYLineChart(
         title,                    // The chart title
         "Nombre d'itérations",    // x axis label
         "Distance euclidienne",   // y axis label
         dataset,                  // The dataset for the chart
         PlotOrientation.VERTICAL,
         false,                    // Is a legend required?
         false,                    // Use tooltips
         false                     // Configure chart to generate URLs?
      );
      
      return chart;
        
    } // createChart(PieDataset dataset, String title)

    
    ///////////////////////////// OBSERVER //////////////////////////////
    
    
    @Override
    public void update(Observable o, Object arg) {
    
        if(arg.toString().equals("2")) {

            this._listeDiff = this._ms.getListeDiff();
            this._listeIterations = this._ms.getListeIterations(); 
            this.afficherGraph();
       
        }
        
    } // update(Observable o, Object arg)
    
    
} // VueCourbe
