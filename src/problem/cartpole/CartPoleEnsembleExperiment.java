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

import agent.agents.*;
import agent.shapings.AvgCombiShaping;
import agent.shapings.ConstantShaping;
import experiment.Experiment;
import util.Util;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey@idsia.ch Date: May
 * 7, 2009 Time: 4:38:23 PM Package: ch.idsia
 */
public class CartPoleEnsembleExperiment {
    
    public static void main(String[] args) throws Exception {
        int experiments = 1;
    
        Experiment.DEBUG = true;
        
        double[][] results = new double[experiments][];
        for (int i = 0; i < experiments; i++) {
            if(Experiment.DEBUG){
                results[i] = experiment(new String[]{"4", "0"});
            } else {
                results[i] = experiment(args);
            }
        }
        System.out.println(Arrays.toString(Util.means(results)));
        System.exit(0);
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
                
        CartPole cp = new CartPole(0.1);
        
        switch (new Integer(args[0])) {
            default:
            case 0:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, -1.0),
                        new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 1:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, 0.0),
                        new ConstantShaping(1.0, gamma, 0.0), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 2:
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, -1.0),
                        new CartPoleHeuristicShaping(new Integer(args[1]), 100.0, gamma), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 3:
                CartPoleHeuristicShaping[] shapings = new CartPoleHeuristicShaping[2];
                for(int i=0; i<shapings.length; i++){
                    shapings[i] = new CartPoleHeuristicShaping(i, 100.0, gamma);
                }
                learner = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, -1.0),
                        new AvgCombiShaping(shapings, gamma), gamma);
                agent = new LinearEnsembleAgent(cp, new QLambdaAgent[]{learner}, policy);
                break;
            case 4:
                agent = new LinearEnsembleAgent(cp, heuristicLearners(alpha, lambda, gamma), policy);
                break;
            case 5:
                agent = new VotingEnsembleAgent(cp, heuristicLearners(alpha, lambda, gamma), policy);
                break;
            case 6:
                agent = new RankingEnsembleAgent(cp, heuristicLearners(alpha, lambda, gamma), policy);
                break;
            case 7:
                agent = new ConfidenceEnsembleAgent(cp, heuristicLearners(alpha, lambda, gamma), policy);
                break;
            case 8:
                agent = new ConfidenceEnsembleAgent(cp, heuristicLearners2(alpha, lambda, gamma), policy);
                break;
        }

        double[] results = Experiment.experiment(cp, agent, 1, episodes, episodes);
        
//        Writer writer = null;
//        try {
//            writer = new BufferedWriter(new OutputStreamWriter(
//                  new FileOutputStream("cp_correlations_episode_lin.txt"), "utf-8"));
////            for(int i=0; i<agent.getTrajectory().size(); i++){
////                for(int j=0; j<agent.getTrajectory().getSample(i).getState().length; j++){
////                    writer.write(agent.getTrajectory().getSample(i).getState()[j] + ",");
////                }
////                writer.write(agent.getTrajectory().getSample(i).getAction() + ",");
////                writer.write(agent.getAgent(0).getV(agent.getTrajectory().getSample(i)) + "\n");
////            }
//            writer.write(Arrays.toString(agent.correlations.toArray()));
//        } catch (IOException ex) {
//           System.out.println("Couldn't write trajectories to file: " + ex.getMessage());
//        } finally {
//           try {writer.close();} catch (Exception ex) {}
//        }
        
        return results;
        
        
        
//        double[] x = RNG.randomDoubleArray(-2.4, 2.4, 10000);
//        double[] xdot = RNG.randomDoubleArray(-6, 6, 10000);
//        double[] t= RNG.randomDoubleArray(-Math.toRadians(12.0d), Math.toRadians(12.0d), 10000);
//        double[] tdot = RNG.randomDoubleArray(-6, 6, 10000);
//        
//        double[] results = Experiment.experiment(cp, agent, 1, episodes);
//        for(int i=0; i<x.length; i++){
////            for(int j=0; j<agent.getTrajectory().getSample(i).getState().length; j++){
////                System.out.print(agent.getTrajectory().getSample(i).getState()[j] + ",");
////            }
//            System.out.print(x[i] + "," + xdot[i] + "," + t[i] + "," + tdot[i] + ",");
//        
//            System.out.println(agent.getAgent(0).getV(new CartPoleStateAction(new double[]{x[i],xdot[i],t[i],tdot[i]}, -1)));
//        }
////        System.out.println(agent.getTrajectory());
//        return results;
    }
    
    protected static QLambdaAgent[] heuristicLearners(double alpha, double lambda, double gamma){
        QLambdaAgent[] learners = new QLambdaAgent[2];
        for(int i=0; i<learners.length; i++){
            learners[i] = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, -1.0),
                        new CartPoleHeuristicShaping(i, 100.0, gamma), gamma);
        }
        return learners;
    }
    
    protected static QLambdaAgent[] heuristicLearners2(double alpha, double lambda, double gamma){
        QLambdaAgent[] learners = new QLambdaAgent[3];
        for(int i=0; i<2; i++){
            learners[i] = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, -1.0),
                        new CartPoleHeuristicShaping(i, 100.0, gamma), gamma);
        }
        CartPoleHeuristicShaping[] shapings = new CartPoleHeuristicShaping[]{new CartPoleHeuristicShaping(0, 100.0, gamma),new CartPoleHeuristicShaping(0, 100.0, gamma)};
        learners[2] = new QLambdaAgent(alpha, lambda, new ConstantShaping(1.0, gamma, -1.0),
                        new AvgCombiShaping(shapings, gamma), gamma);
        return learners;
    }
}
