package controllers;

import com.mysql.cj.jdbc.JdbcPreparedStatement;
import models.Property;
import models.PropertyForm;
import models.Renter;
import models.User;
import util.DatabaseConnection;
import util.PropertyFormUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//Controls login for all users
public class LoginController {
	//Make sure only one instance of this is possible when a user logs in (Singleton)
    private static final LoginController loginController = new LoginController();
	//Create a connection to the database
    private final Connection connection = DatabaseConnection.getConnection();
	//Holds the current user that is logging in
    private User currentUser = null;

    private LoginController() { }
	
	//Login a user based on the email and password that they have provided
    public Optional<User> login(final String email, final String password) {
		
		//Try to login the user
        try {
			//If the user's email does not exist in the database...
            if (!checkUserExists(email)) {
                System.out.println("User " + email + " does not exist.");
				System.exit(1);
            }
			
			//Create a query
            final String query = "SELECT * FROM user WHERE email = ? AND password = ? LIMIT 1";
			
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the provided email and password
            ps.setString(1, email);
            ps.setString(2, password);
			
			//Execute query
            final ResultSet rs = ps.executeQuery();
			
			//Create a user in a container from the result set
            final Optional<User> user = User.createFromResultSet(rs);
			
			//If the user exists
            if (user.isPresent()) {
                System.out.println("Successfully logged in " + user.toString());
				
				//Get the type of user it is
                User updatedUser = user.get();
				
				//If the user is an instance of a renter...
                if (updatedUser instanceof Renter) {
					//Get the notifications of the renter
                    persistNotificationsForRenter((Renter) updatedUser);
                }
				
				//Then update the last login date of the user
                updatedUser = updateUserLastLogin(updatedUser).orElse(updatedUser);
				
				//Set the current user to be the update user
                currentUser = updatedUser;
            } else {
                throw new RuntimeException("Error logging " + email + " in.");
            }
        } catch (final SQLException | RuntimeException throwables) {
            throwables.printStackTrace();
        }
		
		//Return the current user in a container
        return Optional.of(currentUser);
    }
	
	//Update the last login date of the user
    private Optional<User> updateUserLastLogin(final User userModel) throws SQLException {
		//Set the last login of the user to be the current time
        userModel.setLastLogin(LocalDateTime.now());
		
		//Create a query
        final String query = "UPDATE user SET last_login = ? WHERE id = ? LIMIT 1";
		
		//Prepare the statement
        final PreparedStatement ps = connection.prepareStatement(query);
		
		//Insert into the query the Timestamp of the current time and the ID of the user
        ps.setObject(1, userModel.getLastLogin());
        ps.setInt(2, userModel.getId());
		
		//Excute Update
        final int updatedRows = ps.executeUpdate();

		//If the update could not be executed...
        if (updatedRows < 1) {
            System.err.println("Error updated user " + userModel.getEmail() + " last login");
            return Optional.of(userModel);
        }
		
		//Return the user in a container
        return fetchUser(userModel);
    }
	
	//Get a user based off of the user model
    private Optional<User> fetchUser(final User userModel) throws SQLException {
		//Create a query
        final String query = "SELECT * FROM user WHERE id = ? LIMIT 1";
		
		//Prepare the statement
        final PreparedStatement ps = connection.prepareStatement(query);
		
		//Insert into the query the ID of the user
        ps.setInt(1, userModel.getId());
		
		//Execute query
        final ResultSet rs = ps.executeQuery();
		
		//Return the user in a container created from the result set
        return User.createFromResultSet(rs);
    }
	
	//Register a user based on the provided email, password, and their role
    public Optional<User> register(final String email, final String password, final User.UserRole role) throws IllegalArgumentException {
		
		//Check if the user already exists
        try {
			//If the user already exists...
            if (checkUserExists(email)) {
                throw new IllegalArgumentException("email " + email + " is taken");
            }
        } catch (final SQLException e) {
            e.printStackTrace();
            return null;
        }
		
		//Create a new user model from their role, email, and password
        final User user = User.fromRole(role, email, password);
		
		//Create a query
        final String query = "INSERT INTO user(email, password, role) VALUES (?,?,?)";
		
		//Try to insert into the user table a new user
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the email, password, and role of the user
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole().toString());
			
			//Execute update
            final int row = ps.executeUpdate();
			
            System.out.println(row + " row(s) affected from register.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//Once they have been registered, login the user in and return a user in a container
        return login(email, password);
    }

	//Get the notifications for a renter
    public Collection<Property> getNotificationsForRenter(final Renter renter) {
		//Create a new arraylist of properties that should be displayed to the renter
        final List<Property> notifications = new ArrayList<>();
		
		//Create a query
        final String query = "SELECT p.* FROM property p " +
                "INNER JOIN user_notification un ON p.id = un.property_id " +
                "WHERE un.user_id = ?";
				
		//Get all properties that the user should be notified about
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the ID of the user
            ps.setInt(1, renter.getId());
			
			//Execute query
            final ResultSet rs = ps.executeQuery();
			
			//For every result...
            while (rs.next()) {
				//Create a new Property object from the result set and add it to the arraylist
                Property.createFromResultSet(rs).ifPresent(notifications::add);
            }
			
			//Clear the properties that the user should e notified about in the user_notification table
            clearNotificationsForRenter(renter);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//Return the arraylist of notifications 
        return notifications;
    }
	
	//Clear the properties that the renter should be notified about
    private void clearNotificationsForRenter(final Renter renter) {
		//Create a query
        final String query = "DELETE FROM user_notification WHERE user_id = ?";
		
		//Delete all instances notifying the user from the database
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the ID of the user
            ps.setInt(1, renter.getId());
			
			//Execute update
            final int rowsDeleted = ps.executeUpdate();
			
            System.out.println("DEBUG: " + rowsDeleted + " row(s) deleted from user notifications");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

	//Check if the user exists in the database
    private boolean checkUserExists(final String email) throws SQLException {
		//Create a query
        final String query = "SELECT EXISTS(SELECT * FROM user WHERE email = ?)";
		
		//Prepare the statement
        final PreparedStatement ps = connection.prepareStatement(query);
		
		//Insert into the query the email of the user
        ps.setString(1, email);
		
		//Execute query
        final ResultSet rs = ps.executeQuery();

        rs.next();
        return rs.getBoolean(1);
    }
	
	//Get properties that the user should be notified about
    public Collection<Property> getPropertiesForUser(final PropertyForm form, final LocalDateTime lastLogin) {
		//Filter the property form of a user and make sure that the date published of properties is after the last login of the user
        final String query = PropertyFormUtil.getPropertyFilterQuery(form) + " AND date_published > ?";
		
		//Return a collection of properties that the user should be notified about
        return getMatchingPropertiesForUser(query, lastLogin);
    }
	
	//Get the properties that the user should be notified about
    private Collection<Property> getMatchingPropertiesForUser(final String query, final LocalDateTime lastLogin) {
		//Create a new arraylist to hold the properties that a user should be notified about
        final Collection<Property> properties = new ArrayList<>();
		
		//Find properties to display to the user
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the last login of the user
            ps.setObject(1, lastLogin);

            System.out.println("DEBUG: query = " + ((JdbcPreparedStatement) ps).getPreparedSql());
			
			//Execute query
            final ResultSet rs = ps.executeQuery();
			
			//For every result...
            while (rs.next()) {
				//Create a Property Object from the result set and add it to the arraylist
                Property.createFromResultSet(rs).ifPresent(properties::add);
            }

        } catch (final SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//Return a collection of properties that the user should be notified about 
        return properties;
    }
	
	//Get the property form of a user that is stored in the database
    public Optional<PropertyForm> getPropertyForm(final Renter renter) {
		//Check if the renter even has a property form
        if (renter.getPropertyFormId() == null) {
            return Optional.empty();
        }
		
		//Create a query
        final String query = "SELECT property_form.* FROM property_form " +
                "INNER JOIN user u on property_form.id = u.property_form_id WHERE u.id = ? " +
                "LIMIT 1";
				
		//Find the property form of a renter
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the ID of the renter
            ps.setInt(1, renter.getId());
			
			//Execute query
            final ResultSet rs = ps.executeQuery();
			
			//If a property form exists...
            if (rs.next()) {
                System.out.println("DEBUG: property form fetched for " + renter.getId());
				
				//Create a Property object from the result set and return it
                return PropertyForm.createFromResultSet(rs);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//If there does not exist one, then return an empty container
        return Optional.empty();
    }
	
	//Insert into the database the property that a user should be notified about
    public void persistNotificationsForRenter(final Renter renter) {
		//Make sure that the renter actually has a property form
        if (renter.getPropertyFormId() == null) {
            return;
        }
		
		//Create an arraylist of property forms of the user
        final Optional<PropertyForm> propertyForm = getPropertyForm(renter);
		
		//If the arraylist exists...
        propertyForm.ifPresent(form -> {
			//Find all properties that match the criteria of the property form submitted by a renter
            final Collection<Property> matchingProperties = getPropertiesForUser(form, renter.getLastLogin());
			
			//If there are no matching properties...
            if (matchingProperties.isEmpty()) {
                return;
            }
			
            //Create a query
            String query = "INSERT INTO user_notification (user_id, property_id) VALUES ";
			
			//Create an arraylist that contains the values to be inserted into user_notification
			//Values to be inserted are user_id and property_id
            final Collection<String> values = matchingProperties.stream().
                    map(property -> String.format("(%d, %d)", renter.getId(), property.getID()))
                    .collect(Collectors.toList());
            query += String.join(", ", values);
			
			//Insert into user_notification all properties that a user should be notified about
            try {
				//Prepare the statement
                final PreparedStatement ps = connection.prepareStatement(query);
				
				//Execute update
                final int rowsUpdated = ps.executeUpdate();

                System.out.println("DEBUG: " + rowsUpdated + " row(s) updated from fetching new notifications");
            } catch (final SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }
	
	//Logout the current user
    public void logout() {
        currentUser = null;
    }
	
	//Get the current user and return it in a container
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }
	
	//Check if user is logged in
    public boolean isUserLoggedIn() {
        return currentUser != null;
    }
	
	//Logout the current user
    public void logOutUser() {
        currentUser = null;
    }
	
	//Get the login instance (singleton)
    public static LoginController getOnlyInstance() {
        return loginController;
    }
}
