package MyPackage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ali Shahbazi
 */

import java.lang.Math;



public class StatOnline {
    private double Sum=0;
    private double Sum2=0;
    private int Count=0;
    private double Max=-9999999999999999.9;
    private double Min=9999999999999999.9;

    public void Update(double data){
        
        Count++;
        Sum+=data;
        Sum2+=(data*data);
            
        if(data>Max)
            Max=data;
        if(data<Min)
            Min=data;
            
        
        
    }
    
    public double GetAverage(){
        if(Count==0)
            return 0;
        else
            return Sum/Count;
    }
    public double GetSD(){
        if(Count==0)
            return 0;
        
        double average=Sum/Count;
        double s=Sum2/Count;
        double sd=s-(average*average);
        if(sd<=0)
            return 0;
        else
            return Math.sqrt(sd);
    }
    public double GetSum(){
        
        return Sum;
    }
    
    public double GetMin(){
        if(Count==0)
            return 0;
        
        return Min;
    }
    public double GetMax(){
        if(Count==0)
            return 0;
        
        return Max;
    }
    public int GetCount(){
        return Count;
    }
    
    public void Reset(){
        Sum=0;
        Sum2=0;
        Count=0;
        Max=-9999999999999999.9;
        Min=9999999999999999.9;
    }
    

}
