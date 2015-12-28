/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning.Kernel;

/**
 *
 * @author Ali
 */
public abstract class AbstractKernel {
    public abstract double getK(Object objA, Object objB);
    public abstract String getName();//returns kernel's name
}
