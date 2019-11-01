package problem.mario.teaching;

public class Advice {
    private int advice;
    private boolean qReuse;
    private int remainBudget;
    private double remainProb;

    public Advice(int advice, int remainBudget) {
        this.advice = advice;
        this.remainBudget = remainBudget;
    }

    public Advice(int advice, double remainProb) {
        this.advice = advice;
        this.remainProb = remainProb;
    }

    public Advice(int advice, boolean qReuse) {
        this.advice = advice;
        this.qReuse = qReuse;
    }

    public boolean isqReuse() {
        return qReuse;
    }

    public void setqReuse(boolean qReuse) {
        this.qReuse = qReuse;
    }

    public int getAdvice() {
        return advice;
    }

    public void setAdvice(int advice) {
        this.advice = advice;
    }

    public int getRemainBudget() {
        return remainBudget;
    }

    public void setRemainBudget(int remainBudget) {
        this.remainBudget = remainBudget;
    }

    public double getRemainProb() {
        return remainProb;
    }

    public void setRemainProb(double remainProb) {
        this.remainProb = remainProb;
    }
}
