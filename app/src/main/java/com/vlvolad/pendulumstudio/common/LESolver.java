package com.vlvolad.pendulumstudio.common;

/**
 * Class for solving the linear system of equations
 * using the LU decomposition.
 */
public class LESolver {
	int order;
	double a[][];
	double b[];
	double x[];
	public int LUDecomposited;
	
	public LESolver(int sz, double inA[][], double inB[])
	{
		LUDecomposited = 0;
		order = sz;
		a = new double[sz][sz];
		for(int i=0;i<sz;++i)
			for(int j=0;j<sz;++j)
				a[i][j] = inA[i][j];
		b = new double[sz];
		for(int i=0;i<sz;++i) b[i] = inB[i];
		x = new double[sz];
	}
	
	boolean LUDecompose()
	{
		if (LUDecomposited==-1) return false;
		else if (LUDecomposited==1) return true;
		for(int i=0;i<order;++i)
		{
			if (a[i][i]==0)
			{
				LUDecomposited = 0;
				return false;
			}
			for(int j=i+1;j<order;++j) a[i][j] /= a[i][i];
			for(int j=i+1;j<order;++j)
			{
				for(int k=i+1;k<order;++k)
				{
					a[j][k] -= a[j][i]*a[i][k];
				}
			}
		}
		LUDecomposited = 1;
		return true;
	}
	
	public double[] Solve()
	{
		if (LUDecomposited==-1 || (LUDecomposited==0 && !LUDecompose()))
		{
			return x;
		}
		for(int i=0;i<order;++i)
		{
			x[i] = b[i] / a[i][i];
			for(int j=0;j<i;++j)
				x[i] += -a[i][j] * x[j] / a[i][i];
		}
		for(int i=order-1;i>=0;--i)
		{
			for(int j=i+1;j<order;++j)
				x[i] += -a[i][j] * x[j];
		}
		return x;
	}
}
