package problem.mario.teaching;

import agent.state.StateAction;
import problem.mario.TeachingMario;
import problem.mario.teaching.CorrectImportantMistakes;
import problem.mario.utils.Stats;

import java.security.cert.TrustAnchor;

public class AskImportanceCorrectMistakes extends CorrectImportantMistakes{
    private double t_stu;
    public AskImportanceCorrectMistakes(double t, double t_stu) {
        super(t);
        this.t_stu = t_stu;
    }

    /** Until none left. */
    public boolean inUse(TeachingAgent student, StateAction stateAction) {
        // student decide whether ask or not
        double[] qvalues = student.getQs(stateAction);
        double gap = Stats.max(qvalues) - Stats.min(qvalues);

        if (left > 0 && gap < t_stu){
            return true;
        }

        return false;
    }

    // the asking budget still can be used
    public boolean askBudgetInUse(){
        return (left > 0);
    }


}
