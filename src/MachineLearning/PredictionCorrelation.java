/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;

/**
 *
 * @author Ali
 */
public class PredictionCorrelation {
    public double getPredictionCorrelation(DataSetPRes testSet1, DataSetPRes testSet2){
        
        double res=0;
        
        for(int i=0;i<testSet1.size();i++){
            if(testSet1.get(i).labelPredicted == testSet2.get(i).labelPredicted){
                res++;
            }
        }
        
        return res/testSet1.size();
    }
}
