/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.demonstration;

import agent.state.StateAction;
import agent.agents.EnsembleAgent;
import agent.agents.GreedyPolicy;
import agent.agents.QLambdaAgent;
import java.util.logging.Level;
import java.util.logging.Logger;
import problem.Problem;
import util.RNG;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author timbrys
 */
public class LfDAgent extends EnsembleAgent{

    private final ConverterUtils.DataSource source;
    private final Instances data;
    private final J48 tree;

    public LfDAgent(Problem prob, String file) throws Exception {
        super(prob, new QLambdaAgent[0], new GreedyPolicy());
        
        source = new ConverterUtils.DataSource(file);
        data = source.getDataSet();
        data.deleteAttributeAt(data.numAttributes() - 2);
        if (data.classIndex() == -1)
          data.setClassIndex(data.numAttributes() - 1);
        
        tree = new J48();         // new instance of tree
        tree.buildClassifier(data);   // build classifier
    }
    
    @Override
    public int chooseAction(StateAction sa) {
        if(source == null){
            return RNG.randomInt(prob.getNumActions());
        }
        try {
            Instances unlabeled = source.getStructure();
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
            unlabeled.add(new Instance(1.0, sa.normalize()));
            
            return ((int)tree.classifyInstance(unlabeled.firstInstance()));
        } catch (Exception ex) {
            Logger.getLogger(LfDAgent.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
            return -1;
        }
    }

    @Override
    protected double[] getPreferences(StateAction sa) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
