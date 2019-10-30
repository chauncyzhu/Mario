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

import agent.agents.EGreedyPolicy;
import agent.agents.EnsembleAgent;
import agent.agents.GreedyPolicy;
import agent.agents.LinearEnsembleAgent;
import agent.agents.Policy;
import agent.agents.ProbabilisticPolicyReuse;
import agent.agents.QLambdaAgent;
import agent.demonstration.Demonstration;
import agent.demonstration.DemonstrationShaping;
import agent.demonstration.ExtraActionProblem;
import agent.demonstration.HATShaping;
import agent.shapings.ConstantShaping;
import agent.shapings.DynamicShaping;
import agent.state.QLHash;
import experiment.Experiment;
import java.util.Arrays;
import problem.Problem;
import util.Util;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey@idsia.ch Date: May
 * 7, 2009 Time: 4:38:23 PM Package: ch.idsia
 */
public class CartPoleDemoExperiment {
    
    public static void main(String[] args) throws Exception {
        Experiment.DEBUG = false;

//        mergeDemonstrations();
//        demonstrateSimple();
        int experiments = 1;
    
        double[][] results = new double[experiments][];
        for (int i = 0; i < experiments; i++) {
            if(Experiment.DEBUG){
                results[i] = experiment(new String[]{"0", "demos/cp_combinedgoodrl_.arff"});
            } else {
                results[i] = experiment(args);
            }
        }
        System.out.println(Arrays.toString(Util.means(results)));
        System.exit(0);
    }
    
    public static void mergeDemonstrations(){
        String[] files = {"demos/cp_human_116.arff", "demos/cp_human_156.arff", "demos/cp_human_26.arff", "demos/cp_human_28.arff", "demos/cp_human_29.arff", "demos/cp_human_31.arff", "demos/cp_human_32.arff", "demos/cp_human_35.arff", "demos/cp_human_38.arff", "demos/cp_human_78.arff"};
        Demonstration d = new Demonstration(files, CartPoleStateAction.initState());
        d.toFile("cp_human_combined.arff", CartPoleStateAction.initState());
    }
    
    public static double[] experiment(String[] args) throws Exception {
        int episodes = 5000;
        
        double alpha = 0.25/(CartPoleStateAction.nrTiles);
        double gamma = 1.0;
        Policy policy = new EGreedyPolicy(0.05);
        double lambda = 0.25;
        
        EnsembleAgent agent;
        QLambdaAgent learner;
                
        CartPoleStateAction prototype = CartPoleStateAction.initState();
        
        Problem cp = new CartPole(0.1);
        
        switch (new Integer(args[0])) {
            default:
            case 0:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 1:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 2:
                learner = new QLambdaAgent(alpha, lambda, new DemonstrationShaping(1.0, gamma, new Demonstration(args[1], prototype), 0.5), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 3:
                learner = new QLambdaAgent(alpha, lambda, new HATShaping(1.0, gamma, args[1]), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 4:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new DemonstrationShaping(1.0, gamma, new Demonstration(args[1], prototype), 0.5), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 5:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new HATShaping(1.0, gamma, args[1]), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 6:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new DynamicShaping(1.0, gamma, new DemonstrationShaping(1.0, gamma, new Demonstration(args[1], prototype), 0.5), alpha*2, lambda), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 7:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new DynamicShaping(1.0, gamma, new HATShaping(1.0, gamma, args[1]), alpha*2, lambda), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 8:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, new QLHash(alpha, gamma, lambda, new DemonstrationShaping(1.0, gamma, new Demonstration(args[1], prototype), 0.5)), new GreedyPolicy(), 1.0, 0.99, 0.0));
                break;
            case 9:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, new QLHash(alpha, gamma, lambda, new HATShaping(1.0, gamma, args[1])), new GreedyPolicy(), 1.0, 0.99, 0.0));
                break;
            case 10:
                cp = new ExtraActionProblem(cp, new QLHash(alpha, gamma, lambda, new DemonstrationShaping(1.0, gamma, new Demonstration(args[1], prototype), 0.5)), new GreedyPolicy());
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 11:
                cp = new ExtraActionProblem(cp, new QLHash(alpha, gamma, lambda, new HATShaping(1.0, gamma, args[1])), new GreedyPolicy());
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 12:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                feedDemonstration(cp, agent, new Demonstration(args[1], prototype));
                break;
            case 13:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, new QLHash(alpha, gamma, lambda, new DemonstrationShaping(1.0, gamma, new Demonstration(args[1], prototype), 0.5)), new GreedyPolicy(), 1.0, 1.0, 0.0));
                break;
            case 14:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, new QLHash(alpha, gamma, lambda, new HATShaping(1.0, gamma, args[1])), new GreedyPolicy(), 1.0, 1.0, 0.0));
                break;
        }

        return Experiment.experiment(cp, agent, 1, episodes, 1);
    }
    
    protected static void feedDemonstration(Problem prob, EnsembleAgent agent, Demonstration demo){
        prob.setAgents(new EnsembleAgent[]{agent});
        agent.reset();
        
        boolean newEp = true;
        int step = 0;
        
        for(int i=0; i<demo.size(); i++){
            if(newEp){
                prob.reset();
                agent.newEpisode();
                newEp = false;
            }
            
            agent.update(demo.getSample(i), demo.getReward(i), true);
            step++;
            
            if(demo.getReward(i) == -1 || step == 1000){
                agent.endEpisode();
                step = 0;
                System.out.println("End");
                newEp = true;
            }
        }
    }
    
    protected static void demonstrateSimple(){
        CartPole cp = new CartPole(0.1);
        EnsembleAgent agent = new SimpleAgent(cp);
        for(int i=0; i<20; i++){
            double[] results = Experiment.experiment(cp, agent, 1, 1, 1);
            agent.getTrajectory().toFile("demos/cp_simple_" + results[0] + "_" + System.currentTimeMillis() + ".arff", CartPoleStateAction.initState());  
            agent.getTrajectory().clear();
        }
    }
    
    protected static void demonstrateBadRL(){
        CartPole cp = new CartPole(0.1);
        QLambdaAgent learner = new QLambdaAgent(0.25/(CartPoleStateAction.nrTiles), 0.25, new ConstantShaping(1.0, 1.0, 0.0), new ConstantShaping(1.0, 1.0, 0.0), 1.0);
        EnsembleAgent agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, new EGreedyPolicy(0.05));
        for(int i=0; i<20; i++){
            double[] results = Experiment.experiment(cp, agent, 1, 200, 1);
            agent.getTrajectory().toFile("demos/cp_badrl_" + results[199] + "_" + System.currentTimeMillis() + ".arff", CartPoleStateAction.initState());  
            agent.getTrajectory().clear();
        }
    }
    
    protected static void demonstrateGoodRL(){
        CartPole cp = new CartPole(0.1);
        QLambdaAgent learner = new QLambdaAgent(0.25/(CartPoleStateAction.nrTiles), 0.25, new ConstantShaping(1.0, 1.0, 0.0), new ConstantShaping(1.0, 1.0, 0.0), 1.0);
        EnsembleAgent agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, new EGreedyPolicy(0.05));
        for(int i=0; i<20; i++){
            double[] results = Experiment.experiment(cp, agent, 1, 1000, 1);
            agent.getTrajectory().toFile("demos/cp_goodrl_" + results[999] + "_" + System.currentTimeMillis() + ".arff", CartPoleStateAction.initState());  
            agent.getTrajectory().clear();
        }
    }
}
