package controllers;

import models.*;
import util.DatabaseConnection;
import java.sql.*;
import java.util.*;
import java.util.Calendar;
import java.util.Collection;
import javax.swing.table.*;

//Controls the database for the manager
public class DatabaseController {
	//Create a connection to the database
    private final Connection connection = DatabaseConnection.getConnection();
	//Make sure only one instance of this is possible when a user logs in (Singleton)
    private static final DatabaseController databaseController = new DatabaseController();
	
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
						
						//Prepare the query
						mystmt = this.connection.prepareStatement(query2);
			
						//Insert into the query UNPUBLISHED, false, and the houseID
						mystmt.setString(1, Property.Status.UNPUBLISHED.toString());
						mystmt.setBoolean(2, false);
						mystmt.setInt(3, houseID);
						
						//Update the property table
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
	
	//Get the number of houses listed in a period of time
	public int numberOfHousesListed(int period){
		int number = 0;
		PreparedStatement mystmt = null;
		ResultSet result = null;
		
		//Get properties where that have been paid for
		try{
			//Create a statement
			String query = "SELECT * FROM Property WHERE payment_date IS NOT NULL";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//For every result, 
			while(result.next()){
				//Create a new Timestamp that is the current time
				Timestamp currentDate = new Timestamp(System.currentTimeMillis());
				
				//Create a new Timestamp that is the payment_date from the property table
				Timestamp date = result.getTimestamp("payment_date");
				
				//Create a new calendar
				Calendar cal = Calendar.getInstance();
				
				//Set the time of the calendar to the current date
				cal.setTime(currentDate);
				
				//Add to the current date a negative period (currentDate + (-period))
				cal.add(Calendar.DATE, period * (-1));
				
				//If the payment_date is after current date - period
				if(date.after(new Timestamp(cal.getTime().getTime()))){
					//Increment number
					number++;
				}
				
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		//Return the number of houses listed in a period
		return number;
	}
	
	//Get the number of houses rented in a period
	public int numberOfHousesRented(int period){
		int number = 0;
		PreparedStatement mystmt = null;
		ResultSet result = null;
		
		//Get the properties that are rented and are paid for
		try{
			//Create a query
			String query = "SELECT * FROM Property WHERE property_status = ? AND payment_date IS NOT NULL";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Insert into the query RENTED
			mystmt.setString(1, Property.Status.RENTED.toString());
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//For every result...
			while(result.next()){
				//Create a new Timestamp that is the current time
				Timestamp currentDate = new Timestamp(System.currentTimeMillis());
				
				//Create a new Timestamp that is the payment_date from the property table
				Timestamp date = result.getTimestamp("payment_date");
				
				//Create a new calendar
				Calendar cal = Calendar.getInstance();
				
				//Set the time of the calendar to the current date
				cal.setTime(currentDate);
				
				//Add to the current date a negative period (currentDate + (-period))
				cal.add(Calendar.DATE, period * (-1));
				
				//If the payment_date of the rented property is after current date - period
				if(date.after(new Timestamp(cal.getTime().getTime()))){
					//Increment number
					number++;
				}
				
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		//Return the number of houses rented in the period
		return number;
	}
	
	//Get the total number of active listings (regardless of period)
	public int numberOfActiveListings(int period){
		int number = 0;
		PreparedStatement mystmt = null;
		ResultSet result = null;
		
		//Get properties that are ACTIVE
		try{
			//Create a query
			String query = "SELECT * FROM Property WHERE property_status = ?";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Insert into the query ACTIVE
			mystmt.setString(1, Property.Status.ACTIVE.toString());
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//For every result...
			while(result.next()){
				//Increment number
				number++;
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		//Return the total number of ACTIVE listings
		return number;
	}
	
	//Get the houses rented in the period as a table
	public DefaultTableModel getHousesRentedPeriod(int period){
		PreparedStatement mystmt = null;
		ResultSet result = null;
		//Holds certain qualities of properties rented in this period
		ArrayList<ArrayList<String>> rented = new ArrayList<ArrayList<String>>();
		
		//Get properties that are RENTED and are paid for
		try{
			//Create a query
			String query = "SELECT * FROM Property WHERE property_status = ? AND payment_date IS NOT NULL";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Insert into the query RENTED
			mystmt.setString(1, Property.Status.RENTED.toString());
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//For every result...
			while(result.next()){
				//Create a new Timestamp that is the current time
				Timestamp currentDate = new Timestamp(System.currentTimeMillis());
				
				//Create a new Timestamp that is the payment_date from the property table
				Timestamp date = result.getTimestamp("payment_date");
				
				//Create a new calendar
				Calendar cal = Calendar.getInstance();
				
				//Set the time of the calendar to the current date
				cal.setTime(currentDate);
				
				//Add to the current date a negative period (currentDate + (-period))
				cal.add(Calendar.DATE, period * (-1));
				
				//If the payment_date of the rented property is after current date - period
				if(date.after(new Timestamp(cal.getTime().getTime()))){
					//Create a new arraylist of strings
					ArrayList<String> temp = new ArrayList<>();
					
					//Add to the arraylist, the landlord's email, the ID of the property, and the address of the property
					temp.add(result.getString("landlord"));
					temp.add(result.getString("id"));
					temp.add(result.getString("address"));
					
					//Add to the arraylist that holds properties rented in the period
					rented.add(temp);
				}
				
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		//Return a table created from the arraylist of properties rented in the period
		return createRentedTable(rented);
	}
	
	//Create a table of rented properties
	public DefaultTableModel createRentedTable(ArrayList<ArrayList<String>> rented){
		//Create a new array of objects to hold the column names 
		Object[] head = new String[]{"Landlord Email", "House ID", "Address"};
		
		//Create a new table
		DefaultTableModel tableModel = new DefaultTableModel(head, 0);
		
		//Add the column names as a row
		tableModel.addRow(head);
		
		//For every rented property in the arraylist...
		for(ArrayList<String> temp: rented){
			//Get the email, ID, and address
			String email = temp.get(0);
			String id = temp.get(1);
			String address = temp.get(2);
			
			//Create a new arraylist of objects to hold the email, id, and address
			Object[] data = new String[]{email, id, address};
			
			//Add the data of each rented property into the table
			tableModel.addRow(data);
		}
		
		//Return the entire table
		return tableModel;
	}
	
	//Get the users of a certain role
	public DefaultTableModel getUsersOfRole(User.UserRole role) {
        PreparedStatement mystmt = null;
		ResultSet result = null;
		//Holds certain information of users of a certain role
		ArrayList<ArrayList<String>> users = new ArrayList<ArrayList<String>>();
		
		//Get the users of a certain role
		try{
			//Create a query
			String query = "SELECT * FROM user WHERE role = ?";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Insert into the query the provided role converted to a string
			mystmt.setString(1, role.toString());
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//For every result...
			while(result.next()){
				//Create a new arraylist to hold the information of a user
				ArrayList<String> temp = new ArrayList<>();
				
				//Get the id and email of the user
				temp.add(String.valueOf(result.getInt("id")));
				temp.add(result.getString("email"));
				
				//Add the user to the arraylist of users
				users.add(temp);
			}
			
		} catch(SQLException e){
			System.out.println("Could not execute query.");
		}
		
		//Return a table created from the arraylist of users of a certain role
		return createUserTable(users);
    }
	
	//Create a table of users of a certain role
	public DefaultTableModel createUserTable(ArrayList<ArrayList<String>> users){
		//Create an array of objects to hold the column names
		Object[] head = new String[]{"User ID", "Email"};
		
		//Create a new table
		DefaultTableModel tableModel = new DefaultTableModel(head, 0);
		
		//Add the column names as a row
		tableModel.addRow(head);
		
		//For every user in the arraylist...
		for(ArrayList<String> temp: users){
			//Get the id and email
			String id = temp.get(0);
			String email = temp.get(1);
			
			//Create a new array of objects to hold the ID and email
			Object[] data = new String[]{id, email};
			
			//Add the data to the table
			tableModel.addRow(data);
		}
		
		//Return the entire table
		return tableModel;
	}
	
	//Change the state of a property
    public Optional<Property> changePropertyState(final int propertyId, final Property.Status newStatus) {
		
		//First check if the property actually exists
        try {
			//If it does not exist...
            if (!checkPropertyExists(propertyId)) {
                throw new IllegalArgumentException("Property does not exist");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Optional.empty();
        }
		
		//Create a query
        final String query = "UPDATE property SET property_status = ? WHERE id = ? AND property_status = ? LIMIT 1";
		
		//Update the state of a property that is currently ACTIVE
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the new status converted into a string, the id of property that is to be changed, and ACTIVE
            ps.setString(1, newStatus.toString());
            ps.setInt(2, propertyId);
            ps.setString(3, Property.Status.ACTIVE.toString());
			
			//Execute the update
            final int updatedRows = ps.executeUpdate();
			
            System.out.println(updatedRows + " row(s) changed from changing property state");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Optional.empty();
        }
		
		//Return the property in a container
        return getProperty(propertyId);
    }
	
	//Check if a propert exists
    private boolean checkPropertyExists(final int propertyId) throws SQLException {
		//Create a query
        final String query = "SELECT EXISTS(SELECT * FROM property WHERE id = ?)";
		
		//Prepare the statement
        final PreparedStatement ps = connection.prepareStatement(query);
		
		//Insert into the query the provided propertyID
        ps.setInt(1, propertyId);
		
		//Execute the update
        final ResultSet rs = ps.executeQuery();
		
		//If the result exists...
		if(rs.next()){
			//Return true
			return rs.getBoolean(1);
		}
		
		//Otherwise return false
        else{
			return false;
		}
    }
	
	//Get a certain property in a container
	public Optional<Property> getProperty(final int id) {
		
		//Create a query
        final String query = "SELECT * FROM property WHERE id = ? LIMIT 1";
		
		//Get a propert based off of its ID
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the provided ID
            ps.setInt(1, id);
			
			//Execute the query
            final ResultSet rs = ps.executeQuery();
			
			//If the property exists...
			if(rs.next()){
				//Return the property in a container created from this result set
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
	
	//Get all ACTIVE properties and return it as a collection of properties
    public Collection<Property> getProperties() {
		//Create a query
        final String query = "SELECT * FROM property WHERE property_status = '" + Property.Status.ACTIVE.toString() + "'";
		
		//Use a general function to execute the query
        return internalGetProperties(query);
    }
	
	//Get all properties and return it as a collection of properties
	public Collection<Property> getAllProperties(){
		//Create a query
		final String query = "SELECT * FROM property";
		
		//Use a general function to execute the query
        return internalGetProperties(query);
	}
	
	//A general method to execute a query and return a collection of properties
    protected Collection<Property> internalGetProperties(final String query) {
		//Create a new arraylist to hold the properties
        final Collection<Property> properties = new ArrayList<>();
		
		//Try to prepare the statement based on the query provided
        try (final Statement stm = connection.createStatement()) {
			
			//Execute the query
            final ResultSet rs = stm.executeQuery(query);
			
			//For every result...
            while (rs.next()) {
				
				//First create a Property object from the result set, and then add it to the arraylist
                Property.createFromResultSet(rs).ifPresent(properties::add);
            }
        } catch (final SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//Return a collection of properties
        return properties;
    }
	
	//Get the static databaseController variable (singleton)
    public static DatabaseController getOnlyInstance() {
        return databaseController;
    }
	
	//Set/Change the fee amount
	public void setFeeAmount(double feeAmount){
		double fee = feeAmount;
		int feePeriod = 0;
		PreparedStatement mystmt = null;
		ResultSet result = null;
		
		//First, if there is a period already in the table then get it 
		try{
			//Create a query
			String query = "SELECT * FROM manager_configuration";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//If there is a period in the table...
			if(result.next()){
				//Then get it
				feePeriod = result.getInt("period_days");
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		//Next, delete all rows in the table
		try{
			//Create a query
			String query = "TRUNCATE manager_configuration";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Execute the update
			int row = mystmt.executeUpdate();
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		//Next insert the new fee amount and the old fee period into the table
		try{
			//Create a query
			String query = "INSERT INTO manager_configuration(fees, period_days) VALUES (?,?)";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Insert into the query the fee and fee period
			mystmt.setDouble(1, fee);
			mystmt.setInt(2, feePeriod);
			
			//Execute the update
			int row = mystmt.executeUpdate();
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	//Set/Change the fee period
	public void setFeePeriod(int feePeriod){
		int period = feePeriod;
		double feeAmount = 0;
		PreparedStatement mystmt = null;
		ResultSet result = null;
		
		//First, if there is a fee already in the table then get it 
		try{
			//Create a query
			String query = "SELECT * FROM manager_configuration";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Execute the query
			result = mystmt.executeQuery();
			
			//If there is a fee in the table...
			if(result.next()){
				//Then get it
				feeAmount = result.getDouble("fees");
			}
			
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		//Next, delete all rows in the table
		try{
			//Create a query
			String query = "TRUNCATE manager_configuration";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Execute the update
			int row = mystmt.executeUpdate();
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		//Next insert the new fee period and the old fee amount into the table
		try{
			//Create a query
			String query = "INSERT INTO manager_configuration(fees, period_days) VALUES (?,?)";
			
			//Prepare the statement
			mystmt = this.connection.prepareStatement(query);
			
			//Insert into the query the fee amount and the fee period
			mystmt.setDouble(1, feeAmount);
			mystmt.setInt(2, period);
			
			//Execute the update
			int row = mystmt.executeUpdate();
			
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
}
