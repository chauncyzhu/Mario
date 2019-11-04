package problem.mario.teaching;

import agent.state.StateAction;
import problem.mario.TeachingMario;
import problem.mario.utils.Stats;
import util.RNG;

public class AdhocImportance extends TeachingStrategy {
    protected int left; // Advice to give
    protected double askParam;
    protected double giveParam;

    public AdhocImportance(double askParam, double giveParam) {
        left = TeachingMario.BUDGET;
        this.askParam = askParam;
        this.giveParam = giveParam;
    }

    /** When the state has widely varying Q-values, and the student doesn't take the advice action. */
    public boolean giveAdvice(TeachingAgent teacher, StateAction stateAction, int choice, int advice) {

        double[] qvalues = teacher.getQs(stateAction);
        double gap = Stats.max(qvalues) - Stats.min(qvalues);
        double absdeviation = Stats.absdeviation(qvalues, Stats.average(qvalues));
        boolean important = (absdeviation > TeachingMario.TQTHRESHOLD);

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
