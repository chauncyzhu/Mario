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
import agent.agents.VotingEnsembleAgent;
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
public class GammaMarioAdviceExperiment {
    
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
        int episodes = 1000;
        
        double alpha = 0.001;
        double gamma = 0.9;
        Policy policy = new MarioSimplePolicy(0.1);
        double lambda = 0.5;
        
        AdviceRewardMario mario = new AdviceRewardMario();
        Mario testmario = new Mario();
        double[] gammas = new double[]{0.9};//, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1, 0.0};
        
        EnsembleAgent agent;
        QLambdaAgent[] learners = new QLambdaAgent[gammas.length];
        
        switch (new Integer(args[0])) {
            default:
            case 0:
                for(int i=0; i<learners.length; i++){
                    learners[i] = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gammas[i]);
                }
                agent = new VotingEnsembleAgent(mario, learners, policy);
                break;
        }
        

        return experiment(mario, testmario, agent, 1, episodes, 1);
    }
    
    
    
    public static double[] experiment(AdviceRewardMario prob, Mario testprob, EnsembleAgent agent, int experiments, int episodes, int nrCollect){
        prob.setAgent(agent);
        agent.setRecording(false);
        
        double[][] results = new double[experiments][episodes];
        for(int ex=0; ex<experiments; ex++){
            agent.reset();
            for(int ep=0; ep<episodes; ep++){
                if(ep == episodes-nrCollect){
                    agent.setRecording(true);
                }
                results[ex][ep] = episode(ep, prob, testprob, agent);
                if(DEBUG){
                    System.out.println(ep + ": " + results[ex][ep]);
                }
            }
        }
        return means(results);
    }
    
    public static double episode(int ep, AdviceRewardMario prob, Mario testprob, EnsembleAgent agent){
        
        
        if(ep % 5 == 0){
            int it = 0;
            double[] ret;
            double totalReward = 0.0;
            prob.setVisualize(true);
            prob.reset();
    //            System.out.println((MarioEnvironment)prob.getEnvironment());
    //            System.out.println(((MarioEnvironment)prob.getEnvironment()).getMarioViz());
            ((MarioEnvironment)prob.getEnvironment()).getMarioViz().addKeyListener(prob);

            agent.setProblem(prob);
            agent.setPolicy(new MarioSimplePolicy(0.1));
            agent.newEpisode();
            
            while(!prob.isGoalReached(it)){
                it++;
                int action = agent.getAction();
                ret = prob.step(action);
                totalReward += ret[1];
                agent.update(prob.getState(agent), ret[0], false);
            }
            agent.endEpisode();
            
        }
        
        int it = 0;
        double[] ret;
        double totalReward = 0.0;
        testprob.setVisualize(false);
        testprob.reset();

        agent.setProblem(testprob);
        agent.setPolicy(new EGreedyPolicy(0.05));
        agent.newEpisode();

        while(!testprob.isGoalReached(it)){
            it++;
            int action = agent.getAction();
            ret = testprob.step(action);
            totalReward += ret[1];
            agent.update(testprob.getState(agent), ret[0], false, true);
        }
        agent.endEpisode();
        return totalReward;
    }
    
//    public static double episode(int ep, AdviceRewardMario prob, Mario testprob, EnsembleAgent agent){
//        
//        
//        int it = 0;
//        double[] ret;
//        double totalReward = 0.0;
//        testprob.setVisualize(false);
//        testprob.reset();
////            System.out.println((MarioEnvironment)prob.getEnvironment());
////            System.out.println(((MarioEnvironment)prob.getEnvironment()).getMarioViz());
////        ((MarioEnvironment)prob.getEnvironment()).getMarioViz().addKeyListener(prob);
//
//        agent.setProblem(testprob);
//        agent.setPolicy(new MarioSimplePolicy(0.1));
//        agent.newEpisode();
//
//        while(!testprob.isGoalReached(it)){
//            it++;
//            int action = agent.getAction();
//            ret = testprob.step(action);
//            totalReward += ret[1];
//            agent.update(testprob.getState(agent), ret[0], false);
//        }
//        agent.endEpisode();
//            
//            
//        it = 0;
//
//        totalReward = 0.0;
//        testprob.setVisualize(false);
//        testprob.reset();
//
//        agent.setProblem(testprob);
//        agent.setPolicy(new EGreedyPolicy(0.05));
//        agent.newEpisode();
//
//        while(!testprob.isGoalReached(it)){
//            it++;
//            int action = agent.getAction();
//            ret = testprob.step(action);
//            totalReward += ret[1];
//            agent.update(testprob.getState(agent), ret[0], false, true);
//        }
//        agent.endEpisode();
//        return totalReward;
//    }
}
