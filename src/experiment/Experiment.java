/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiment;

import agent.agents.EnsembleAgent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import problem.Problem;
import util.RNG;

/**
 *
 * @author timbrys
 */
public class Experiment {
    
    public static boolean DEBUG = false;

    public static double[] experiment(Problem prob, EnsembleAgent agent, int experiments, int episodes){
        return experiment(prob, agent, experiments, episodes, 1);
    }
    
    public static double[] experiment(Problem prob, EnsembleAgent agent, int experiments, int episodes, int nrCollect){
        return experiment(prob, new EnsembleAgent[]{agent}, experiments, episodes, nrCollect);
    }
    
//        prob.setAgents(new EnsembleAgent[]{agent});
//        agent.setRecording(false);
//        
//        double[][] results = new double[experiments][episodes];
//        for(int ex=0; ex<experiments; ex++){
//            agent.reset();
//            for(int ep=0; ep<episodes; ep++){
//                if(ep == episodes-nrCollect){
//                    agent.setRecording(true);
//                }
//                results[ex][ep] = episode(ep, prob, agent);
//                if(DEBUG){
//                    System.out.println(ep + ": " + results[ex][ep]);
//                }
//            }
//        }
//        return means(results);
//    }
    
//    public static double episode(int ep, Problem prob, EnsembleAgent agent){
//        int it = 0;
//        double[] ret;
//        double totalReward = 0.0;
//        prob.reset();
//        agent.newEpisode();
//        while(!prob.isGoalReached(it)){
//            it++;
//            ret = prob.step(new int[]{agent.getAction()});
//            totalReward += ret[1];
//            agent.update(prob.getState(agent), ret[0]);
//        }
//        agent.endEpisode();
//        return totalReward;
//    }
    
    public static double[] experiment(Problem prob, EnsembleAgent[] agents, int experiments, int episodes){
        return experiment(prob, agents, experiments, episodes, 1);
    }
    
    public static double[] experiment(Problem prob, EnsembleAgent[] agents, int experiments, int episodes, int nrCollect){
        prob.setAgents(agents);
        for(EnsembleAgent agent : agents){
            agent.setRecording(false);
        }
        
        double[][] results = new double[experiments][episodes];
        for(int ex=0; ex<experiments; ex++){
            for(EnsembleAgent agent : agents){
                agent.reset();
            }
            for(int ep=0; ep<episodes; ep++){
                if(ep == episodes-nrCollect){
                    for(EnsembleAgent agent : agents){
                        agent.setRecording(false);
                    }
                }
                results[ex][ep] = episode(ep, prob, agents);
                if(DEBUG){
                    System.out.println("Curr Episode:" + ep + ": " + results[ex][ep]);
                }
            }
        }
        return means(results);
    }
    
    public static double episode(int ep, Problem prob, EnsembleAgent[] agents){
        int it = 0;
        double[] ret;
        double totalReward = 0.0;
        prob.reset();
        
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
    
    public static double sum(double[] res){
        double sum = 0.0;
        for(int i=0; i<res.length; i++){
            sum += res[i];
        }
        return sum;
    }
    
    public static void printArray(int[][] matrix, int which){
        System.out.print("figure; plot([");
        for(int i=0; i<matrix[0].length; i++){
            System.out.print(1.0*matrix[which][i]/matrix[3][i] + ",");
        }
        System.out.println("]);");
    }
    
    
    public static void printMatrix(int[][][] matrix){
        System.out.print("figure; surf([");
        for(int i=0; i<matrix[0].length; i++){
            System.out.print("[");
            for(int j=0; j<matrix[0][i].length; j++){
                int ob = -1;
                
                int best = -1;
                ArrayList<Integer> ibestp = new ArrayList<Integer>();
                for(int o=0; o<3; o++){
                    if(matrix[o][i][j] >= best){
                        if(matrix[o][i][j] > best){
                            ibestp.clear();
                        }
                        best = matrix[o][i][j];
                        ibestp.add(o);
                    }
                }

                ob = ibestp.get(RNG.randomInt(ibestp.size()));
                System.out.print(ob + ",");
            }
            System.out.print("];");
        }
        System.out.println("]);");
    }
    
    public static void printMatrix(int[][] matrix, int[][] total){
        System.out.print("figure; surf([");
        for(int i=0; i<matrix.length; i++){
            System.out.print("[");
            for(int j=0; j<matrix[i].length; j++){
                if(total[i][j] == 0){
                    System.out.print(0.0 + ",");
                } else {
                    System.out.print(1.0*matrix[i][j]/total[i][j] + ",");
                }
            }
            System.out.print("];");
        }
        System.out.println("]);");
    }
    
    public static double[] means(double[][] stats){
        double[] means = new double[stats[0].length];
        for(int j=0; j<means.length; j++){
            for(int i=0; i<stats.length; i++){
                means[j] += stats[i][j];
            }
            means[j] = 1.0*means[j]/(stats.length);
        }
        return means;
    }
    
    public static double[] xs(double[][] stats, int every){
        double[] x = new double[stats[0].length/every];
        for(int i=0; i<stats[0].length; i+=every){
            x[i/every] = i;
        }
        return x;
    }
    
    public static double[] ys(double[] stats, int every){
        double[] x = new double[stats.length/every];
        for(int i=0; i<stats.length; i+=every){
            x[i/every] = stats[i];
        }
        return x;
    }
    
    public static double[][] stds(double[][] stats, int every){
        double[] mean = means(stats);
        
        double[] stdL = new double[stats[0].length/every];
        double[] stdU = new double[stats[0].length/every];
        int counterL = 0;
        int counterU = 0;
        
        for(int j=0; j<stats[0].length; j+=every){
            for(int i=0; i<stats.length; i++){
                if(stats[i][j] < mean[j]){
                    stdL[j/every] += Math.pow(stats[i][j] - mean[j], 2.0);
                    counterL++;
                } else {
                    stdU[j/every] += Math.pow(stats[i][j] - mean[j], 2.0);
                    counterU++;
                }
            }
            if(counterL > 0){
                stdL[j/every] = Math.sqrt(stdL[j/every]/counterL);
            }
            if(counterU > 0){
                stdU[j/every] = Math.sqrt(stdU[j/every]/counterU);
            }
            
//            stdL[j/every] = stdL[j/every];
//            stdU[j/every] = stdU[j/every];
        }
        
        return new double[][]{stdL, stdU};
    }
    
    public static double[] runningAVG(double[] data, int n){
        double[] data2 = new double[data.length-n];
        double avg = 0.0;
        for(int i=0; i<n; i++){
            avg += data[i];
        }
        data2[0] = avg/n;
        for(int i=1; i<data.length-n; i++){
            avg -= data[i-1] + data[i+n-1];
            data2[i] = avg/n;
        }
        return data2;
    }
    
}
