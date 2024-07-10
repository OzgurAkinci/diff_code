package com.app.diff_code.api;

import java.io.*;
import java.math.*;

public class FTex {
  public static PrintWriter pw = null;
  public static String latexFile = "fDiff";

  public static void main(String[] args) {
    //int d = 1; //d. t�rev hesabi
    //int[] m = {-5,-4,-3,-2,-1,1,2,3,4,5};
    //int[] m = {1,2,3,4,5};
    //int[] m = {-3,-2,-1,1};

    //int d = 1;
    //int[] m = {-4,-3,-2,-1,1,2,3,4,5};
    int d = 2;
    int[] m = {-1,1,2};

    //int[] m = {-5,-4,-3,-2,-1,1,2,3,4};
    //int[] m = {1,2,3,4,5,6,7,8,9,10};
  	//int[] m = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
  	//int[] m = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};
  	//int[] m = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};
    /*int[] m = new int[50];
    for (int i=0; i<50; i++) {
      m[i] = (i+1);
    }*/

    if (d>=m.length) {
      System.out.println("T�revin derecesinden daha fazla say�da nokta girilmelidir.");
      return;
    }

    Table table = new Table();

    //h ve y de�erlerini tabloya yaz.
    //int h = 1;
    double h = Math.PI/20;
    double y0 = Math.PI/5;
    table.put("h", new Ratio(h));
    table.put("y0", new Ratio(Math.sin(y0)));
    for (int i=0; i<m.length; i++) {
      //table.put("y"+i, new Ratio(y0+i*h));
      //table.put("y"+i, new Ratio(Math.sin(y0+i*h)));
      if (m[i]>0)
        table.put("y"+m[i], new Ratio(Math.sin(y0+m[i]*h)));
      else
        table.put("y_"+(-1*m[i]), new Ratio(Math.sin(y0+m[i]*h)));
    }

    boolean toText = true;  //Metin bazli goster
    boolean toPdf = true;   //Pdf olustur
    new FTex().findDiff(table, m, d, toText, toPdf);
  }

  public void findDiff(Table table, int[] m, int d, boolean toText, boolean toPdf) {
    FDiff fd = new FDiff(m);

  	Tex_Create();

  	if (toText)
  	  System.out.println("Sayisal Turev icin �leri Yon Sonlu Fark Form�lleri");
  	if (toPdf)
	  PutTextLn("\\section{Say�sal T�rev i�in �leri Y�n Sonlu Fark Form�lleri}");

	//System.out.println("---1---");
	if (toText) {
	  System.out.println("Taylor Serisi");
	  System.out.println("Bir f(x) fonksiyonunun x=x0 noktasindaki Taylor serisine acilimi asagidaki ifade ile hesaplanir.");
	  System.out.println("f(x) = f(x0) + f'(x0)/1!*(x-x0) + f''(x0)/2!*(x-x0)^2 + f'''(x0)/3!*(x-x0)^3 + ...");
	}
	if (toPdf) {
	  PutTextLn("\\subsection{Taylor Serisi}");
	  PutTextLn("Bir $f(x)$ fonksiyonunun $x=x_{0}$ noktas�ndaki Taylor serisine a��l�m� a�a��daki ifade ile hesaplan�r.\\\\");
	  PutTextLn("$\\displaystyle f(x)=\\sum_{n=0}^{\\infty}\\frac{f^{(n)}(x_{0})}{n!}(x-x_{0})^n = f(x_{0}) + \\frac{f'(x_{0})}{1!}(x-x_{0}) + \\frac{f''(x_{0})}{2!}(x-x_{0})^2 + \\frac{f'''(x_{0})}{3!}(x-x_{0})^3 + \\cdot\\cdot\\cdot$");
	}

	//System.out.println("---2---");
	//fTex.NoktasalAcilim(fTex, m);
	if (toText) {
	  System.out.println("Ornek Noktalar");
	  System.out.println("Bu ifade xm=x0+mh biciminde secilen her bir nokta icin duzenlenir.");
	}
	if (toPdf) {
	  PutTextLn("\\subsection{�rnek Noktalar}");
	  PutTextLn("Bu ifade $x_{m}=x_{0}+mh$ bi�iminde se�ilen her bir nokta i�in d�zenlenir.\\\\");
	}
    Texp[] e = fd.series();
    for (int i=0; i<e.length; i++) {
      if (toText)
        System.out.println(e[i]);
      if (toPdf)
        PutTextLn("$\\displaystyle "+e[i].toLatex(false)+"$\\\\");
    }

	//System.out.println("---3---");
	//fTex.Carpim(fTex, m);
	if (toText) {
	  System.out.println("Katsayilar ile Carpim");
	  System.out.println("Denklemler t1, t2, t3, ... katsayilari ile carpilir.");
	}
	if (toPdf) {
	  PutTextLn("\\subsection{Katsay�lar ile �arp�m}");
	  PutTextLn("Denklemler $t_{1}, t_{2}, t_{3}, ...$ katsay�lar� ile �arp�l�r.\\\\");
	}
    Texp[] e2 = fd.multiply(e);
    for (int i=0; i<e2.length; i++) {
      if (toText)
        System.out.println(e2[i]);
      if (toPdf)
        PutTextLn("$\\displaystyle "+e2[i].toLatex(false)+"$\\\\");
    }

    //System.out.println("---4---");
	//fTex.Birlestir(fTex, m);
	if (toText) {
	  System.out.println("Birlestirme");
	  System.out.println("Denklemlerin her iki yani toplanir.");
	}
	if (toPdf) {
	  PutTextLn("\\subsection{Birle�tirme}");
	  PutTextLn("Denklemlerin her iki yan� toplan�r.\\\\");
	}
    Texp e3 = fd.add(e2);
    if (toText)
      System.out.println(e3);
    if (toPdf)
      PutTextLn("$\\displaystyle "+e3.toLatex(false)+"$");

    //System.out.println("---5---");
    if (toText) {
	  System.out.println("Katsayi Ifadeleri}");
	  System.out.println("Bu toplam ifadesinden her bir terimin katsayi ifadesi cikarilir.");
    }
    if (toPdf) {
	  PutTextLn("\\subsection{Katsay� �fadeleri}");
	  PutTextLn("Bu toplam ifadesinden her bir terimin katsay� ifadesi ��kar�l�r.\\\\");
    }
    Exp[] e4 = fd.equations(e3, fd.getM().length);
    for (int i=0; i<e4.length; i++) {
      if (toText)
        System.out.println("Exp["+i+"]="+e4[i].getE1());
      if (toPdf)
        PutTextLn("$\\displaystyle Exp_{"+i+"}: "+e4[i].getE1().toLatex(false)+"$\\\\");
    }

	if (toText) {
	  System.out.println("Denklem Sistemi");
	  System.out.println("Bu katsayi ifadelerinden ilki ile "+d+". turevin ifadesi cikarilarak asagidaki denklemler elde edilir.");
	}
	if (toPdf) {
	  PutTextLn("\\subsection{Denklem Sistemi}");
	  PutTextLn("Bu katsay� ifadelerinden ilki ile "+d+". t�revin ifadesi ��kar�larak a�a��daki denklemler elde edilir.\\\\");
	}
	for (int i=0; i<e4.length; i++) {
	  if (i!=0 && i!=d) {
	  	if (toText)
	  	  System.out.println(e4[i].getE1()+"=0");
	    if (toPdf)
          PutTextLn("$\\displaystyle "+e4[i].getE1().toLatex(false)+"=0$\\\\");
	  }
	}

	//System.out.println("---6---");
	//Exp[][] e5 = fd.coeffMatrix(e4);  //(1)
	if (toText)
	  System.out.println("Bu denklemlerden katsay� matrisi olusturulur.");
	if (toPdf)
	  PutTextLn("Bu denklemlerden katsay� matrisi olu�turulur.");
	Exp[][] e5 = fd.coeffMatrix(e4, d); //(2)
	if (toText)
	  System.out.println(fd.showMatrix(e5));
	if (toPdf)
      PutMatrix(e5, d, false, "");

    //System.out.println("---7---");
    if (toText)
      System.out.println("Denklemler, t"+(e5.length-1)+" bagimsiz degisken yapilarak (sag tarafa tasinarak) a�agidaki gibi yeniden duzenlenir.");
    if (toPdf)
	  PutTextLn("Denklemler, $t_{"+(e5.length-1)+"}$ ba��ms�z de�i�ken yap�larak (sa� tarafa ta��narak) a�a��daki gibi yeniden d�zenlenir.\\\\");
    Exp[][] e6 = fd.diffMatrix(e5);      //(3)
    //Exp[][] e6 = fd.diffMatrix(e5, d); //(4)
    if (toText)
      System.out.println(fd.showMatrix(e6));
    if (toPdf)
      PutMatrix(e6, d, false, "|");

    //System.out.println("---8---");
    //Asagiya dogru indirge
    if (toText)
      System.out.println("Denklem Cozumu");
    if (toPdf)
      PutTextLn("\\subsection{Denklem ��z�m�}");
    for (int i=0; i<e6.length-1; i++) {
      e6 = fd.exchangeMatrix(e6, i);
      if (fd.exchange>0) {
        if (toText) {
          System.out.println((i+1)+". sat�r "+(fd.exchange+1)+". satir ile yer de�i�tirilir.");
          System.out.println(fd.showMatrix(e6));
          System.out.println("------");
        }
        if (toPdf) {
          PutText((i+1)+". sat�r "+(fd.exchange+1)+". satir ile yer de�i�tirilir.");
          PutMatrix(e6, d, false, "|");
        }
        fd.exchange = 0;
      }
      if (((Ratio)e6[i][i]).getR1().compareTo(BigDecimal.ZERO)==0)
        continue;
      for (int j=i+1; j<e6.length; j++) {
      	if (((Ratio)e6[j][i]).getR1().compareTo(BigDecimal.ZERO)==0)
      	  continue;
        e6 = fd.gaussMatrix(e6, i, j);
        if (toText) {
          System.out.println((i+1)+". sat�r kullanilarak asagisindaki ("+(j+1)+","+(i+1)+") elemani 0 yap�l�r.");
          System.out.println(fd.showMatrix(e6));
          System.out.println("------");
        }
        if (toPdf) {
          PutText((i+1)+". sat�r kullan�larak  a�a��s�ndaki ("+(j+1)+","+(i+1)+") eleman� 0 yap�l�r.");
          PutMatrix(e6, d, false, "|");
        }
      }
    }

    //Yukariya dogru indirge
    for (int i=e6.length-1; i>0; i--) {
      if (((Ratio)e6[i][i]).getR1().compareTo(BigDecimal.ZERO)==0)
        continue;
      for (int j=i-1; j>=0; j--) {
      	if (((Ratio)e6[j][i]).getR1().compareTo(BigDecimal.ZERO)==0)
      	  continue;
        e6 = fd.gaussMatrix(e6, i, j);
        if (toText) {
          System.out.println((i+1)+". sat�r kullan�larak  yukar�s�ndaki ("+(j+1)+","+(i+1)+") elemani 0 yap�l�r.");
          System.out.println(fd.showMatrix(e6));
          System.out.println("------");
        }
        if (toPdf) {
          PutText((i+1)+". sat�r kullan�larak  yukar�s�ndaki ("+(j+1)+","+(i+1)+") eleman� 0 yap�l�r.");
          PutMatrix(e6, d, false, "|");
        }
      }
    }

    //Birim matrise donustur
    e6 = fd.identityMatrix(e6);
    if (fd.exchange>0) {
      if (toText)
        System.out.println("y["+d+"]: Birim matrise d�n���m yap�lamadi.");
      if (toPdf)
        PutTextLn("$y^{("+d+")}:$ Birim matrise d�n���m yap�lamad�.\\\\");
      Tex_Close();
      if (toPdf)
        Tex_pdf(toPdf);
      return;
    }
    else {
      if (toText) {
        System.out.println("Birim matrise d�n���m yap�l�r.");
        System.out.println(fd.showMatrix(e6));
        System.out.println("------");
      }
      if (toPdf) {
        PutTextLn("Katsay� matrisi birim matrise d�n��t�r�l�r.");
        PutMatrix(e6, d, true, "|");
      }
    }

    //Katsay� ifadeleri
    //System.out.println("---9---");
	PutTextLn("Buradan katsay� ��z�mleri a�a��daki gibi belirlenir.\\\\");
    for (int i=0; i<e6.length; i++) {
      if (toText)
        System.out.println("t"+(i+1)+"="+e6[i][e6.length]+"*t"+(e6.length+1)+"");
      if (toPdf)
        PutTextLn("$\\displaystyle t_{"+(i+1)+"}="+e6[i][e6.length].toLatex(true)+"*t_{"+(e6.length+1)+"}$\\\\");
    }

    //System.out.println("---10---");
    //EKOK hesapla
    PutTextLn("\\subsection{Katsay�lar i�in Tamsay� De�erleri}");
    PutTextLn("$t_{"+(e6.length+1)+"}$ de�i�kenine atanabilecek en k���k tamsay� de�eri EKOK hesab� ile belirlenir.\\\\");
    BigInteger ekok = BigInteger.ONE;
    String params = "";
    for (int i=0; i<e6.length; i++) {
      //params += ","+((Ratio)e6[i][e6.length]).getR2().toBigInteger().intValue();
      //params += ","+((Ratio)e6[i][e6.length]).getR2();
      if (((Ratio)e6[i][e6.length]).getR2().intValue()!=1)
        params += ","+((Ratio)e6[i][e6.length]).getR2();
      ekok = ((Ratio)e6[i][e6.length]).lcm(ekok);
    }
    if (params.length()==0)
      params = ",";
    if (toText)
      System.out.println("t"+(e6.length+1)+"=EKOK("+params.substring(1)+")=" + ekok);
    if (toPdf)
      PutTextLn("$t_{"+(e6.length+1)+"}=EKOK("+params.substring(1)+")=" + ekok + "$\\\\");

    //System.out.println("---11---");
    //Katsay� degerleri hesapla ve tabloya yaz.
    PutTextLn("\\\\");
    PutTextLn("Bu de�er yard�m�yla katsay� de�i�kenlerine atanacak de�erler hesaplan�r.\\\\");
    /*for (int i=0; i<e4.length; i++) {
      System.out.println(e4[i].eval(table).Simplify());
    }*/
    table.put("t"+(e6.length+1), new Ratio(ekok));
    for (int i=0; i<e6.length; i++) {
      table.put("t"+(i+1), ((Ratio)e6[i][e6.length]).multiplyR(new Ratio(ekok)).Simplify());
      if (toText)
        System.out.println("t"+(i+1)+"="+new Times(e6[i][e6.length], new Id("t"+(e6.length+1))).insert(table)+"="+((Ratio)e6[i][e6.length]).multiplyR(new Ratio(ekok)).Simplify());
      if (toPdf)
        PutTextLn("$\\displaystyle t_{"+(i+1)+"}="+new Times(e6[i][e6.length], new Id("t"+(e6.length+1))).insert(table)+"="+((Ratio)e6[i][e6.length]).multiplyR(new Ratio(ekok)).Simplify().toLatex(true)+"$\\\\");
    }
    table.put("t0", e4[0].eval(table).Simplify());
    table.put("td", e4[d].eval(table).Simplify());
    if (toText)
      System.out.println("t"+(e6.length+1)+"=" + new Ratio(ekok));
    if (toPdf)
      PutTextLn("$\\displaystyle t_{"+(e6.length+1)+"}=" + new Ratio(ekok).toLatex(true)+"$\\\\");

    //System.out.println("---12---");
    //y terimlerinin katsay�lar�
    //e4 : denklem katsayilarinin listesi
    //tablodan oku g�ster
    System.out.println("y0 ile birlikte t�m katsayilar");
    PutTextLn("\\\\");
    PutTextLn("$y_{0}$ noktas�n�n katsay�s� ($t_{0}$) ile birlikte t�m katsay�lar a�a��daki gibi belirlenir.\\\\");
    PutTextLn("$y_{0}: t_{0}="+e4[0].getE1().toLatex(false)+"="+e4[0].insert(table).getE1().toLatex(false)+"="+e4[0].eval(table).Simplify().toLatex(false)+"$\\\\");
    for (int i=0; i<=e6.length; i++) {
      if (toText)
        System.out.println("y"+(i+1)+": t"+(i+1)+"="+table.get("t"+(i+1)));
      if (toPdf)
        PutTextLn("$y_{"+(i+1)+"}: t_{"+(i+1)+"}="+table.get("t"+(i+1))+"$\\\\");
    }
    if (toText)
      System.out.println("y["+d+"]: "+e4[d].eval(table).Simplify()+"*h^"+d);
    if (toPdf)
      PutTextLn("$y^{("+d+")}: "+e4[d].eval(table).Simplify()+"*h"+(d==1?"":"^"+d)+"$\\\\");

    //0 ile b�lme hatas� varsa
    if (e4[d].eval(table).getR2().compareTo(BigDecimal.ZERO)==0) {
      if (toText)
        System.out.println("y["+d+"] hesaplanamad�!");
      if (toPdf)
        PutTextLn("$y^{("+d+")}$ hesaplanamad�!\\\\");
      Tex_Close();
      if (toPdf)
        Tex_pdf(toPdf);
      return;
    }

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

    String hp;
    if (negate) {
      System.out.println("T�m katsayilar (-1) ile �arp�l�r.");
      PutTextLn("\\\\");
      PutTextLn("$y^{("+d+")}$ teriminin katsay�s� negatif oldu�undan t�m katsay�lar (-1) ile �arp�l�r.\\\\");
      //System.out.println("---13---");
      //katsayilari g�ster
      Ratio big = new Ratio();
      for (int i=0; i<=e6.length+1; i++) {
      	if (toText)
          System.out.println("y"+i+": t"+i+"="+table.get("t"+i));
        if (toPdf)
          PutTextLn("$y_{"+i+"}: t_{"+i+"}="+table.get("t"+i)+"$\\\\");
        big =big.addR(table.get("t"+i));
      }
      hp = d==1?"":"^{"+d+"}";
      if (toText) {
        System.out.println("y["+d+"]: "+table.get("td")+"*h"+hp);
        System.out.println("t_sum = " + big + " (katsay�lar do�ru ise toplam� 0 olmal�d�r)");
      }
      if (toPdf) {
        PutTextLn("$y^{("+d+")}: "+table.get("td")+"*h"+hp+"$\\\\");
        PutTextLn("$t_{sum} = " + big + "$ (katsay�lar do�ru ise toplam� 0 olmal�d�r)\\\\");
      }
    }

    //System.out.println("---14---");
    //T�rev ifadesi
    PutTextLn("\\\\");
    PutTextLn("$y^{("+d+")}$ ifadesi hesaplan�r.\\\\");
    Exp df = null;
    for (int i=0; i<=e6.length; i++) {
      if (m[i]>0 && i==0)
        df = difExp(df, table.get("t0"), "y0");
      df = difExp(df, table.get("t"+(i+1)), m[i] < 0 ? "y_"+(-1*m[i]) : "y"+m[i]);
      if (m[i]<0 && (i==e6.length || m[i+1]>0))
        df = difExp(df, table.get("t0"), "y0");
    }
    //df = new Divide(df, new Times(table.get("td"), new Power(new Id("h"),new Num(d))));
    df = new Divide(new Par(df), new Times(table.get("td"), new Power(new Id("h"),new Num(d))));
    hp = e4.length-d==1?"":"^{"+(e4.length-d)+"}";
    if (toText) {
      System.out.println("y["+d+"]=" + df + "+O(h^"+(e4.length-d)+")");
      System.out.println("y["+d+"]=" + df.insert(table) + "+O(h^"+(e4.length-d)+")");
      System.out.println("y["+d+"]=" + df.eval(table).getDecimal() + "+O(h^"+(e4.length-d)+")");
    }
    if (toPdf) {
      PutTextLn("$y^{("+d+")}=" + df.toLatex(true) + "+O(h"+hp+")$\\\\");
      PutTextLn("$y^{("+d+")}=" + df.insert(table).toLatex(true) + "+O(h"+hp+")$\\\\");
      PutTextLn("$y^{("+d+")}=" + df.eval(table).getDecimal() + "+O(h"+hp+")$\\\\");
    }

    Tex_Close();
    if (toPdf)
      Tex_pdf(toPdf);
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

  public void PutMatrix(Exp[][] ms, int d, boolean ltx, String or) {
    String header = "";
	for(int i=0;i<ms[0].length-1;i++){
	  header += "r";
	}
	header += or + "r";

	PutTextLn("\\begin{center}");
  	PutTextLn("$$ \\left[\\begin{array}{"+header+"}");

  	PutText("t_{1}");
  	for(int i=1;i<ms[0].length;i++){
	  PutText(" & t_{"+(i+1)+"}");
	}
	PutTextLn("\\\\");

    for (int i=0; i<ms.length; i++) {
      PutText(ms[i][0].toLatex(false)+"");
      for (int j=1; j<ms[i].length; j++) {
        PutText(" & " + ms[i][j].toLatex(ltx));
      }
      PutTextLn("\\\\");
    }

    PutTextLn("\\end{array}\\right] $$");
  	PutTextLn("\\end{center}");
  }

  public void Tex_pdf(boolean toPdf) {
	try {
	  ProcessBuilder builder = new ProcessBuilder("pdflateX", latexFile+".tex");
	  builder.redirectErrorStream(true);
	  Process process = builder.start();
	  InputStream is = process.getInputStream();
	  InputStreamReader isr = new InputStreamReader(is);
	  BufferedReader br = new BufferedReader(isr);
	  String line;
	  while ((line = br.readLine()) != null) {
	  	if (toPdf)
	      System.out.println(line);
	  }
	} catch (IOException ex) {
	  ex.printStackTrace();
	}

	try {
	  if ((new File(latexFile+".pdf")).exists()) {
		Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+latexFile+".pdf");
	    p.waitFor();
	  }
	  else {
	    System.out.println("File does not exists: "+latexFile+".pdf");
	  }
  	} catch (Exception ex) {
	  ex.printStackTrace();
	}
  }

  public void Tex_Create() {
	try {
	  File file = new File(latexFile+".pdf");
      file.delete();
	  pw = new PrintWriter(latexFile+".tex", "UTF-8");
	} catch (FileNotFoundException e) {
	  throw new RuntimeException(e);
	} catch (UnsupportedEncodingException e) {
	  throw new RuntimeException(e);
	}

	pw.println("\\documentclass{article}");
    pw.println("\\usepackage{amsmath}");
	pw.println("\\usepackage{amssymb}");
	pw.println("\\usepackage{cancel}");
	pw.println("\\usepackage{setspace}");
	pw.println("\\usepackage{graphicx}");
	pw.println("\\usepackage{enumitem}");
	pw.println("\\usepackage[colorlinks=true, allcolors=blue]{hyperref}");
	pw.println("\\usepackage[english]{babel}");
	pw.println("\\usepackage[letterpaper,top=2cm,bottom=2cm,left=1cm,right=2cm,marginparwidth=1 cm]{geometry}");
	pw.println("\\usepackage{nicefrac, xfrac}");
	pw.println("\\usepackage{indentfirst}");
	pw.println("\\onehalfspacing");
	pw.println("\\begin{document}");
	pw.println("\\setlength\\parindent{0pt}"); //Set noindent for entire file
  }

  public void Tex_Close(){
	pw.println("\\end{document}");
	pw.close();
  }

  public void PutText(String s){
	pw.print(s);
  }

  public void PutTextLn(String s){
	pw.println(s);
  }

  public void PutTextLnLn(String s){
	pw.println(s + "\n\n");
  }

  ///Kullan�lmayan tan�mlamalar
  public void NoktasalAcilim(FTex fTex, int[] m) {
  	String h, c;
	for (int i=0; i<m.length; i++) {
	  if (m[i]==1) h = "h";
	  else if (m[i]==-1) h = "-h";
	  else h = m[i]+"h";
	  c = m[i] == 1 ? h : "("+h+")";
	  PutTextLnLn("     $\\displaystyle y_{"+(i+1)+"}=f(x_{0}+"+h+") = f(x_{0}) + \\frac{"+h+"}{1!}f'(x_{0}) + \\frac{"+c+"^2}{2!}f''(x_{0}) + \\frac{"+c+"^3}{3!}f'''(x_{0}) + \\cdot\\cdot\\cdot$");
	}
  }

  public void Carpim(FTex fTex, int[] m) {
    String h, c;
  	for (int i=0; i<m.length; i++) {
	  if (m[i]==1) h = "h";
	  else if (m[i]==-1) h = "-h";
	  else h = m[i]+"h";
	  c = m[i] == 1 ? h : "("+h+")";
	  PutTextLnLn("     $\\displaystyle t_{"+(i+1)+"}*y_{"+(i+1)+"}=t_{"+(i+1)+"}*\\left(f(x_{0}) + \\frac{"+h+"}{1!}f'(x_{0}) + \\frac{"+c+"^2}{2!}f''(x_{0}) + \\frac{"+c+"^3}{3!}f'''(x_{0}) + \\cdot\\cdot\\cdot\\right)$");
	}
  }

  public void Birlestir(FTex fTex, int[] m) {
  	String left = "";
	String[] right = {"", "", "", ""};
	for (int i=0; i<m.length; i++) {
	  left += "+t_{"+(i+1)+"}*y_{"+(i+1)+"}";
	  right[0] += "+t_{"+(i+1)+"}";

	  if (m[i]==1) right[1] += "+t_{"+(i+1)+"}";
	  else if (m[i]==-1) right[1] += "-t_{"+(i+1)+"}";
	  else if (m[i]<-1) right[1] += m[i]+"t_{"+(i+1)+"}";
	  else right[1] += "+"+m[i]+"t_{"+(i+1)+"}";

	  if (m[i]==1 || m[i]==-1) right[2] += "+t_{"+(i+1)+"}";
	  else right[2] += "+"+(m[i]*m[i])+"t_{"+(i+1)+"}";

	  if (m[i]==1) right[3] += "+t_{"+(i+1)+"}";
	  else if (m[i]==-1) right[3] += "-t_{"+(i+1)+"}";
	  else if (m[i]<-1) right[3] += (m[i]*m[i]*m[i])+"t_{"+(i+1)+"}";
	  else right[3] += "+"+(m[i]*m[i]*m[i])+"t_{"+(i+1)+"}";
	  //left = left.substring(1);
	  if (right[0].charAt(0)=='+')
	    right[0] = right[0].substring(1);
	  if (right[1].charAt(0)=='+')
	    right[1] = right[1].substring(1);
	  if (right[2].charAt(0)=='+')
	    right[2] = right[2].substring(1);
	  if (right[3].charAt(0)=='+')
	    right[3] = right[3].substring(1);

	  if (i==4) {
	    left += "+\\cdot\\cdot\\cdot";
	    right[0] += "+\\cdot\\cdot\\cdot";
	    right[1] += "+\\cdot\\cdot\\cdot";
	    right[2] += "+\\cdot\\cdot\\cdot";
	    right[3] += "+\\cdot\\cdot\\cdot";
	    break;
	  }
	}
	PutTextLnLn("     $\\displaystyle "+left.substring(1)+"=\\left("+right[0]+"\\right)f(x_{0})+ \\left("+right[1]+"\\right)\\frac{h}{1!}f'(x_{0})+ \\left("+right[2]+"\\right)\\frac{h^2}{2!}f''(x_{0})+ \\left("+right[3]+"\\right)\\frac{h^3}{3!}f'''(x_{0}) + \\cdot\\cdot\\cdot$");
  }
}

