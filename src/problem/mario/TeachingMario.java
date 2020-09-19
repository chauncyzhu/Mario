package problem.mario;

import agent.agents.EGreedyPolicy;
import agent.agents.Policy;
import agent.shapings.ConstantShaping;
import agent.shapings.PotentialBasedShaping;
import javafx.beans.binding.DoubleExpression;
import problem.Problem;
import problem.mario.teaching.*;
import problem.mario.utils.DataFile;
import problem.mario.utils.Stats;

import org.apache.commons.cli.*;

import java.io.File;

public class TeachingMario {

    public static int REPEATS = 60; // 10 Curves to average
    public static int TRAIN = 50000; // Train episodes

    // independent will simply use independent learners whatever the STUDENT and STRATEGY are assigned.
    // teacherStudent will use STRATEGY rather than STUDENT
    // reusingAdvice will consider STRATEGY and STUDENT together.
    public static String TEACHER = "teacherS";  // Teacher algorithm
    public static String STUDENT;  // different students: studentS (default), change, budget, decay, margin, qChange
    public static String STRATEGY;  // baseline, advise, correct, askCorrect, adhoctd
    public static Boolean DEBUG = true; // print log info
    public static int DEBUG_LENGTH = 3000; // print log info
    public static String DIR = "data/"+TEACHER; // Where to store data

    // parameters of agents
    static double epsilon = 0.05;
    static double alpha = 0.001;
    static double gamma = 0.9;
    static double lambda = 0.5;
    static PotentialBasedShaping potential = new ConstantShaping(1.0, gamma, 0.0);
    static PotentialBasedShaping constant = new ConstantShaping(1.0, gamma, 0.0);

    // params of teacher-student
    public static int BUDGET = Integer.MAX_VALUE; // Advice budget
    public static double TQTHRESHOLD;
    public static double SQTHRESHOLD;
    public static double ASKPARAME;  // lower value means higher asking prob, 2 means agent asks for advice when visit times are smaller than 30
    public static double GIVEPARAM;  // higher give param means higher giving prob, 1 means give advice with prob 0.5 when visit times is 10000
    public static int beginEpisodes = 0;  // the episode that an agent asks for advice

    // params of reusing
    public static double QTHRED;
    public static int REUSINGBUDGET;
    public static double DECAY;

    /*
     * Results Description
     * regular:    Run:1--9000...30000--Episode reward:2810.0--Sum reward:1159.3731807576935
                    Advice Count:69705--cumReuseTimes:0
     * decay-0.5:  Run:0--9000...30000--Episode reward:-394.0--Sum reward:1257.7404732807465
                    Advice Count:46192--cumReuseTimes:212981
       decay-0.6:  Run:0--9000...30000--Episode reward:678.0--Sum reward:1258.0368847905788
                    Advice Count:44859--cumReuseTimes:262815
                   Run:0--15000...30000--Episode reward:2850.0--Sum reward:1346.5374308379442
                    Advice Count:45993--cumReuseTimes:299033
       decay-0.65: Run:0--9000...30000--Episode reward:2502.0--Sum reward:1023.5522719697811
                    Advice Count:43957--cumReuseTimes:292110
       decay-0.7:  Run:0--9000...30000--Episode reward:1702.0--Sum reward:1065.3722919675592
                    Advice Count:43376--cumReuseTimes:332954
       decay-0.8:  Run:0--9000...30000--Episode reward:-546.0--Sum reward:1091.3549605599378
                    Advice Count:41616--cumReuseTimes:451576
     * budget-5:   Run:0--3000...30000--Episode reward:-450.0--Sum reward:924.3605464845052
                    Advice Count:35801--cumReuseTimes:150887
       budget-3:   Run:0--24000...30000--Episode reward:2458.0--Sum reward:1468.5357276780135
                    Advice Count:44841--cumReuseTimes:130655
     */


    /**
     * Run experiments.
     */
    public static void main(String[] args) throws Exception{
        // Create a Parser
        CommandLineParser parser = new DefaultParser();
        Options options = new Options( );

        /* Set the appropriate variables based on supplied options
        -m: running mode
        -s: student strategy. must be set when m = "r"
         */
        options.addOption("t", "StartTime", true, "start time");
        options.addOption("m", "Mode", true, "experiment mode");
        options.addOption("sty", "Strategy", true, "advising strategy"); // baseline, advise, correct, askCorrect, adhoctd
        options.addOption("stu", "Student", true, "student type"); // baseline, advise, correct, askCorrect, adhoctd

        // importance parameters
        options.addOption("sq", "SQThreshold", true, "student q threshold of importance");
        options.addOption("tq", "TQThreshold", true, "teacher q threshold of importance");

        // adhoctd parameters
        options.addOption("ap", "AskParam", true, "asking parameter of adhoctd");
        options.addOption("gp", "GiveParam", true, "giving parameter of adhoctd");

        // reusing parameeters
        options.addOption("qt", "QTHRED", true, "QTHRED of QChange"); // different students: studentS (default), change, budget, decay, margin, qChange
        options.addOption("reb", "ReBudget", true, "Reusing Budget of Budget");
        options.addOption("de", "Decay", true, "Decay Prob of Decay");

        // Parse the program arguments
        CommandLine commandLine = parser.parse( options, args );
        String mode = "";
        if( commandLine.hasOption('m') ) {
            mode = commandLine.getOptionValue("m");
        }else{
            System.out.println("Wrong mode!");
            System.exit(0);
        }

        // start time
        int startTime = 0;
        if( commandLine.hasOption("t") ) {
            startTime = Integer.parseInt(commandLine.getOptionValue("t"));
        }

        // prepare the strategy parameters
        if (!mode.equals("i")) {
            String strategy = "";
            if( commandLine.hasOption("sty") ) {
                strategy = commandLine.getOptionValue("sty");
            }

            switch (strategy){
                case "askCorrect": {
                    // set tq and sq
                    double tQThreshold = 0;
                    if( commandLine.hasOption("tq") ) {
                        tQThreshold = Double.parseDouble(commandLine.getOptionValue("tq"));
                    }

                    double sQThreshold = 0;
                    if( commandLine.hasOption("sq") ) {
                        sQThreshold = Double.parseDouble(commandLine.getOptionValue("sq"));
                    }

                    TQTHRESHOLD = tQThreshold;
                    SQTHRESHOLD = sQThreshold;
                    STRATEGY = strategy;

                    System.out.println(STRATEGY+"-tQThreshold:"+TQTHRESHOLD+"-sQThreshold:"+SQTHRESHOLD);
                } break;
                case "adhoctd": {
                    // set ap and gp
                    double ap = 0;
                    if( commandLine.hasOption("ap") ) {
                        ap = Double.parseDouble(commandLine.getOptionValue("ap"));
                    }

                    double gp = 0;
                    if( commandLine.hasOption("gp") ) {
                        gp = Double.parseDouble(commandLine.getOptionValue("gp"));
                    }

                    ASKPARAME = ap;
                    GIVEPARAM = gp;
                    STRATEGY = strategy;

                    System.out.println(STRATEGY+"-askParam:"+ASKPARAME+"-giveParam:"+GIVEPARAM);
                } break;
                default: {
                    System.out.println("Wrong strategy!");
                    System.exit(0);
                }
            }
        }

        // independent, teacherStudent, reusingAdvice
        switch (mode) {
            case "i": {
                System.out.println("Mode:independent-Start:"+startTime);
                train("independent", startTime);
            } break;
            case "t": {
                System.out.println("Mode:teacherStudent-Start:"+startTime);
                train("teacherStudent", startTime);
            } break;
            case "r": {
                System.out.println("Mode:reusingAdvice-Start:"+startTime);
                String student = "";
                if( commandLine.hasOption("stu") ) {
                    student = commandLine.getOptionValue("stu");
                }else{
                    System.out.println("Wrong student!");
                    System.exit(0);
                }

                switch (student) {
                    case "ch": {
                        STUDENT = "change";
                        double qt = 0;
                        if( commandLine.hasOption("qt") ) {
                            qt = Double.parseDouble(commandLine.getOptionValue("qt"));
                        }
                        QTHRED = qt;
                        System.out.println("qThred:"+QTHRED);
                    } break;
                    case "bu": {
                        STUDENT = "budget";
                        int reb = 0;
                        if( commandLine.hasOption("reb") ) {
                            reb = Integer.parseInt(commandLine.getOptionValue("reb"));
                        }
                        REUSINGBUDGET = reb;
                        System.out.println("budget:"+REUSINGBUDGET);
                    } break;
                    case "de": {
                        STUDENT = "decay";
                        double de = 0;
                        if( commandLine.hasOption("de") ) {
                            de = Double.parseDouble(commandLine.getOptionValue("de"));
                        }
                        DECAY = de;
                        System.out.println("decay:"+DECAY);
                    } break;
                    case "deM": {
                        STUDENT = "margin";
                        double de = 0;
                        if( commandLine.hasOption("de") ) {
                            de = Double.parseDouble(commandLine.getOptionValue("de"));
                        }
                        DECAY = de;
                        System.out.println("margin decay:"+DECAY);
                    } break;
                    case "deQ": {
                        STUDENT = "qChange";
                        double qt = 0;
                        if( commandLine.hasOption("qt") ) {
                            qt = Double.parseDouble(commandLine.getOptionValue("qt"));
                        }
                        QTHRED = qt;

                        double de = 0;
                        if( commandLine.hasOption("de") ) {
                            de = Double.parseDouble(commandLine.getOptionValue("de"));
                        }
                        DECAY = de;
                        System.out.println("qChange decay:"+DECAY+"-qThred:"+QTHRED);
                    } break;
                    default: STUDENT = "studentS"; break;
                }
                train("reusingAdvice", startTime);
            } break;
            default: {
                System.out.println("Mode:independent-Start:"+startTime);
                train("independent", startTime);
            } break;
        }
    }


//    public static void main(String[] args) {
//        STUDENT = "qChange";  // studentS, change, budget, decay, margin, qChange
//        train("reusingAdvice", 0);  // independent, teacherStudent, reusingAdvice
//    }


    /** Set up a learner. */
    public static TeachingAgent create(String learner, Problem prob) {
        Policy policy = new EGreedyPolicy(epsilon);

        if (learner.startsWith("independent")) {
            System.out.println(learner);
            return new TeachingAgent(policy, alpha, lambda, potential, constant, gamma);
        }

        if (learner.startsWith("teacherStudent") || learner.startsWith("reusingAdvice")) {
            System.out.println(learner.startsWith("reusingAdvice") ? TEACHER+"-"+STRATEGY+"-"+STUDENT+"|TQ:"+TQTHRESHOLD+"-SQ:"+SQTHRESHOLD+"-ASK:"+
                    ASKPARAME+"-GIVE:"+GIVEPARAM +"|Q:" +QTHRED+"-budget:"+REUSINGBUDGET+"-decay:"+DECAY
                    : TEACHER+"-"+STRATEGY+"|TQ:"+TQTHRESHOLD+"-SQ:"+SQTHRESHOLD+"-ASK:"+ ASKPARAME+"-GIVE:"+GIVEPARAM);

            // load teacher
            TeachingAgent teacher = new TeachingAgent(policy, alpha, lambda, potential, constant, gamma);
            teacher.loadPolicy(DIR+"/independent/policy0");
            teacher.setProblem(prob);

            // load strategy
            TeachingStrategy strategy;
            switch (STRATEGY){
                case "baseline": strategy = new AdviseAtFirst(); break;
                case "advise": strategy = new AdviseImportantStates(TQTHRESHOLD); break;
                case "correct": strategy = new CorrectImportantMistakes(TQTHRESHOLD); break;
                case "askCorrect": strategy = new AskImportanceCorrectMistakes(TQTHRESHOLD, SQTHRESHOLD); break;
                case "adhoctd": strategy = new AdhocTD(ASKPARAME, GIVEPARAM); break;
                default: strategy = null; break;
            }

            // load student
            TeachingAgent student;
            if (learner.startsWith("reusingAdvice")){
                switch (STUDENT){
                    case "change": student = new StudentQChange(QTHRED, policy, teacher, strategy, alpha, lambda, potential, constant, gamma); break;
                    case "budget": student = new StudentReusingBudget(REUSINGBUDGET, policy, teacher, strategy, alpha, lambda, potential, constant, gamma); break;
                    case "decay": student = new StudentDecay(DECAY, policy, teacher, strategy, alpha, lambda, potential, constant, gamma); break;
                    case "margin": student = new StudentDecayMargin(DECAY, policy, teacher, strategy, alpha, lambda, potential, constant, gamma); break;
                    case "qChange": student = new StudentDecayQChange(DECAY, QTHRED, policy, teacher, strategy, alpha, lambda, potential, constant, gamma); break;
                    default: student = new Student(policy, teacher, strategy, alpha, lambda, potential, constant, gamma); break;
                }
                return student;
            }
            return new Student(policy, teacher, strategy, alpha, lambda, potential, constant, gamma);
        }
        // default agent
        return new TeachingAgent(policy, alpha, lambda, potential, constant, gamma);
    }

    /** get learner name **/
    public static String getLearnerName(String learner){
        switch (learner){
            case "teacherStudent":
            {
                switch (STRATEGY){
                    case "advise": return learner + "/" + STRATEGY + "_tQ_" + TQTHRESHOLD + "_ep_" + beginEpisodes;
                    case "correct": return learner + "/" + STRATEGY + "_tQ_" + TQTHRESHOLD + "_ep_" + beginEpisodes;
                    case "askCorrect": return learner + "/" + STRATEGY + "_tQ_" + TQTHRESHOLD + "_sQ_" + SQTHRESHOLD + "_ep_" + beginEpisodes;
                    case "adhoctd": return learner + "/" + STRATEGY  + "_ask_" + ASKPARAME + "_give_" + GIVEPARAM + "_ep_" + beginEpisodes;
                    default: return learner + "/" + STRATEGY;
                }
            }
            case "reusingAdvice": {
                String learnerName = learner;
                switch (STUDENT){
                    case "change": learnerName += "/change_thred_" + QTHRED + "_"; break;
                    case "budget": learnerName += "/budget_budget_" + REUSINGBUDGET + "_"; break;
                    case "decay": learnerName += "/decay_prob_" + DECAY + "_"; break;
                    case "margin": learnerName += "/decay_margin_" + DECAY + "_"; break;
                    case "qChange": learnerName += "/decay_qChange_" + DECAY + "_" + QTHRED + "_"; break;
                    default: learnerName += "/"; break;
                }

                switch (STRATEGY){
                    case "advise": return learnerName + STRATEGY + "_tQ_" + TQTHRESHOLD + "_ep_" + beginEpisodes;
                    case "correct": return learnerName + STRATEGY + "_tQ_" + TQTHRESHOLD + "_ep_" + beginEpisodes;
                    case "askCorrect": return learnerName + STRATEGY + "_tQ_" + TQTHRESHOLD + "_sQ_" + SQTHRESHOLD + "_ep_" + beginEpisodes;
                    case "adhoctd": return learnerName + STRATEGY  + "_ask_" + ASKPARAME + "_give_" + GIVEPARAM + "_ep_" + beginEpisodes;
                    default: return learnerName + STRATEGY;
                }
            }

            default: return learner;
        }
    }


    /** Generate learning curves. */
    public static void train(String learner, int start) {
        // Begin new curves
        for (int re=start; re<(REPEATS+start); re++) {
            System.out.println("Training "+DIR+"/"+learner+" " + re + "...");
            // create data directory
            String new_learner = getLearnerName(learner);
            File dirFile = new File(DIR+"/"+new_learner);
            if (!dirFile.exists()){
                dirFile.mkdirs();
            }
            DataFile file = new DataFile(DIR+"/"+new_learner+"/score"+re);
            file.clear();

            Problem mario = new Mario();
            TeachingAgent marioLeaner = create(new_learner, mario);   // single learner or student-teacher
            marioLeaner.setProblem(mario);   // the connection between mario and learner, not for teacher

            // set recording demonstrations
            marioLeaner.reset();
            double[] results = new double[TRAIN];
            for(int ep=0; ep<TRAIN; ep++){
                results[ep] = episode(ep, mario, marioLeaner);
                if(DEBUG && ep % DEBUG_LENGTH == 0){
                    System.out.println("Run:" + re + "--" + ep  + "..."+ TRAIN + "--Episode reward:" + results[ep] + "--Sum reward:" + Stats.sum(results)/(ep + 1));
                    if (new_learner.startsWith("teacherStudent") || new_learner.startsWith("reusingAdvice")) {
                        System.out.println("Advice Count:" + ((Student) marioLeaner).getAdviceCount() + "--cumReuseTimes:" + ((Student) marioLeaner).getCumReuseTimes());
                    }
                }
                // record reward data
                if (new_learner.startsWith("teacherStudent") || new_learner.startsWith("reusingAdvice"))
                    file.append(ep+","+results[ep]+","+Stats.sum(results)/(ep + 1)+","+((Student) marioLeaner).getAdviceCount()+"\n");
                else
                    file.append(ep+","+results[ep]+","+Stats.sum(results)/(ep + 1)+"\n");
            }
            System.out.println("Run:" + re + "--Episode finished, sum reward:" + Stats.sum(results)/TRAIN);

            file.close();

            // Save new curve and policy
            if (new_learner.startsWith("independent")) {
                // marioLeaner.savePolicy(DIR+"/"+new_learner+"/policy"+re);
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
