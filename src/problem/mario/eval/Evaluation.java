package problem.mario.eval;

import agent.agents.EGreedyPolicy;
import agent.agents.Policy;
import agent.shapings.ConstantShaping;
import agent.shapings.PotentialBasedShaping;
import agent.state.QLHash;
import problem.mario.teaching.TeachingAgent;
import problem.mario.utils.Stats;
import util.Util;

import java.util.*;

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

        List<Double> maxminList = new ArrayList<>();
        List<Integer> visitList = new ArrayList<>();

        for (Long val : stateActionList){
            double[] stateQ = new double[qTable.size()];
            for (int i=0; i<qTable.size(); i++){
                stateQ[i] = qTable.get(i).get(val);
            }

            double max = Stats.max(stateQ);
            double min = Stats.min(stateQ);

            double maxmin = max - min;

            double currentvar = Stats.absdeviation(stateQ, Stats.average(stateQ));

            // 0.1 --- total length:522996-variance length:247766-percent:0.4737435850369793
            // 0.2 --- total length:522996-variance length:205431-percent:0.39279650322373405
            // 0.3 --- total length:522996-variance length:172435-percent:0.3297061545403789
            // 0.5 --- total length:522996-variance length:140119-percent:0.2679160070057897
            // 1   --- total length:522996-variance length:80728-percent:0.1543568210846737
            // 1.5 --- total length:522996-variance length:60964-percent:0.11656685710789375
            // 2   --- total length:522996-variance length:47283-percent:0.09040795723103044
            // 3   --- total length:522996-variance length:28866-percent:0.05519353876511484
            // 4   --- total length:522996-variance length:16785-percent:0.032093935708877315
            // 5   --- total length:522996-variance length:9326-percent:0.01783187634322251

            maxminList.add(maxmin);
            visitList.add(visits.get(val));

        }

        Map<Double, List<Double>> proMap = new HashMap<>();
        for (int i=0; i<=10; i++){
            double giveParam = i*0.1;
            List<Double> probList = new ArrayList<>();
            for (int j=0; j<visitList.size();j++){
                int visitTimes = visitList.get(j);
                double maxmin = maxminList.get(j);
                double value = Math.sqrt(visitTimes) * maxmin;
                double prob = 1 - (Math.pow((1 + giveParam),-value));
                probList.add(prob);
            }
            proMap.put(giveParam, probList);
        }

        /**
         * count the number of prob > a
         */

        for (Map.Entry<Double, List<Double>> entry:proMap.entrySet()){
            int[] count = new int[10];
            for (int i=0; i<=10; i++){
                double currProb = i*0.1;
                for (Double val : entry.getValue()){
                    if (val > currProb)
                        count[i]++;
                }
            }
            System.out.println("GiveParam:"+entry.getKey()+ Arrays.toString(count));
        }
    }


}
