package view;
import javax.swing.*;

import controllers.LoginController;
import models.User;

//view for the login system
public class LoginForm {
	//creating the frame to put all the buttons and information on to
	JFrame userRoleFrame = new JFrame("User Role: ");
	//used to define what role the person loging in is (i.e. registered renter or landlord)
	User.UserRole roleToBeRegistered = null;
	//creating an object to access the logincontroller 
	private final LoginController loginController = LoginController.getOnlyInstance();

	//creating the form to select the type of login the user needs
	public LoginForm() {
		//setting up the frame
		userRoleFrame.setVisible(true);
		userRoleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userRoleFrame.setSize(500,500);

		//Create new panel to allow for disposing later
		final JPanel p1 = new JPanel();

		//setting up all the buttons for any registered user (i.e. landlords and registerd renters, etc)
		for (final User.UserRole role : User.UserRole.values()) {
			//initialising the buttons
			final JButton button = new JButton(role.toString().substring(0,1).toUpperCase() + role.toString().substring(1).toLowerCase());
			//setting up the litsener for each button
			button.addActionListener(e -> {
				System.out.println("DEBUG: role to be registered is " + role.toString());
				//setting the selected role (i.e. landlord)
				setRoleToBeRegistered(role);
				//removing the old frame after loging in
				userRoleFrame.dispose();
				//creating the login view to enter there login information
				loginView();
			});
			//adding the new button 
			p1.add(button);
		}
		//button to let regular renters see properties without loging in
		final JButton continueWithoutLoginButton = new JButton("Continue without loggin in (unregistered renter)");
		//litsener stup for the button
		continueWithoutLoginButton.addActionListener(e -> {
			System.out.println("DEBUG: continuing without logging in");
			//disposing the old frame after loging in
			userRoleFrame.dispose();
			//calling the renter view so they can start looking for properties
			new RenterView();
		});
		//adding the new button to let unregistered renters access the properties 
		p1.add(continueWithoutLoginButton);
		//removing the role selection list
		userRoleFrame.add(p1);
	}
	
	//setter function to define the role
	public void setRoleToBeRegistered(final User.UserRole roleToBeRegistered) {
		this.roleToBeRegistered = roleToBeRegistered;
	}
	
	//setting up the login view
	public void loginView(){
		//setting up the frame for the user to enter the login information 
		userRoleFrame = new JFrame("Login");
		userRoleFrame.setVisible(true);
		userRoleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userRoleFrame.setSize(500,500);
		//button to return to the previous screen
        final JButton goBack = new JButton("Go back");
		//setup for you to enter your accound information
		final JPanel basePanel = new JPanel();
		final JLabel emailLabel = new JLabel("Email:");
		final JLabel passwordLabel = new JLabel("Password:");
		final JTextField emailText = new JTextField(20);
		final JTextField passwordText = new JTextField(20);
		//buttons to submit the account information or regiseter a new account
		final JButton submitButton = new JButton("Submit");
		final JButton createNewAccountButton = new JButton("Create New Account");
		//adding all the new elements to the pannel
		basePanel.add(goBack);
		basePanel.add(emailLabel);
		basePanel.add(emailText);
		basePanel.add(passwordLabel);
		basePanel.add(passwordText);
		basePanel.add(submitButton);
		basePanel.add(createNewAccountButton);
		//adding the pannel to the frame
		userRoleFrame.add(basePanel);
		//setting up the litseners for teh submission button
		submitButton.addActionListener(e -> {
			if(!emailText.getText().equals("") && !passwordText.getText().equals("")){
				final String email = emailText.getText();
				final String password = passwordText.getText();
				//sending the information to the login controller
				loginController.login(email, password);
				//disposing of the old frame
				userRoleFrame.dispose();
				//transfering to the proper view for the type of user
				nextStep();
			}
			else{
				userRoleFrame.dispose();
				loginView();
			}
			
		});

		//creating the litsener for the new account button
		createNewAccountButton.addActionListener(e -> {
			if(!emailText.getText().equals("") && !passwordText.getText().equals("")){
				//getting the information from the desired text fields
				final String email = emailText.getText();
				final String password = passwordText.getText();
				//passing information to the login controller to setup the account
				loginController.register(email, password, roleToBeRegistered);
				//disposing of the old un-needed frame
				userRoleFrame.dispose();
				//transfering to the proper view for the type of user
				nextStep();
			}
			
			else{
				userRoleFrame.dispose();
				loginView();
			}
		});
		
		//button to go back to the role selection screen (frame that selects the type of login)
		goBack.addActionListener(e -> {
			//dsiposing of the curent frame
			userRoleFrame.dispose();
			//creating a new login selection 
			new LoginForm();
		});
	}
	
	//method to allow the user to enter the veiw needed for thier respective roles
	private void nextStep() {
		loginController.getCurrentUser().ifPresent(user -> {
			switch (user.getRole()) {
				case RENTER:
					new RenterView();
					break;
				case LANDLORD:
					new LandlordView();
					break;
				case MANAGER:
					new ManagerView();
					break;
			}
		});
	}
}

