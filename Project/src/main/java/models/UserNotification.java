package models;

//A class the emulates the user_notification table in the database
public class UserNotification extends DatabaseModel {
    private static final String tableName = "user_notification";
	//User ID
    private Integer userId;
	//Property ID
    private Property propertyId;
	
	//Get the user's ID
    public Integer getUserId() {
        return userId;
    }
	
	//Set the user's ID
    public void setUserId(final Integer userId) {
        this.userId = userId;
    }

	//Get the Property
    public Property getPropertyId() {
        return propertyId;
    }

	//Set the Property
    public void setPropertyId(final Property propertyId) {
        this.propertyId = propertyId;
    }
}
