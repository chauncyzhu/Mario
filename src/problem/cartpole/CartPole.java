/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.cartpole;

import agent.state.StateAction;
import problem.Problem;
import problem.SingleAgentProblem;
//import rlpark.plugin.rltoys.envio.actions.Action;
//import rlpark.plugin.rltoys.envio.actions.ActionArray;
//import rlpark.plugin.rltoys.math.ranges.Range;
import util.RNG;

/**
 *
 * @author timbrys
 */
public class CartPole extends SingleAgentProblem {

    private final static double GRAVITY = 9.8;
    private final static double MASSCART = 1.0;
    private static double MASSPOLE = 0.1;
    private static double TOTAL_MASS = (MASSPOLE + MASSCART);
    private final static double LENGTH = 0.5;     /* actually half the pole's length */

    private static double POLEMASS_LENGTH = (MASSPOLE * LENGTH);
    private final static double FORCE_MAG = 10.0;
    private final static double TAU = 0.02;       /* seconds between state updates */

    private final static double FOURTHIRDS = 4.0d / 3.0d;
    final static double DEFAULTLEFTCARTBOUND = -2.4;
    final static double DEFAULTRIGHTCARTBOUND = 2.4;
    final static double DEFAULTLEFTANGLEBOUND = -Math.toRadians(12.0d);
    final static double DEFAULTRIGHTANGLEBOUND = Math.toRadians(12.0d);
    final static double DEFAULTLEFTSPEEDBOUND = -6.0;
    final static double DEFAULTRIGHTSPEEDBOUND = 6.0;
    /*Current Values */
    double x;                   /* cart position, meters */

    double x_dot;                       /* cart velocity */

    double theta;                       /* pole angle, radians */

    double theta_dot;           /* pole angular velocity */

    private double noise;
    private boolean randomStarts;

    public CartPole(double masspole) {

        randomStarts = false;
        noise = 0.0d;
        
        MASSPOLE = masspole;
        TOTAL_MASS = (MASSPOLE + MASSCART);
        POLEMASS_LENGTH = (MASSPOLE * LENGTH);
    }

    @Override
    public int getNumActions() {
        return 2;
    }

    public void reset() {
        x = 0.0f;
        x_dot = 0.0f;
        theta = 0.0f;
        theta_dot = 0.0f;

        if (randomStarts) {
            //Going to have the random start states be near to equilibrium
            x = RNG.randomDouble() - .5d;
            x_dot = RNG.randomDouble() - .5d;
            theta = (RNG.randomDouble() - .5d) / 8.0d;
            theta_dot = (RNG.randomDouble() - .5d) / 8.0d;
        }

    }

    public boolean isAtGoal(){
        if (x < DEFAULTLEFTCARTBOUND || x > DEFAULTRIGHTCARTBOUND || theta < DEFAULTLEFTANGLEBOUND || theta > DEFAULTRIGHTANGLEBOUND) {
            return true;
        } /* to signal failure */
        return false;
    }

    @Override
    public boolean isGoalReached(int iteration) {
        return isAtGoal() || iteration > 999;
    }

    public double[] step(int action) {
        double xacc;
        double thetaacc;
        double force;
        double costheta;
        double sintheta;
        double temp;

        if (action > 0) {
            force = FORCE_MAG;
        } else {
            force = -FORCE_MAG;
        }

        //Noise of 1.0 means possibly full opposite action
        double thisNoise = 2.0d * noise * FORCE_MAG * (RNG.randomDouble() - .5d);

        force += thisNoise;

        costheta = Math.cos(theta);
        sintheta = Math.sin(theta);

        temp = (force + POLEMASS_LENGTH * theta_dot * theta_dot * sintheta) / TOTAL_MASS;

        thetaacc = (GRAVITY * sintheta - costheta * temp) / (LENGTH * (FOURTHIRDS - MASSPOLE * costheta * costheta / TOTAL_MASS));

        xacc = temp - POLEMASS_LENGTH * thetaacc * costheta / TOTAL_MASS;

        /**
         * * Update the four state variables, using Euler's method. **
         */
        x += TAU * x_dot;
        x_dot += TAU * xacc;
        if (x_dot < -6.0) {
            x_dot = -6.0;
        }
        if (x_dot > 6.0) {
            x_dot = 6.0;
        }
        theta += TAU * theta_dot;
        theta_dot += TAU * thetaacc;
        if (theta_dot < -6.0) {
            theta_dot = -6.0;
        }
        if (theta_dot > 6.0) {
            theta_dot = 6.0;
        }

        /**
         * These probably never happen because the pole would crash *
         */
        while (theta >= Math.PI) {
            theta -= 2.0d * Math.PI;
        }
        while (theta < -Math.PI) {
            theta += 2.0d * Math.PI;
        }
        return new double[]{isAtGoal() ? -1 : 0, 1};
    }

    @Override
    public StateAction getState() {
        double[] state = new double[4];
        state[0] = getX();
        state[1] = getXDot();
        state[2] = getTheta();
        state[3] = getThetaDot();
        return new CartPoleStateAction(state, -1);
    }

    public double getX() {
        return x;
    }

    public double getXDot() {
        return x_dot;
    }

    public double getTheta() {
        return theta;
    }

    public double getThetaDot() {
        return theta_dot;
    }
    
    
//    protected static final Range positionRange = new Range(DEFAULTLEFTCARTBOUND, DEFAULTRIGHTCARTBOUND);
//    protected static final Range angleRange = new Range(DEFAULTLEFTANGLEBOUND, DEFAULTRIGHTANGLEBOUND);
//    protected static final Range speedRange = new Range(DEFAULTLEFTSPEEDBOUND, DEFAULTRIGHTSPEEDBOUND);
//    public static final ActionArray LEFT = new ActionArray(0);
//    public static final ActionArray RIGHT = new ActionArray(1);
//    protected static final Action[] Actions = { LEFT, RIGHT };
//    
//    public static Range[] getObservationRanges() {
//      return new Range[] { positionRange, speedRange, angleRange, speedRange };
//    }
}
