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

import agent.agents.EGreedyPolicy;
import agent.agents.EnsembleAgent;
import agent.agents.GreedyPolicy;
import agent.agents.LinearEnsembleAgent;
import agent.agents.Policy;
import agent.agents.ProbabilisticPolicyReuse;
import agent.agents.QLambdaAgent;
import agent.agents.SoftMaxPolicy;
import agent.demonstration.ExtraActionProblem;
import agent.shapings.ConstantShaping;
import agent.shapings.DynamicShaping;
import agent.shapings.PotentialBasedShaping;
import agent.shapings.SumCombiShaping;
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
public class MarioTransferExperiment {
    
    public static void main(String[] args) throws Exception {
        Experiment.DEBUG = true;

        int experiments = 1;
    
        double[][] results = new double[experiments][];
        for (int i = 0; i < experiments; i++) {
            if(Experiment.DEBUG){
                results[i] = experiment(new String[]{"21"});
            } else {
                results[i] = experiment(args);
            }
        }
        System.out.println(Arrays.toString(Util.means(results)));
        System.exit(0);
    }
    
    public static QLHash learnSourceTask(){
        double alpha = 0.001;
        double gamma = 0.9;
        Policy policy = new EGreedyPolicy(0.05);
        double lambda = 0.5;
        
        Mario cp = new Mario(false,false);
        QLambdaAgent learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
        EnsembleAgent agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
        Experiment.experiment(cp, agent, 1, 1000);
        return agent.getAgent(0).getValues();
    }
    
    public static QLHash learnDoubleSourceTask(){
        double alpha = 0.001;
        double gamma = 0.9;
        Policy policy = new EGreedyPolicy(0.05);
        double lambda = 0.5;
        
        Mario cp = new Mario(false,false);
        QLambdaAgent learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
        EnsembleAgent agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
        Experiment.experiment(cp, agent, 1, 500);
        QLHash values = agent.getAgent(0).getValues();
        
        cp = new Mario(true,true);
        learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
        agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, values, new SoftMaxPolicy(5.0), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
        Experiment.experiment(cp, agent, 1, 500);
        return agent.getAgent(0).getValues();
    }
    
    public static double[] experiment(String[] args) throws Exception {
        int episodes = 50000;
        
        double alpha = 0.001;
        double gamma = 0.9;
        Policy policy = new EGreedyPolicy(0.05);
        double lambda = 0.5;
        
        EnsembleAgent agent;
        QLambdaAgent learner;
                
        Problem mario = new Mario();
        
        double initial = 0;
        
        switch (new Integer(args[0])) {
            default:
            case 0:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 1:
                learner = new QLambdaAgent(alpha, lambda, new SumCombiShaping(new PotentialBasedShaping[]{new ConstantShaping(1.0, gamma, initial), new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioNoEnemiesMapping(), mario, new GreedyPolicy())}, gamma),
                        new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 2:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial),
                        new DynamicShaping(1.0, gamma, 
                                new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioNoEnemiesMapping(), mario, new GreedyPolicy()), alpha, lambda), 
                        gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 3:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new GreedyPolicy(), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
            case 4:
                mario = new ExtraActionProblem(mario, learnSourceTask(), new GreedyPolicy(), new MarioNoEnemiesMapping());
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 5:
                QLHash source = learnSourceTask();
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial),
                        new DynamicShaping(1.0, gamma, 
                                new PolicyTransferShaping(1.0, gamma, source, new MarioNoEnemiesMapping(), mario, new GreedyPolicy()), alpha/10, lambda), 
                        gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, source, new GreedyPolicy(), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
            case 6:
                QLHash source2 = learnSourceTask();
                learner = new QLambdaAgent(alpha, lambda, new SumCombiShaping(new PotentialBasedShaping[]{new ConstantShaping(1.0, gamma, initial), new PolicyTransferShaping(1.0, gamma, source2, new MarioNoEnemiesMapping(), mario, new GreedyPolicy())}, gamma),
                        new DynamicShaping(1.0, gamma, 
                                new PolicyTransferShaping(1.0, gamma, source2, new MarioNoEnemiesMapping(), mario, new GreedyPolicy()), alpha/10, lambda), 
                        gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 7:
                learner = new QLambdaAgent(alpha, lambda, new QValueTransferShaping(1.0, gamma, learnSourceTask(), new MarioNoEnemiesMapping()),
                        new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
                
            case 8:
                learner = new QLambdaAgent(alpha, lambda, new SumCombiShaping(new PotentialBasedShaping[]{new ConstantShaping(1.0, gamma, initial), new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioNoEnemiesMapping(), mario, new SoftMaxPolicy(5.0))}, gamma),
                        new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 9:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial),
                        new DynamicShaping(1.0, gamma, 
                                new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioNoEnemiesMapping(), mario, new SoftMaxPolicy(5.0)), alpha, lambda), 
                        gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 10:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new SoftMaxPolicy(5.0), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
            case 11:
                mario = new ExtraActionProblem(mario, learnSourceTask(), new SoftMaxPolicy(5.0), new MarioNoEnemiesMapping());
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 12:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new SoftMaxPolicy(1.0), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
                
            case 13:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new SoftMaxPolicy(2.5), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
            case 14:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new SoftMaxPolicy(5.0), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
            case 15:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new SoftMaxPolicy(7.5), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
            case 16:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new SoftMaxPolicy(10.0), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
            case 17:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new SoftMaxPolicy(0.1), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
            case 18:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new SoftMaxPolicy(100.0), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
            case 19:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new SoftMaxPolicy(0.01), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
            case 20:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnSourceTask(), new SoftMaxPolicy(50), 1.0, 0.99, 0.0, new MarioNoEnemiesMapping()));
                break;
                
            case 21:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, initial), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, learnDoubleSourceTask(), new SoftMaxPolicy(5.0), 1.0, 0.99, 0.0, new TransferMapping()));
                break;
                
//            case 20:
//                learner = new QLambdaAgent(alpha, lambda, new SumCombiShaping(new PotentialBasedShaping[]{new ConstantShaping(1.0, gamma, initial), new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioMapping(), mario, new SoftMaxPolicy(1.0))}, gamma),
//                        new ConstantShaping(1.0, gamma, 0.0), gamma);
//                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
//                break;
//            case 21:
//                learner = new QLambdaAgent(alpha, lambda, new SumCombiShaping(new PotentialBasedShaping[]{new ConstantShaping(1.0, gamma, initial), new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioMapping(), mario, new SoftMaxPolicy(2.5))}, gamma),
//                        new ConstantShaping(1.0, gamma, 0.0), gamma);
//                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
//                break;
//            case 22:
//                learner = new QLambdaAgent(alpha, lambda, new SumCombiShaping(new PotentialBasedShaping[]{new ConstantShaping(1.0, gamma, initial), new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioMapping(), mario, new SoftMaxPolicy(5.0))}, gamma),
//                        new ConstantShaping(1.0, gamma, 0.0), gamma);
//                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
//                break;
//            case 23:
//                learner = new QLambdaAgent(alpha, lambda, new SumCombiShaping(new PotentialBasedShaping[]{new ConstantShaping(1.0, gamma, initial), new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioMapping(), mario, new SoftMaxPolicy(7.5))}, gamma),
//                        new ConstantShaping(1.0, gamma, 0.0), gamma);
//                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
//                break;
//            case 24:
//                learner = new QLambdaAgent(alpha, lambda, new SumCombiShaping(new PotentialBasedShaping[]{new ConstantShaping(1.0, gamma, initial), new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioMapping(), mario, new SoftMaxPolicy(10.0))}, gamma),
//                        new ConstantShaping(1.0, gamma, 0.0), gamma);
//                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
//                break;
//            case 25:
//                learner = new QLambdaAgent(alpha, lambda, new SumCombiShaping(new PotentialBasedShaping[]{new ConstantShaping(1.0, gamma, initial), new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioMapping(), mario, new SoftMaxPolicy(0.1))}, gamma),
//                        new ConstantShaping(1.0, gamma, 0.0), gamma);
//                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
//                break;
//            case 26:
//                learner = new QLambdaAgent(alpha, lambda, new SumCombiShaping(new PotentialBasedShaping[]{new ConstantShaping(1.0, gamma, initial), new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioMapping(), mario, new SoftMaxPolicy(100.0))}, gamma),
//                        new ConstantShaping(1.0, gamma, 0.0), gamma);
//                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
//                break;
//            case 27:
//                learner = new QLambdaAgent(alpha, lambda, new SumCombiShaping(new PotentialBasedShaping[]{new ConstantShaping(1.0, gamma, initial), new PolicyTransferShaping(1.0, gamma, learnSourceTask(), new MarioMapping(), mario, new SoftMaxPolicy(0.01))}, gamma),
//                        new ConstantShaping(1.0, gamma, 0.0), gamma);
//                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
//                break;
        }

        return Experiment.experiment(mario, agent, 1, episodes, 1);
    }
}
