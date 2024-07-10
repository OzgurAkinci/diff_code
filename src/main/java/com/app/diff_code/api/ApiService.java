package com.app.diff_code.api;


import com.app.diff_code.dto.RequestDTO;
import com.app.diff_code.dto.ResponseDTO;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;

@Service
public class ApiService {
    public static PrintWriter pw = null;
    public static String latexFile = "PInteg";

    public ResponseDTO run(RequestDTO requestDTO) {
        int d = requestDTO.getD();

        String mTxt = requestDTO.getM();
        String[] strArr = mTxt.split(",");


        int[] m = new int[strArr.length];
        for(int i=0; i<strArr.length; i++) {
            m[i] = Integer.parseInt(strArr[i]);
        }

        Table table = new Table();

        //double h = Math.PI/20;
        //double y0 = Math.PI/5;
        double h = requestDTO.getH().doubleValue();
        double y0 = requestDTO.getY().doubleValue();
        table.put("h", new Ratio(h));
        table.put("y0", new Ratio(Math.sin(y0)));
        for (int i=0; i<m.length; i++) {
            if (m[i]>0)
                table.put("y"+m[i], new Ratio(Math.sin(y0+m[i]*h)));
            else
                table.put("y_"+(-1*m[i]), new Ratio(Math.sin(y0+m[i]*h)));
        }

        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder latexStringBuilder = new StringBuilder();

        return runMethod(stringBuilder, latexStringBuilder, table, m, d, requestDTO.isToText(), requestDTO.isToPdf());
    }

    public ResponseDTO runMethod(StringBuilder stringBuilder, StringBuilder latexStringBuilder, Table table, int[] m, int d, boolean toText, boolean toPdf) {
        Tex_Create();
        var resp = findDiff(stringBuilder, latexStringBuilder, table, m, d, toText, toPdf);
        PutText(resp.getLatexFormat().toString());
        Tex_Close();

        if (toPdf) {
            resp = Tex_pdf(resp);
        }

        return resp;
    }

    public ResponseDTO findDiff(StringBuilder stringBuilder, StringBuilder latexStringBuilder, Table table, int[] m, int d, boolean toText, boolean toPdf) {
        FDiff fd = new FDiff(m);

        Tex_Create();

        if (toText)
            stringBuilder.append("Sayisal Turev icin ileri Yon Sonlu Fark Formulleri").append("\n");
        if (toPdf)
            latexStringBuilder.append("\\section{Sayisal Turev icin Ileri Yon Sonlu Fark Formulleri}").append("\n");

        //System.out.println("---1---");
        if (toText) {
            stringBuilder.append("Taylor Serisi").append("\n");
            stringBuilder.append("Bir f(x) fonksiyonunun x=x0 noktasindaki Taylor serisine acilimi asagidaki ifade ile hesaplanir.").append("\n");
            stringBuilder.append("f(x) = f(x0) + f'(x0)/1!*(x-x0) + f''(x0)/2!*(x-x0)^2 + f'''(x0)/3!*(x-x0)^3 + ...").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("\\subsection{Taylor Serisi}").append("\n");
            latexStringBuilder.append("Bir $f(x)$ fonksiyonunun $x=x_{0}$ noktasindaki Taylor serisine acilimi asagidaki ifade ile hesaplanir.\\\\").append("\n");
            latexStringBuilder.append("$\\displaystyle f(x)=\\sum_{n=0}^{\\infty}\\frac{f^{(n)}(x_{0})}{n!}(x-x_{0})^n = f(x_{0}) + \\frac{f'(x_{0})}{1!}(x-x_{0}) + \\frac{f''(x_{0})}{2!}(x-x_{0})^2 + \\frac{f'''(x_{0})}{3!}(x-x_{0})^3 + \\cdot\\cdot\\cdot$").append("\n");
        }

        //System.out.println("---2---");
        //fTex.NoktasalAcilim(fTex, m);
        if (toText) {
            stringBuilder.append("Ornek Noktalar").append("\n");
            stringBuilder.append("Bu ifade xm=x0+mh biciminde secilen her bir nokta icin duzenlenir.").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("\\subsection{Ornek Noktalar}").append("\n");
            latexStringBuilder.append("Bu ifade $x_{m}=x_{0}+mh$ biciminde secilen her bir nokta icin duzenlenir.\\\\").append("\n");
        }
        Texp[] e = fd.series();
        for (int i = 0; i < e.length; i++) {
            if (toText)
                stringBuilder.append(e[i]).append("\n");
            if (toPdf)
                latexStringBuilder.append("$\\displaystyle ").append(e[i].toLatex(false)).append("$\\\\").append("\n");
        }

        //System.out.println("---3---");
        //fTex.Carpim(fTex, m);
        if (toText) {
            stringBuilder.append("Katsayilar ile Carpim").append("\n");
            stringBuilder.append("Denklemler t1, t2, t3, ... katsayilari ile carpilir.").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("\\subsection{Katsayilar ile carpim}").append("\n");
            latexStringBuilder.append("Denklemler $t_{1}, t_{2}, t_{3}, ...$ katsayilari ile carpilir.\\\\").append("\n");
        }
        Texp[] e2 = fd.multiply(e);
        for (int i = 0; i < e2.length; i++) {
            if (toText)
                stringBuilder.append(e2[i]).append("\n");
            if (toPdf)
                latexStringBuilder.append("$\\displaystyle ").append(e2[i].toLatex(false)).append("$\\\\").append("\n");
        }

        //System.out.println("---4---");
        //fTex.Birlestir(fTex, m);
        if (toText) {
            stringBuilder.append("Birlestirme").append("\n");
            stringBuilder.append("Denklemlerin her iki yani toplanir.").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("\\subsection{Birlestirme}").append("\n");
            latexStringBuilder.append("Denklemlerin her iki yani toplanir.\\\\").append("\n");
        }
        Texp e3 = fd.add(e2);
        if (toText)
            stringBuilder.append(e3).append("\n");
        if (toPdf)
            latexStringBuilder.append("$\\displaystyle ").append(e3.toLatex(false)).append("$").append("\n");

        //System.out.println("---5---");
        if (toText) {
            stringBuilder.append("Katsayi Ifadeleri}").append("\n");
            stringBuilder.append("Bu toplam ifadesinden her bir terimin katsayi ifadesi cikarilir.").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("\\subsection{Katsayi ifadeleri}").append("\n");
            latexStringBuilder.append("Bu toplam ifadesinden her bir terimin katsayi ifadesi cikarilir.\\\\").append("\n");
        }
        Exp[] e4 = fd.equations(e3, fd.getM().length);
        for (int i = 0; i < e4.length; i++) {
            if (toText)
                stringBuilder.append("Exp[").append(i).append("]=").append(e4[i].getE1()).append("\n");
            if (toPdf)
                latexStringBuilder.append("$\\displaystyle Exp_{").append(i).append("}: ").append(e4[i].getE1().toLatex(false)).append("$\\\\").append("\n");
        }

        if (toText) {
            stringBuilder.append("Denklem Sistemi").append("\n");
            stringBuilder.append("Bu katsayi ifadelerinden ilki ile ").append(d).append(". turevin ifadesi cikarilarak asagidaki denklemler elde edilir.").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("\\subsection{Denklem Sistemi}").append("\n");
            latexStringBuilder.append("Bu katsayi ifadelerinden ilki ile ").append(d).append(". turevin ifadesi cikarilarak asagidaki denklemler elde edilir.\\\\").append("\n");
        }
        for (int i = 0; i < e4.length; i++) {
            if (i != 0 && i != d) {
                if (toText)
                    stringBuilder.append(e4[i].getE1()).append("=0").append("\n");
                if (toPdf)
                    latexStringBuilder.append("$\\displaystyle ").append(e4[i].getE1().toLatex(false)).append("=0$\\\\").append("\n");
            }
        }

        //System.out.println("---6---");
        //Exp[][] e5 = fd.coeffMatrix(e4);  //(1)
        if (toText)
            stringBuilder.append("Bu denklemlerden katsayi matrisi olusturulur.").append("\n");
        if (toPdf)
            latexStringBuilder.append("Bu denklemlerden katsayi matrisi olusturulur.").append("\n");
        Exp[][] e5 = fd.coeffMatrix(e4, d); //(2)
        if (toText)
            stringBuilder.append(fd.showMatrix(e5)).append("\n");
        if (toPdf)
            latexStringBuilder = PutMatrix(latexStringBuilder, e5, d, false, "");

        //System.out.println("---7---");
        if (toText)
            stringBuilder.append("Denklemler, t").append(e5.length - 1).append(" bagimsiz degisken yapilarak (sag tarafa tasinarak) asagidaki gibi yeniden duzenlenir.").append("\n");
        if (toPdf)
            latexStringBuilder.append("Denklemler, $t_{").append(e5.length - 1).append("}$ bagimsiz degisken yapilarak (sag tarafa tasinarak) asagidaki gibi yeniden duzenlenir.\\\\").append("\n");
        Exp[][] e6 = fd.diffMatrix(e5);      //(3)
        //Exp[][] e6 = fd.diffMatrix(e5, d); //(4)
        if (toText)
            stringBuilder.append(fd.showMatrix(e6)).append("\n");
        if (toPdf)
            latexStringBuilder = PutMatrix(latexStringBuilder, e6, d, false, "|");

        //System.out.println("---8---");
        //Asagiya dogru indirge
        if (toText)
            stringBuilder.append("Denklem Cozumu").append("\n");
        if (toPdf)
            latexStringBuilder.append("\\subsection{Denklem cozumu}").append("\n");
        for (int i = 0; i < e6.length - 1; i++) {
            e6 = fd.exchangeMatrix(e6, i);
            if (fd.exchange > 0) {
                if (toText) {
                    stringBuilder.append((i + 1)).append(". satir ").append(fd.exchange + 1).append(". satir ile yer degistirilir.").append("\n");
                    stringBuilder.append(fd.showMatrix(e6)).append("\n");
                    stringBuilder.append("------").append("\n");
                }
                if (toPdf) {
                    latexStringBuilder.append((i + 1)).append(". satir ").append(fd.exchange + 1).append(". satir ile yer degistirilir.");
                    latexStringBuilder = PutMatrix(latexStringBuilder, e6, d, false, "|");
                }
                fd.exchange = 0;
            }
            if (((Ratio) e6[i][i]).getR1().compareTo(BigDecimal.ZERO) == 0)
                continue;
            for (int j = i + 1; j < e6.length; j++) {
                if (((Ratio) e6[j][i]).getR1().compareTo(BigDecimal.ZERO) == 0)
                    continue;
                e6 = fd.gaussMatrix(e6, i, j);
                if (toText) {
                    stringBuilder.append((i + 1)).append(". satir kullanilarak asagisindaki (").append(j + 1).append(",").append(i + 1).append(") elemani 0 yapilir.").append("\n");
                    stringBuilder.append(fd.showMatrix(e6)).append("\n");
                    stringBuilder.append("------").append("\n");
                }
                if (toPdf) {
                    latexStringBuilder.append((i + 1)).append(". satir kullanilarak  asagisindaki (").append(j + 1).append(",").append(i + 1).append(") elemani 0 yapilir.");
                    latexStringBuilder = PutMatrix(latexStringBuilder, e6, d, false, "|");
                }
            }
        }

        //Yukariya dogru indirge
        for (int i = e6.length - 1; i > 0; i--) {
            if (((Ratio) e6[i][i]).getR1().compareTo(BigDecimal.ZERO) == 0)
                continue;
            for (int j = i - 1; j >= 0; j--) {
                if (((Ratio) e6[j][i]).getR1().compareTo(BigDecimal.ZERO) == 0)
                    continue;
                e6 = fd.gaussMatrix(e6, i, j);
                if (toText) {
                    stringBuilder.append((i + 1)).append(". satir kullanilarak  yukarisindaki (").append(j + 1).append(",").append(i + 1).append(") elemani 0 yapilir.").append("\n");
                    stringBuilder.append(fd.showMatrix(e6)).append("\n");
                    stringBuilder.append("------").append("\n");
                }
                if (toPdf) {
                    latexStringBuilder.append((i + 1)).append(". satir kullanilarak  yukarisindaki (").append(j + 1).append(",").append(i + 1).append(") elemani 0 yapilir.");
                    latexStringBuilder = PutMatrix(latexStringBuilder, e6, d, false, "|");
                }
            }
        }

        //Birim matrise donustur
        e6 = fd.identityMatrix(e6);
        if (fd.exchange > 0) {
            if (toText)
                stringBuilder.append("y[").append(d).append("]: Birim matrise donusum yapilamadi.").append("\n");
            if (toPdf)
                latexStringBuilder.append("$y^{(").append(d).append(")}:$ Birim matrise donusum yapilamadi.\\\\").append("\n");
            return new ResponseDTO(stringBuilder, latexStringBuilder, null, null);
        } else {
            if (toText) {
                stringBuilder.append("Birim matrise donusum yapilir.").append("\n");
                stringBuilder.append(fd.showMatrix(e6)).append("\n");
                stringBuilder.append("------").append("\n");
            }
            if (toPdf) {
                latexStringBuilder.append("Katsayi matrisi birim matrise donusturulur.").append("\n");
                latexStringBuilder = PutMatrix(latexStringBuilder, e6, d, true, "|");
            }
        }

        //Katsay� ifadeleri
        //System.out.println("---9---");
        latexStringBuilder.append("Buradan katsayi cozumleri asagidaki gibi belirlenir.\\\\").append("\n");
        for (int i = 0; i < e6.length; i++) {
            if (toText)
                stringBuilder.append("t" + (i + 1) + "=" + e6[i][e6.length] + "*t" + (e6.length + 1) + "").append("\n");
            if (toPdf)
                latexStringBuilder.append("$\\displaystyle t_{").append(i + 1).append("}=").append(e6[i][e6.length].toLatex(true)).append("*t_{").append(e6.length + 1).append("}$\\\\").append("\n");
        }

        //System.out.println("---10---");
        //EKOK hesapla
        latexStringBuilder.append("\\subsection{Katsayilar icin Tamsayi Degerleri}").append("\n");
        latexStringBuilder.append("$t_{").append(e6.length + 1).append("}$ degiskenine atanabilecek en kucuk tamsayi degeri EKOK hesabi ile belirlenir.\\\\").append("\n");
        BigInteger ekok = BigInteger.ONE;
        String params = "";
        for (int i = 0; i < e6.length; i++) {
            if (((Ratio) e6[i][e6.length]).getR2().intValue() != 1)
                params += "," + ((Ratio) e6[i][e6.length]).getR2();
            ekok = ((Ratio) e6[i][e6.length]).lcm(ekok);
        }
        if (params.length() == 0)
            params = ",";
        if (toText)
            stringBuilder.append("t").append(e6.length + 1).append("=EKOK(").append(params.substring(1)).append(")=").append(ekok).append("\n");
        if (toPdf)
            latexStringBuilder.append("$t_{").append(e6.length + 1).append("}=EKOK(").append(params.substring(1)).append(")=").append(ekok).append("$\\\\").append("\n");

        //Katsay� degerleri hesapla ve tabloya yaz.
        latexStringBuilder.append("\\\\").append("\n");
        latexStringBuilder.append("Bu deger yardimiyla katsayi degiskenlerine atanacak degerler hesaplanir.\\\\").append("\n");
        table.put("t" + (e6.length + 1), new Ratio(ekok));
        for (int i = 0; i < e6.length; i++) {
            table.put("t" + (i + 1), ((Ratio) e6[i][e6.length]).multiplyR(new Ratio(ekok)).Simplify());
            if (toText)
                stringBuilder.append("t").append(i + 1).append("=").append(new Times(e6[i][e6.length], new Id("t" + (e6.length + 1))).insert(table)).append("=").append(((Ratio) e6[i][e6.length]).multiplyR(new Ratio(ekok)).Simplify()).append("\n");
            if (toPdf)
                latexStringBuilder.append("$\\displaystyle t_{").append(i + 1).append("}=").append(new Times(e6[i][e6.length], new Id("t" + (e6.length + 1))).insert(table)).append("=").append(((Ratio) e6[i][e6.length]).multiplyR(new Ratio(ekok)).Simplify().toLatex(true)).append("$\\\\").append("\n");
        }
        table.put("t0", e4[0].eval(table).Simplify());
        table.put("td", e4[d].eval(table).Simplify());
        if (toText)
            stringBuilder.append("t").append(e6.length + 1).append("=").append(new Ratio(ekok)).append("\n");
        if (toPdf)
            latexStringBuilder.append("$\\displaystyle t_{").append(e6.length + 1).append("}=").append(new Ratio(ekok).toLatex(true)).append("$\\\\").append("\n");

        //System.out.println("---12---");
        //y terimlerinin katsay�lar�
        //e4 : denklem katsayilarinin listesi
        //tablodan oku g�ster
        stringBuilder.append("y0 ile birlikte t�m katsayilar").append("\n");
        latexStringBuilder.append("\\\\").append("\n");
        latexStringBuilder.append("$y_{0}$ noktasinin katsayisi ($t_{0}$) ile birlikte tum katsayilar asagidaki gibi belirlenir.\\\\").append("\n");
        latexStringBuilder.append("$y_{0}: t_{0}=").append(e4[0].getE1().toLatex(false)).append("=").append(e4[0].insert(table).getE1().toLatex(false)).append("=").append(e4[0].eval(table).Simplify().toLatex(false)).append("$\\\\").append("\n");
        for (int i = 0; i <= e6.length; i++) {
            if (toText)
                stringBuilder.append("y").append(i + 1).append(": t").append(i + 1).append("=").append(table.get("t" + (i + 1))).append("\n");
            if (toPdf)
                latexStringBuilder.append("$y_{").append(i + 1).append("}: t_{").append(i + 1).append("}=").append(table.get("t" + (i + 1))).append("$\\\\").append("\n");
        }
        if (toText)
            stringBuilder.append("y[" + d + "]: " + e4[d].eval(table).Simplify() + "*h^" + d).append("\n");
        if (toPdf)
            latexStringBuilder.append("$y^{(" + d + ")}: " + e4[d].eval(table).Simplify() + "*h" + (d == 1 ? "" : "^" + d) + "$\\\\").append("\n");

        //0 ile bolme hatasi varsa
        if (e4[d].eval(table).getR2().compareTo(BigDecimal.ZERO) == 0) {
            if (toText)
                stringBuilder.append("y[").append(d).append("] hesaplanamadi!").append("\n");
            if (toPdf)
                latexStringBuilder.append("$y^{(").append(d).append(")}$ hesaplanamadi!\\\\").append("\n");
            return new ResponseDTO(stringBuilder, latexStringBuilder, null, null);
        }

        //Turev katsayisi negatif ise
        boolean negate = false;
        if (e4[d].eval(table).Simplify().getDecimal().signum() < 0) {
            negate = true;
            for (int i = 1; i <= e6.length + 1; i++) {
                table.put("t" + i, table.get("t" + i).multiplyR(new Ratio(-1)));
            }
            table.put("td", table.get("td").multiplyR(new Ratio(-1)));
        } else {
            table.put("t0", table.get("t0").multiplyR(new Ratio(-1)));
        }

        String hp;
        if (negate) {
            stringBuilder.append("Tum katsayilar (-1) ile carpilir.").append("\n");
            latexStringBuilder.append("\\\\").append("\n");
            latexStringBuilder.append("$y^{(").append(d).append(")}$ teriminin katsayisi negatif oldugundan tum katsayilar (-1) ile carpilir.\\\\").append("\n");
            //System.out.println("---13---");
            //katsayilari g�ster
            Ratio big = new Ratio();
            for (int i = 0; i <= e6.length + 1; i++) {
                if (toText)
                    stringBuilder.append("y").append(i).append(": t").append(i).append("=").append(table.get("t" + i)).append("\n");
                if (toPdf)
                    latexStringBuilder.append("$y_{").append(i).append("}: t_{").append(i).append("}=").append(table.get("t" + i)).append("$\\\\").append("\n");
                big = big.addR(table.get("t" + i));
            }
            hp = d == 1 ? "" : "^{" + d + "}";
            if (toText) {
                stringBuilder.append("y[").append(d).append("]: ").append(table.get("td")).append("*h").append(hp).append("\n");
                stringBuilder.append("t_sum = ").append(big).append(" (katsayilar dogru ise toplami 0 olmalidir)").append("\n");
            }
            if (toPdf) {
                latexStringBuilder.append("$y^{(").append(d).append(")}: ").append(table.get("td")).append("*h").append(hp).append("$\\\\").append("\n");
                latexStringBuilder.append("$t_{sum} = ").append(big).append("$ (katsayilar dogru ise toplami 0 olmalidir)\\\\").append("\n");
            }
        }

        //System.out.println("---14---");
        //T�rev ifadesi
        latexStringBuilder.append("\\\\").append("\n");
        latexStringBuilder.append("$y^{(").append(d).append(")}$ ifadesi hesaplanir.\\\\").append("\n");
        Exp df = null;
        for (int i = 0; i <= e6.length; i++) {
            if (m[i] > 0 && i == 0)
                df = difExp(df, table.get("t0"), "y0");
            df = difExp(df, table.get("t" + (i + 1)), m[i] < 0 ? "y_" + (-1 * m[i]) : "y" + m[i]);
            if (m[i] < 0 && (i == e6.length || m[i + 1] > 0))
                df = difExp(df, table.get("t0"), "y0");
        }
        //df = new Divide(df, new Times(table.get("td"), new Power(new Id("h"),new Num(d))));
        df = new Divide(new Par(df), new Times(table.get("td"), new Power(new Id("h"), new Num(d))));
        hp = e4.length - d == 1 ? "" : "^{" + (e4.length - d) + "}";
        if (toText) {
            stringBuilder.append("y[").append(d).append("]=").append(df).append("+O(h^").append(e4.length - d).append(")").append("\n");
            stringBuilder.append("y[").append(d).append("]=").append(df.insert(table)).append("+O(h^").append(e4.length - d).append(")").append("\n");
            stringBuilder.append("y[").append(d).append("]=").append(df.eval(table).getDecimal()).append("+O(h^").append(e4.length - d).append(")").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("$y^{(").append(d).append(")}=").append(df.toLatex(true)).append("+O(h").append(hp).append(")$\\\\").append("\n");
            latexStringBuilder.append("$y^{(").append(d).append(")}=").append(df.insert(table).toLatex(true)).append("+O(h").append(hp).append(")$\\\\").append("\n");
            latexStringBuilder.append("$y^{(").append(d).append(")}=").append(df.eval(table).getDecimal()).append("+O(h").append(hp).append(")$\\\\").append("\n");
        }

        return new ResponseDTO(stringBuilder, latexStringBuilder, null, null);
    }

    public Exp difExp(Exp df, Ratio dn, String yn) {
        if (dn.getR1().compareTo(BigDecimal.ZERO) == 0)
            return df;
        if (dn.getDecimal().signum() > 0) {
            if (dn.getR1().compareTo(BigDecimal.ONE) == 0)
                return (df == null ? new Id(yn) : new Plus(df, new Id(yn)));
            else
                return (df == null ? new Times(dn, new Id(yn)) : new Plus(df, new Times(dn, new Id(yn))));
        } else {
            if (dn.getR1().abs().compareTo(BigDecimal.ONE) == 0)
                return (df == null ? new Minus(new Ratio(0), new Id(yn)) : new Minus(df, new Id(yn)));
            else
                return (df == null ? new Times(dn, new Id(yn)) : new Minus(df, new Times(dn.abs(), new Id(yn))));
        }
    }

    public StringBuilder PutMatrix(StringBuilder sb, Exp[][] ms, int d, boolean ltx, String or) {
        String header = "";
        for (int i = 0; i < ms[0].length - 1; i++) {
            header += "r";
        }
        header += or + "r";

        sb.append("\\begin{center}").append("\n");
        sb.append("$$ \\left[\\begin{array}{").append(header).append("}").append("\n");

        sb.append("t_{1}");
        for (int i = 1; i < ms[0].length; i++) {
            sb.append(" & t_{").append(i + 1).append("}");
        }
        sb.append("\\\\").append("\n");

        for (int i = 0; i < ms.length; i++) {
            sb.append(ms[i][0].toLatex(false) + "");
            for (int j = 1; j < ms[i].length; j++) {
                sb.append(" & ").append(ms[i][j].toLatex(ltx));
            }
            sb.append("\\\\").append("\n");
        }

        sb.append("\\end{array}\\right] $$").append("\n");
        sb.append("\\end{center}").append("\n");
        return sb;
    }

    public ResponseDTO Tex_pdf(ResponseDTO responseDTO) {
        try {
            ProcessBuilder builder = new ProcessBuilder("pdflateX", latexFile + ".tex");
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            var file = new File(latexFile+".pdf");
            if (file.exists()) {
                //Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+latexFile+".pdf");
                //p.waitFor();
                responseDTO.setPdfFile(Files.readAllBytes(file.toPath()));
                responseDTO.setPdfFilePath(file.getAbsolutePath());
                return responseDTO;
            } else {
                System.out.println("File does not exists: " + latexFile + ".pdf");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void Tex_Create() {
        try {
            File file = new File(latexFile + ".pdf");
            file.delete();
            pw = new PrintWriter(latexFile + ".tex", "UTF-8");
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

    public void Tex_Close() {
        pw.println("\\end{document}");
        pw.close();
    }

    public void PutText(String s) {
        pw.print(s);
    }

    public void PutTextLn(String s) {
        pw.println(s);
    }

    public void PutTextLnLn(String s) {
        pw.println(s + "\n\n");
    }

    ///Kullan�lmayan tan�mlamalar
    public void NoktasalAcilim(FTex fTex, int[] m) {
        String h, c;
        for (int i = 0; i < m.length; i++) {
            if (m[i] == 1) h = "h";
            else if (m[i] == -1) h = "-h";
            else h = m[i] + "h";
            c = m[i] == 1 ? h : "(" + h + ")";
            PutTextLnLn("     $\\displaystyle y_{" + (i + 1) + "}=f(x_{0}+" + h + ") = f(x_{0}) + \\frac{" + h + "}{1!}f'(x_{0}) + \\frac{" + c + "^2}{2!}f''(x_{0}) + \\frac{" + c + "^3}{3!}f'''(x_{0}) + \\cdot\\cdot\\cdot$");
        }
    }

    public void Carpim(FTex fTex, int[] m) {
        String h, c;
        for (int i = 0; i < m.length; i++) {
            if (m[i] == 1) h = "h";
            else if (m[i] == -1) h = "-h";
            else h = m[i] + "h";
            c = m[i] == 1 ? h : "(" + h + ")";
            PutTextLnLn("     $\\displaystyle t_{" + (i + 1) + "}*y_{" + (i + 1) + "}=t_{" + (i + 1) + "}*\\left(f(x_{0}) + \\frac{" + h + "}{1!}f'(x_{0}) + \\frac{" + c + "^2}{2!}f''(x_{0}) + \\frac{" + c + "^3}{3!}f'''(x_{0}) + \\cdot\\cdot\\cdot\\right)$");
        }
    }

    public void Birlestir(FTex fTex, int[] m) {
        String left = "";
        String[] right = {"", "", "", ""};
        for (int i = 0; i < m.length; i++) {
            left += "+t_{" + (i + 1) + "}*y_{" + (i + 1) + "}";
            right[0] += "+t_{" + (i + 1) + "}";

            if (m[i] == 1) right[1] += "+t_{" + (i + 1) + "}";
            else if (m[i] == -1) right[1] += "-t_{" + (i + 1) + "}";
            else if (m[i] < -1) right[1] += m[i] + "t_{" + (i + 1) + "}";
            else right[1] += "+" + m[i] + "t_{" + (i + 1) + "}";

            if (m[i] == 1 || m[i] == -1) right[2] += "+t_{" + (i + 1) + "}";
            else right[2] += "+" + (m[i] * m[i]) + "t_{" + (i + 1) + "}";

            if (m[i] == 1) right[3] += "+t_{" + (i + 1) + "}";
            else if (m[i] == -1) right[3] += "-t_{" + (i + 1) + "}";
            else if (m[i] < -1) right[3] += (m[i] * m[i] * m[i]) + "t_{" + (i + 1) + "}";
            else right[3] += "+" + (m[i] * m[i] * m[i]) + "t_{" + (i + 1) + "}";
            //left = left.substring(1);
            if (right[0].charAt(0) == '+')
                right[0] = right[0].substring(1);
            if (right[1].charAt(0) == '+')
                right[1] = right[1].substring(1);
            if (right[2].charAt(0) == '+')
                right[2] = right[2].substring(1);
            if (right[3].charAt(0) == '+')
                right[3] = right[3].substring(1);

            if (i == 4) {
                left += "+\\cdot\\cdot\\cdot";
                right[0] += "+\\cdot\\cdot\\cdot";
                right[1] += "+\\cdot\\cdot\\cdot";
                right[2] += "+\\cdot\\cdot\\cdot";
                right[3] += "+\\cdot\\cdot\\cdot";
                break;
            }
        }
        PutTextLnLn("     $\\displaystyle " + left.substring(1) + "=\\left(" + right[0] + "\\right)f(x_{0})+ \\left(" + right[1] + "\\right)\\frac{h}{1!}f'(x_{0})+ \\left(" + right[2] + "\\right)\\frac{h^2}{2!}f''(x_{0})+ \\left(" + right[3] + "\\right)\\frac{h^3}{3!}f'''(x_{0}) + \\cdot\\cdot\\cdot$");
    }
}
