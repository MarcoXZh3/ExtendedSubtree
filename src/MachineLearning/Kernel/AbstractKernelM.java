/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning.Kernel;

import MachineLearning.DataSet;
import MachineLearning.Record;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Ali
 */
public abstract class AbstractKernelM extends AbstractKernel implements Serializable{
    
    static final long serialVersionUID = -6246253063846255860L;
    
    private boolean useKernelMatrix=false;
    private ArrayList<float[]> kernelMatrix;
    
    public boolean useKernelMatrix(){
        return useKernelMatrix;
    }
    
    public double getK(int indexRow, int indexCol){
        return kernelMatrix.get(indexRow)[indexCol];
    }
    
    public double getK(Record recRow, Record recCol){
        if(useKernelMatrix==true) {
            return kernelMatrix.get(recRow.getID())[recCol.getID()];
        }
        else {
            return this.getK(recRow.getData(), recCol.getData());
        }
    }
    
    public void setK(int indexRow, int indexCol, double value){
        if(useKernelMatrix==true){
            kernelMatrix.get(indexRow)[indexCol]=(float)value;
        }
    }
    
    public ArrayList<float[]> getKernelMatrix(){                
        return kernelMatrix;
    }
    
    public void buildKernelMatrix(DataSet rowSet, DataSet colSet, boolean setDataSetIDs){
        
        //setIDs, ids are used to access kernelMatrix later, so it is wise to set them here
        if(setDataSetIDs==true){
            rowSet.setIDs();
            colSet.setIDs();
        }
        //
        kernelMatrix=new ArrayList(rowSet.size());
        for(int row=0;row<rowSet.size();row++){
            float[] kernelMatrixRow=new float[colSet.size()];
            for(int col=0; col<colSet.size(); col++){
                kernelMatrixRow[col]=(float)this.getK(rowSet.get(row).getData(), colSet.get(col).getData());                
            }   
            kernelMatrix.add(kernelMatrixRow);
            if(row%100 ==0) {
                System.out.println("KernelMatrix "+this.getName()+", row "+row+" done");
            }
        }   
        useKernelMatrix=true;
    }
    
    public void clearKernelMatrix(){
        useKernelMatrix=false;
        kernelMatrix=null;
    }

    

    @Override
    public String toString(){
        if(useKernelMatrix==false){
            return this.getName();
        }
        
        String str=this.getName()+" Kernel Matrix:\r\n";
        for(int row=0;row<kernelMatrix.size();row++){
            float[] aRow=kernelMatrix.get(row);
            for(int col=0;col<aRow.length;col++){
                str+=aRow[col]+", ";
            }
            str+="\r\n";
        }
        return str;
    }
        
        
    
}
