
package business;

/**
 *
 * @author pdaniel
 */
public abstract class Financial implements Runnable {
    private double amt, rate;
    private int term;
    private boolean built;
    
    public Financial() {
        this(0,0,0);
        built = false;
    }
    public Financial(double a, double r, int t) {
        this.amt = a;
        this.rate = r;
        this.term = t;
        built = false;
    }

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    protected boolean isBuilt() {
        return built;
    }

    protected void setBuilt(boolean built) {
        this.built = built;
    }
    public abstract String getTitle();
    public abstract String getAmountDesc();
    public abstract String getResultDesc();
    public abstract double getResult();
    
    public abstract double getBegBal(int mo);
    public abstract String getBegBalDesc();
    
    public abstract double getPrinFactor(int mo);
    public abstract String getPrinFactorDesc();
    
    public abstract double getIntFactor(int mo);
    public abstract String getIntFactorDesc();
    
    public abstract double getEndBal(int mo);
    public abstract String getEndBalDesc();
    
    @Override
    public abstract void run();
}
