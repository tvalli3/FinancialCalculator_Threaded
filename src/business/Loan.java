
package business;

import java.text.NumberFormat;
import java.util.Random;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author ptd
 */
public class Loan extends Financial {
    public static final String TITLE = "Loan Schedule";
    public static final String AMOUNTDESC = "Loan Amount";
    public static final String RESULTDESC = "Monthly Payment";
    public static final String INTFACTORDESC = "Int. Charge";
    public static final String BEGBALDESC = "Beg Loan Bal.";
    public static final String ENDBALDESC = "End Loan Bal.";
    public static final String PRINFACTORDESC = "Payment";
    
    private double mopmt;
    private double[] bbal, ichg, ebal;
    private JTable tArea;
    
    public Loan() {
        super();
        this.tArea = null;
    }
    public Loan(double p, double r, int t) {
        super(p,r,t);
        mopmt = 0;
        this.tArea = null;
        buildLoan();
    }
    public Loan(double p, double r, int t, JTable tArea) {
        super(p,r,t);
        mopmt = 0;
        this.tArea = tArea;
        buildLoan();
    }    
    public String getTitle() {
        return Loan.TITLE;
    }
    public double getResult() {
        if (!super.isBuilt()) { buildLoan(); }
        return mopmt;
    }
    public String getResultDesc() {
        return Loan.RESULTDESC;
    }
    public double getBegBal(int mo) {
        if (!super.isBuilt()) { buildLoan(); }
        if (mo < 1 || mo > super.getTerm()) { return 0; }
        return this.bbal[mo-1];
    }
    public String getBegBalDesc() {
        return Loan.BEGBALDESC;
    }
    public double getIntFactor(int mo) {
        if (!super.isBuilt()) { buildLoan(); }
        if (mo < 1 || mo > super.getTerm()) { return 0; }
        return this.ichg[mo-1];
    }
    public String getIntFactorDesc() {
        return Loan.INTFACTORDESC;
    }
    public double getEndBal(int mo) {
        if (!super.isBuilt()) { buildLoan(); }
        if (mo < 1 || mo > super.getTerm()) { return 0; }
        return this.ebal[mo-1];
    }
    public String getEndBalDesc() {
        return Loan.ENDBALDESC;
    }
    public double getPrinFactor(int mo) {
        return this.mopmt;
    }
    public String getPrinFactorDesc() {
        return Loan.PRINFACTORDESC;
    }
    public String getAmountDesc() {
        return Loan.AMOUNTDESC;
    }
    public double getPrinPaid(int mo) {
        if (!super.isBuilt()) { buildLoan(); }
        if (mo < 1 || mo > super.getTerm()) { return 0; }
        return this.bbal[mo-1] - this.ebal[mo-1];
    }
    private void buildLoan() {
        //calculate Monthly Payment....
        double morate = super.getRate() / 12.0;
        double denom = Math.pow((1+morate),super.getTerm()) - 1;
        this.mopmt = (morate + morate/denom) * super.getAmt();
        
        this.bbal = new double[super.getTerm()];
        this.ichg = new double[super.getTerm()];
        this.ebal = new double[super.getTerm()];
        
        this.bbal[0] = super.getAmt();
        for(int i=0; i< super.getTerm(); i++) {
            if (i > 0) {
                this.bbal[i] = this.ebal[i-1];
            }
            this.ichg[i] = this.bbal[i] * morate;
            this.ebal[i] = 
                    this.bbal[i] + this.ichg[i] - this.mopmt;
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
                Thread.currentThread().getId() + " loan " +
                curr.format(super.getAmt()) + ", " +
                pct.format(getRate()) + ", " +
                super.getTerm() + " months = pmt of: " +
                curr.format(this.mopmt);
        
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

            String[] cols = { "Month", "Beg.Bal", "Payment",
                              "Int.Rate", "End.Bal", "Prin.Paid" };
            String[][] t = new String[super.getTerm()][6];
            mod = new DefaultTableModel(t,cols);
            sched = new JTable(mod);
            for (int i=0; i<super.getTerm(); i++) {
                sched.setValueAt((i+1), i, 0);
                sched.setValueAt(curr.format(this.bbal[i]), i, 1);
                sched.setValueAt(curr.format(this.mopmt), i, 2);
                sched.setValueAt(pct.format(super.getRate()), i, 3);
                sched.setValueAt(curr.format(this.ebal[i]), i, 4);
                sched.setValueAt(curr.format(getPrinPaid(i+1)), i, 5);
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