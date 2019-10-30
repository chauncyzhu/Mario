/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.demonstration;

import agent.agents.EnsembleAgent;
import agent.agents.GreedyPolicy;
import agent.agents.Policy;
import agent.agents.ProbabilisticPolicyReuse;
import agent.state.QLHash;
import agent.state.StateAction;
import agent.transfer.TransferMapping;
import problem.Problem;
import problem.mario.teaching.TeachingAgent;

/**
 *
 * @author timbrys
 */
public class ExtraActionProblem extends Problem{

    protected Problem problem;
    protected TransferMapping mapping;
    protected Policy policy;
    
    public ExtraActionProblem(Problem problem, QLHash policy, Policy reusePolicy){
        this(problem, policy, reusePolicy, new TransferMapping());
    }
    
    public ExtraActionProblem(Problem problem, QLHash policy, Policy reusePolicy, TransferMapping mapping){
        this.problem = problem;
        this.policy = new ProbabilisticPolicyReuse(new GreedyPolicy(), policy, reusePolicy, 1.0, mapping);
    }
    
    public void setAgents(EnsembleAgent[] agents){
        problem.setAgents(this.agents = agents);
    }


    @Override
    public int getNumActions() {
        return problem.getNumActions()+1;
    }

    @Override
    public StateAction getState(EnsembleAgent agent) {
        return problem.getState(agent);
    }

    public StateAction getState(TeachingAgent agent){return null;}


    @Override
    public double[] step(int[] action) {
        int[] trueAction = new int[action.length];
        for(int i=0; i<action.length; i++){
            if(action[i] == problem.getNumActions()){
                StateAction features = getState(problem.getAgent(i));
                trueAction[i] = policy.chooseAction(features, new double[0]);
            } else {
                trueAction[i] = action[i];
            }
        }
        return problem.step(trueAction);
    }

    @Override
    public boolean isGoalReached(int iteration) {
        return problem.isGoalReached(iteration);
    }

    @Override
    public void reset() {
        problem.reset();
    }
    
}
