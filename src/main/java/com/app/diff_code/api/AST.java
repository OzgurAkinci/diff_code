package com.app.diff_code.api;

import java.math.*;

class Texp {
  Exp e1, e2;
  public Texp(Exp a, Exp b) {
    e1 = a;
    e2 = b;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public String toString() {
    return e1 + "=" + e2;
  }
  
  public String toLatex(boolean x) {
    return e1.toLatex(x) + "=" + e2.toLatex(x);
  }
  
  public String toClass() {
    return "Texp("+e1.toClass()+","+e2.toClass()+")";
  }
}

abstract class Exp {
  public abstract Exp getE1();
  public abstract Exp getE2();
  public abstract String toClass();
  public abstract Exp insert(Table t);
  public Ratio eval(Table t) { return new Ratio(); }
  public BigDecimal getDecimal() { return BigDecimal.ZERO; }
  public String toLatex(boolean x) { return ""; }
}

class Plus extends Exp {
  Exp e1, e2;
  public Plus(Exp a, Exp b) {
    e1 = a;
    e2 = b;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public Exp insert(Table t) {
    //return new Plus(e1.insert(t), e2.insert(t));
  	Exp a = e1.insert(t);
  	Exp b = e2.insert(t);
  	if ((a instanceof Ratio && ((Ratio)a).getDecimal().signum()==0) && (b instanceof Ratio && ((Ratio)b).getDecimal().signum()==0))
  	  return new Ratio();
  	if (a instanceof Ratio && ((Ratio)a).getDecimal().signum()==0)
  	  return b;
  	if (b instanceof Ratio && ((Ratio)b).getDecimal().signum()==0)
  	  return a;
    return new Plus(a, b);
  }
  
  public Ratio eval(Table t) {
    return new Ratio(e1.eval(t)).addR(e2.eval(t));
  }

  public String toString() {
    return e1 + "+" + e2;
  }
  
  public String toLatex(boolean x) {
    return e1.toLatex(x) + "+" + e2.toLatex(x);
  }
  
  public String toClass() {
    return "Plus("+e1.toClass()+","+e2.toClass()+")";
  }
}

class Minus extends Exp {
  Exp e1, e2;
  public Minus(Exp a, Exp b) {
    e1 = a;
    e2 = b;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public Exp insert(Table t) {
    return new Minus(e1.insert(t), e2.insert(t));
  }
  
  public Ratio eval(Table t) {
    return new Ratio(e1.eval(t)).subtractR(e2.eval(t));
  }

  public String toString() {
    if (e1 instanceof Ratio && ((Ratio)e1).getR1().compareTo(BigDecimal.ZERO)==0)
      return "-" + e2;
    return e1 + "-" + e2;
  }

  public String toLatex(boolean x) {
    if (x && e1 instanceof Ratio && ((Ratio)e1).getR1().compareTo(BigDecimal.ZERO)==0)
      return "-" + e2.toLatex(x);
    return e1.toLatex(x) + "-" + e2.toLatex(x);
  }

  public String toClass() {
    return "Minus("+e1.toClass()+","+e2.toClass()+")";
  }
}

class Times extends Exp {
  int n;
  Exp e1, e2;
  public Times(Exp a) {
    n = 0;
    e1 = new Num(1);
    e2 = a;
  }
  public Times(int a, Exp b) {
    n = a;
    e1 = new Num(1);
    e2 = b;
  }
  public Times(Exp a, Exp b) {
    n = 1;
    e1 = a;
    e2 = b;
  }
  public Times(int a, Exp b, Exp c) {
    n = a;
    e1 = b;
    e2 = c;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public Exp insert(Table t) {
    //return new Times(e1.insert(t), e2.insert(t));
  	Exp a = e1.insert(t);
  	Exp b = e2.insert(t);
  	if (a instanceof Ratio && ((Ratio)a).getR1().compareTo(BigDecimal.ZERO)==0 && b instanceof Ratio && ((Ratio)b).getR1().compareTo(BigDecimal.ZERO)==0)
  	  return new Times(a, b);
  	//if ((a instanceof Ratio && ((Ratio)a).getDecimal().signum()==0) || (b instanceof Ratio && ((Ratio)b).getDecimal().signum()==0))
  	if ((a instanceof Ratio && ((Ratio)a).getR2().compareTo(BigDecimal.ZERO)==0) || (b instanceof Ratio && ((Ratio)b).getR2().compareTo(BigDecimal.ZERO)==0))
  	  return new Ratio();
    return new Times(a, b);
  }
  
  public Ratio eval(Table t) {
    return new Ratio(e1.eval(t)).multiplyR(e2.eval(t));
  }

  public String toString() {
    return e1 + "*" + e2;
  }
  
  public String toString2() {
    if (n==0)
      return e2+"";
    else if (n==1)
      return "h*f[1]";
    return e1+"*"+e2+"*h^"+n+"f["+n+"]";
  }

  public String toLatex(boolean x) {
    //System.out.println("e1: " + e1.toClass());
    //System.out.println("e2: " + e2.toClass());
  	if (x) {
  	  /*if (e1 instanceof Ratio && ((Ratio)e1).getR1().compareTo(BigDecimal.ONE)==0)
  	  	return e2.toLatex(x);
  	  if (e1 instanceof Ratio && ((Ratio)e1).getR1().abs().compareTo(BigDecimal.ONE)==0)
  	  	return "-" + e2.toLatex(x);*/
  	  return e1.toLatex(x) + "*" + e2.toLatex(x);
  	}

    //h^0/0! terimini ��kar
    if (e1 instanceof Power) {
      if (((Power)e1).e1 instanceof Id && ((Id)((Power)e1).e1).id.equals("h") && ((Num)((Power)e1).e2).n.intValue()==0)
        return e2.toLatex(x);
    }
    //1^n terimlerini ��kar
    //n^1 terimlerinden "^1" i ��kar
    if (e1 instanceof Ratio) {
      Exp e = ((Ratio)e1).e1;
      if (e instanceof Power && ((Power)e).e1 instanceof Num && ((Num)((Power)e).e1).n.intValue()==1)
        return e2.toLatex(x);
      if (e instanceof Power && ((Power)e).e2 instanceof Num) {
        //n^0 terimlerini ��kar
        if (((Num)((Power)e).e2).n.intValue()==0)
          return e2.toLatex(x);
        //n^1 terimlerinden "^1" i ��kar
        if (((Num)((Power)e).e2).n.intValue()==1)
          return ((Power)e).e1.toLatex(x) + "*" + e2.toLatex(x);
      }
    }
    if (e2 instanceof Ratio) {
      Exp e = ((Ratio)e2).e1;
      //1^n terimlerini ��kar
      if (e instanceof Power && ((Power)e).e1 instanceof Num && ((Num)((Power)e).e1).n.intValue()==1)
        return e1.toLatex(x);
      if (e instanceof Power && ((Power)e).e2 instanceof Num) {
        //n^0 terimlerini ��kar
        if (((Num)((Power)e).e2).n.intValue()==0)
          return e1.toLatex(x);
        //n^1 terimlerinden "^1" i ��kar
        if (((Num)((Power)e).e2).n.intValue()==1)
          return e1.toLatex(x) + "*" + ((Power)e).e1.toLatex(x);
      }
    }
    return e1.toLatex(x) + "*" + e2.toLatex(x);
  }

  public String toClass() {
    return "Times("+e1.toClass()+","+e2.toClass()+")";
  }
}

class Divide extends Exp {
  Exp e1, e2;
  public Divide(Exp a, Exp b) {
    e1 = a;
    e2 = b;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public Exp insert(Table t) {
    return new Divide(e1.insert(t), e2.insert(t));
  }
  
  public Ratio eval(Table t) {
    return new Ratio(e1.eval(t)).divideR(e2.eval(t));
  }

  public String toString() {
    return e1 + "/" + e2;
  }

  public String toLatex(boolean x) {
    return "\\frac{"+e1.toLatex(x)+"}{"+e2.toLatex(x)+"}";
  }

  public String toClass() {
    return "Divide("+e1.toClass()+","+e2.toClass()+")";
  }
}

class Power extends Exp {
  Exp e1, e2;
  public Power(Exp a, Exp b) {
    e1 = a;
    e2 = b;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public Exp insert(Table t) {
    return new Power(e1.insert(t), e2.insert(t));
  }
  
  public BigDecimal getDecimal() {
	if (e1 instanceof Num && e2 instanceof Num)
	  return e1.getDecimal().pow(e2.getDecimal().toBigInteger().intValue());
	return BigDecimal.ONE;
  }
  
  public Ratio eval(Table t) {
    return e1.eval(t).powR(e2.eval(t));
  }

  public String toString() {
    return e1 + "^" + e2;
  }
  
  public String toLatex(boolean x) {
    if (e1 instanceof Id && ((Id)e1).id.equals("h")) {
      int n = ((Num)e2).n.intValue();
      if (n==1)
        return "h";
      if (!x)
        return "\\frac{h^{"+n+"}}{"+n+"!}";
    }
    if (e1 instanceof Num && ((Num)e1).n.intValue()==1)
      return "1";
    if (e2 instanceof Num && ((Num)e2).n.intValue()==1)
      return e1.toLatex(x);
    if (e1 instanceof Id || e1 instanceof Num || e1 instanceof Ratio)
      return e1.toLatex(x)+"^{"+e2.toLatex(x)+"}";
    return "("+e1.toLatex(x)+")^{"+e2.toLatex(x)+"}";
  }

  public String toClass() {
    return "Power("+e1.toClass()+","+e2.toClass()+")";
  }
}

class Par extends Exp {
  Exp e;
  public Par(Exp a) {
    e = a;
  }
  
  public Exp getE1() {
    return e;
  }

  public Exp getE2() {
    return e;
  }

  public Exp insert(Table t) {
    return new Par(e.insert(t));
  }
  
  public Ratio eval(Table t) {
    return e.eval(t);
  }
  
  public BigDecimal getDecimal() {
    return e.getDecimal();
  }

  public String toString() {
    return "(" + e + ")";
  }
  
  public String toLatex(boolean x) {
  	if (x)
  	  return e.toLatex(x);

    return "("+e.toLatex(x)+")";
  }

  public String toClass() {
    return "Par("+e.toClass()+")";
  }
}

class RFact extends Exp {
  int n;
  public RFact(int a) {
    n = a;
  }
  
  public Exp getE1() {
    return this;
  }

  public Exp getE2() {
    return this;
  }

  public Exp insert(Table t) {
    return new RFact(n);
  }
  
  public Ratio eval(Table t) {
    return new Ratio(n);
  }
  
  public BigInteger fact() {
	BigInteger result = BigInteger.ONE;
    for(int i = 2; i <= n; i++)
      result = result.multiply(BigInteger.valueOf(i));
	return result;
  }
  
  public BigDecimal getDecimal() {
    return new BigDecimal(fact());
  }

  public String toString() {
    return n + "!";
  }
  
  public String toLatex(boolean x) {
    return n + "!";
  }

  public String toClass() {
    return "RFact("+n+")";
  }
}

class Id extends Exp {
  String id;
  public Id(String a) {
    id = a;
  }
  
  public Exp getE1() {
    return this;
  }

  public Exp getE2() {
    return this;
  }
  
  public Exp insert(Table t) {
    Ratio dn = t.get(id);
    //System.out.println("table.get("+id+"): "+dn);
    if (dn==null)
      return new Id(id);
    if (dn.getR1().compareTo(BigDecimal.ZERO)==0) {
      return new Ratio();
    }
    if (dn.getR1().compareTo(BigDecimal.ZERO)<0)
      return new Par(dn);
    return dn;
  }

  public Ratio eval(Table t) {
    return t.get(id);
  }

  public String toString() {
    return id;
  }
  
  public String toLatex(boolean x) {
    if (id.charAt(0)=='t' || id.charAt(0)=='y') {
      String n = id.charAt(1)=='_'? "-"+id.substring(2) : id.substring(1);
      return id.charAt(0)+"_{"+n+"}";
    }
    return id;
  }

  public String toClass() {
    return "Id("+id+")";
  }
}

class Num extends Exp {
  BigInteger n;	
  public Num(int a){
    n=BigInteger.valueOf(a);
  }
  
  public Num(BigInteger a){
	n = a;
  }

  public Exp getE1() {
    return this;
  }

  public Exp getE2() {
    return this;
  }

  public Exp insert(Table t) {
    return new Num(n);
  }
  
  public Ratio eval(Table t) {
    return new Ratio(n);
  }
  
  public BigDecimal getDecimal() {
    return new BigDecimal(n);
  }

  public String toString(){
	return n.signum()<0 ? "("+n+")" : n+"";
  }
  
  public String toLatex(boolean x) {
    return n.signum()<0 ? "("+n+")" : n+"";
  }

  public String toClass() {
    return "Num("+n+")";
  }
}

class Diff extends Exp {
  int d;
  public Diff(int a) {
    d = a;
  }
  public Exp getE1() {
    return this;
  }

  public Exp getE2() {
    return this;
  }
    
  public Exp insert(Table t) {
    return new Diff(d);
  }

  public String toString(){
	return "f["+d+"]";
  }
  
  public String toLatex(boolean x) {
    if (d==0)
      return "f(x_{0})";
    if (d==1)
      return "f'(x_{0})";
    if (d==2)
      return "f''(x_{0})";
    if (d==3)
      return "f'''(x_{0})";
    return "f^{("+d+")}(x_{0})";
  }

  public String toClass() {
    return "Diff("+d+")";
  }
}

class RDec extends Exp {
  BigDecimal n;
  public RDec(){
	n = BigDecimal.ZERO;
	//n = BigDecimal.ONE;
  }
  public RDec(int a){
	n = BigDecimal.valueOf(a);
  }
  public RDec(BigInteger a){
	n = new BigDecimal(a);
  }
  public RDec(double a){
	n = BigDecimal.valueOf(a);
  }
  public RDec(BigDecimal a){
	n = a;
  }
  public Exp getE1() {
    return this;
  }

  public Exp getE2() {
    return this;
  }

  public Exp insert(Table t) {
    return new RDec(n);
  }
  
  public Ratio eval(Table t) {
    return new Ratio(n);
  }
  
  public BigDecimal getDecimal() {
    return n;
  }

  public String toString(){
	//return n.signum()<0 ? "("+n+")" : n+"";
	return n + "";
  }
  public String toString2(){
	//return  "$\\frac{"+sayi1+"}{"+sayi2+"}$";
    return n + "";
  }
  
  public String toLatex(boolean x) {
    //return n.signum()<0 ? "("+n+")" : n+"";
    return n + "";
  }

  public String toClass() {
    return "RDec("+n+")";
  }
}

class Ratio extends Exp {
  Exp e1, e2;
  public Ratio() {
	e1 = new RDec(0);
	e2 = new RDec(1);
  }
  public Ratio(int n) {
	e1 = new RDec(n);
	e2 = new RDec(1);
  }
  public Ratio(double n) {
	e1 = new RDec(n);
	e2 = new RDec(1);
  }
  public Ratio(Ratio a) {
	e1 = a.e1;
	e2 = a.e2;
  }
  public Ratio(Exp a, Exp b) {
	e1 = a;
	e2 = b;
  }
  public Ratio(BigInteger a) {
	e1 = new RDec(a);
	e2 = new RDec(1);
  }
  public Ratio(BigDecimal a) {
	e1 = new RDec(a);
	e2 = new RDec(1);
  }
  public Ratio(BigDecimal a, BigDecimal b) {
	e1 = new RDec(a);
	e2 = new RDec(b);
  }
  public Exp getE1() {
    return new Ratio(getR1());
  }
  public Exp getE2() {
    return new Ratio(getR2());
  }
  public BigDecimal getR1() {
    return e1.getDecimal();
  }
  public BigDecimal getR2() {
    return e2.getDecimal();
  }
  public Ratio getRatio(BigDecimal n1, BigDecimal n2) {
  	if(n2.signum() < 0) {  // compareTo(BigDecimal.ZERO)<0) {
  	  //n1 = new BigDecimal(-1).multiply(n1);
  	  //n2 = new BigDecimal(-1).multiply(n2);
  	  n1 = n1.negate();
  	  n2 = n2.negate();
  	}
  	return new Ratio(n1, n2);
  }
  public Ratio addR(Ratio a) {
  	BigDecimal n1 = getR1().multiply(a.getR2()).add(getR2().multiply(a.getR1()));
  	BigDecimal n2 = getR2().multiply(a.getR2());  	
  	return getRatio(n1, n2);
  }
  public Ratio subtractR(Ratio a) {
  	BigDecimal n1 = getR1().multiply(a.getR2()).subtract(getR2().multiply(a.getR1()));
  	BigDecimal n2 = getR2().multiply(a.getR2());  	
  	return getRatio(n1, n2);
  }
  public Ratio multiplyR(Ratio a) {
  	BigDecimal n1 = getR1().multiply(a.getR1());
  	BigDecimal n2 = getR2().multiply(a.getR2());
  	return getRatio(n1, n2);
  }
  public Ratio divideR(Ratio a) {
  	BigDecimal n1 = getR1().multiply(a.getR2());
  	BigDecimal n2 = getR2().multiply(a.getR1());
  	return getRatio(n1, n2);
  }
  public Ratio powR(Ratio a) { //a daima integer olmal�d�r
  	BigDecimal n1 = getR1().pow(a.getR1().intValue());
  	BigDecimal n2 = getR2().pow(a.getR1().intValue());
  	return getRatio(n1, n2);
  }
  public Ratio Simplify() {
  	try {
  	  BigInteger gcd = ebob(getR1().toBigIntegerExact().abs(), getR2().toBigIntegerExact());
	  e1 = new RDec(getR1().toBigInteger().divide(gcd));
	  e2 = new RDec(getR2().toBigInteger().divide(gcd));
  	}
  	catch (ArithmeticException exc) {
  	}
  	return this;
  }
  public Ratio abs() {
  	return new Ratio(getR1().abs(), getR2());
  }
  public BigInteger ebob(BigInteger a, BigInteger b) {
	while (!b.equals(BigInteger.ZERO)) {
	  BigInteger t = b;
	  b = a.mod(b);
	  a = t;
    }
    return a;
  }
  public BigInteger gcd(BigInteger a, BigInteger b) {
    if (b.equals(BigInteger.ZERO)) {
      return a;
    }
    else {
      return gcd(b, a.mod(b));
    }
  }
  public BigInteger lcm(BigInteger a) {
  	try {
  	  return getR2().toBigIntegerExact().multiply(a).divide(gcd(getR2().toBigIntegerExact(), a));
  	}
  	catch (Exception exc) {
  	  return getR2().toBigInteger();
  	}
  }
  public BigInteger lcm(BigInteger a, BigInteger b) {
    return a.multiply(b).divide(gcd(a, b));
  }
  public BigInteger lcm2(BigInteger[] numbers) {
    BigInteger result = numbers[0];
    for (int i = 1; i < numbers.length; i++) {
      result = lcm(result, numbers[i]);
    }
    return result;
  }
  public Exp insert(Table t) {
    return new Ratio(e1, e2);
  }
  public Ratio eval(Table t) {
    return this;
  }
  public String toString(){
    if (getR1().equals(BigDecimal.ZERO))
      return e1 + "";
    else if (getR2().equals(BigDecimal.ONE))
      return e1 + "";
    else
      return e1 + "/" + e2;
  }
  public String toString2(){
    if (getR1().equals(BigDecimal.ZERO))
      return e1 + "";
    else if (getR2().equals(BigDecimal.ONE))
      return getR1() + "";
    else
      return getR1() + "/" + getR2();
  }
  public String toLatex(boolean x){
    if (!x)
      return e1.toLatex(x) + "";
    
    if (getR1().equals(BigDecimal.ZERO))
      return e1.toLatex(x) + "";
    
    if (getR2().equals(BigDecimal.ONE))
      return e1.toLatex(x) + "";
    
    return "\\frac{"+e1.toLatex(x)+"}{"+e2.toLatex(x)+"}";
  }
  public BigDecimal getDecimal() {
  	//return getR1().divide(getR2());
  	return getR1().divide(getR2(), 10, RoundingMode.DOWN);
  }
  public double getValue() {
  	return getDecimal().doubleValue();
  }
  public String toClass() {
    return "Ratio("+e1.toClass()+","+e2.toClass()+")";
  }
}
