/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package psimulator.dataLayer.Singletons;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class TimerKeeperSingleton {
    
    private static final TimingSource ts = new SwingTimerTimingSource();
    
    private TimerKeeperSingleton() {
    }
    
    public static TimerKeeperSingleton getInstance() {
        return TimerKeeperSingletonHolder.INSTANCE;
    }
    
    private static class TimerKeeperSingletonHolder {
        private static final TimerKeeperSingleton INSTANCE = new TimerKeeperSingleton();
    }
    
    public void initTimingSource(){
        Animator.setDefaultTimingSource(ts);
        ts.init();
    }
    
    public TimingSource getTimingSource(){
        return ts;
    }
}
