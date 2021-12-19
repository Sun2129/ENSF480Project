package controllers;

import models.User;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

//Controls the database for the user
public class UserController {
	//Create a connection to the database
    private static final UserController userController = new UserController();
	//Make sure only one instance of this is possible when a user logs in (Singleton)
    private final Connection connection = DatabaseConnection.getConnection();


    private UserController() {}
	
	//Update the property form of a user
    public Optional<User> updateUserPropertyForm(final User user) {
		//Make sure that the user actually has a property form
        if (user.getPropertyFormId() == null) {
            throw new IllegalArgumentException("No property form ID on user");
        }
		
		//Create a query
        final String query = "UPDATE user SET property_form_id = ? WHERE id = ? LIMIT 1";
		
		//Update the property search form for a specific user
        try {
			//Prepare the statement
            final PreparedStatement ps = connection.prepareStatement(query);
			
			//Insert into the query the new property form ID and the user ID
            ps.setInt(1, user.getPropertyFormId());
            ps.setInt(2, user.getId());
			
			//Execute update
            final int updatedRows = ps.executeUpdate();
			
			//If there were not rows to update...
            if (updatedRows < 1) {
                System.err.println("No rows updated from updating user's property form");
            } 
			
			//Otherwise return the user in a container
			else {
                return Optional.of(user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//If the above does not execute, return an empty container
        return Optional.empty();
    }
	
	//Get all the users of a specific role
    public Collection<? extends User> getUsersOfRole(final User.UserRole role) {
		//Create a query
        final String query = "SELECT * FROM user WHERE role = ?";
		
		//Create an arraylist of users
        final Collection<User> users = new ArrayList<>();
		
		//Prepare the statement
        try (PreparedStatement ps = connection.prepareStatement(query)) {
			//Insert into the query the role of the user converted into a string
            ps.setString(1, role.toString());
			
			//Execute query
            final ResultSet rs = ps.executeQuery();
			
			//For every result...
            while (rs.next()) {
				//Create a new User object and add it to the arraylist
                User.createFromResultSet(rs).ifPresent(users::add);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
		
		//Return all the users of a specific role
        return users;
    }
	
	//Get this instance of a user (singleton)
    public static UserController getOnlyInstance() {
        return userController;
    }
}
