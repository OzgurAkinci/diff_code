package com.app.diff_code.api;

import java.math.*;

public class BFCApp {
  public static void main(String[] args) {
    int d = 3; //d. turev hesabi
    int p = 10; //number of points
    
    BFCApp fn = new BFCApp();
    //fn.findAllDiff(d, p);
    
    System.out.println(p+" noktal� t�rev form�lleri");
    for (int t=1; t<=d; t++) {
      if (t>=p) {
        System.out.println("T�revin derecesinden daha fazla say�da nokta girilmelidir.");
        continue;
      }
      System.out.println(t+". t�revler");
      int[] m = new int[p-1];
      for (int i=1; i<=p; i++) {
        int k = 0;
        for (int j=i-p; j<i; j++) {
          if (j!=0)
            m[k++] = j;
        }
        Exp df = fn.getDiff(m, t);
        if (df == null)
          System.out.println("y["+t+","+(p-i)+","+(i-1)+"] hesaplanamadi!");
        else
          System.out.println("y["+t+","+(p-i)+","+(i-1)+"]=" + df  + "+O(h^"+(m.length+1-t)+")");
      }
      //System.out.println();
    }
  }
  
  public void findAllDiff(int d, int p) {
  	//Backward difference formulas
    System.out.println("Backward difference formulas: y[d,p] (p sayida nokta ile d. turev)");
    for (int i=d; i<=p; i++) {
      int[] m = new int[i];
      for (int j=0; j<i; j++) {
        m[i-j-1] = -(j+1);
      }
      Exp df = getDiff(m, d);
      if (df == null)
        System.out.println("y["+d+","+((i<9?"0":"")+(i+1))+"] hesaplanamadi!");
      else
        System.out.println("y["+d+","+((i<9?"0":"")+(i+1))+"]=" + df  + "+O(h^"+(m.length+1-d)+")");
    }

    //Forward difference formulas
    System.out.println("Forward difference formulas: y[d,p] (p sayida nokta ile d. turev)");
    for (int i=d; i<=p; i++) {
      int[] m = new int[i];
      for (int j=0; j<i; j++) {
        m[j] = j+1;
      }
      Exp df = getDiff(m, d);
      if (df == null)
        System.out.println("y["+d+","+((i<9?"0":"")+(i+1))+"] hesaplanamadi!");
      else
        System.out.println("y["+d+","+((i<9?"0":"")+(i+1))+"]=" + df  + "+O(h^"+(m.length+1-d)+")");
    }

    //Central difference formulas
    System.out.println("Central difference formulas: y[d,p] (p sayida nokta ile d. turev)");
    for (int i=d; i<=p/2; i++) {
      int k = i;
      int[] m = new int[2*i];
      for (int j=0; j<i; j++) {
        m[k-2*j-1] = -(j+1);
        m[k++] = j+1;
      }
      Exp df = getDiff(m, d);
      if (df == null)
        System.out.println("y["+d+","+((i<5?"0":"")+(2*i+1))+"] hesaplanamadi!");
      else
        System.out.println("y["+d+","+((i<5?"0":"")+(2*i+1))+"]=" + df  + "+O(h^"+(m.length+2-2*((d+1)/2))+")");
    }
  }
  
  public Exp getDiff(int[] mh, int d) {
    FDiff fd = new FDiff(mh);
    Texp[] e = fd.series();
    Texp[] e2 = fd.multiply(e);
    Texp e3 = fd.add(e2);
    Exp[] e4 = fd.equations(e3, fd.getM().length);
    Exp[][] e5 = fd.coeffMatrix(e4);
    Exp[][] e6 = fd.diffMatrix(e5, d);
 
    //Asagiya dogru indirge
    for (int i=0; i<e6.length; i++) {
      e6 = fd.exchangeMatrix(e6, i);
      for (int j=i+1; j<e6.length; j++) {
        e6 = fd.gaussMatrix(e6, i, j);
      }
    }
    
    //Yukariya dogru indirge
    for (int i=e6.length-1; i>0; i--) {
      for (int j=i-1; j>=0; j--) {
        e6 = fd.gaussMatrix(e6, i, j);
      }
    }
    
    //Birim matrise donustur
    e6 = fd.identityMatrix(e6);

    //EKOK hesapla
    BigInteger ekok = BigInteger.ONE;
    for (int i=0; i<e6.length; i++) {
      ekok = ((Ratio)e6[i][e6.length]).lcm(ekok);
    }

    //Katsay� degerleri hesapla ve tabloya yaz.
    Table table = new Table();
    table.put("t"+(e6.length+1), new Ratio(ekok));
    for (int i=0; i<e6.length; i++) {
      table.put("t"+(i+1), ((Ratio)e6[i][e6.length]).multiplyR(new Ratio(ekok)).Simplify());
    }
    table.put("t0", e4[0].eval(table).Simplify());
    table.put("td", e4[d].eval(table).Simplify());
    
    //0 ile b�lme hatas� varsa
    if (e4[d].eval(table).getR2().compareTo(BigDecimal.ZERO)==0)
      return null;

    //T�rev katsayisi negatif ise
    boolean negate = false;
    if (e4[d].eval(table).Simplify().getDecimal().signum()<0) {
      negate = true;
      for (int i=1; i<=e6.length+1; i++) {
        table.put("t"+i, table.get("t"+i).multiplyR(new Ratio(-1)));
      }
      table.put("td", table.get("td").multiplyR(new Ratio(-1)));
    }
    else {
      table.put("t0", table.get("t0").multiplyR(new Ratio(-1)));
    }

    //T�rev ifadesi
    //Exp df = new Times(table.get("t0"), new Id("y0"));
    //Bazi tn katsay�lar� 0 olabilmektedir.
    Exp df = null;
    for (int i=0; i<=e6.length; i++) {
      if (mh[i]>0 && i==0)
        df = difExp(df, table.get("t0"), "y0");
      df = difExp(df, table.get("t"+(i+1)), mh[i] < 0 ? "y_"+(-1*mh[i]) : "y"+mh[i]);
      if (mh[i]<0 && (i==e6.length || mh[i+1]>0))
        df = difExp(df, table.get("t0"), "y0");
    }
    Ratio dn2 = table.get("td");
    if (d==1 && dn2.getValue()==1)
      df = new Divide(new Par(df), new Id("h"));
    else if (d==1)
      df = new Divide(new Par(df), new Times(dn2, new Id("h")));
    else if (dn2.getValue()==1)
      df = new Divide(new Par(df), new Power(new Id("h"),new Num(d)));
    else
      df = new Divide(new Par(df), new Times(dn2, new Power(new Id("h"),new Num(d))));
    
    return df;
  }
  
  public Exp difExp(Exp df, Ratio dn, String yn) {
    if (dn.getR1().compareTo(BigDecimal.ZERO)==0)
      return df;
    if (dn.getDecimal().signum()>0) {
      if (dn.getR1().compareTo(BigDecimal.ONE)==0)
        return (df == null ? new Id(yn) : new Plus(df, new Id(yn)));
      else
        return (df == null ? new Times(dn, new Id(yn)) : new Plus(df, new Times(dn, new Id(yn))));
    }      
    else {
      if (dn.getR1().abs().compareTo(BigDecimal.ONE)==0)
        return (df == null ? new Minus(new Ratio(0), new Id(yn)) : new Minus(df, new Id(yn)));
      else
        return (df == null ? new Times(dn, new Id(yn)) : new Minus(df, new Times(dn.abs(), new Id(yn))));
    }
  }
}

