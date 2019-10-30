/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.mario;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Comparator;
import problem.mario.ch.idsia.agents.Agent;
import problem.mario.ch.idsia.agents.controllers.BasicMarioAIAgent;
import problem.mario.ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;


/**
 *
 * @author timbrys
 */
public class MarioInbetween extends BasicMarioAIAgent implements Agent {
    
    protected float[] prevMarioPos;
    
    public MarioInbetween(){
        super("Mario");
        prevMarioPos = new float[]{32.0f,32.0f};
    }
    
    public void step(){
        prevMarioPos[0] = marioFloatPos[0];
        prevMarioPos[1] = marioFloatPos[1];
    }
    
    /*
     * State
     * 0 can jump? 0-1
     * 1 on ground? 0-1
     * 2 able to shoot? 0-1
     * 3 current direction 0-8
     * 4 close enemies yes or no in 8 directions 0-255
     * 5 midrange enemies yes or no in 8 directions 0-255
     * 6 far enemies yes or no in 8 directions 0-255
     * 7 obstacles in front 0-15
     * 8 closest enemy x 0-21
     * 9 closest enemy y 0-21
     * 
     */
    public agent.state.StateAction getState(){
//        double[] state = new double[10];
//        
//        // CLOSEST TWO ENEMIES
//        state[0] = isMarioAbleToJump ? 0 : 1;
//        state[1] = isMarioOnGround ? 0 : 1;
//        state[2] = isMarioAbleToShoot ? 0 : 1;//marioMode;//
//        float xdiff = marioFloatPos[0] - prevMarioPos[0];
//        float ydiff = marioFloatPos[1] - prevMarioPos[1];
//        state[3] = xdiff < 0 ? 0 : (xdiff == 0 ? 1 : 2);
//        state[3] += 3*(ydiff < 0 ? 0 : (ydiff == 0 ? 1 : 2));
//        
//        state[4] = enemies(1, 0);
//        state[5] = enemies(3, 1);
//        state[6] = 0;//enemies(5, 3);
//        
//        state[7] = obstacle();
//        
//        int[] enemy = closestEnemy();
//        if(Math.abs(enemy[0]) < 11 && Math.abs(enemy[1]) < 11){
//            state[8] = enemy[0]+10;
//            state[9] = enemy[1]+10;
//        } else {
//            state[8] = 21;
//            state[9] = 21;
//        }
        
//        state[10] = gap();
        
//        float[] extraState = new float[2];
//        extraState[0] = marioFloatPos[0];
//        extraState[1] = marioFloatPos[1];
//        
//        return new MarioStateAction(state, extraState, -1); //2*2*2*2*13*13*12

        double[] state = new double[27];
        state[0] = isMarioAbleToJump ? 0 : 1;
        state[1] = isMarioOnGround ? 0 : 1;
        state[2] = isMarioAbleToShoot ? 0 : 1;//marioMode;//
        float xdiff = marioFloatPos[0] - prevMarioPos[0];
        float ydiff = marioFloatPos[1] - prevMarioPos[1];
        state[3] = (xdiff < 0) ? 0 : ((xdiff == 0) ? 1 : 2);
        state[4] = (ydiff < 0) ? 0 : ((ydiff == 0) ? 1 : 2);
        boolean[] ens = enemies(1, 0, true);
        for(int i=0; i<ens.length; i++){
            state[5+i] = ens[i] ? 1 : 0;
        }
        ens = enemies(3, 1, true);
        for(int i=0; i<ens.length; i++){
            state[13+i] = ens[i] ? 1 : 0;
        }
        for(int i=0; i<4; i++){
            state[21+i] = (getReceptiveFieldCellValue(marioEgoPos[0]-i, marioEgoPos[1]+1) < 0) ? 1 : 0;
        }
        int[] enemy = closestEnemy();
        if(Math.abs(enemy[0]) < 11 && Math.abs(enemy[1]) < 11){
            state[25] = enemy[0]+10;
            state[26] = enemy[1]+10;
        } else {
            state[25] = 21;
            state[26] = 21;
        }
        return new MarioStateAction(state, -1);
    }
    
    protected boolean hasGap(int y){
        for(int x=marioEgoPos[0]; x<levelScene.length; x++){
            if(levelScene[x][y] == GeneralizerLevelScene.FLOWER_POT_OR_CANNON
                    || levelScene[x][y] == GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH 
                    || levelScene[x][y] == GeneralizerLevelScene.BRICK
                    || levelScene[x][y] == 1){
                return false;
            }
        }
        return true;
    }
    
    protected int gap(){
        int gap = hasGap(marioEgoPos[1]) ? 1 : 0;
        gap += hasGap(marioEgoPos[1]+1) ? 2 : 0;
        gap += hasGap(marioEgoPos[1]+2) ? 4 : 0;
        return gap;
    }
    
    protected int obstacle(){
        int obstacle = 0;
        int obst;
        for(int i=0; i<4; i++){
            obst = getReceptiveFieldCellValue(marioEgoPos[0]-i, marioEgoPos[1]+1);
            if(obst < 0){
                obstacle += Math.pow(2, i);
            }
        }
        return obstacle;
    }
    
    public Integer[] sortEnemies(){
        Integer[] sorted = new Integer[enemiesFloatPos.length/3];
        for(int i=0; i<sorted.length; i++){
            sorted[i] = i*3;
        }
        Arrays.sort(sorted, new Comparator<Integer>(){
            @Override
            public int compare(Integer o1, Integer o2) {
                double dist1 = (enemiesFloatPos[o1+1]*enemiesFloatPos[o1+1] + enemiesFloatPos[o1+2]*enemiesFloatPos[o1+2]);
                double dist2 = (enemiesFloatPos[o2+1]*enemiesFloatPos[o2+1] + enemiesFloatPos[o2+2]*enemiesFloatPos[o2+2]);
                return ((Double)dist1).compareTo((Double)dist2);
            }
        });
        return sorted;
    }
    
    public int[] closestEnemy(){
        Integer[] sorted = sortEnemies();
        
        int[] enemy = new int[2];
        if(sorted.length == 0){
            enemy[0] = 1000;
            enemy[1] = 1000;
        } else {
            enemy[0] = (int)(enemiesFloatPos[sorted[0]+1]/13);
            enemy[1] = (int)(enemiesFloatPos[sorted[0]+2]/13);
        }
        return enemy;
    }
    
    protected int enemies(int out, int in){
        boolean[] ens = new boolean[8];
        int total = 0;
        int dir;
        int enemy;
        int extra = 0;
        if(marioMode > 0){
            extra = -1;
        }
        for(int i=-out+extra; i<out+1; i++){
            for(int j=-out; j<out+1; j++){
                if(!(i >= -in && i <= in && j >= -in && j <= in)){
                    enemy = getEnemiesCellValue(marioEgoPos[0]+i, marioEgoPos[1]+j);
                    if((enemy >= 80 && enemy < 100) || enemy == 13){
                        dir = i < 0 ? 0 : (i == 0 ? 2 : 1);
                        dir += 3*(j < 0 ? 0 : (j == 0 ? 2 : 1));
                        ens[dir] = true;
                    }
                }
            }
        }
        for(int i=0; i<ens.length; i++){
            if(ens[i]){
                total += Math.pow(2, i);
            }
        }
        return total;
    }
    
    protected boolean[] enemies(int out, int in, boolean t){
        boolean[] ens = new boolean[8];
        int dir;
        int enemy;
        int extra = 0;
        if(marioMode > 0){
            extra = -1;
        }
        for(int i=-out+extra; i<out+1; i++){
            for(int j=-out; j<out+1; j++){
                if(!(i >= -in && i <= in && j >= -in && j <= in)){
                    enemy = getEnemiesCellValue(marioEgoPos[0]+i, marioEgoPos[1]+j);
                    if((enemy >= 80 && enemy < 100) || enemy == 13){
                        dir = i < 0 ? 0 : (i == 0 ? 2 : 1);
                        dir += 3*(j < 0 ? 0 : (j == 0 ? 2 : 1));
                        ens[dir] = true;
                    }
                }
            }
        }
        return ens;
    }
    
    public void keyPressed(KeyEvent e) {
        toggleKey(e.getKeyCode(), true);
    }

    public void keyReleased(KeyEvent e) {
        toggleKey(e.getKeyCode(), false);
    }

    private void toggleKey(int keyCode, boolean isPressed) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_LEFT] = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_RIGHT] = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_DOWN] = isPressed;
                break;
            case KeyEvent.VK_UP:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_UP] = isPressed;
                break;
            case KeyEvent.VK_S:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP] = isPressed;
                break;
            case KeyEvent.VK_A:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_SPEED] = isPressed;
                break;
        }
    }

    
}
