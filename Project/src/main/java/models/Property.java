package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Collection;
import javax.swing.table.*;
import java.sql.Timestamp;

//Class property that emulates the property table in the database
public class Property extends DatabaseModel {
	//The ID of the property
	private int ID;
	//The address of the property
    private String address;
	//The property type of the property
    private Type propertyType;
	//The number of bedrooms of the property
    private int numberOfBedrooms;
	//The number of bathrooms of the property
    private int numberOfBathrooms;
	//The furnish status of the property
    private boolean isFurnished;
	//The city quadrant of the property
    private CityQuadrant cityQuadrant;
	//The landlord of the property
    private Landlord landlord;
	//The landlord's email
    private String landlordEmail;
	//The fee status of the property
    private boolean isFeePaid;
	//The property static of the property
    private Status propertyStatus;
	//The date the property was published
    private Timestamp datePublished;
	//The date the property was paid for
    private Timestamp paymentDate;
	
	//Set the qualities of this property
    public Property(final String address, final Type propertyType, final int numberOfBedrooms,
                    final int numberOfBathrooms, final boolean isFurnished,
                    final CityQuadrant cityQuadrant, final String landlordEmail,
                    final boolean isFeePaid, final Status propertyStatus,
                    final Timestamp datePublished, final Timestamp paymentDate) {
        this.address = address;
        this.propertyType = propertyType;
        this.numberOfBedrooms = numberOfBedrooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.isFurnished = isFurnished;
        this.cityQuadrant = cityQuadrant;
        this.landlordEmail = landlordEmail;
        this.isFeePaid = isFeePaid;
        this.propertyStatus = propertyStatus;
        this.datePublished = datePublished;
        this.paymentDate = paymentDate;
    }
	
	//Get a property from a result set (used with the result set returned from controllers)
    public static Optional<Property> createFromResultSet(final ResultSet rs) throws SQLException {
		//Get the ID of the property
        final Integer id = rs.getInt("id");
		//Get the address of the property
        final String address = rs.getString("address");
		//Get the type of property
        final Type propertyType = Type.valueOf(rs.getString("property_type"));
		//Get the number of bedrooms of the property
        final int numberOfBedrooms = rs.getInt("number_of_bedrooms");
		//Get the number of bathrooms of the property
        final int numberOfBathrooms = rs.getInt("number_of_bathrooms");
		//Get the furnish status of the property
        final boolean isFurnished = rs.getBoolean("is_furnished");
		//Get the city quadrant of the property
        final CityQuadrant quadrant = CityQuadrant.valueOf(rs.getString("city_quadrant"));
		//Get the fee status of the property
        final boolean isFeePaid = rs.getBoolean("is_fee_paid");
		//Get the landlord's email for the property
        final String landlordEmail = rs.getString("landlord");
		//Get the property status of the property
        final Status status = Status.valueOf(rs.getString("property_status"));
		//Get the date published of the property
        final Timestamp datePublished = rs.getTimestamp("date_published");
		//Get the payment date of the property
        final Timestamp paymentDate = rs.getTimestamp("payment_date");
		
		//Create a new property with the above features
        final Property property = new Property(address, propertyType, numberOfBedrooms, numberOfBathrooms, isFurnished, quadrant, landlordEmail, isFeePaid, status, datePublished, paymentDate);
		//Set the id of the property separately
		property.setID(id);
		//Return the property in a container
        return Optional.of(property);
    }
	
	//Get the payment date of the property
    public Timestamp getPaymentDate() {
        return paymentDate;
    }
	
	//Set the payment date of the property
    public void setPaymentDate(final Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }
	
	//Get the email of the landlord of the property
    public String getLandlordEmail() {
        return landlordEmail;
    }

	//Set the email of the landlord of the property
    public void setLandlordEnail(final String landlordEmail) {
        this.landlordEmail = landlordEmail;
    }
	
	//Get the ID of the property
	public int getID() {
        return ID;
    }

	//Set the ID of the property
    public void setID(final int ID) {
        this.ID = ID;
    }
	
	//Get the furnish status of the property
    public boolean isFurnished() {
        return isFurnished;
    }

	//Set the furnish status of the property
    public void setFurnished(final boolean furnished) {
        isFurnished = furnished;
    }
	
	//Get the date published of the property
    public Timestamp getDatePublished() {
        return datePublished;
    }
	
	//Set the date published of the property
    public void setDatePublished(final Timestamp datePublished) {
        this.datePublished = datePublished;
    }

	//Get the address of the property
    public String getAddress() {
        return address;
    }

	//Set the address of the property
    public void setAddress(final String address) {
        this.address = address;
    }

	//Get the type of property
    public Type getPropertyType() {
        return propertyType;
    }

	//Set the type of property
    public void setPropertyType(final Type propertyType) {
        this.propertyType = propertyType;
    }

	//Get the number of bedrooms of the property
    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }
	
	//Set the number of bedrooms of the property
    public void setNumberOfBedrooms(final int numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

	//Get the number of bathrooms of the property
    public int getNumberOfBathrooms() {
        return numberOfBathrooms;
    }
	
	//Set the number of bathrooms of the property
    public void setNumberOfBathrooms(final int numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

	//Get the furnish status of the property
    public boolean getIsFurnished() {
        return isFurnished;
    }
	
	//Set the furnish status of the property
    public void setIsFurnished(final boolean isFurnished) {
        this.isFurnished = isFurnished;
    }
	
	//Get the city quadrant of the property
    public CityQuadrant getCityQuadrant() {
        return cityQuadrant;
    }
	
	//Set the city quadrant of the property
    public void setCityQuadrant(final CityQuadrant cityQuadrant) {
        this.cityQuadrant = cityQuadrant;
    }
	
	//Get the landlord of the property
    public Landlord getLandlord() {
        return landlord;
    }

	//Set the landlord of the property
    public void setLandlord(final Landlord landlord) {
        this.landlord = landlord;
    }
	
	//Get the fee status of the property
    public boolean isFeePaid() {
        return isFeePaid;
    }

	//Set the fee status of the property
    public void setFeePaid(final boolean feePaid) {
        this.isFeePaid = feePaid;
    }

	//Get the status of the property
    public Status getPropertyStatus() {
        return propertyStatus;
    }
	
	//Set the status of the property
    public void setPropertyStatus(final Status propertyStatus) {
        this.propertyStatus = propertyStatus;
    }
	
	//Make a table out of a collection of properties
	public static DefaultTableModel getTable(Collection<Property> properties){
		//Create the column names
		Object[] head = new String[]{"House ID", "Address", "House Type", "Bedrooms", "Bathrooms", "Is Furnished", "City Quadrant", 
			"Fee Status", "Property Status", "Date Published", "Payment Date"};
		
		//Create the table to be populated
		DefaultTableModel tableModel = new DefaultTableModel(head, 0);
		//Add the column names as a row
		tableModel.addRow(head);
		
		//For each property in the collection...
		for (final Property property : properties) {
			String furnish = "";
			String feeStatus = "";
			String payDate = "";
			String pubDate = "";
			
			//If the boolean furnish status is true, set the string as true
			if(property.getIsFurnished() == true){
				furnish = "True";
			}
			//Otherwise, set it as false
			else{
				furnish = "False";
			}
			
			//If the boolean fee status is true, set the string as true
			if(property.isFeePaid() == true){
				feeStatus = "True";
			}
			//Otherwise, set it as false
			else{
				feeStatus = "False";
			}
			
			//If the date published is null, set the string as null
			if(property.getDatePublished() == null){
				payDate = "Null";
			}
			//Otherwise, convert the date and time to a string 
			else{
				payDate = property.getDatePublished().toString();
			}
			
			//If the payment date is null, set the string as null
			if(property.getPaymentDate() == null){
				pubDate = "Null";
			}
			//Otherwise, convert the date and time to a string
			else{
				pubDate = property.getPaymentDate().toString();
			}
			
			//Convert all qualities of a property into strings and place them in an array of Objects
			Object[] data = {Integer.toString(property.getID()), property.getAddress(), property.getPropertyType().toString(), Integer.toString(property.getNumberOfBedrooms()), 
				Integer.toString(property.getNumberOfBathrooms()), furnish, property.getCityQuadrant().toString(), feeStatus, property.getPropertyStatus().toString(), 
					payDate, pubDate};
			
			//Add the array of Objects to the table as a reow
			tableModel.addRow(data);
		}
		
		//Return the entire table
		return tableModel;
	}
	
	//ENUMS for the quadrants of a city
    public enum CityQuadrant {
        NW,
        NE,
        SW,
        SE
    }

	//ENUMS for the possible statuses of a property
    public enum Status {
        UNPUBLISHED,
        ACTIVE,
        RENTED,
        CANCELLED,
        SUSPENDED
    }
	
	//ENUMS for the types of properties
    public enum Type {
        APARTMENT,
        ATTACHED,
        DETACHED,
        TOWNHOUSE,
    }
}
