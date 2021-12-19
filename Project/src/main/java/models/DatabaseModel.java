package models;

//A generic model to hold the name of a table and its id
public abstract class DatabaseModel {
	//Table name
    private static String tableName;
	//Table ID
    private Integer id;
	
	//Get table name
    public static String getTableName() {
        return tableName;
    }
	
	//Get table ID
    public Integer getId() {
        return id;
    }
	
	//Set table ID
    public void setId(final Integer id) {
        this.id = id;
    }

}
