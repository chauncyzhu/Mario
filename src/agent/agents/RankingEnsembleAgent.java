/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.agents;

import agent.state.StateAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import problem.Problem;
import util.Util;


/**
 *
 * @author timbrys
 */
public class RankingEnsembleAgent extends EnsembleAgent{
    
    public RankingEnsembleAgent(Problem prob, QLambdaAgent[] agents, Policy policy) {
        super(prob, agents, policy);
    }
    
    public RankingEnsembleAgent(Problem prob, QLambdaAgent[] agents, Policy policy, boolean recording) {
        super(prob, agents, policy, recording);
    }

    @Override
    protected double[] getPreferences(StateAction sa) {
        ArrayList<TreeMap<Double, Integer>> maps = new ArrayList<TreeMap<Double, Integer>>();
        
        for (QLambdaAgent agent : agents) {
            maps.add(new TreeMap<Double, Integer>());
        }
        
        for(int i=0; i<prob.getNumActions(); i++){
            for(int o=0; o<agents.length; o++){
                sa.setAction(i);
                maps.get(o).put(agents[o].getQ(sa), i);
            }
        }
        
        double[] ranking = new double[prob.getNumActions()];
        for(int o=0; o<agents.length; o++){
            Collection<Integer> values = maps.get(o).values();
            Iterator<Integer> it = values.iterator();
//            it.next();
            int i=0;
            while(it.hasNext()){
                ranking[it.next()] += (1.0)*i/(ranking.length-1);
                i++;
            }
        }
        return ranking;
    }
    
}
