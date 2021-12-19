package models;

import java.util.Objects;

//A type of user
public class Renter extends User {
	//Set the email and password of this user
    protected Renter(final String email, final String password) {
        super(email, password);
		//Differentiate between the user by setting a role
        this.role = UserRole.RENTER;
    }
	
	//Override the default equals() method
    @Override
    public boolean equals(final Object o) {
		//Return true/false depending on if the IDs of two renters are equal
        return Objects.equals(getId(), ((Renter) o).getId());
    }
	
	//Override the hasCode() method
    @Override
    public int hashCode() {
		//Returns the ID of the user
        return Objects.hash(getId());
    }
}
