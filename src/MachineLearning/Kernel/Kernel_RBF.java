/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning.Kernel;

/**
 *
 * @author Ali
 */
public class Kernel_RBF extends AbstractKernelM  {
    
    double gamma;
    public Kernel_RBF(double gamma){
        this.gamma=gamma;
    }
    
    
    @Override
    public double getK(Object objA, Object objB){
        if(!(objA instanceof double[] && objB instanceof double[])){
            throw new IllegalArgumentException("The input object to getK in Kernel_RBF is not a double[] instance");
        }
        double x_squarei=this.dot((double[])objA, (double[])objA);
        double x_squarej=this.dot((double[])objB, (double[])objB);
        return Math.exp(-gamma * (x_squarei + x_squarej - 2 * dot((double[])objA, (double[])objB)));
    }
    
    double dot(double[] x, double[] y) {
        double sum = 0;
        int len = x.length;
        for(int i=0;i<len;i++){
            sum+=(x[i]*y[i]);
        }
        
        return sum;
    }
    
    @Override
    public String getName(){
        return "RBF";
    }
}
