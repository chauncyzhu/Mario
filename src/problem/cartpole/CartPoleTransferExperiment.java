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
package problem.cartpole;

import agent.agents.DeterministicGreedyPolicy;
import agent.agents.EGreedyPolicy;
import agent.agents.EnsembleAgent;
import agent.agents.GreedyPolicy;
import agent.agents.LinearEnsembleAgent;
import agent.agents.Policy;
import agent.agents.ProbabilisticPolicyReuse;
import agent.agents.QLambdaAgent;
import agent.demonstration.ExtraActionProblem;
import agent.shapings.ConstantShaping;
import agent.shapings.DynamicShaping;
import agent.state.QLHash;
import agent.transfer.TransferMapping;
import agent.transfer.PolicyTransferShaping;
import agent.transfer.QValueTransferShaping;
import experiment.Experiment;
import java.util.Arrays;
import problem.Problem;
import util.Util;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey@idsia.ch Date: May
 * 7, 2009 Time: 4:38:23 PM Package: ch.idsia
 */
public class CartPoleTransferExperiment {
    
    public static void main(String[] args) throws Exception {
        int experiments = 1;
    
        Experiment.DEBUG = false;
        
        double[][] results = new double[experiments][];
        for (int i = 0; i < experiments; i++) {
            if(Experiment.DEBUG){
                results[i] = experiment(new String[]{"2"});
            } else {
                results[i] = experiment(args);
            }
        }
        System.out.println(Arrays.toString(Util.means(results)));
        System.exit(0);
    }
    
    public static QLHash learnSourceTask(){
        double alpha = 0.25/(CartPoleStateAction.nrTiles);
        double gamma = 1.0;
//        double epsilon = 0.05;
        Policy policy = new EGreedyPolicy(0.05);
        double lambda = 0.25;
        
        CartPole cp = new CartPole(0.1);
        QLambdaAgent learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
        EnsembleAgent agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
        Experiment.experiment(cp, agent, 1, 250);
        return agent.getAgent(0).getValues();
    }
    
    public static double[] experiment(String[] args) throws Exception {
        int episodes = 1000;
        
        double alpha = 0.25/(CartPoleStateAction.nrTiles);
        double gamma = 1.0;
//        double epsilon = 0.05;
        Policy policy = new EGreedyPolicy(0.05);
        double lambda = 0.25;
        
        EnsembleAgent agent;
        QLambdaAgent learner;
                
        Problem cp = new CartPole(1.0);
        
        switch (new Integer(args[0])) {
            default:
            case 0:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 1:
                learner = new QLambdaAgent(alpha, lambda, new PolicyTransferShaping(1.0, gamma, learnSourceTask(), cp, new DeterministicGreedyPolicy()),
                        new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 2:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0),
                        new DynamicShaping(1.0, gamma, 
                                new PolicyTransferShaping(1.0, gamma, learnSourceTask(), cp, new DeterministicGreedyPolicy()), alpha*2, lambda), 
                        gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 3:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new DeterministicGreedyPolicy(), 1.0, 0.99, 0.0));
                break;
            case 4:
                cp = new ExtraActionProblem(cp, learnSourceTask(), new DeterministicGreedyPolicy());
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 5:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0),
                        new DynamicShaping(1.0, gamma, 
                                new PolicyTransferShaping(1.0, gamma, learnSourceTask(), cp, new DeterministicGreedyPolicy()), alpha*2, lambda), 
                        gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new DeterministicGreedyPolicy(), 1.0, 0.99, 0.0));
                break;
            case 6:
                QLHash source = learnSourceTask();
                learner = new QLambdaAgent(alpha, lambda, new PolicyTransferShaping(1.0, gamma, source, cp, new DeterministicGreedyPolicy()),
                        new DynamicShaping(1.0, gamma, 
                                new PolicyTransferShaping(1.0, gamma, source, cp, new DeterministicGreedyPolicy()), alpha*2, lambda), 
                        gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 7:
                QLHash source2 = learnSourceTask();
                cp = new ExtraActionProblem(cp, source2, new DeterministicGreedyPolicy());
                learner = new QLambdaAgent(alpha, lambda, new PolicyTransferShaping(1.0, gamma, source2, cp, new DeterministicGreedyPolicy()),
                        new DynamicShaping(1.0, gamma, 
                                new PolicyTransferShaping(1.0, gamma, source2, cp, new DeterministicGreedyPolicy()), alpha*2, lambda), 
                        gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, source2, new DeterministicGreedyPolicy(), 1.0, 0.99, 0.0));
                break;
            case 8:
                learner = new QLambdaAgent(alpha, lambda, new QValueTransferShaping(1.0, gamma, learnSourceTask()), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
        }

        return Experiment.experiment(cp, agent, 1, episodes);
    }
    
}
