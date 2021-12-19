package models;

//Manager Configuration class that emulates the manager_configuration table in the database
public class ManagerConfiguration extends DatabaseModel {
    private static final String tableName = "manager_configuration";
	//Period given after payment
    private Integer periodDays;
	//Fee amount
    private Double fees;
	
	//Get fee amount
    public Double getFees() {
        return fees;
    }
	
	//Set fee amount
    public void setFees(final Double fees) {
        this.fees = fees;
    }

	//Get period
    public Integer getPeriodDays() {
        return periodDays;
    }
	
	//Set period
    public void setPeriodDays(final Integer periodDays) {
        this.periodDays = periodDays;
    }
}
