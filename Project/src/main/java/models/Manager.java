package models;

//A type of user
public class Manager extends User {
	//Set the email and password of user
    public Manager(final String email, final String password) {
        super(email, password);
		//Differentiate between users by setting a role
        this.role = UserRole.MANAGER;
    }
}