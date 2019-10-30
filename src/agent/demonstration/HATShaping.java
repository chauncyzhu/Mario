/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.demonstration;

import agent.state.StateAction;
import agent.shapings.PotentialBasedShaping;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author timbrys
 */
public class HATShaping extends PotentialBasedShaping{

    
    private final ConverterUtils.DataSource source;
    private final Instances data;
    private final J48 tree;
    
    public HATShaping(double scaling, double gamma, String file) throws Exception{
        super(scaling, gamma);
        source = new ConverterUtils.DataSource(file);
        data = source.getDataSet();
        data.deleteAttributeAt(data.numAttributes() - 2);
        if (data.classIndex() == -1)
          data.setClassIndex(data.numAttributes() - 1);
        
        tree = new J48();         // new instance of tree
        tree.buildClassifier(data);   // build classifier
    }
    
    @Override
    protected double actualPotential(StateAction sa) {
        try {
            Instances unlabeled = source.getStructure();
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
            unlabeled.add(new Instance(1.0, sa.normalize()));
            
            if(((int)tree.classifyInstance(unlabeled.firstInstance())) == sa.getAction()){
                return 1.0;
            } else {
                return 0.0;
            }
        } catch (Exception ex) {
            Logger.getLogger(HATShaping.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
            return 0.0;
        }
    }
    
}
