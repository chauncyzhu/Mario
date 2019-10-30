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
package problem.mario;

import agent.advice.AdviceShaping;
import agent.agents.EGreedyPolicy;
import agent.agents.EnsembleAgent;
import agent.agents.LinearEnsembleAgent;
import agent.agents.Policy;
import agent.agents.ProbabilisticPolicyReuse;
import agent.agents.QLambdaAgent;
import agent.demonstration.ExtraActionProblem;
import agent.shapings.ConstantShaping;
import agent.shapings.DynamicShaping;
import agent.state.QLHash;
import experiment.Experiment;
import static experiment.Experiment.DEBUG;
import static experiment.Experiment.episode;
import static experiment.Experiment.means;
import java.util.Arrays;
import problem.Problem;
import problem.mario.ch.idsia.benchmark.mario.environments.MarioEnvironment;
import util.Util;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey@idsia.ch Date: May
 * 7, 2009 Time: 4:38:23 PM Package: ch.idsia
 */
public class MarioAdviceExperiment {
    
    public static void main(String[] args) throws Exception {
        Experiment.DEBUG = true;

        int experiments = 1;
    
        double[][] results = new double[experiments][];
        for (int i = 0; i < experiments; i++) {
            if(Experiment.DEBUG){
                results[i] = experiment(new String[]{"1", "1"});
            } else {
                results[i] = experiment(args);
            }
        }
        System.out.println(Arrays.toString(Util.means(results)));
        System.exit(0);
    }
    
    public static double[] experiment(String[] args) throws Exception {
        int episodes = 10000;
        
        double alpha = 0.001;
        double gamma = 0.9;
//        double epsilon = 0.05;
        Policy policy = new EGreedyPolicy(0.05);
        double lambda = 0.5;
        
        EnsembleAgent agent;
        QLambdaAgent learner;
                
        Mario mario = new Mario();
        
        AdviceShaping advice = new AdviceShaping(1.0, gamma);
        
        switch (new Integer(args[0])) {
            default:
            case 0:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 1:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0),
                        new DynamicShaping(1.0, gamma, advice, 0.1, lambda), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
        }
        

        return experiment(mario, agent, 1, episodes, 1, advice);
    }
    
    
    
    public static double[] experiment(Mario prob, EnsembleAgent agent, int experiments, int episodes, int nrCollect, AdviceShaping advice){
        prob.setAgent(agent);
        agent.setRecording(false);
        
        double[][] results = new double[experiments][episodes];
        for(int ex=0; ex<experiments; ex++){
            agent.reset();
            for(int ep=0; ep<episodes; ep++){
                if(ep == episodes-nrCollect){
                    agent.setRecording(true);
                }
                results[ex][ep] = episode(ep, prob, new EnsembleAgent[]{agent}, advice);
                if(DEBUG){
                    System.out.println(ep + ": " + results[ex][ep]);
                }
            }
        }
        return means(results);
    }
    
    public static double episode(int ep, Mario prob, EnsembleAgent[] agents, AdviceShaping advice){
        int it = 0;
        double[] ret;
        double totalReward = 0.0;
        if(ep < 5){
            prob.setVisualize(true);
        } else {
            prob.setVisualize(false);
        }
        prob.reset();
        if(ep < 5){
            System.out.println((MarioEnvironment)prob.getEnvironment());
            System.out.println(((MarioEnvironment)prob.getEnvironment()).getMarioViz());
            ((MarioEnvironment)prob.getEnvironment()).getMarioViz().addKeyListener(advice);
        }
        
        for(EnsembleAgent agent : agents){
            agent.newEpisode();
        }
        while(!prob.isGoalReached(it)){
            it++;
            int[] actions = new int[agents.length];
            for(int i=0; i<agents.length; i++){
                actions[i] = agents[i].getAction();
            }
            ret = prob.step(actions);
            totalReward += ret[1];
            for (EnsembleAgent agent : agents) {
                agent.update(prob.getState(agent), ret[0]);
            }
        }
        for(EnsembleAgent agent : agents){
            agent.endEpisode();
        }
        return totalReward;
    }
}
