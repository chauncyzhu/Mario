/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.agents;

import agent.state.StateAction;
import org.apache.commons.math3.stat.inference.TTest;
import problem.Problem;
import util.RNG;

import java.util.ArrayList;


/**
 *
 * @author timbrys
 */
public class ConfidenceEnsembleAgent extends EnsembleAgent{

//    public static double[][][] objectivesSelected = new double[6][101][101];
    
    public ConfidenceEnsembleAgent(Problem prob, QLambdaAgent[] ensemble, Policy policy){
        super(prob, ensemble, policy);
    }
    
    public ConfidenceEnsembleAgent(Problem prob, QLambdaAgent[] ensemble, Policy policy, boolean recording){
        super(prob, ensemble, policy, recording);
    }
    
    protected double confidence(QLambdaAgent agent, StateAction sa){
        
        //Will store best and worst Q-value per objective
        double best = -Double.MAX_VALUE;
        double worst = Double.MAX_VALUE;
        
        //Will store best and worst actions per objective (multiple actions can have same Q-value)
        ArrayList<Integer> ibest = new ArrayList<Integer>();
        ArrayList<Integer> iworst = new ArrayList<Integer>();
        
        StateAction satmp = sa.clone();
        double Q;
        
        //For every action
        for(int i=0; i<prob.getNumActions(); i++){
            satmp.setAction(i);
            Q = agent.getQ(satmp);
            
            //If better than current best ....
            if(Q >= best){
                if(Q > best){
                    ibest.clear();
                }
                ibest.add(i);
                best = Q;
            }
            //If worse than current worst ....
            if(Q <= worst){
                if(Q < worst){
                    iworst.clear();
                }
                iworst.add(i);
                worst = Q;
            }
        }
        
        //For each objective, randomly select a best and worst representative 
        //among the equally good best and worst actions
        int b = ibest.get(RNG.randomInt(ibest.size()));
        int w = iworst.get(RNG.randomInt(iworst.size()));
        
        //Adaptive objective selection
        TTest test = new TTest();

        //Store a p-value for each objective, indicating confidence 
        //in that objective (lower p = higher confidence)
        satmp.setAction(b);
        double[] valuesBest = agent.getValues().getTileValues(satmp);
        satmp.setAction(w);
        double[] valuesWorst = agent.getValues().getTileValues(satmp);
        double p = test.pairedTTest(valuesBest, valuesWorst);

        //When weights are completely the same, the test returns null,
        //catch that and set to 1 (lowest confidence)
        if(Double.isNaN(p)){
            p = 1;
        }
        return 1.0-p;
    }
    
//    public int[][][] getObjectivesSelected(){
//        return objectivesSelected;
//    }

    @Override
    protected double[] getPreferences(StateAction sa) {
        ArrayList<Integer> bestEnsembles = new ArrayList<Integer>();
        double confidence = -1.0;
        double p;
        
        double[] Qs = new double[prob.getNumActions()];
        for(int e=0; e<agents.length; e++){
            p = confidence(agents[e], sa);
            if(p >= confidence){
                if(p > confidence){
                    bestEnsembles.clear();
                }
                confidence = p;
                bestEnsembles.add(e);
            }
        }
        
        StateAction satmp = sa.clone();
        int bestEnsemble = bestEnsembles.get(RNG.randomInt(bestEnsembles.size()));
        for(int i=0; i<Qs.length; i++){
            satmp.setAction(i);
            Qs[i] += agents[bestEnsemble].getQ(satmp);
        }
        
//        int x = (int)(40*Math.abs(sa.getState()[0]+sa.getState()[1])/(Pursuit.size*2.0));
//        int y = (int)(40*(((Pursuit)prob).getAngle())/Math.PI);
//        objectivesSelected[bestEnsemble][x][y]++;
//        objectivesSelected[3][x][y]++;
        
//        int x = (int)(100*(sa.getState()[0] - MountainCar3D.minPosition)/(MountainCar3D.maxPosition-MountainCar3D.minPosition));
//        int y = (int)(100*(sa.getState()[1] - MountainCar3D.minPosition)/(MountainCar3D.maxPosition-MountainCar3D.minPosition));
////        int y = (int)(100*(sa.getState()[1] - MountainCar3D.minVelocity)/(MountainCar3D.maxVelocity-MountainCar3D.minVelocity));
//        objectivesSelected[bestEnsemble][x][y]++;
//        objectivesSelected[5][x][y]++;
        
        return Qs;
    }
    
}
