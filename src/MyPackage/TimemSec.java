package MyPackage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ali Shahbazi
 */



public class TimemSec {
    private long timeS;

    public TimemSec(){
        timeS=System.currentTimeMillis();
    }
    public void Start(){
        timeS=System.currentTimeMillis();
        
    }
    public long GetTime(){
        
        return System.currentTimeMillis()-timeS;
    }
}
