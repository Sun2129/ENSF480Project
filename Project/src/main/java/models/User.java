package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

//An abstract class that emulates the user table in the database
abstract public class User extends DatabaseModel {
    private static final String tableName = "user";
	//Email of this user
    protected String email;
	//Password of this user
    protected String password;
	//The role of this user
    protected UserRole role;
	//The last login of this user
    protected LocalDateTime lastLogin;
	//The property form ID of this user (only for registered renters)
    protected Integer propertyFormId;
	
	//Set the email and password of this user
    protected User(final String email, final String password) {
        this.email = email;
        this.password = password;
    }
	
	//Get a user from a result set (used with the result set returned from controllers)
    public static Optional<User> createFromResultSet(final ResultSet rs) {
        User user = null;
        try {
            if (rs.isBeforeFirst()) {
                rs.next();
            }
			//Get the ID of the user
            final Integer id = rs.getInt("id");
			//Get the email of the user
            final String email = rs.getString("email");
			//Get the password of the user
            final String password = rs.getString("password");
			//Get the role of the user
            final UserRole role = UserRole.valueOf(rs.getString("role"));
			//Get the last login of the user
            final LocalDateTime lastLogin = rs.getObject("last_login", LocalDateTime.class);
			//Get the property form id of the user
            final Integer propertyFormId = rs.getInt("property_form_id");
			
			//Create a new user
            user = fromRole(role, email, password);
			//Set the id of the user separately
            user.setId(id);
			//Set the last login of the user separately
            user.setLastLogin(lastLogin);
			//Set the property form ID of the user separately
            user.setPropertyFormId(propertyFormId);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
		//Return user in a container
        return Optional.of(user);
    }
	
	//Create a new user based off of the role provided
    public static User fromRole(final UserRole role, final String email, final String password) {
        User user = null;

        switch (role) {
			//If the role is a renter, make a new renter
            case RENTER:
                user = new Renter(email, password);
                break;
			//If the role is a landlord, make a new landlord
            case LANDLORD:
                user = new Landlord(email, password);
                break;
			//If the role is a manager, make a new manager
            case MANAGER:
                user = new Manager(email, password);
                break;
			//Otherwise, the role provided is not a recognized role
            default:
                System.err.println("Mismatching role?");
                break;
        }

        return user;
    }
	
	//Get property form id
    public Integer getPropertyFormId() {
        return propertyFormId;
    }
	
	//Set property form id
    public void setPropertyFormId(final Integer propertyFormId) {
        this.propertyFormId = propertyFormId;
    }

	//Get last login
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

	//Set last login
    public void setLastLogin(final LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

	//Get email
    public String getEmail() {
        return email;
    }

	//Set email
    public void setEmail(final String email) {
        this.email = email;
    }

	//Get password
    public String getPassword() {
        return password;
    }

	//Set password
    public void setPassword(final String password) {
        this.password = password;
    }

	//Get the role of the user
    public UserRole getRole() {
        return role;
    }

	//Set the role of the user
    public void setRole(final UserRole role) {
        this.role = role;
    }

	//Overrid the defauly toString() method
    @Override
    public String toString() {
		//Return the user's email and role
        return "User{" +
                "email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

	//ENUMS for the different type of user roles
    public enum UserRole {
        RENTER,
        LANDLORD,
        MANAGER;
    }
}
