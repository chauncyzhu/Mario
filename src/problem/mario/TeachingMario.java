package problem.mario;

import agent.agents.EGreedyPolicy;
import agent.agents.Policy;
import agent.shapings.ConstantShaping;
import agent.shapings.PotentialBasedShaping;
import problem.Problem;
import problem.mario.teaching.*;
import problem.mario.utils.DataFile;
import problem.mario.utils.Stats;

import java.io.File;
import java.util.Random;

public class TeachingMario {

    public static String TEACHER = "teacherS"; // Teacher feature set and algorithm
//    public static String STUDENT = "studentS"; // Student feature set and algorithm old customS
    public static String STRATEGY = "baseline"; // Student feature set and algorithm old customS
    public static String EXPLORATION = "eGreedy"; // epsilon-greedy
    public static Boolean DEBUG = true; // print log info
    public static int DEBUG_LENGTH = 100; // print log info

    public static double QTHRESHOLD = 0.2;
    public static double COORECTTHRESHOLD = 0.2;

    public static String DIR = "data/"+TEACHER; // Where to store pacman.data

    public static int BUDGET = 10000; // Advice budget
    public static int REPEATS = 10; // 10 Curves to average
    public static int TRAIN = 30000; // Train episodes

    public static Random rng = new Random();
    //static File file = new File("file.txt");
    //file.getParentFile().mkdirs();

    // parameters of agents
    static double epsilon = 0.05;
    static double alpha = 0.001;
    static double gamma = 0.9;
    static double lambda = 0.5;
    static PotentialBasedShaping potential = new ConstantShaping(1.0, gamma, 0.0);
    static PotentialBasedShaping constant = new ConstantShaping(1.0, gamma, 0.0);


    /**
     * Run experiments.
     */
    public static void main(String[] args) {

        // independent
        train("independent", 0);
//		 findBestTeacher();
//		watch(independent);
//		 train("advise197", 0); //abs 62
        //watch(independent);
        //watch(advise108000);
        //watch(independent);

    }


    /** Set up a learner. */
    public static TeachingAgent create(String learner, Problem prob) {
        Policy policy;
        if (EXPLORATION.equals("eGreedy")){
            policy = new EGreedyPolicy(epsilon);
        }else{
            policy = new EGreedyPolicy(epsilon);
        }

        if (learner.startsWith("independent")) {
            return new TeachingAgent(policy, alpha, lambda, potential, constant, gamma);
        } else if (learner.startsWith("teacherStudent")) {
            TeachingAgent teacher;
            if (TEACHER.endsWith("S")){
                teacher = new TeachingAgent(policy, alpha, lambda, potential, constant, gamma);
                teacher.loadPolicy("data/"+TEACHER+"/independent_policy/policy0");
            } else {
                teacher = new TeachingAgent(policy, alpha, lambda, potential, constant, gamma);
                teacher.loadPolicy("data/"+TEACHER+"/independent_policy/policy0");
            }
            teacher.setProblem(prob);

            TeachingAgent student;
            TeachingStrategy strategy;
            if (STRATEGY.equals("baseline")){
                strategy = new AdviseAtFirst();
            } else if (STRATEGY.equals("advise")) {
                strategy = new AdviseImportantStates(QTHRESHOLD);
            } else if (STRATEGY.equals("correct")) {
                strategy = new CorrectImportantMistakes(COORECTTHRESHOLD);
            } else{
                strategy = null;
            }

            student = new Student(policy, teacher, strategy, alpha, lambda, potential, constant, gamma);

            return student;
        }

        // default agent
        return new TeachingAgent(policy, alpha, lambda, potential, constant, gamma);
    }


    /** Generate learning curves. */
    public static void train(String learner, int start) {

        double[][] results = new double[REPEATS][TRAIN];

        // Begin new curves
        for (int re=start; re<REPEATS; re++) {
            System.out.println("Training "+DIR+"/"+learner+" " + re + "...");
            // create data directory
            learner = learner.equals("independent") ? learner : learner + "/" + STRATEGY;
            File dirFile = new File(DIR+"/"+learner);
            if (!dirFile.exists()){
                dirFile.mkdirs();
            }
            DataFile file = new DataFile(DIR+"/"+learner+"/score"+re);
            file.clear();

            Problem mario = new Mario();
            TeachingAgent marioLeaner = create(learner, mario);   // single learner or student-teacher
            marioLeaner.setProblem(mario);   // the connection between mario and learner, not for teacher

            // set recording demonstrations
            marioLeaner.reset();
            for(int ep=0; ep<TRAIN; ep++){
                results[re][ep] = episode(ep, mario, marioLeaner);
                if(DEBUG && ep % DEBUG_LENGTH == 0){
                    System.out.println("Run:" + re + "--" + ep  + "..."+ TRAIN + "--Episode reward:" + results[re][ep] + "--Sum reward:" + Stats.sum(results)/(ep + 1));
                    if (learner.startsWith("teacherStudent")) {
                        System.out.println("Advice Count:" + ((Student) marioLeaner).getAdviceCount());
                    }
                }
                // record reward data
                file.append(ep+","+results[re][ep]+","+Stats.sum(results)/(ep + 1)+"\n");
            }

            file.close();

            // Save new curve and policy
            if (learner.startsWith("independent")) {
                marioLeaner.savePolicy(DIR+"/"+learner+"/policy"+re);
            }
        }
        System.out.println("Done.");
    }

    /** Train a learner for one more episode. */
    public static double episode(int ep, Problem prob, TeachingAgent agent){
        int it = 0;
        double[] ret;
        double totalReward = 0.0;
        prob.reset();

        agent.newEpisode();

        while(!prob.isGoalReached(it)){
            it++;
            int action = agent.getAction();

            ret = prob.step(action);
            totalReward += ret[1];
            agent.update(prob.getState(agent), ret[0]);

        }
        agent.endEpisode();

        return totalReward;
    }

}
