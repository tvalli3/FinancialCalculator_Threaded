package business;

import java.text.NumberFormat;
import java.util.Random;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @author pdaniel
 */
public class PV  extends Financial {
    public static final String TITLE="Present Value Schedule";
    public static final String AMOUNTDESC = "Lump Sum Amount";
    public static final String RESULTDESC = "Present Value";
    public static final String INTFACTORDESC = "Discount";
    public static final String BEGBALDESC = "Present Value";
    public static final String ENDBALDESC = "Present Value";
    public static final String PRINFACTORDESC = "Value Change";
    
    private JTable tArea;
    private double[] bbal, ifactor;
    
    public PV() {
        super();
        this.tArea = null;
    }
    public PV(double a, double r, int t) {
        super(a,r,t);
        this.tArea = null;
        calcPV();
    }
    public PV(double a, double r, int t, JTable tbl) {
        super(a,r,t);
        this.tArea = tbl;
        calcPV();
    }
    public String getTitle() {
        return PV.TITLE;
    }
    public double getResult() {
        if (!super.isBuilt()) {
            calcPV();            
        }
        return this.bbal[0];
    }
    public String getAmountDesc() {
        return PV.AMOUNTDESC;
    }
    public String getResultDesc() {
        return PV.RESULTDESC;
    }
    public double getBegBal(int mo) {
        if (!super.isBuilt()) { calcPV(); }
        if (mo < 0 || mo > super.getTerm()) { return 0; }
        return this.bbal[mo];
    }
    public String getBegBalDesc() {
        return PV.BEGBALDESC;
    }
    public double getIntFactor(int mo) {
        if (!super.isBuilt()) { calcPV(); }
        if (mo < 0 || mo > super.getTerm()) { return 0; }
        return this.ifactor[mo];
    }
    public String getIntFactorDesc() {
        return PV.INTFACTORDESC;
    }
    public double getPrinFactor(int mo) {
        if (mo < 0 || mo > (super.getTerm()+1)) { return 0; }
        return (this.bbal[mo+1] - this.bbal[mo]);
    }
    public String getPrinFactorDesc() {
        return PV.PRINFACTORDESC;
    }
    public double getEndBal(int mo) {
        if (!super.isBuilt()) { calcPV(); }
        if (mo < 1 || mo > super.getTerm()) { return 0; }
        return this.bbal[mo-1];
    }
    public String getEndBalDesc() {
        return PV.ENDBALDESC;
    }
    private void calcPV() {
        //internal logic for building an annuity...
        //this.fv = 0;
        //double intearned=0;
        this.ifactor = new double[super.getTerm()+1];
        this.bbal = new double[super.getTerm()+1];
        
        for (int i=0; i<super.getTerm()+1; i++) {
            this.bbal[i] = super.getAmt() / 
                   Math.pow((1+super.getRate()/12.0),(super.getTerm() - i));
            this.ifactor[i] = (super.getAmt() - this.bbal[i]);           
        }
        super.setBuilt(true);
    }
    
    @Override
    public void run() {
        NumberFormat curr = NumberFormat.getCurrencyInstance();
        NumberFormat pct = NumberFormat.getPercentInstance();
        pct.setMaximumFractionDigits(3);
        pct.setMinimumFractionDigits(3);
        
        String title = "Thread " +
                Thread.currentThread().getId() + " Present Value: " +
                curr.format(super.getAmt()) + ", " +
                pct.format(getRate()) + ", " +
                super.getTerm() + " months = present value now of: " +
                curr.format(this.bbal[0]);
        
        if (this.tArea != null) {
            synchronized(tArea) {
                String rate = pct.format(super.getRate());
                for (int i=0; i < tArea.getRowCount(); i++) {
                    if (((String)tArea.getValueAt(i,0)).equalsIgnoreCase(rate)) {
                        tArea.setValueAt(title, i, 1);
                        break;
                    }
                }
            }
        } else {
            JTable sched;
            DefaultTableModel mod;

            String[] cols = { "Month", "Discount", "Present Value" };
            String[][] t = new String[super.getTerm()+1][3];
            mod = new DefaultTableModel(t,cols);
            sched = new JTable(mod);
            for (int i=0; i<=super.getTerm(); i++) {
                sched.setValueAt((i), i, 0);
                sched.setValueAt(curr.format(getIntFactor(i)), i, 1);
                sched.setValueAt(curr.format(getBegBal(i)), i, 2);
            }
            JScrollPane sp = new JScrollPane(sched);
            JDialog dg = new JDialog();
            dg.add(sp);
            dg.setTitle(title);
            int vertoffset = (int) (new Random().nextInt(100));
            dg.setBounds(150,300+vertoffset,650,200);
            dg.setVisible(true);
        }
    }
}
