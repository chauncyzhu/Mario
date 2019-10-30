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

import problem.cartpole.*;
import agent.agents.EGreedyPolicy;
import agent.agents.EnsembleAgent;
import agent.agents.LinearEnsembleAgent;
import agent.agents.Policy;
import agent.agents.QLambdaAgent;
import agent.agents.RankingEnsembleAgent;
import agent.agents.VotingEnsembleAgent;
import agent.shapings.AvgCombiShaping;
import agent.shapings.ConstantShaping;
import experiment.Experiment;
import java.util.Arrays;
import util.Util;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey@idsia.ch Date: May
 * 7, 2009 Time: 4:38:23 PM Package: ch.idsia
 */
public class MarioEnsembleExperiment {
    
    public static void main(String[] args) throws Exception {
        Experiment.DEBUG = true;

        int experiments = 1;
    
        double[][] results = new double[experiments][];
        for (int i = 0; i < experiments; i++) {
            if(Experiment.DEBUG){
                results[i] = experiment(new String[]{"7", "1"});
            } else {
                results[i] = experiment(args);
            }
        }
        System.out.println(Arrays.toString(Util.means(results)));
        System.exit(0);
    }
    
    public static double[] experiment(String[] args) throws Exception {
        int episodes = 50000;
        
        double alpha = 0.001;
        double gamma = 0.9;
//        double epsilon = 0.05;
        Policy policy = new EGreedyPolicy(0.05);
        double lambda = 0.5;
        
        EnsembleAgent agent;
        QLambdaAgent learner;
                
        Mario mario = new Mario();
        
        switch (new Integer(args[0])) {
            default:
            case 0:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 1:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 1.0), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 2:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new MarioHeuristicShaping(new Integer(args[1]), 1.0, gamma), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 3:
                MarioHeuristicShaping[] shapings = new MarioHeuristicShaping[]{new MarioHeuristicShaping(0, 1.0, gamma), new MarioHeuristicShaping(1, 1.0, gamma)};
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new AvgCombiShaping(shapings, gamma), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 4:
                agent = new LinearEnsembleAgent(mario, heuristicLearners(alpha, lambda, gamma), policy);
                break;
            case 5:
                agent = new VotingEnsembleAgent(mario, heuristicLearners(alpha, lambda, gamma), policy);
                break;
            case 6:
                agent = new RankingEnsembleAgent(mario, heuristicLearners(alpha, lambda, gamma), policy);
                break;
        }

        return Experiment.experiment(mario, agent, 1, episodes, 1);
    }
    
    protected static QLambdaAgent[] heuristicLearners(double alpha, double lambda, double gamma){
        QLambdaAgent[] learners = new QLambdaAgent[2];
        for(int i=0; i<learners.length; i++){
            learners[i] = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0),
                        new MarioHeuristicShaping(i, 1.0, gamma), gamma);
        }
        return learners;
    }
}
