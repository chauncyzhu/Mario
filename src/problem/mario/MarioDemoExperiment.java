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
import agent.demonstration.Demonstration;
import agent.demonstration.Demonstration2Shaping;
import agent.demonstration.Demonstration3Shaping;
import agent.demonstration.DemonstrationShaping;
import agent.demonstration.ExtraActionProblem;
import agent.demonstration.HATShaping;
import agent.shapings.ConstantShaping;
import agent.shapings.DynamicShaping;
import agent.state.QLHash;
import experiment.Experiment;
import static experiment.Experiment.DEBUG;
import static experiment.Experiment.episode;
import java.util.Arrays;
import problem.Problem;
import util.Util;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey@idsia.ch Date: May
 * 7, 2009 Time: 4:38:23 PM Package: ch.idsia
 */
public class MarioDemoExperiment {
    
    public static void main(String[] args) throws Exception {
        Experiment.DEBUG = true;

		
		
        demonstrateHuman(); //Record demonstration

        System.exit(0);
    }
    
    public static void mergeDemonstrations(){
        String[] files = {"demos/mario_goodrl_-218.0.arff", "demos/mario_goodrl_-266.0.arff", "demos/mario_goodrl_-30.0.arff", "demos/mario_goodrl_-448.0.arff", "demos/mario_goodrl_-458.0.arff", "demos/mario_goodrl_1748.0.arff", "demos/mario_goodrl_2020.0.arff", "demos/mario_goodrl_204.0.arff", "demos/mario_goodrl_2110.0.arff", "demos/mario_goodrl_2452.0.arff", "demos/mario_goodrl_2458.0.arff", "demos/mario_goodrl_2642.0.arff", "demos/mario_goodrl_2662.0.arff", "demos/mario_goodrl_2734.0.arff", "demos/mario_goodrl_276.0.arff", "demos/mario_goodrl_2894.0.arff", "demos/mario_goodrl_2932.0.arff", "demos/mario_goodrl_556.0.arff", "demos/mario_goodrl_806.0.arff", "demos/mario_goodrl_90.0.arff"};
        Demonstration d = new Demonstration(files, MarioStateAction.initState());
        d.toFile("mario_combinedgoodrl_1258.2.arff", MarioStateAction.initState());
    }
    
    public static double[] experiment(String[] args) throws Exception {
        int episodes = 50000;
        
        double alpha = 0.01;
        double gamma = 0.9;
//        double epsilon = 0.05;
        Policy policy = new EGreedyPolicy(0.05);
        double lambda = 0.5;
        
        EnsembleAgent agent;
        QLambdaAgent learner;
                
        MarioStateAction prototype = MarioStateAction.initState();
        
        Problem mario = new Mario();
        
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
                learner = new QLambdaAgent(alpha, lambda, new DemonstrationShaping(1.0, gamma, new Demonstration(args[1], prototype), 0.5), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 3:
                learner = new QLambdaAgent(alpha, lambda, new HATShaping(1.0, gamma, args[1]), new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 4:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new DemonstrationShaping(1.0, gamma, new Demonstration(args[1], prototype), 0.5), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 5:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new HATShaping(1.0, gamma, args[1]), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 6:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new DynamicShaping(1.0, gamma, new DemonstrationShaping(1.0, gamma, new Demonstration(args[1], prototype), 0.5), 0.1, lambda), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 7:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), new DynamicShaping(1.0, gamma, new HATShaping(1.0, gamma, args[1]), 0.1, lambda), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 8:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, new QLHash(alpha, gamma, lambda, new DemonstrationShaping(1.0, gamma, new Demonstration(args[1], prototype), 0.5)), new GreedyPolicy(), 1.0, 0.99, 0.0));
                break;
            case 9:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new ProbabilisticPolicyReuse(policy, new QLHash(alpha, gamma, lambda, new HATShaping(1.0, gamma, args[1])), new GreedyPolicy(), 1.0, 0.99, 0.0));
                break;
            case 10:
                mario = new ExtraActionProblem(mario, new QLHash(alpha, gamma, lambda, new DemonstrationShaping(1.0, gamma, new Demonstration(args[1], prototype), 0.5)), new GreedyPolicy());
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 11:
                mario = new ExtraActionProblem(mario, new QLHash(alpha, gamma, lambda, new HATShaping(1.0, gamma, args[1])), new GreedyPolicy());
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                break;
            case 12:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, policy);
                feedDemonstration(mario, agent, new Demonstration(args[1], prototype));
                break;
        }

        return Experiment.experiment(mario, agent, 1, episodes, 1);
    }
    
    protected static void feedDemonstration(Problem prob, EnsembleAgent agent, Demonstration demo){
        prob.setAgents(new EnsembleAgent[]{agent});
        agent.reset();
        
        boolean newEp = true;
        
        for(int i=0; i<demo.size(); i++){
            if(newEp){
                prob.reset();
                agent.newEpisode();
                newEp = false;
            }
            
            agent.update(demo.getSample(i), demo.getReward(i), true);
            
            if(demo.getReward(i) <= -512 || demo.getReward(i) >= 1024 ){
                agent.endEpisode();
                newEp = true;
            }
        }
    }
    
    protected static void demonstrateHuman(){
        Mario mario = new Mario();
        EnsembleAgent agent = new HumanAgent(mario, mario.getMario());
        double[] results = Experiment.experiment(mario, agent, 1, 1, 1);
        agent.getTrajectory().toFile("demos/mario_human_" + results[0] + ".arff", MarioStateAction.initState());
		
    }
    
    protected static void demonstrateSimple(){
        Mario mario = new Mario();
        EnsembleAgent agent = new SimpleAgent(mario);
        for(int i=0; i<20; i++){
            double[] results = Experiment.experiment(mario, agent, 1, 1, 1);
            agent.getTrajectory().toFile("demos/mario_simple_" + results[0] + ".arff", MarioStateAction.initState());  
            agent.getTrajectory().clear();
        }
    }
    
    protected static void demonstrateBadRL(){
        Mario mario = new Mario();
        QLambdaAgent learner = new QLambdaAgent(0.01, 0.5, new ConstantShaping(1.0, 0.9, 1.0), new ConstantShaping(1.0, 0.9, 0.0), 0.9);
        EnsembleAgent agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new EGreedyPolicy(0.05));
        for(int i=0; i<1; i++){
            double[] results = Experiment.experiment(mario, agent, 1, 100, 1);
            agent.getTrajectory().toFile("demos/mario_badrl_" + results[99] + ".arff", MarioStateAction.initState());  
            agent.getTrajectory().clear();
        }
    }
    
    protected static void demonstrateGoodRL(){
        Mario mario = new Mario();
        QLambdaAgent learner = new QLambdaAgent(0.01, 0.5, new ConstantShaping(1.0, 0.9, 1.0), new ConstantShaping(1.0, 0.9, 0.0), 0.9);
        EnsembleAgent agent = new LinearEnsembleAgent(mario, new QLambdaAgent[]{learner}, new EGreedyPolicy(0.05));

        for(int i=0; i<20; i++){
            double[] results = Experiment.experiment(mario, agent, 1, 5000, 1);
            agent.getTrajectory().toFile("mario_goodrl_" + results[4999] + ".arff", MarioStateAction.initState());  
            agent.getTrajectory().clear();
        }
    }
}
