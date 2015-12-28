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
import java.util.*;


public class StatOnlineExtended {
    
    
    private double Sum=0;
    private double Sum2=0;
    private int Count=0;
    
    private ArrayList<Double> nMaxKey;
    private ArrayList<Double> nMinKey;
    private ArrayList nMaxObj;
    private ArrayList nMinObj;
    
    private int NumofMax=0;
    private int NumofMin=0;
    
    public StatOnlineExtended(int NumofMax, int NumofMin){//if -1: all data
        this.NumofMax=NumofMax;
        this.NumofMin=NumofMin;
        
        nMaxKey=new ArrayList();
        nMinKey=new ArrayList();
        
        nMaxObj=new ArrayList();
        nMinObj=new ArrayList();
    }
    
    public void Update(double key, Object obj){
        
        
        Count++;
        Sum+=key;
        Sum2+=(key*key);

        //max
        if(nMaxKey.size()<NumofMax || NumofMax==-1){
            for(int i=0;i<nMaxKey.size()+1;i++){
                if(i==nMaxKey.size()){
                    nMaxKey.add(key);
                    nMaxObj.add(obj);
                    break;
                }
                else{
                    if(key>nMaxKey.get(i)){
                        nMaxKey.add(i, key);
                        nMaxObj.add(i, obj);
                        break;
                    }
                }
            }
        }
        else{
            for(int i=0;i<NumofMax;i++){
                if(key>nMaxKey.get(i)){
                    nMaxKey.add(i, key);
                    nMaxObj.add(i, obj);
                    //remove additional records
                    nMaxKey.remove(NumofMax);
                    nMaxObj.remove(NumofMax);
                    break;
                }
            }
        }
        
        
        //min
        if(nMinKey.size()<NumofMin || NumofMin==-1){
            for(int i=0;i<nMinKey.size()+1;i++){
                if(i==nMinKey.size()){
                    nMinKey.add(key);
                    nMinObj.add(obj);
                    break;
                }
                else{
                    if(key<nMinKey.get(i)){
                        nMinKey.add(i, key);
                        nMinObj.add(i, obj);
                        break;
                    }
                }
            }
        }
        else{
            for(int i=0;i<NumofMin;i++){
                if(key<nMinKey.get(i)){
                    nMinKey.add(i, key);
                    nMinObj.add(i, obj);
                    //remove additional records
                    nMinKey.remove(NumofMin);
                    nMinObj.remove(NumofMin);
                    break;
                }
            }
        }
        
        
            
        
        
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
    
    public ArrayList<Double> GetMinKey(){//index 0 has lowest       
        return nMinKey;
    }
    public ArrayList GetMinObj(){//index 0 has lowest       
        return nMinObj;
    }
    
    public ArrayList<Double> GetMaxKey(){//index 0 has largest       
        return nMaxKey;
    }
    public ArrayList GetMaxObj(){     
        return nMaxObj;
    }
    
    
    public int GetCount(){
        return Count;
    }
    
    public void Reset(){
        Sum=0;
        Sum2=0;
        Count=0;
        
        nMaxKey.clear();
        nMaxObj.clear();
        nMinKey.clear();
        nMinObj.clear();
    }
    
    @Override
    public String toString(){
        String res="";
        res+="Count= "+this.GetCount()+"\r\n";
        res+="Average= "+this.GetAverage()+"\r\n";
        res+="SD= "+this.GetSD()+"\r\n";
        res+="Sum= "+this.GetSum()+"\r\n\r\n";
        res+="Max ("+this.GetMaxKey().size()+")= \r\n";
        for(int i=0;i<this.GetMaxKey().size();i++){
            res+=i+"- key= "+this.GetMaxKey().get(i)+" -- " +this.GetMaxObj().get(i).toString()+"\r\n";
        }
        res+="\r\n";
        res+="Min ("+this.GetMinKey().size()+")= \r\n";
        for(int i=0;i<this.GetMinKey().size();i++){
            res+=i+"- key= "+this.GetMinKey().get(i)+" -- " +this.GetMinObj().get(i).toString()+"\r\n";
        }
        return res;
    }

}
