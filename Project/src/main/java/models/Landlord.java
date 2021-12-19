package models;

//A type of user
public class Landlord extends User {
	//Set the email and password of this user
    protected Landlord(final String email, final String password) {
        super(email, password);
		//Differentiate between users by setting a role
        this.role = UserRole.LANDLORD;
    }
}