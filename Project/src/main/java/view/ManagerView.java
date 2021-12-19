package view;
import controllers.DatabaseController;
import controllers.UserController;
import models.Landlord;
import models.Property;
import models.Renter;
import models.User;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Component;
import java.util.Objects;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.table.DefaultTableModel;
import java.util.Arrays;

//view for the manager 
class ManagerView {
	//initializing objects needed for the specfic controller classes
	final private UserController userController = UserController.getOnlyInstance();
	final private DatabaseController databaseController = DatabaseController.getOnlyInstance();
	//creating the frame used for the view
	JFrame managerViewFrame = null;
	//making it so the constructor instantly creates the new manager view
	public ManagerView(){
		main();
	}
	//main method used to create the veiw
	public void main(){
		//updating properties in the database
		databaseController.updateDatabase();
		//setting up the frame for manager
		managerViewFrame = new JFrame("Manager Options");
		managerViewFrame.setVisible(true);
		managerViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        managerViewFrame.setSize(500,500);
		//setting up the panel to host all the buttons needed
		JPanel panel = new JPanel();
		//setting up the buttons
		JButton createSummaryButton = new JButton("Create Summary");
		JButton getRenters = new JButton("Get Renters");
		JButton getLandlords = new JButton("Get Landlords");
		JButton getProperties = new JButton("Get Properties");
		JButton changeListingStatus = new JButton("Change Listing Status");
		JButton changeFeeAmount = new JButton("Change Fee Amount");
		JButton changeFeePeriod = new JButton("Change Fee Period");
		//adding the buttons 
		panel.add(createSummaryButton);
		panel.add(getRenters);
		panel.add(getLandlords);
		panel.add(getProperties);
		panel.add(changeListingStatus);
		panel.add(changeFeeAmount);
		panel.add(changeFeePeriod);
		//adding the pannel to the frame
		managerViewFrame.add(panel);
		//setting up the litseners for each button
		createSummaryButton.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of the old frame
					managerViewFrame.dispose();
					//creating the summery
					createSummary();
				}
			}
		);

		getRenters.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of the old frame
					managerViewFrame.dispose();
					//getting the list of renters
					getRenters();
				}
			}
		);

		getLandlords.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of the old frame
					managerViewFrame.dispose();
					//getting the list of landlords
					getLandlords();
				}
			}
		);

		getProperties.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of the old frame
					managerViewFrame.dispose();
					//getting the list of properties 
					getProperties();
				}
			}
		);

		changeListingStatus.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of the old frame
					managerViewFrame.dispose();
					//calling the method to change status
					changeListingStatus();
				}
			}
		);

		changeFeeAmount.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of the old frame
					managerViewFrame.dispose();
					//calling method ot change fee amount
					changeFeeAmount();
				}
			}
		);

		changeFeePeriod.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of the old frame
					managerViewFrame.dispose();
					//calling method to dispose of the old frame
					changeFeePeriod();
				}
			}
		);

	}

	//method to create the summery
	public void createSummary(){
		//resetting the frame for the summery
		managerViewFrame = new JFrame("Summary");
		managerViewFrame.setVisible(true);
		managerViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        managerViewFrame.setSize(500,500);
		//creating a new pannel to put everything on
		JPanel p1 = new JPanel();
		//label and text field for the peroid
		JLabel j1 = new JLabel("Period:");
		JTextField periodText = new JTextField(20);
		//adding the text field and peroid to the pannel
		p1.add(j1);
		p1.add(periodText);
		//new buttons to submit and go back
		JButton submit = new JButton("Submit");
		
		JButton goBack = new JButton("Go Back");
		//adding the buttons to the pannels
		p1.add(submit);
		p1.add(goBack);
		//adding the pannel to the frame
		managerViewFrame.add(p1);
		//litsener for the frame
		submit.addActionListener(
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e){
					//dsiposing of the old frame
					managerViewFrame.dispose();
					//resetting the frame
					managerViewFrame = new JFrame("Summary Results");
					managerViewFrame.setVisible(true);
					managerViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					managerViewFrame.setSize(500,500);
					try{
						//getting the peroid
						int period = Integer.parseInt(periodText.getText());
						//new pannel ot hold the information
						JPanel p2 = new JPanel();
						//labels for the information
						JLabel j2 = new JLabel("Number of Houses Listed in the last " + period + " days: " + String.valueOf(databaseController.numberOfHousesListed(period)));
						JLabel j3 = new JLabel("Number of Houses Rented in the last " + period + " days: " + String.valueOf(databaseController.numberOfHousesRented(period)));
						JLabel j4 = new JLabel("Total Number of Active Listings: "+ String.valueOf(databaseController.numberOfActiveListings(period)));
						JLabel j5 = new JLabel("Total Number of Houses Rented in the last " + period + " days: ");
						//getting information from the database
						DefaultTableModel tableModel = databaseController.getHousesRentedPeriod(period);
						//adding information to the table 
						JTable table = new JTable(tableModel);
						JScrollPane js = new JScrollPane(table);
						//alligning the label
						j2.setAlignmentX(Component.CENTER_ALIGNMENT);
						j3.setAlignmentX(Component.CENTER_ALIGNMENT);
						j4.setAlignmentX(Component.CENTER_ALIGNMENT);
						j5.setAlignmentX(Component.CENTER_ALIGNMENT);
						//adding information to the pannels
						p2.add(j2);
						p2.add(j3);
						p2.add(j4);
						p2.add(j5);
						p2.add(table);
						p2.add(js);
						//button to return to the prevouse screen
						JButton goBack2 = new JButton("Go Back");
						goBack2.setAlignmentX(Component.CENTER_ALIGNMENT);
						//adding the button to pannel
						p2.add(goBack);
						
						p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
						//adding pannel to the frame
						managerViewFrame.add(p2);
						//setting up the litsener for the goback button
						goBack2.addActionListener(
							new ActionListener() {

								public void actionPerformed(ActionEvent e){
									//dsiposing of the old frame 
									managerViewFrame.dispose();
									//returning to the orignal manager selection
									main();
								}
							}
						);
					//catching an exception	
					} catch(NumberFormatException f){
						System.out.println("Incorrect input");
						System.exit(1);
					}
					
				}
			}
		);

		//listener for the orignal go back button
		goBack.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//dsiposing of the old frame 
					managerViewFrame.dispose();
					//returning to the orignal manager selection
					main();
				}
			}
		);
		
	}

	//method to setup the renters information
	public void getRenters(){
		//resetting the manager frame to be used again
		managerViewFrame = new JFrame("All Renters");
		managerViewFrame.setVisible(true);
		managerViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        managerViewFrame.setSize(500,500);
		//creating a pannel to put all the information on
        JPanel p1 = new JPanel();
		//gatting information from the database
        DefaultTableModel tableModel = databaseController.getUsersOfRole(User.UserRole.RENTER);
		//creating a table with all the information on it
		JTable table = new JTable(tableModel);
		JScrollPane js = new JScrollPane(table);
		//adding the information to the pannel
		p1.add(table);
		p1.add(js);
		//creating a button to go back
		JButton j3 = new JButton("Go Back");
		p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
		//adding button to the pannel
		p1.add(j3);
		//adding button to the frame
		managerViewFrame.add(p1);
		//setting up a litsener for the go back button
		j3.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of the old frame
					managerViewFrame.dispose();
					//returning to the orignal manager selection
					main();
				}
			}
		);
	}

	//method for landlord information
	public void getLandlords(){
		//resetting the frame to be used for the landlords information
		managerViewFrame = new JFrame("All Landlords");
		managerViewFrame.setVisible(true);
		managerViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        managerViewFrame.setSize(500,500);
		//adding a new pannel to hold the infromation
        JPanel p1 = new JPanel();
		//getting the information from the database
        DefaultTableModel tableModel = databaseController.getUsersOfRole(User.UserRole.LANDLORD);
		//putting the information in a table
		JTable table = new JTable(tableModel);
		JScrollPane js = new JScrollPane(table);
		//adding table to the pannel
		p1.add(table);
		p1.add(js);
		//creating the button to go back
		JButton j3 = new JButton("Go Back");
		p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
		//adding the button to the pannel
		p1.add(j3);
		//adding the pannel to the frame
		managerViewFrame.add(p1);
		//setting up litsener for the go back button
		j3.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of the old frame
					managerViewFrame.dispose();
					//returing to the orignal manager selection
					main();
				}
			}
		);

	}

	//method for the information on properties 
	public void getProperties(){
		//resetting the frame to be used for the properties information
		managerViewFrame = new JFrame("All Properties");
		managerViewFrame.setVisible(true);
		managerViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        managerViewFrame.setSize(500,500);

		//getting the properties infromation from the database
		Collection<Property> properties = databaseController.getAllProperties();
		//creating a new pannel to hold the information
		JPanel p1 = new JPanel();
		//putting the properties in a table
		DefaultTableModel tableModel = Property.getTable(properties);
		
		JTable table = new JTable(tableModel);
		
		JScrollPane js = new JScrollPane(table);
		//adding the table to the pannel
		p1.add(table);
		p1.add(js);
		//creating a go back button
		JButton j3 = new JButton("Go Back");
		//adding button to the pannel
		p1.add(j3);
		//adding pannel to the frame
		managerViewFrame.add(p1);
		//creating the litsener for the go back button
		j3.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of the old frame
					managerViewFrame.dispose();
					//returing to the orignal manager selection
					main();
				}
			}
		);
	}
	
	//method for fee status
	public void changeListingStatus(){
		//resetting the frame to be used for fee statue
		managerViewFrame = new JFrame("Change Listing State");
		managerViewFrame.setVisible(true);
		managerViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        managerViewFrame.setSize(500,500);
		//getting information from the database
		Collection<Property> properties = databaseController.getAllProperties();
		//creating a new pannel to hold the information
		JPanel p1 = new JPanel();
		//adding information to a table
		DefaultTableModel tableModel = Property.getTable(properties);

		JTable table = new JTable(tableModel);
		
		JScrollPane js = new JScrollPane(table);
		//adding the table to the pannel
		p1.add(table);
		p1.add(js);
		//label for house ID
		JLabel j1 = new JLabel("HouseID:");
		//comboBox to hold IDs
		final JComboBox<Integer> propertyIdComboBox = new JComboBox<>();
		//addign all IDs to the propertyIdComboBox
		for (final Property property : properties) {
			propertyIdComboBox.addItem(property.getID());
		}
		//getting all the listing statuses
		JLabel j2 = new JLabel("New Listing State:");
		final JComboBox<Property.Status> propertyStatusComboBox = new JComboBox<>(filterPropertyStatusValues());
		//adding everything to the pannel
		p1.add(j1);
		p1.add(propertyIdComboBox);
		p1.add(j2);
		p1.add(propertyStatusComboBox);
		
		//buttons to submit and to go back
		JButton submit = new JButton("Submit");
		
		JButton goBack = new JButton("Go Back");
		//adding buttons to the pannel
		p1.add(submit);
		p1.add(goBack);
		//adding the pannel
		managerViewFrame.add(p1);
		//creating the submit litsener
		submit.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//Make sure property ID is not null and that the new status is not null
					if (!Objects.isNull(propertyIdComboBox.getSelectedItem()) && !Objects.isNull(propertyStatusComboBox.getSelectedItem())) {
						//getting the selected house and new status
						final int houseID = (int) propertyIdComboBox.getSelectedItem();
						final Property.Status newStatus = (Property.Status) propertyStatusComboBox.getSelectedItem();
						//updating the database with changes
						databaseController.changePropertyState(houseID, newStatus);

					}
					//disposing of old frame 
					managerViewFrame.dispose();
					//returning to the manager selection
					main();
				}
			}
		);

		//listener for the orignal button
		goBack.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of old frame 
					managerViewFrame.dispose();
					//returning to the manager selection
					main();
				}
			}
		);
	}

	//method for changing fee amount
	public void changeFeeAmount(){
		//resetting view for fee amount
		managerViewFrame = new JFrame("Fee Amount");
		managerViewFrame.setVisible(true);
		managerViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        managerViewFrame.setSize(500,500);
		//new pannel to hold information
		JPanel p1 = new JPanel();
		//label and text field for the new fee
		JLabel j1 = new JLabel("New Fee Amount:");
		JTextField feeText = new JTextField(20);
		//buttons to submit changes or go back
		JButton j2 = new JButton("Submit");
		JButton j3 = new JButton("Go Back");
		//adding everything to the pannels
		p1.add(j1);
		p1.add(feeText);
		p1.add(j2);
		p1.add(j3);
		//adding pannel to frame
		managerViewFrame.add(p1);
		//setting up litsener for submit button
		j2.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					try{
						//changing the fee in the database
						double fee = Double.parseDouble(feeText.getText());
						databaseController.setFeeAmount(fee);
					} catch (NumberFormatException f){
						System.out.println("Incorrect Input");
						System.exit(1);
					}
					
					//disposing of old frame
					managerViewFrame.dispose();
					//returning to the selection
					main();
				}
			}
		);
		//listener for go back button
		j3.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of old frame
					managerViewFrame.dispose();
					//returning to the selection
					main();
				}
			}
		);

	}

	//method to change the fee peroid
	public void changeFeePeriod(){
		//updating the frame for fee peroid
		managerViewFrame = new JFrame("Fee Period");
		managerViewFrame.setVisible(true);
		managerViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        managerViewFrame.setSize(500,500);
		//now pannel to hold information
		JPanel p1 = new JPanel();
		//textfield and lable to change the fee peroid
		JLabel j1 = new JLabel("New Fee Period:");
		JTextField periodText = new JTextField(20);
		//creating buttons to go back and submit changes
		JButton j2 = new JButton("Submit");
		JButton j3 = new JButton("Go Back");
		//adding everything to the pannel
		p1.add(j1);
		p1.add(periodText);
		p1.add(j2);
		p1.add(j3);
		//adding pannel to the frame
		managerViewFrame.add(p1);
		//litsener for the submission button
		j2.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					try{
						//updating the database with new fee peroid
						int period = Integer.parseInt(periodText.getText());
						databaseController.setFeePeriod(period);
					} catch (NumberFormatException f){
						System.out.println("Incorrect Input");
						System.exit(1);
					}

					//disposing of old frame
					managerViewFrame.dispose();
					//returning to the selection
					main();
				}
			}
		);
		//litsener for go back button
		j3.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//disposing of old frame
					managerViewFrame.dispose();
					//returning to the selection
					main();
				}
			}
		);
	}
	
	//Used to get all possible states that the landlord can change a property to
	private Property.Status[] filterPropertyStatusValues() {
		//All state except ACTIVE
		return Arrays.stream(Property.Status.values()).filter(status -> !Property.Status.ACTIVE.equals(status) && !Property.Status.UNPUBLISHED.equals(status)).toArray(Property.Status[]::new);
	}
}