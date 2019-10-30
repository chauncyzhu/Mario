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
public class VotingEnsembleAgent extends EnsembleAgent{
    
    public VotingEnsembleAgent(Problem prob, QLambdaAgent[] agents, Policy policy) {
        super(prob, agents, policy);
    }
    
    public VotingEnsembleAgent(Problem prob, QLambdaAgent[] agents, Policy policy, boolean record) {
        super(prob, agents, policy, record);
    }

    @Override
    protected double[] getPreferences(StateAction sa) {
        double[] votes = new double[prob.getNumActions()];
        for (QLambdaAgent agent : agents) {
            double[] Qs = new double[prob.getNumActions()];
            for (int i = 0; i<Qs.length; i++) {
                sa.setAction(i);
                Qs[i] += agent.getQ(sa);
            }
            votes[Util.argMax(Qs)]++;
        }
        return votes;
    }
    
}
