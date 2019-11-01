package problem.mario.teaching;

import util.RNG;
import agent.state.StateAction;
import problem.mario.TeachingMario;
import problem.mario.utils.Stats;

public class AdhocTD extends TeachingStrategy {
    protected int left; // Advice to give
    protected double askParam;
    protected double giveParam;

    public AdhocTD(double askParam, double giveParam) {
        left = TeachingMario.BUDGET;
        this.askParam = askParam;
        this.giveParam = giveParam;
    }

    /** When the state has widely varying Q-values, and the student doesn't take the advice action. */
    public boolean giveAdvice(TeachingAgent teacher, StateAction stateAction, int choice, int advice) {

        double[] qvalues = teacher.getQs(stateAction);
        double gap = Math.abs(Stats.max(qvalues) - Stats.min(qvalues));

        // the number of times teacher visits the state
        int visitTimes = teacher.getCmac().getVisits(stateAction);
        double value = Math.sqrt(visitTimes) * gap;
        double prob = 1 - (Math.pow((1 + giveParam),-value));

        boolean important = (prob > RNG.randomDouble());

        if (important) {
//            boolean mistake = (choice != advice);
//            if (mistake) {
            left--;
            return true;
        }

        return false;
    }

    /** Until none left. */
    public boolean inUse(TeachingAgent student, StateAction stateAction) {
        int visitTimes = student.getCmac().getVisits(stateAction);
        double prob = Math.pow((1 + askParam), -1*Math.sqrt(visitTimes));  //计算询问的概率

        boolean important = (prob > RNG.randomDouble());

        return (important && left > 0);
    }

    // the asking budget still can be used
    public boolean askBudgetInUse(){
        return (left > 0);
    }

}
