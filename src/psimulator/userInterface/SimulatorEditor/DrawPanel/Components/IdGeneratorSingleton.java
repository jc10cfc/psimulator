/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

/**
 *
 * @author Martin
 */
public class IdGeneratorSingleton {
    
    private int nextId = 0;
    
    private IdGeneratorSingleton() {
    }
    
    public static IdGeneratorSingleton getInstance() {
        return IdGeneratorSingletonHolder.INSTANCE;
    }
    
    private static class IdGeneratorSingletonHolder {

        private static final IdGeneratorSingleton INSTANCE = new IdGeneratorSingleton();
    }
    
    public int getNextId(){
        return nextId++;
    }
}
