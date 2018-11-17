package com.vlvolad.pendulumstudio.common;

/**
 * Abstract class for a generic mechanical system
 * Characterized by vectors of generalized coordinates and velocities
 * System's motion is determined by the Euler-Lagrange equations
 * Descendant classes to provide explicit expressions for 2nd time-derivatives
 * of generalized coordinates as functions of these coordinates and their first derivatives
 */
public abstract class MechSystemrkf45 {
	protected int sz;
	protected double q[], qv[];
	protected double qt[], qvt[], a[];
	protected double k1[],k2[],k3[],k4[],k5[],k6[];
	protected double a2,a3,a4,a5,a6;
	protected double b2,b3,b4,b5,b6;
	protected double c3,c4,c5,c6;
	protected double d4,d5,d6;
	protected double e5,e6;
	protected double f6;
	protected double r1,r3,r4,r5,r6;
	protected double n1,n3,n4,n5;
	protected double hmin,hmax,h;
	protected double eps,err;
	protected double t,s;
	
	protected MechSystemrkf45()
	{
		this.sz = 0;
		a2=1/4.0; a3=3/8.0; a4=12/13.0; a5=1; a6=1/2.0;
	    b2=1/4.0; b3=3/32.0; b4=1932/2197.0; b5=439/216.0; b6=-8/27.0;
	    c3=9/32.0; c4=-7200/2197.0; c5=-8; c6=2;
	    d4=7296/2197.0; d5=3680/513.0; d6=-3544/2565.0;
	    e5=-845/4104.0; e6=1859/4104.0;
	    f6=-11/40.0;
	    r1=1/360.0; r3=-128/4275.0; r4=-2197/75240.0; r5=1/50.0; r6=2/55.0;
	    n1=25/216.0; n3=1408/2565.0; n4=2197/4104.0; n5=-1/5.0;
		h=0.001;
		eps=1e-10;
	}
	
	protected abstract void accel(double a[],double qt[],double qvt[]);
	protected void integrate(double dt, int pre)
	{
		t = 0;
		hmin = dt/pre;
		//hmax = max(dt/pre,dt/10.0);
		hmax = dt;
		if (h>hmax) h = hmax;
		if (h<hmin) h = hmin;
		
		while (t<dt)
		{
			if (t+h>dt) h=dt-t;
			for(int i=0;i<sz;++i) 
			{
				qt[i] = q[i]; 
				qvt[i] = qv[i];
			}	
			accel(a,qt,qvt);
			for(int i=0;i<sz;++i) 
			{
				k1[i] = h*qvt[i]; 
				k1[sz + i] = h*a[i];
			}

			for(int i=0;i<sz;++i) 
			{
				qt[i] = q[i]+b2*k1[i];
				qvt[i] = qv[i]+b2*k1[sz+i];
			}
			accel(a,qt,qvt);
			for(int i=0;i<sz;++i) 
			{
				k2[i] = h*qvt[i];
				k2[sz + i] = h*a[i];
			}

			for(int i=0;i<sz;++i) 
			{
				qt[i] = q[i]+b3*k1[i]+c3*k2[i];
				qvt[i] = qv[i]+b3*k1[sz+i]+c3*k2[sz+i];
			}
			accel(a,qt,qvt);
			for(int i=0;i<sz;++i) 
			{
				k3[i] = h*qvt[i];
				k3[sz + i] = h*a[i];
			}

			for(int i=0;i<sz;++i) 
			{
				qt[i] = q[i]+b4*k1[i]+c4*k2[i]+d4*k3[i]; 
				qvt[i] = qv[i]+b4*k1[sz+i]+c4*k2[sz+i]+d4*k3[sz+i];
			}
			accel(a,qt,qvt);
			for(int i=0;i<sz;++i) 
			{
				k4[i] = h*qvt[i];
				k4[sz + i] = h*a[i];
			}

			for(int i=0;i<sz;++i) 
			{
				qt[i] = q[i]+b5*k1[i]+c5*k2[i]+d5*k3[i]+e5*k4[i];
				qvt[i] = qv[i]+b5*k1[sz+i]+c5*k2[sz+i]+d5*k3[sz+i]+e5*k4[sz+i];
			}
			accel(a,qt,qvt);
			for(int i=0;i<sz;++i) 
			{
				k5[i] = h*qvt[i];
				k5[sz + i] = h*a[i];
			}

			for(int i=0;i<sz;++i) 
			{
				qt[i] = q[i]+b6*k1[i]+c6*k2[i]+d6*k3[i]+e6*k4[i]+f6*k5[i];
				qvt[i] = qv[i]+b6*k1[sz+i]+c6*k2[sz+i]+d6*k3[sz+i]+e6*k4[sz+i]+f6*k5[sz+i];
			}
			accel(a,qt,qvt);
			for(int i=0;i<sz;++i) 
			{
				k6[i] = h*qvt[i];
				k6[sz + i] = h*a[i];
			}

	        err = Math.abs(r1*k1[0]+r3*k3[0]+r4*k4[0]+r5*k5[0]+r6*k6[0]);
			for(int i=0;i<2*sz-1;++i)
	            if (err<Math.abs(r1*k1[i+1]+r3*k3[i+1]+r4*k4[i+1]+r5*k5[i+1]+r6*k6[i+1]))
	                err = Math.abs(r1*k1[i+1]+r3*k3[i+1]+r4*k4[i+1]+r5*k5[i+1]+r6*k6[i+1]);
			if (err<eps || h<2*hmin)
			{
				for(int i=0;i<sz;++i) 
				{
					q[i] += n1*k1[i]+n3*k3[i]+n4*k4[i]+n5*k5[i];
					qv[i] += n1*k1[sz+i]+n3*k3[sz+i]+n4*k4[sz+i]+n5*k5[sz+i];
				}
				t += h;
			}
			s = Math.sqrt( Math.sqrt ( eps*h/2/err) );
			h *= s;
	        if (h>hmax) h = hmax;
	        if (h<hmin) h = hmin;
		}
	}
}
