/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package agent.agents;

import agent.state.QLHash;
import agent.shapings.PotentialBasedShaping;
import agent.state.StateAction;
import agent.shapings.ConstantShaping;
import agent.shapings.Shaping;
import problem.Problem;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: Sep 1, 2009 Time: 3:12:07 PM Package: competition.cig.sergeykarakovskiy
 */
public class QLambdaAgent{
    
    protected QLHash cmac;
    
    protected double alpha;
    protected double gamma;
    protected double lambda;
    
    protected Shaping shape;
    protected PotentialBasedShaping initialization;
    
    protected Problem prob;
    
    public QLambdaAgent(double alpha, double lambda, Shaping shape, double gamma){
        this(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), shape, gamma);
    }
    
    public QLambdaAgent(double alpha, double lambda, PotentialBasedShaping initialization, Shaping shape, double gamma){
        this.alpha = alpha;
        this.gamma = gamma;
        this.lambda = lambda;
        
        this.shape = shape;
        this.initialization = initialization;
        
        reset();
    }

    public QLHash getCmac(){
        return cmac;
    }
    
    public void reset(){
        cmac = new QLHash(alpha, gamma, lambda, initialization);
    }
    
    public void setProblem(Problem prob){
        this.prob = prob;
        this.shape.setProblem(prob);
        this.initialization.setProblem(prob);
    }
    
    public double getQ(StateAction stateaction){
        return cmac.getValue(stateaction);
    }
    
    public double[] getQs(StateAction stateaction){
        return cmac.getValues(stateaction, prob.getNumActions());
    }
    
    public double getV(StateAction stateaction){
        StateAction sa = stateaction.clone();
        double value, bestvalue = -Double.MAX_VALUE;
        for(int i=0; i<prob.getNumActions(); i++){
            sa.setAction(i);
            value = cmac.getValue(sa);
            if(value > bestvalue){
                bestvalue = value;
            }
        }
        return bestvalue;
    }
    
    public void update(StateAction previous, double reward, StateAction next) {
        StateAction state = next.clone();
        
        cmac.setTraces(previous);
        
        double delta = shape.shape(previous, next, reward);
        delta -= getQ(previous);
        
        double[] Qs = new double[prob.getNumActions()];
        double best = -Double.MAX_VALUE;
        for(int i=0; i<prob.getNumActions(); i++){
            state.setAction(i);
            Qs[i] = getQ(state);
            if(Qs[i] > best){
                best = Qs[i];
            }
        }
        delta = delta + gamma*best;
        
        cmac.update(alpha*delta);
        
        if(Qs[next.getAction()] == best){
            cmac.decay();
        } else {
            cmac.resetEs();
        }
    }
    
    public void endEpisode(StateAction previous) {
        StateAction dummy = previous.clone();
        dummy.setAction(-1);
        
        double delta = shape.shape(previous, dummy, 0.0);
        delta -= getQ(previous);
        
        cmac.update(alpha*delta);
        
        shape.endEpisode();
        
        cmac.resetEs();
    }
    
    public QLHash getValues(){
        return cmac;
    }
}
