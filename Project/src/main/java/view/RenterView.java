package view;

import controllers.LoginController;
import controllers.PropertyController;
import models.Property;
import models.PropertyForm;
import models.Renter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.*;
import java.util.stream.Collectors;

//View of a renter
public class RenterView {
    private static final String BLANK = "";
	//Get this instance of the login of a user
    final private LoginController loginController = LoginController.getOnlyInstance();
	//Get this instance of the controller
    private final PropertyController propertyController = PropertyController.getOnlyInstance();
	
	//Property columns that should not be displayed to a renter
    final private static List<String> columnsToRemove = List.of("Fee Status", "Property Status", "Date Published", "Payment Date");
    private final JFrame baseFrame;
	
	//Main frame 
    public RenterView() {
		//Update the database
		propertyController.updateDatabase();
		
		//Frame renter view
        baseFrame = new JFrame("Renter view");
        baseFrame.setVisible(true);
        baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        baseFrame.setSize(500,500);
		
		//Create new panel to allow for disposing later
        final JPanel renterOptions = new JPanel();
		
		//Button for search properties
        final JButton searchListingsButtons = new JButton("Search for properties");
		//search property listing action listener
        searchListingsButtons.addActionListener(e -> {
			//Get rid of the panel on this frame
            baseFrame.setVisible(false);
			//Go to search property listing frame
            searchPropertyListing();
        });
		
		//Add the button to the panel
        renterOptions.add(searchListingsButtons);
		
		//Button for looking at notifications is only created if the the user is logged in
        if (loginController.isUserLoggedIn()) {
			//Button for looking at notifications
            final JButton seeNotifications = new JButton("See notifications");
			
			//Add the button
            renterOptions.add(seeNotifications);
			
			//See notifications Action Listener
            seeNotifications.addActionListener(e -> {
                baseFrame.setVisible(false);
                notificationView();
            });
        }
		
		//Add the panel to the frame
        baseFrame.add(renterOptions);
    }
	
	//Search property listing frame
    protected void searchPropertyListing() {
		//New Search property listing frame
        final JFrame renterViewFrame = new JFrame("Property Search Form");
        renterViewFrame.setVisible(true);
        renterViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        renterViewFrame.setSize(500,500);
		
		//New panel to allow for disposing later
        final JPanel panel = new JPanel();
		
		//Label to show user to enter criteria
        final JLabel infoLabel = new JLabel("Enter your search criteria, leave blank if necessary");
        final JLabel newLine = new JLabel("<html><br><br><p></p></html>");
		
		//Combo box for type of houses (fixed)
        final JLabel houseTypeLabel = new JLabel("House Type:");
        final JComboBox<String> houseTypeComboBox = new JComboBox<>(enumStringValues(Property.Type.values()));
		
		//Text field for number of bedrooms
        final JLabel numBedroomLabel = new JLabel("Number Of Bedrooms:");
        final JTextField numberOfBedroomsText = new JTextField(20);
		
		//Text field for number of bathrooms
        final JLabel numBathroomLabel = new JLabel("Number Of Bathrooms:");
        final JTextField numberOfBathroomsText = new JTextField(20);
		
		//Combo box for city quadrant (fixed)
        final JLabel cityQuadrantLabel = new JLabel("City quadrant:");
        final JComboBox<String> cityQuadrantComboBox = new JComboBox<>(enumStringValues(Property.CityQuadrant.values()));
		
		//Check box for recieving notifications only if user is logged in
        final JCheckBox receiveNotificationsForSearch = loginController.isUserLoggedIn()
                ? new JCheckBox("Receive notifications for this search?") : null;
		
		//If user is logged in, then display check box for notifications
        if (loginController.isUserLoggedIn()) {
            panel.add(receiveNotificationsForSearch);
        }
		
		//Submit button 
        final JButton submitButton = new JButton("Submit");
        final JButton goBack = new JButton("Go Back");
		
		//Add all components to the panel
        panel.add(infoLabel);
        panel.add(newLine);
        panel.add(houseTypeLabel);
        panel.add(houseTypeComboBox);
        panel.add(numBedroomLabel);
        panel.add(numberOfBedroomsText);
        panel.add(numBathroomLabel);
        panel.add(numberOfBathroomsText);
        panel.add(cityQuadrantLabel);
        panel.add(cityQuadrantComboBox);
		
		//Add the submit button and go back button to the panel
        panel.add(submitButton);
        panel.add(goBack);
		
		//Add the panel to the frame
        renterViewFrame.add(panel);
		
		//Submit button
        submitButton.addActionListener(e -> {
			//Create a new property form
            final PropertyForm propertyForm = new PropertyForm();
			
			//Get the house type and set it in the property form
            System.out.println("houseTypeComboBox = " + houseTypeComboBox.getSelectedItem());
            if (!Objects.equals(houseTypeComboBox.getSelectedItem(), BLANK)) {
                propertyForm.setPropertyType(Property.Type.valueOf((String) houseTypeComboBox.getSelectedItem()));
            }
			
			//Get the number of bedrooms and set it in the property form
            try {
                propertyForm.setNumberOfBedrooms(Integer.parseInt(numberOfBedroomsText.getText()));
            } catch (final NumberFormatException | NullPointerException ex) {
                System.out.println("DEBUG: numBedroom text was invalid or null");
            }
			
			//Get the number of bathrooms and set it in the property form
            try {
                propertyForm.setNumberOfBathrooms(Integer.parseInt(numberOfBathroomsText.getText()));
            } catch (final NumberFormatException | NullPointerException ex) {
                System.out.println("DEBUG: numBathroom text was invalid or null");
            }
			
			//Get the city quadrant and set it in the property form
            if (!Objects.equals(cityQuadrantComboBox.getSelectedItem(), BLANK)) {
                propertyForm.setCityQuadrant(Property.CityQuadrant.valueOf((String) cityQuadrantComboBox.getSelectedItem()));
            }
			
			//If the user is loggen in and the notification check box is selected...
            if (loginController.isUserLoggedIn() && receiveNotificationsForSearch != null && receiveNotificationsForSearch.isSelected()) {
				//Get the check box status and set it in the property form
                System.out.println("DEBUG: saving property form for user " + loginController.getCurrentUser());
                propertyController.savePropertyForm(propertyForm, (Renter) loginController.getCurrentUser().get());
            }
			
			//Dispose of the panel on this frame
            renterViewFrame.dispose();
			
			//Go to the property listing view
            propertyListingView(propertyForm);
        });
		
		//Go back action listener
        goBack.addActionListener(e -> {
			//Dispose the panel on this frame
            renterViewFrame.dispose();
			//Move to the base frame
            baseFrame.setVisible(true);
        });
    }
	
	//Property Listing View 
    protected void propertyListingView(final PropertyForm propertyForm) {
		//New Property Listing View 
        final JFrame propertyViewFrame = new JFrame("All Properties");
        propertyViewFrame.setVisible(true);
        propertyViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        propertyViewFrame.setSize(610,500);
		
		//Create a collection of properties that match the property form of the user and 
		//were published after the last login of the user
        final Collection<Property> properties = propertyController.getProperties(propertyForm);
        final JPanel panel = new JPanel();
		
		//Filter the table and add it to the panel
        addFilteredTable(properties, propertyViewFrame, panel);
		
		//Create a new email button
        final JButton emailButton = new JButton("Email landlord");
		
		//Add the button to the panel
        panel.add(emailButton);
		
		//Email button action listener
        emailButton.addActionListener(e -> {
            System.out.println("DEBUG: email button");
			//Dispose the panel on this frame
            propertyViewFrame.dispose();
			//Move to email view frame
            emailView();
        });
		
		//Add the panel to this frame
        propertyViewFrame.add(panel);
    }
	
	//Email view frame
    protected void emailView() {
		//Create new email view frame
        final JFrame emailViewFrame = new JFrame("Contact Landlord");
        emailViewFrame.setVisible(true);
        emailViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        emailViewFrame.setSize(500,500);
		
		//Create new panel to allow for disposing later
        final JPanel panel = new JPanel();
		
		//Label/Title
        final JLabel infoLabel = new JLabel("Interested in a property or have any further questions?");
        final JLabel newLine = new JLabel("<html><br><br><p></p></html>");
		
		//Text field for the ID of the property
        final JTextField id = new JTextField(20);
        final JLabel idLabel = new JLabel("Please enter the House ID of the property you are interested in:");
		
		//Message to be submitted
        final JTextField message = new JTextField(20);
        final JLabel messageLabel = new JLabel("Please enter your message:");
		
		//Email of the current user
        final JTextField email = new JTextField(20);
        final JLabel emailLabel = new JLabel("Please enter your email address:");
		
		//Submit button 
        final JButton submitButton = new JButton("Submit");
		
		//Add all the components to the panel
        panel.add(infoLabel);
        panel.add(newLine);
        panel.add(emailLabel);
        panel.add(email);
        panel.add(newLine);
        panel.add(idLabel);
        panel.add(id);
        panel.add(newLine);
        panel.add(messageLabel);
        panel.add(message);
        panel.add(submitButton);
		
		//Submit button action listener
        submitButton.addActionListener(z -> {
			//Check whether message and email are not empty
			if(!message.getText().equals("") && !email.getText().equals("")){
				System.out.println("DEBUG: email submittion button");
				//Dispose the panel on this frame
				emailViewFrame.dispose();
				//Move to confirmation view
				confirmationView(id, message);
			}
			else{
				//Dispose the panel on this frame
				emailViewFrame.dispose();
				//Move to the email view frame
				emailView();
			}
        });
		
		//Go back button
        final JButton goBackButton = new JButton("Go Back");
		
		//Add the button to the panel
        panel.add(goBackButton);
		
		//Go back action listener
        goBackButton.addActionListener(e -> {
			//Dispose the panel on this frame
            emailViewFrame.dispose();
			//Move to the base frame
            baseFrame.setVisible(true);
        });
		
		//Add the panel to this frame
        emailViewFrame.add(panel);
    }
	
	//Confirmation view
    protected void confirmationView(final JTextField id, final JTextField message) {
		Integer ID = null;
		
		//Get the id of the house and the message 
		try{
			ID = Integer.parseInt(id.getText());
		} catch(NumberFormatException f){
			System.out.println("Incorrect Input");
			System.exit(1);
		}
        final String msg = message.getText();
		
		//New Confirmation view
        final JFrame confirmationViewFrame = new JFrame("Confirmation");
        confirmationViewFrame.setVisible(true);
        confirmationViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        confirmationViewFrame.setSize(400,130);
		
		//New panel to allor for disposing later
        final JPanel panel = new JPanel();
		
		//Message sent confirmation
        final JLabel msgSentLabel = new JLabel("Message has been sent successfully to the landlord of House " + ID + "!");
        final JLabel newLine = new JLabel("<html><br><br><p></p></html>");
		
		//Add components to panel
        panel.add(newLine);
        panel.add(msgSentLabel);
        panel.add(newLine);
		
		//Go back button
        final JButton goBackButton = new JButton("Go Back");
		
		//Add the go back button to the panel
        panel.add(goBackButton);
		
		//Go back button action listener
        goBackButton.addActionListener(e -> {
			//Dispose of the panel on this frame
            confirmationViewFrame.dispose();
			//Move to the base frame
            baseFrame.setVisible(true);
        });
		
		//Add the panel to this frame
        confirmationViewFrame.add(panel);
    }
	
	//Notification view 
    protected void notificationView() {
		//New notification view
        final JFrame notificationFrame = new JFrame("Notifications");
        notificationFrame.setVisible(true);
        notificationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        notificationFrame.setSize(400, 130);
		
		//Get properties that the renter should be notified about
        final Collection<Property> properties = loginController.getNotificationsForRenter((Renter) loginController.getCurrentUser().get());
		
		//New panel to allow for disposing later
        final JPanel panel = new JPanel();
		
		//Add a filtered table to the panel
        addFilteredTable(properties, notificationFrame, panel);
		
		//Add the panel to this frame
        notificationFrame.add(panel);
    }
	
	//Create a filtered table that the user should be displayed
    private void addFilteredTable(final Collection<Property> properties, final JFrame frame, final JPanel panel) {
		//Get a table from the collection of properties
        final DefaultTableModel tableModel = Property.getTable(properties);
		
		//Create a new JTable to display
        final JTable propertyTable = new JTable(tableModel);
		
		//For every column that shouldn't be displayed to the user...
        for (final String col : columnsToRemove){
			//Create a new table column from the column we want to remove
            final TableColumn colFee = propertyTable.getColumn(col);
			//Remove the column from the JTable
            propertyTable.removeColumn(colFee);
        }
		
		//Add a scroll pane for the table
        final JScrollPane js = new JScrollPane(propertyTable);
		
		//Go back button
        final JButton goBackButton = new JButton("Go back");
		
		//Go back button action listener
        goBackButton.addActionListener(e -> {
			//DDispose the panel on this frame
            frame.dispose();
			//Move to the base frame
            baseFrame.setVisible(true);
        });
		
		//Add components to the panel
        panel.add(propertyTable);
        panel.add(js);
        panel.add(goBackButton);
    }
	
	//Convert ENUM values to an array of string values
    private static String[] enumStringValues(final Enum<?>[] enumValues) {
        final List<String> stringValues = new ArrayList<>();
        stringValues.add(BLANK);
        stringValues.addAll(Arrays.stream(enumValues).map(Enum::toString).collect(Collectors.toList()));
        return stringValues.toArray(new String[0]);
    }
}
