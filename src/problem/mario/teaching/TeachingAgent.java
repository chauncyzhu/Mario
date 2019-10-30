package problem.mario.teaching;

import agent.agents.Policy;
import agent.agents.QLambdaAgent;
import agent.shapings.PotentialBasedShaping;
import agent.shapings.Shaping;
import agent.state.QLHash;
import agent.state.StateAction;
import problem.mario.utils.DataOperator;
import util.Util;

import java.io.File;

public class TeachingAgent extends QLambdaAgent{
    protected StateAction prevSA;
    protected Policy policy;


    public TeachingAgent(Policy policy, double alpha, double lambda, PotentialBasedShaping initialization, Shaping shape, double gamma){
        super(alpha, lambda, initialization, shape, gamma);
        this.policy = policy;
    }


    public void newEpisode(){
        prevSA = prob.getState(this);
        prevSA.setAction(chooseAction(prevSA));
    }

    protected int chooseAction(StateAction sa){
        return policy.chooseAction(sa, getPreferences(sa));
    }


    protected int chooseBestAction(StateAction sa){
        return Util.argMax(getPreferences(sa));
    }


    protected double[] getPreferences(StateAction sa) {
        double[] Qs = new double[prob.getNumActions()];
        for (int i = 0; i<Qs.length; i++) {
            sa.setAction(i);
            Qs[i] += getQ(sa);
        }
        return Qs;
    }

    public int getAction(){
        return prevSA.getAction();
    }


    public void update(StateAction sa, double reward) {
        update(sa, reward, false);
    }

    public void update(StateAction sa, double reward, boolean passive){
        update(sa, reward, passive, true);
    }

    public void update(StateAction sa, double reward, boolean passive, boolean learning) {
        if(!passive){
            sa.setAction(chooseAction(sa));  // next action
        }

        if(learning){
            update(prevSA, reward, sa);

            // set state visits
            cmac.setVisits(prevSA);
        }

        prevSA = sa;
    }

    public void endEpisode() {
        endEpisode(prevSA);

        policy.endEpisode();
//        correlations.add(correlation/stepCounter);
//        correlation = 0.0;
//        stepCounter = 0;
    }


    /** Return to a policy from a file. */
    public void loadPolicy(String filename){
        // load Q-table : cmac
        cmac = (QLHash) DataOperator.getObjectByObjectInput(new File((filename)));
    }

    /** save to a policy from a file. */
    public void savePolicy(String filename){
        // save Q-table : cmac
        DataOperator.saveObjectByObjectOutput(cmac, new File(filename));
    }


}
