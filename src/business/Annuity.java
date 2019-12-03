package business;

import java.text.NumberFormat;
import java.util.Random;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @author ptd
 */
public class Annuity extends Financial {
    public static final String TITLE = "Annuity Schedule";
    public static final String AMOUNTDESC = "Deposit Amount";
    public static final String RESULTDESC = "Final Value";
    public static final String INTFACTORDESC = "Int. Earned";
    public static final String BEGBALDESC = "Beg Annuity Value";
    public static final String ENDBALDESC = "End Annuity Value";
    public static final String PRINFACTORDESC = "Deposit";
    
    private double[] bbal, iearn, ebal;
    private double totalInt;
    private JTable tArea;
    
    public Annuity() {
        super();
        this.tArea = null;
    }
    public Annuity(double d, double r, int t) {
        super(d,r,t);
        this.tArea = null;
        calcAnnuity();
    }
    public Annuity(double d, double r, int t, JTable tbl) {
        super(d,r,t);
        this.tArea = tbl;
        calcAnnuity();
    }
    public String getTitle() {
        return Annuity.TITLE;
    }
    public String getAmountDesc() {
        return Annuity.AMOUNTDESC;
    }
    public double getResult() {
        if (!super.isBuilt()) { calcAnnuity(); }
        return this.ebal[super.getTerm()-1];
    }
    public String getResultDesc() {
        return Annuity.RESULTDESC;
    }
    public double getBegBal(int mo) {
        if (!super.isBuilt()) { calcAnnuity(); }
        if (mo < 1 || mo > super.getTerm()) { return 0; }
        return this.bbal[mo-1];
    }
    public String getBegBalDesc() {
        return Annuity.BEGBALDESC;
    }
    public double getIntFactor(int mo) {
        if (!super.isBuilt()) { calcAnnuity(); }
        if (mo < 1 || mo > super.getTerm()) { return 0; }
        return this.iearn[mo-1];
    }
    public String getIntFactorDesc() {
        return Annuity.INTFACTORDESC;
    }
    public double getPrinFactor(int mo) {
        return super.getAmt();
    }
    public String getPrinFactorDesc() {
        return Annuity.PRINFACTORDESC;
    }
    public double getEndBal(int mo) {
        if (!super.isBuilt()) { calcAnnuity(); }
        if (mo < 1 || mo > super.getTerm()) { return 0; }
        return this.ebal[mo-1];
    }
    public String getEndBalDesc() {
        return Annuity.ENDBALDESC;
    }
    private void calcAnnuity() {
        this.bbal = new double[super.getTerm()];
        this.iearn = new double[super.getTerm()];
        this.ebal = new double[super.getTerm()];
        
        bbal[0] = 0;
        for (int i=0; i < super.getTerm(); i++) {
            if (i > 0) {
                this.bbal[i] = this.ebal[i-1];
            }
            this.iearn[i] = 
                    (this.bbal[i] + super.getAmt() )* (super.getRate()/12.0);
            this.ebal[i] = 
                    this.bbal[i] + this.iearn[i] + super.getAmt();
            //this.fv = this.fv + intearned + this.deposit;
        }
        super.setBuilt(true);
    }
    public double getTotIntEarn() {
        if (!super.isBuilt()) { calcAnnuity(); }
        
        this.totalInt = 0;
        for (int i=0; i<iearn.length; i++) {
            this.totalInt+=this.iearn[i];
        }
        return this.totalInt;
    }
    
    @Override
    public void run() {
        NumberFormat curr = NumberFormat.getCurrencyInstance();
        NumberFormat pct = NumberFormat.getPercentInstance();
        pct.setMaximumFractionDigits(3);
        pct.setMinimumFractionDigits(3);
        
        String title = "Thread " +
                Thread.currentThread().getId() + " Annuity: " +
                curr.format(super.getAmt()) + ", " +
                pct.format(getRate()) + ", " +
                super.getTerm() + " months = total interest earned: " +
                curr.format(getTotIntEarn());
        
        if (this.tArea != null) { //ModelDG
            synchronized(tArea) {
                String rate = pct.format(super.getRate());
                for (int i=0; i < tArea.getRowCount(); i++) {
                    if (((String)tArea.getValueAt(i,0)).equalsIgnoreCase(rate)) {
                        tArea.setValueAt(title, i, 1);
                        break;
                    }
                }
            }
        } else { //Model
            JTable sched;
            DefaultTableModel mod;

            String[] cols = { "Month", "Beg.Bal", "Deposit",
                              "Int.Rate", "Int.Earn.", "End.Bal." };
            String[][] t = new String[super.getTerm()][6];
            mod = new DefaultTableModel(t,cols);
            sched = new JTable(mod);
            for (int i=0; i<super.getTerm(); i++) {
                sched.setValueAt((i+1), i, 0);
                sched.setValueAt(curr.format(this.bbal[i]), i, 1);
                sched.setValueAt(curr.format(super.getAmt()), i, 2);
                sched.setValueAt(pct.format(super.getRate()), i, 3);
                sched.setValueAt(curr.format(this.iearn[i]), i, 4);
                sched.setValueAt(curr.format(this.ebal[i]), i, 5);
            }
            JScrollPane sp = new JScrollPane(sched);
            JDialog dg = new JDialog();
            dg.add(sp);
            dg.setTitle(title);
            int vertoffset = (int) (new Random().nextInt(100));
            dg.setBounds(150,300+vertoffset,600,200);
            dg.setVisible(true);
        }
    }
}
