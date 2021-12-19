package view;
import controllers.LoginController;
import controllers.PropertyController;
import models.Landlord;
import models.Property;

import javax.swing.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.table.*;
import java.util.Arrays;

//View of a landlord
public class LandlordView {
	//Get this instance of the controller
	final private PropertyController propertyController = PropertyController.getOnlyInstance();
	//Get this instance of the login of a user
	final private LoginController loginController = LoginController.getOnlyInstance();
	JFrame baseFrame = null;
	Landlord landlord = null;
	
	//Constructor
	public LandlordView() {
		main();
	}
	
	//The main frame with all options present
	public void main() {
		landlord = null;
		
		//Make sure current user is landlord
		landlord = (Landlord) loginController.getCurrentUser()
				.filter(user -> user instanceof Landlord)
				.orElseThrow(() -> new IllegalStateException("Current user must be landlord."));
		
		//Update the database
		propertyController.updateDatabase();
		
		//Frame Online Application
		baseFrame = new JFrame("Online Application");
		baseFrame.setVisible(true);
		baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        baseFrame.setSize(500,500);
		
		//Add a new panel to allow for disposing later
		final JPanel panel = new JPanel();
		
		//Three Options: register property, pay fees, change listing status
		final JButton registerPropertyButton = new JButton("Register a Property");
		final JButton payFeesButton = new JButton("Pay Fees");
		final JButton changeListingStatusButton = new JButton("Change Listing Status");
		
		//Add all three buttons to panel
		panel.add(registerPropertyButton);
		panel.add(payFeesButton);
		panel.add(changeListingStatusButton);

		//Add panel to frame
		baseFrame.add(panel);
		
		//Add action listener to register property button
		registerPropertyButton.addActionListener(e -> {
			//Dispose the panel on this frame
			baseFrame.dispose();
			//Move to register property frame
			registerProperty();
		});
		
		//Add action listener to pay fee button
		payFeesButton.addActionListener(e -> {
			//Dispose the panel on this frame
			baseFrame.dispose();
			//Move to pay fee frame
			payFee(baseFrame);
		});

		changeListingStatusButton.addActionListener(e -> {
			//Dispose the panel on this frame
			baseFrame.dispose();
			//Move to change listing status frame
			changeListingStatus();
		});
	}
	
	//Register Property frame
	public void registerProperty(){
		//New frame register property
		baseFrame = new JFrame("Register Property");
		baseFrame.setVisible(true);
		baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        baseFrame.setSize(500,500);
		
		//New panel to allow for disposing later
		final JPanel p1 = new JPanel();
		
		//Information of a property needed to register it into database
		final JLabel j1 = new JLabel("Address:");
		final JLabel j2 = new JLabel("House Type:");
		final JLabel j3 = new JLabel("Number Of Bedrooms:");
		final JLabel j4 = new JLabel("Number Of Bathrooms:");
		final JLabel j5 = new JLabel("Furnished:");
		final JLabel j6 = new JLabel("City Quadrant:");
		
		//Text field for address
		final JTextField addressText = new JTextField(20);
		//Text field for number of bedrooms
		final JTextField numberOfBedroomsText = new JTextField(20);
		//Text field for number of bathrooms
		final JTextField numberOfBathroomsText = new JTextField(20);
		//Combo box for house type (since they are predetermined)
		final JComboBox<Property.Type> houseTypeComboBox = new JComboBox<>(Property.Type.values());
		//Combo box for city quadrant (since they are predetermined)
		final JComboBox<Property.CityQuadrant> cityQuadrantComboBox = new JComboBox<>(Property.CityQuadrant.values());
		//Check box for furnish status (since it is a yes or no)
		final JCheckBox isFurnishedCheckbox = new JCheckBox();
		
		//Submit button to submit form
		JButton j7 = new JButton("Submit");
		//Go back button to return to main 
		JButton j8 = new JButton("Go Back");
		
		//Add all labels, text fields, combo boxes, check boxes, and buttons to the panel
		p1.add(j1);
		p1.add(addressText);
		p1.add(j2);
		p1.add(houseTypeComboBox);
		p1.add(j3);
		p1.add(numberOfBedroomsText);
		p1.add(j4);
		p1.add(numberOfBathroomsText);
		p1.add(j5);
		p1.add(isFurnishedCheckbox);
		p1.add(j6);
		p1.add(cityQuadrantComboBox);
		p1.add(j7);
		p1.add(j8);
		
		//Add panel to frame
		baseFrame.add(p1);
		
		//Submit button action listener
		j7.addActionListener(e -> {
			if(!addressText.getText().equals("")){
				//Get all information entered as text or selected in combo box or check box
				final String address = addressText.getText();
				final Property.Type houseType = (Property.Type) houseTypeComboBox.getSelectedItem();
				final String numberOfBedrooms = numberOfBedroomsText.getText();
				final String numberOfBathrooms = numberOfBathroomsText.getText();
				final boolean isFurnished = isFurnishedCheckbox.isSelected();
				final Property.CityQuadrant cityQuadrant = (Property.CityQuadrant) cityQuadrantComboBox.getSelectedItem();
				
				
				//Try to convert string entered into text field into integer (for bathrooms and bedrooms)
				try{
					//Convert to int
					final int bedrooms = Integer.parseInt(numberOfBedrooms);
					final int bathrooms = Integer.parseInt(numberOfBathrooms);
					
					//Upload property using the above information gained from user
					propertyController.uploadProperty(new Property(address, houseType, bedrooms,
							bathrooms, isFurnished, cityQuadrant,
							loginController.getCurrentUser().get().getEmail(), false,
							Property.Status.UNPUBLISHED, null, null));

				} catch (NumberFormatException f){
					System.out.println("Incorrect Input");
					System.exit(1);
				}
				
				//Dispose the panel on this frame
				baseFrame.dispose();
				//Return to main
				main();
			}
			else{
				//Dispose the panel on this frame
				baseFrame.dispose();
				//Return to main
				main();
			}
		});
		
		//Go back button
		j8.addActionListener(e -> {
			//Dispose the panel on this frame
			baseFrame.dispose();
			//Return to main
			main();
		});
	}
	
	//Pay Fee frame
	public void payFee(JFrame oldFrame){
		//New frame Pay Fee
		baseFrame = new JFrame("Pay Fee");
		baseFrame.setVisible(true);
		baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        baseFrame.setSize(900,500);
		
		//First, display all properties that have not been paid for yet 
		final Collection<Property> properties = propertyController.getPaymentProperties(landlord);
		final double paymentFee = propertyController.getPropertyPaymentFee();
		final JPanel p1 = new JPanel();
		
		//Create a new table that is filled with properties that haven't been paid for yet
		final DefaultTableModel tableModel = Property.getTable(properties);
		
		//Label for Fee
		final JLabel requiredFeeLabel = new JLabel("Required fee is $" + paymentFee);
		p1.add(requiredFeeLabel);
		
		//JTable that displays the table that was created previously
		final JTable table = new JTable(tableModel);
		
		//Scroll pane for table
		final JScrollPane js = new JScrollPane(table);
		
		//Add both table and scroll pane to the panel
		p1.add(table);
		p1.add(js);
		
		//Combo Box for property ID's that haven't been paid for
		final JLabel j1 = new JLabel("HouseID:");
		final JComboBox<Integer> propertyIdComboBox = new JComboBox<>();
		
		//For every property that hasn't been paid for, add it to the combo box
		for (final Property property : properties) {
			propertyIdComboBox.addItem(property.getID());
		}
		
		//Check box for whether the landlord is actually paying 
		final JCheckBox payingFeeCheck = new JCheckBox(String.format("Paying $%.2f in fees", paymentFee));
		
		//Add all components to the panel
		p1.add(j1);
		p1.add(propertyIdComboBox);
		p1.add(payingFeeCheck);
		
		//Submit button
		final JButton submit = new JButton("Submit");
		//Return button
		final JButton goBack = new JButton("Go Back");
		
		//Add submit and return button to panel
		p1.add(submit);
		p1.add(goBack);
		
		//Add the panel to the frame
		baseFrame.add(p1);
		
		//Submit button action listener
		submit.addActionListener(e -> {
			//Get the property ID that the user selected to pay for
			final Integer propertyId = (Integer) propertyIdComboBox.getSelectedItem();
			
			//Make sure property ID is not null and that the pay fee check box is selected
			if (!Objects.isNull(propertyId) && payingFeeCheck.isSelected()) {
				//Make sure that the property ID matches one of the properties not yet paid for
				final Property propertyToPublish = properties.stream().filter(p -> p.getID() == propertyId).findFirst().orElse(null);
				
				//Make sure that the property found through property ID is not null itself
				if (!Objects.isNull(propertyToPublish)) {
					//Publish this property 
					propertyController.publishProperty(propertyToPublish);
				}
			}
			//Dispose the panel on this frame
			baseFrame.dispose();
			//Return to main
			main();
		});
		
		//Go back button action listener
		goBack.addActionListener((e) -> {
			//Dispose the panel on this frame
			baseFrame.dispose();
			//Return to main
			main();
		});

	}
	
	//Change Listing Status frame
	public void changeListingStatus(){
		//New Change Listing Status frame
		baseFrame = new JFrame("Change Listing State");
		baseFrame.setVisible(true);
		baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        baseFrame.setSize(500,500);
		
		//Get all properties of a landlord 
		Collection<Property> properties = propertyController.getAllProperties(landlord);
		
		//Create a new panel
		JPanel p1 = new JPanel();
		
		//Get a table that has all properties of a landlord
		DefaultTableModel tableModel = Property.getTable(properties);
		
		//Create a new JTable to display this table
		JTable table = new JTable(tableModel);
		
		//Add a new scroll pane for the table
		JScrollPane js = new JScrollPane(table);
	
		//Add the table and the scroll pane to the panel
		p1.add(table);
		p1.add(js);
		
		//Combo box for property ID
		JLabel j1 = new JLabel("HouseID:");
		final JComboBox<Integer> propertyIdComboBox = new JComboBox<>();
	
		//Add all properties of a landlord
		for (final Property property : properties) {
			propertyIdComboBox.addItem(property.getID());
		}
		
		//Combo box for property status that is filled with all possible statuses it could be changed to 
		final JLabel j2 = new JLabel("New Listing State:");
		final JComboBox<Property.Status> propertyStatusComboBox = new JComboBox<>(filterPropertyStatusValues());
		
		//Add property ID and property status combo box to panel
		p1.add(j1);
		p1.add(propertyIdComboBox);
		p1.add(j2);
		p1.add(propertyStatusComboBox);
		
		//Submit button
		JButton submit = new JButton("Submit");
		
		//Go back button
		JButton goBack = new JButton("Go Back");
		
		//Add submit and go back button to panel
		p1.add(submit);
		p1.add(goBack);
		
		//Add panel to frame
		baseFrame.add(p1);
		
		//Submit button action listener
		submit.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					
					
					//Make sure property ID is not null and that the new status is not null
					if (!Objects.isNull(propertyIdComboBox.getSelectedItem()) && !Objects.isNull(propertyStatusComboBox.getSelectedItem())) {
						//Get the property ID selected and the property state selected
						final int houseID = (int) propertyIdComboBox.getSelectedItem();
						final Property.Status newStatus = (Property.Status) propertyStatusComboBox.getSelectedItem();
						
						//Change the status of this property
						propertyController.changePropertyState(houseID, newStatus, landlord);
						
					}
					
					//Dispose the panel on this frame
					baseFrame.dispose();
					//Return to main
					main();
				}
			}
		);
		
		//Go back action listener
		goBack.addActionListener(
			new ActionListener() {

				public void actionPerformed(ActionEvent e){
					//Dispose the panel on this frame
					baseFrame.dispose();
					//Return to main
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