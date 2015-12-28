
package MachineLearning.Classification.libsvm;

//

import MachineLearning.DataSet;
import MachineLearning.Kernel.AbstractKernelM;


// An SMO algorithm in Fan et al., JMLR 6(2005), p. 1889--1918
// Solves:
//
//	min 0.5(\alpha^T Q \alpha) + p^T \alpha
//
//		y^T \alpha = \delta
//		y_i = +1 or -1
//		0 <= alpha_i <= Cp for y_i = 1
//		0 <= alpha_i <= Cn for y_i = -1
//
// Given:
//
//	Q, p, y, Cp, Cn, and an initial feasible point \alpha
//	l is the size of vectors and matrices
//	eps is the stopping tolerance
//
// solution will be put in \alpha, objective value will be put in obj
//

public class Solver {

    AbstractKernelM kernel;
    DataSet prob;
    int active_size;
    byte[] y;
    double[] G;		// gradient of objective function
    static final byte LOWER_BOUND = 0;
    static final byte UPPER_BOUND = 1;
    static final byte FREE = 2;
    byte[] alpha_status;	// LOWER_BOUND, UPPER_BOUND, FREE
    double[] alpha;

    double eps;
    double Cp, Cn;
    double[] p;
    int[] active_set;
    double[] G_bar;		// gradient, if we treat free variables as 0
    int l;
    boolean unshrink;	// XXX
    static final double INF = java.lang.Double.POSITIVE_INFINITY;

    double get_C(int i) {
        return (y[i] > 0) ? Cp : Cn;
    }

    void update_alpha_status(int i) {
        if (alpha[i] >= get_C(i)) {
            alpha_status[i] = UPPER_BOUND;
        } else if (alpha[i] <= 0) {
            alpha_status[i] = LOWER_BOUND;
        } else {
            alpha_status[i] = FREE;
        }
    }

    boolean is_upper_bound(int i) {
        return alpha_status[i] == UPPER_BOUND;
    }

    boolean is_lower_bound(int i) {
        return alpha_status[i] == LOWER_BOUND;
    }

    boolean is_free(int i) {
        return alpha_status[i] == FREE;
    }

    // java: information about solution except alpha,
    // because we cannot return multiple values otherwise...
    static class SolutionInfo {

        double obj;
        double rho;
        double upper_bound_p;
        double upper_bound_n;
        double r;	// for Solver_NU
    } 

    
    

    

    float getK(int row, int col){
        return (float)SVMClassifier2Class.kernelConvert(kernel.getK(prob.get(row), prob.get(col)));
    }
    float getQ(int row, int col){
        return (float)(y[row]* y[col]*getK(row, col));
    }
    
    void Solve(int l, AbstractKernelM kernel, DataSet prob, double[] p_, byte[] y_,
            double[] alpha_, double Cp, double Cn, double eps, SolutionInfo si) {
        this.l = l;
        this.kernel=kernel;
        this.prob=prob;
        
        //QD = Q.get_QD();
        p = (double[]) p_.clone();
        y = (byte[]) y_.clone();
        alpha = (double[]) alpha_.clone();
        this.Cp = Cp;
        this.Cn = Cn;
        this.eps = eps;
        this.unshrink = false;

        // initialize alpha_status       
        alpha_status = new byte[l];
        for (int i = 0; i < l; i++) {
            update_alpha_status(i);
        }
        

        // initialize active set (for shrinking)
        active_set = new int[l];
        for (int i = 0; i < l; i++) {
            active_set[i] = i;
        }
        active_size = l;
        
        // initialize gradient        
        G = new double[l];
        G_bar = new double[l];
        int i;
        for (i = 0; i < l; i++) {
            G[i] = p[i];
            G_bar[i] = 0;
        }
        for (i = 0; i < l; i++) {
            if (!is_lower_bound(i)) {
                //float[] Q_i = Q.get_Q(i, l);
                double alpha_i = alpha[i];
                int j;
                for (j = 0; j < l; j++) {
                    G[j] += alpha_i * getQ(i,j);
                }
                if (is_upper_bound(i)) {
                    for (j = 0; j < l; j++) {
                        G_bar[j] += get_C(i) * getQ(i,j);
                    }
                }
            }
        }
        

        // optimization step

        int iter = 0;
        int max_iter = Math.max(10000000, l > Integer.MAX_VALUE / 100 ? Integer.MAX_VALUE : 100 * l);
        int counter = Math.min(l, 1000) + 1;
        int[] working_set = new int[2];

        while (iter < max_iter) {
            // show progress and do shrinking

            if (--counter == 0) {
                counter = Math.min(l, 1000);               
                System.out.print(".");
            }

            if (select_working_set(working_set) != 0) {

                // reset active set size and check
                active_size = l;
                System.out.print("*");
                if (select_working_set(working_set) != 0) {
                    break;
                } else {
                    counter = 1;	// do shrinking next iteration
                }
            }

            i = working_set[0];
            int j = working_set[1];

            ++iter;

            // update alpha[i] and alpha[j], handle bounds carefully

            

            double C_i = get_C(i);
            double C_j = get_C(j);

            double old_alpha_i = alpha[i];
            double old_alpha_j = alpha[j];

            if (y[i] != y[j]) {
                //double quad_coef = QD[i] + QD[j] + 2 * Q_i[j];
                double quad_coef = getK(i,i) + getK(j,j) + 2 * getQ(i,j);
                if (quad_coef <= 0) {
                    quad_coef = 1e-12;
                }
                double delta = (-G[i] - G[j]) / quad_coef;
                double diff = alpha[i] - alpha[j];
                alpha[i] += delta;
                alpha[j] += delta;

                if (diff > 0) {
                    if (alpha[j] < 0) {
                        alpha[j] = 0;
                        alpha[i] = diff;
                    }
                } else {
                    if (alpha[i] < 0) {
                        alpha[i] = 0;
                        alpha[j] = -diff;
                    }
                }
                if (diff > C_i - C_j) {
                    if (alpha[i] > C_i) {
                        alpha[i] = C_i;
                        alpha[j] = C_i - diff;
                    }
                } else {
                    if (alpha[j] > C_j) {
                        alpha[j] = C_j;
                        alpha[i] = C_j + diff;
                    }
                }
            } else {
                //double quad_coef = QD[i] + QD[j] - 2 * Q_i[j];
                double quad_coef = getK(i,i) + getK(j,j) - 2 * getQ(i,j);
                if (quad_coef <= 0) {
                    quad_coef = 1e-12;
                }
                double delta = (G[i] - G[j]) / quad_coef;
                double sum = alpha[i] + alpha[j];
                alpha[i] -= delta;
                alpha[j] += delta;

                if (sum > C_i) {
                    if (alpha[i] > C_i) {
                        alpha[i] = C_i;
                        alpha[j] = sum - C_i;
                    }
                } else {
                    if (alpha[j] < 0) {
                        alpha[j] = 0;
                        alpha[i] = sum;
                    }
                }
                if (sum > C_j) {
                    if (alpha[j] > C_j) {
                        alpha[j] = C_j;
                        alpha[i] = sum - C_j;
                    }
                } else {
                    if (alpha[i] < 0) {
                        alpha[i] = 0;
                        alpha[j] = sum;
                    }
                }
            }

            // update G

            double delta_alpha_i = alpha[i] - old_alpha_i;
            double delta_alpha_j = alpha[j] - old_alpha_j;

            for (int k = 0; k < active_size; k++) {
                G[k] += getQ(i,k) * delta_alpha_i + getQ(j,k) * delta_alpha_j;
            }

            // update alpha_status and G_bar
            boolean ui = is_upper_bound(i);
            boolean uj = is_upper_bound(j);
            update_alpha_status(i);
            update_alpha_status(j);
            int k;
            if (ui != is_upper_bound(i)) { 
                if (ui) {
                    for (k = 0; k < l; k++) {
                        G_bar[k] -= C_i * getQ(i,k);
                    }
                } else {
                    for (k = 0; k < l; k++) {
                        G_bar[k] += C_i * getQ(i,k);
                    }
                }
            }

            if (uj != is_upper_bound(j)) {
                if (uj) {
                    for (k = 0; k < l; k++) {
                        G_bar[k] -= C_j * getQ(j,k);
                    }
                } else {
                    for (k = 0; k < l; k++) {
                        G_bar[k] += C_j * getQ(j,k);
                    }
                }
            }
            

        }

        if (iter >= max_iter) {           
            System.out.println("\nWARNING: reaching max number of iterations");
        }

        // calculate rho
        si.rho = calculate_rho();

        // calculate objective value
        double v = 0;

        for (i = 0; i < l; i++) {
            v += alpha[i] * (G[i] + p[i]);
        }

        si.obj = v / 2;


        // put back the solution
        for (i = 0; i < l; i++) {
            alpha_[active_set[i]] = alpha[i];
        }
        

        si.upper_bound_p = Cp;
        si.upper_bound_n = Cn;

        System.out.println("\noptimization finished, #iter = " + iter);
    }

    // return 1 if already optimal, return 0 otherwise
    int select_working_set(int[] working_set) {
        // return i,j such that
        // i: maximizes -y_i * grad(f)_i, i in I_up(\alpha)
        // j: mimimizes the decrease of obj value
        //    (if quadratic coefficeint <= 0, replace it with tau)
        //    -y_j*grad(f)_j < -y_i*grad(f)_i, j in I_low(\alpha)

        double Gmax = -INF;
        double Gmax2 = -INF;
        int Gmax_idx = -1;
        int Gmin_idx = -1;
        double obj_diff_min = INF;

        for (int t = 0; t < active_size; t++) {
            if (y[t] == +1) {
                if (!is_upper_bound(t)) {
                    if (-G[t] >= Gmax) {
                        Gmax = -G[t];
                        Gmax_idx = t;
                    }
                }
            } else {
                if (!is_lower_bound(t)) {
                    if (G[t] >= Gmax) {
                        Gmax = G[t];
                        Gmax_idx = t;
                    }
                }
            }
        }

        int i = Gmax_idx;


        for (int j = 0; j < active_size; j++) {
            if (y[j] == +1) {
                if (!is_lower_bound(j)) {
                    double grad_diff = Gmax + G[j];
                    if (G[j] >= Gmax2) {
                        Gmax2 = G[j];
                    }
                    if (grad_diff > 0) {
                        double obj_diff;
                        //double quad_coef = QD[i] + QD[j] - 2.0 * y[i] * Q_i[j];
                        double quad_coef = getK(i,i) + getK(j,j) - 2.0 * y[i] * getQ(i,j);
                        if (quad_coef > 0) {
                            obj_diff = -(grad_diff * grad_diff) / quad_coef;
                        } else {
                            obj_diff = -(grad_diff * grad_diff) / 1e-12;
                        }

                        if (obj_diff <= obj_diff_min) {
                            Gmin_idx = j;
                            obj_diff_min = obj_diff;
                        }
                    }
                }
            } else {
                if (!is_upper_bound(j)) {
                    double grad_diff = Gmax - G[j];
                    if (-G[j] >= Gmax2) {
                        Gmax2 = -G[j];
                    }
                    if (grad_diff > 0) {
                        double obj_diff;
                        //double quad_coef = QD[i] + QD[j] + 2.0 * y[i] * Q_i[j];
                        double quad_coef = getK(i,i) + getK(j,j) + 2.0 * y[i] * getQ(i,j);
                        if (quad_coef > 0) {
                            obj_diff = -(grad_diff * grad_diff) / quad_coef;
                        } else {
                            obj_diff = -(grad_diff * grad_diff) / 1e-12;
                        }

                        if (obj_diff <= obj_diff_min) {
                            Gmin_idx = j;
                            obj_diff_min = obj_diff;
                        }
                    }
                }
            }
        }

        if (Gmax + Gmax2 < eps) {
            return 1;
        }

        working_set[0] = Gmax_idx;
        working_set[1] = Gmin_idx;
        return 0;
    }

    


    double calculate_rho() {
        double r;
        int nr_free = 0;
        double ub = INF, lb = -INF, sum_free = 0;
        for (int i = 0; i < active_size; i++) {
            double yG = y[i] * G[i];

            if (is_lower_bound(i)) {
                if (y[i] > 0) {
                    ub = Math.min(ub, yG);
                } else {
                    lb = Math.max(lb, yG);
                }
            } else if (is_upper_bound(i)) {
                if (y[i] < 0) {
                    ub = Math.min(ub, yG);
                } else {
                    lb = Math.max(lb, yG);
                }
            } else {
                ++nr_free;
                sum_free += yG;
            }
        }

        if (nr_free > 0) {
            r = sum_free / nr_free;
        } else {
            r = (ub + lb) / 2;
        }

        return r;
    }
}

