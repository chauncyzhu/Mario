package problem.mario.eval;

import agent.agents.EGreedyPolicy;
import agent.agents.Policy;
import agent.shapings.ConstantShaping;
import agent.shapings.PotentialBasedShaping;
import agent.state.QLHash;
import problem.mario.teaching.TeachingAgent;
import util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluation {
    public static void main(String[] args){
        // parameters of agents
        double epsilon = 0.05;
        double alpha = 0.001;
        double gamma = 0.9;
        double lambda = 0.5;
        PotentialBasedShaping potential = new ConstantShaping(1.0, gamma, 0.0);
        PotentialBasedShaping constant = new ConstantShaping(1.0, gamma, 0.0);

        Policy policy = new EGreedyPolicy(epsilon);

        TeachingAgent teacher = new TeachingAgent(policy, alpha, lambda, potential, constant, gamma);
        teacher.loadPolicy("data/teacherS/independent/policy0");

        ArrayList<HashMap<Long, Double>> qTable = teacher.getCmac().getWeights();

        List<Double> qList = new ArrayList<>();
        List<Long> stateActionList = new ArrayList<>();
        HashMap<Long, Integer> visits = teacher.getCmac().getVisitsMap();

        System.out.println("action length:"+qTable.size());

        for (HashMap<Long, Double> val : qTable){
            for (Map.Entry<Long, Double> qOne : val.entrySet()){
                qList.add(qOne.getValue());
                if (qOne.getValue() != 0){
                    stateActionList.add(qOne.getKey());
                }
            }
        }

        List<Double> varianceList = new ArrayList<>();
        List<Double> visitList = new ArrayList<>();

        for (Long val : stateActionList){
            double[] stateQ = new double[qTable.size()];
            for (int i=0; i<qTable.size(); i++){
                stateQ[i] = qTable.get(i).get(val);
            }

            double max = stateQ[Util.argMax(stateQ)];
            double min = stateQ[Util.argMin(stateQ)];

            double maxmin = max - min;

            if (maxmin > 0.1){
                varianceList.add(max-min);
                System.out.println("variance:"+(max-min));
            }

            if (visits.get(val) > 1000){
                visitList.add(1.0 * visits.get(val));
            }
        }

        System.out.println("total length:"+stateActionList.size()+"-variance length:"+
                varianceList.size()+"-percent:"+(varianceList.size()/(1.0*stateActionList.size())));


        System.out.println("total length:"+stateActionList.size()+"-visit length:"+
                visitList.size()+"-percent:"+(visitList.size()/(1.0*stateActionList.size())));

        System.out.println("Max Visit:" + visitList.get(Util.argMax(visitList)) + "-Min visit:" +
                visitList.get(Util.argMin(visitList)));

    }
}
