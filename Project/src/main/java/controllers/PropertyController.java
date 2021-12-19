package controllers;

import static java.util.Optional.ofNullable;

import models.*;
import util.DatabaseConnection;
import util.PropertyFormUtil;

import java.sql.*;
import java.util.*;
import java.util.Calendar;

public class PropertyController {
	//Get this instance of the login of a user
    private final LoginController loginController = LoginController.getOnlyInstance();
	//Get this instance of a user
    private final UserController userController = UserController.getOnlyInstance();
	
	//Create a connection to the database
    private final Connection connection = DatabaseConnection.getConnection();
	//Make sure only one instance of this is possible when a user logs in (Singleton)
    private static final PropertyController propertyController = new PropertyController();
	
	//Update the properties in the database
	public void updateDatabase(){
		PreparedStatement mystmt = null;
		ResultSet result = null;
		int feePeriod = 0;
		
		//First, get the fee period in the manager_configuration table (the period that a property can stay active for after it has been paid for)
		try{
			//Create a query 
			String query = "SELECT * FROM manager_configuration";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//For every result...
			while(result.next()){
				//Set the fee period (there should only ever be 1 result with how the database is set up)
				feePeriod = result.getInt("period_days");
			}
			
		} catch(SQLException e){
			System.out.println("Could not execute query.");
		}
		
		//Get all properties that are currently ACTIVE and have their fee paid for
		try{
			//Create a query 
			String query = "SELECT * FROM Property WHERE property_status = ? AND is_fee_paid = ?";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Insert into the query ACTIVE and true
			mystmt.setString(1, Property.Status.ACTIVE.toString());
			mystmt.setBoolean(2, true);
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//For every result...
			while(result.next()){
				//Get the payment date in the table as a Timestamp
				Timestamp date = result.getTimestamp("payment_date");
				
				//Create a new calendar
				Calendar cal = Calendar.getInstance();
				
				//Get the ID of the property
				int houseID = result.getInt("id");
				
				//Set the time of the calendar to be the payment date
				cal.setTime(date);
				
				//Add to the payment date the fee period
				cal.add(Calendar.DATE, feePeriod);
				
				//Create another Timestamp that is the current date and time
				Timestamp currentDate = new Timestamp(System.currentTimeMillis());
				
				//If the current date is equal to the payment_date + fee period or it is past the payment_date + fee period...
				if(currentDate.compareTo(new Timestamp(cal.getTime().getTime())) == 0 || currentDate.compareTo(new Timestamp(cal.getTime().getTime())) > 0){
					//Update the property so that it is now UNPUBLISHED and its fee status is now false
					try{
						//Create a query 
						String query2 = "UPDATE Property SET property_status = ?, is_fee_paid = ? WHERE id = ?";
						
						//Prepare the statement
						mystmt = this.connection.prepareStatement(query2);
						
						//Insert into the query UNPUBLISHED, false, and the houseID
						mystmt.setString(1, Property.Status.UNPUBLISHED.toString());
						mystmt.setBoolean(2, false);
						mystmt.setInt(3, houseID);
						
						//Execute update
						int row = mystmt.executeUpdate();
						
					} catch(SQLException f){
						f.printStackTrace();
					}
				}
			}
			
		} catch(SQLException e){
			System.out.println("Could not execute query.");
		}
	}

	//Upload a property into the database
    public Property uploadProperty(final Property property) {
		//Make sure that the current user is a landlord
        final User user = loginController.getCurrentUser().filter(u -> u.getRole().equals(User.UserRole.LANDLORD))
                .orElseThrow(() -> new RuntimeException("Current user must be a landlord."));
		
		//Create a query 
        final String query = "INSERT INTO property(address, property_type, number_of_bedrooms, number_of_bathrooms, is_furnished, city_quadrant, landlord, is_fee_paid, property_status, date_published, payment_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
		//Insert into the database a new property with the properties of the given property
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			//Insert into the query the address, property type, number of bedrooms, number of bathrooms, furnish status, cit quadrant, landlord email,
			//fee status, property status, date published, and payment date for the given property
            ps.setString(1, property.getAddress());
            ps.setString(2, property.getPropertyType().toString());
            ps.setInt(3, property.getNumberOfBedrooms());
            ps.setInt(4, property.getNumberOfBathrooms());
            ps.setBoolean(5, property.isFurnished());
            ps.setString(6, property.getCityQuadrant().toString());
            ps.setString(7, user.getEmail());
            ps.setBoolean(8, false);
            ps.setString(9, Property.Status.UNPUBLISHED.toString());
            ps.setDate(10, null);
            ps.setDate(11, null);
			
			//Execute update
            ps.executeUpdate();
			
			//Get the keys generated from the result (since this is an update statement)
            final ResultSet rs = ps.getGeneratedKeys();
			
			//Make sure that the property was uploaded into the database
            if (rs.next()) {
				//Set the id of the property object to be the id of the property in the database (reassurance)
                property.setId(rs.getInt(1));
                System.out.println("Property " + property.getId() + " successfully uploaded");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
		
		//Return the property uploaded
        return property;
    }
	
	//Get a specific property based on ID
    public Optional<Property> getProperty(final int id) {
		//Create a query 
        final String query = "SELECT * FROM property WHERE id = ? LIMIT 1";
		
		//Get a property based on ID 
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the ID
            ps.setInt(1, id);
			
			//Execute the query
            final ResultSet rs = ps.executeQuery();
			
			//If there if a property with the ID...
			if(rs.next()){
				//Create a new Property object from the result set
				return Property.createFromResultSet(rs);
			}
			
			//Otherwise return null
			else{
				return null;
			}
            
        } catch (final SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
	
	//Save a property form for a specific renter
    public Optional<PropertyForm> savePropertyForm(final PropertyForm form, final Renter renter) {
		//Check if the user already has a property form...
	    if (renter.getPropertyFormId() != null) {
			//If they do, then clear it from the renter in the database and set it to null in the object
	        clearPropertyForm(renter);
	        renter.setPropertyFormId(null);
        }
		
		//Create a query 
	    final String query = "INSERT INTO property_form(property_type, number_of_bedrooms, number_of_bathrooms, city_quadrant)" +
                "VALUES (?, ?, ?, ?)";
				
		//Insert a new property form for the given renter
	    try {
			//Prepare the statement
	        final PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			//Insert into the query the property type, number of bedrooms, number of bathrooms, and city quadrant that the renter
			//specified in their property form that they want to be notified about
	        ps.setString(1, ofNullable(form.getPropertyType()).map(Enum::toString).orElse(null));
	        ps.setObject(2, form.getNumberOfBedrooms());
	        ps.setObject(3, form.getNumberOfBathrooms());
	        ps.setString(4, ofNullable(form.getCityQuadrant()).map(Enum::toString).orElse(null));
			
			//Execute update
	        ps.executeUpdate();
			
			//Generate keys for the update 
	        final ResultSet rs = ps.getGeneratedKeys();
			
			//If the property form was able to be inserted into the database
	        if (rs.next()) {
                System.out.println("DEBUG: successfully saved property form for " + renter);
				
				//Get the id of the new form
	            final Integer id = rs.getInt(1);
				
				//Set the id of the new form for the property form
	            form.setId(id);
				
				//Set the id of the new form for the user
	            renter.setPropertyFormId(id);
				
				//Update the property form of this renter
	            userController.updateUserPropertyForm(renter);
				
				//Return the given property form in a container
	            return Optional.of(form);
            } 
			
			//Otherwise
			else {
                System.err.println("Failed to save property form");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//If the above doesn't execute, then return an empty container
	    return Optional.empty();
    }
	
	//Clear the property form of a user in the database
    public Renter clearPropertyForm(final Renter renter) {
		//Create a query 
	    final String query = "DELETE FROM property_form WHERE id = ? LIMIT 1";
		
		//Delete the property form of a user
	    try {
			//Prepare the statement
	        final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the ID of the user we want to delete from
	        ps.setInt(1, renter.getPropertyFormId());
			
			//Execute update
	        ps.executeUpdate();
			
			//Set the property form id of this renter to null
	        renter.setPropertyFormId(null);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//Return the renter
	    return renter;
    }
	
	//Get the payment fee required to be able to post a property
    public double getPropertyPaymentFee() {
		//Create a query 
	    final String query = "SELECT fees FROM manager_configuration LIMIT 1";
		
		//Find the payment fee in the manager_configuration table
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Execute the query
            final ResultSet rs = ps.executeQuery();
			
			//If there is a payment fee 
            if (rs.next()) {
				//Get the fee from the database and return it
                return rs.getDouble("fees");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//If the above doesn't execute, then return 0
        return 0d;
    }
	
	//Allows a landlord to pay for their property to be posted online
	public void payProperty(final int propertyID, double amount, Landlord landlord){
		PreparedStatement mystmt = null;
		ResultSet result = null;
		
		//First, get the fee from the manager_configuration table
		try{
			//Create a query 
			String query = "SELECT fees FROM manager_configuration";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//If the fee exists in the table
			while(result.next()){
				//If the amount given by the landlord matches the fee in the table...
				if(amount == result.getDouble("fees")){
					//Then continue
					continue;
				}
				//Otherwise return
				else{
					return;
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		//Next find the property that the landlord wants to pay to post
		try{
			//Create a query 
			String query = "SELECT * FROM property WHERE id = ? AND landlord = ?";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Insert into the query the ID of the property and the landlord's email
			mystmt.setInt(1, propertyID);
			mystmt.setString(2, landlord.getEmail());
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//For each result...
			while(result.next()){
				//Publish the property to be online
				publishProperty(Property.createFromResultSet(result).get());
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	//Post the property to be online
    public void publishProperty(final Property property) {
		//Create a query 
        final String query = "UPDATE property SET is_fee_paid = true, property_status = ?, date_published = NOW(), payment_date = NOW() WHERE id = ?";
		
		//Update the property so that its status is ACTIVE, its fee status is true, its date published is the current time,
		//and the payment date is the current time
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query ACTIVE and the ID of the property to be updated
            ps.setString(1, Property.Status.ACTIVE.toString());
            ps.setInt(2, property.getID());
			
			//Execute update
            final int row = ps.executeUpdate();
			
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
	
	//Change the state of a specific property of a landlord from ACTIVE 
    public Optional<Property> changePropertyState(final int propertyId, final Property.Status newStatus, Landlord landlord) {
		//Make sure that the property exists 
        try {
            if (!checkPropertyExists(propertyId)) {
                throw new IllegalArgumentException("Property does not exist");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Optional.empty();
        }
		
		//Create a query 
        final String query = "UPDATE property SET property_status = ? WHERE id = ? AND landlord = ? AND property_status = ? LIMIT 1";
		
		//Update the status of a specific property only if its current status is ACTIVE
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the new status of the property, the ID of the property, the email of the landlord, and ACTIVE
            ps.setString(1, newStatus.toString());
            ps.setInt(2, propertyId);
            ps.setString(3, landlord.getEmail());
            ps.setString(4, Property.Status.ACTIVE.toString());
			
			//Execute update
            final int updatedRows = ps.executeUpdate();
			
            System.out.println(updatedRows + " row(s) changed from changing property state");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Optional.empty();
        }
		
		//Return the property in a container
        return getProperty(propertyId);
    }
	
	//Check whether a specific property exists using property ID
    private boolean checkPropertyExists(final int propertyId) throws SQLException {
		//Create a query 
        final String query = "SELECT EXISTS(SELECT * FROM property WHERE id = ?)";
		
		//Prepare the statement
        final PreparedStatement ps = connection.prepareStatement(query);
		
		//Insert into the query the ID of the property to be found
        ps.setInt(1, propertyId);
		
		//Execute the query
        final ResultSet rs = ps.executeQuery();
		
		//If the property does exist...
		if(rs.next()){
			//Return true
			return rs.getBoolean(1);
		}
		
		//Otherwise...
        else{
			//Return false
			return false;
		}
    }
	
	//Get the properties that the landlord has not paid for yet (or properties with other statuses)
    public Collection<Property> getPaymentProperties(final Landlord landlord) {
		//Create a query 
        final String query = "SELECT * FROM property WHERE landlord = ? AND property_status = ?";
		
		//Create a new arraylist of properties that the landlord has not yet paid for
        final List<Property> results = new ArrayList<>();
		
		//Find properties that have not yet been paid for
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the email of the landlord and ACTIVE
            ps.setString(1, landlord.getEmail());
            ps.setString(2, Property.Status.UNPUBLISHED.toString());
			
			//Execute the query
            final ResultSet rs = ps.executeQuery();
			
			//For every result...
            while (rs.next()) {
				//Create a new Property object and add it to the arraylist
                Property.createFromResultSet(rs).ifPresent(results::add);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//Return the properties a landlord has not yet paid for
        return results;
    }

	//Get all ACTIVE properties
    public Collection<Property> getProperties() {
		//Create a query 
        final String query = "SELECT * FROM property WHERE property_status = '" + Property.Status.ACTIVE.toString() + "'";
		
		//Use a general function that executes the query
        return internalGetProperties(query);
    }
	
	//Fetch a property with a certain ID
    public Optional<Property> fetchProperty(final int id) {
		//Create a query 
        final String query = "SELECT * FROM property WHERE id = ? LIMIT 1";
	
		//Prepare the statement
        try (final PreparedStatement ps = connection.prepareStatement(query)) {
			
			//Insert into the query the ID of the property to be found
            ps.setInt(1, id);
			
			//Execute the query
            final ResultSet rs = ps.executeQuery();
			
			//If the property exists...
            if (rs.next()) {
				//Create a new property object from a result set and return it in a container
                return Property.createFromResultSet(rs);
            } 
			
			//Otherwise...
			else {
                throw new RuntimeException("Property at id " + id + " could not be fetched");
            }
        } catch (final SQLException | RuntimeException throwables) {
            throwables.printStackTrace();
            return Optional.empty();
        }
    }
	
	//Get Properties that match the criteria of a registered user
    public Collection<Property> getProperties(final PropertyForm propertyForm) {
		//Create a new query based on the specifications of a user
        final String query = PropertyFormUtil.getPropertyFilterQuery(propertyForm);
		
		//Use a general function that executes the query
        return internalGetProperties(query);
    }
	
	//Get a landlord based on the property
    public Landlord getLandlord(final Property property) {
		//If a property does not have a landlord set...
        if (property.getLandlord() == null) {
			
			//Find a user with a specific email
            try {
				//Create a query 
                final String query = "SELECT * FROM user WHERE email = ? LIMIT 1";
				
				//Prepare the statement
                final PreparedStatement ps = connection.prepareStatement(query);
				
				//Insert into the query the email of the landlord
                ps.setString(1, property.getLandlordEmail());
				
				//Execute the query
                final ResultSet rs = ps.executeQuery();
				
				//If a user with the email exists...
                if (rs.next()) {
					//Create a new landlord with the appropriate user information
                    final Landlord landlord = (Landlord) User.createFromResultSet(rs).filter(user -> user instanceof Landlord)
                            .orElseThrow(() -> new RuntimeException("Could not find landlord at Email " + property.getLandlordEmail()));
							
					//Set the landlord of the property
                    property.setLandlord(landlord);
                }
            } catch (final SQLException | RuntimeException throwables) {
                throwables.printStackTrace();
            }
        }
		
		//Return the landlord of the given property
        return property.getLandlord();
    }
	
	//Get every single property of a landlord in the database
	public Collection<Property> getAllProperties(Landlord landlord){
		//Create a new arraylist of properties that holds every property of a landlord in the database
		final Collection<Property> properties = new ArrayList<>();
		PreparedStatement mystmt = null;
		ResultSet result = null;
		
		//Find every property with a specific landlord
		try{
			//Create a query 
			String query = "SELECT * FROM property WHERE landlord = ? AND property_status = ?";
			
			//Prepare the statement
			mystmt = connection.prepareStatement(query);
			
			//Insert into the query the email of a landlord and ACTIVE
			mystmt.setString(1, landlord.getEmail());
			mystmt.setString(2, Property.Status.ACTIVE.toString());
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//For every result...
			while(result.next()){
				//Create a new Property object and add it to the arraylist
				Property.createFromResultSet(result).ifPresent(properties::add);
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		//Return the arraylist that holds every property of a landlord
		return properties;
		
	}
	
	//A general function that executes a provided query
    protected Collection<Property> internalGetProperties(final String query) {
		//Create a new arraylist to hold the properties found from the query
        final Collection<Property> properties = new ArrayList<>();
		
		//Prepare the statement
        try (final Statement stm = connection.createStatement()) {
			//Execute the query
            final ResultSet rs = stm.executeQuery(query);
			
			//For every result...
            while (rs.next()) {
				//Create a new Property object and add it to the arraylist
                Property.createFromResultSet(rs).ifPresent(properties::add);
            }
        } catch (final SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//Return the arraylist that holds every property found from the query
        return properties;
    }
	
	//Get this instance of the controller (singleton)
    public static PropertyController getOnlyInstance() {
        return propertyController;
    }
}
