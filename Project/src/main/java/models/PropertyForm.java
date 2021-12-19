package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

//A class the emulates the property_form table in the database
//This class is what is recorded into the database for a registered renter
public class PropertyForm extends DatabaseModel {
    private static final String tableName = "property_form";
	//The type of property
    private Property.Type propertyType;
	//The number of bedrooms of a property
    private Integer numberOfBedrooms;
	//The number of bathrooms of a property
    private Integer numberOfBathrooms;
	//The city quadrant of a property
    private Property.CityQuadrant cityQuadrant;
	
	//Se the property type, number of bedrooms, number of bathrooms, and the city quadrant of this property form
    public PropertyForm(final Property.Type propertyType, final Integer numberOfBedrooms,
                        final Integer numberOfBathrooms, final Property.CityQuadrant cityQuadrant) {
        this.propertyType = propertyType;
        this.numberOfBedrooms = numberOfBedrooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.cityQuadrant = cityQuadrant;
    }

    public PropertyForm() { }
	
	//Get a property form from a result set (used with the result set returned from controllers)
    public static Optional<PropertyForm> createFromResultSet(final ResultSet rs) throws SQLException {
		//Get the property type
        final Property.Type type = Optional.ofNullable(rs.getString("property_type")).map(Property.Type::valueOf).orElse(null);
		//Get the number of bedrooms
        final Integer numberOfBedrooms = rs.getObject("number_of_bedrooms", Integer.class);
		//Get the number of bathrooms
        final Integer numberOfBathrooms = rs.getObject("number_of_bathrooms", Integer.class);
		//Get the city quadrant
        final Property.CityQuadrant quadrant = Optional.ofNullable(rs.getString("city_quadrant")).map(Property.CityQuadrant::valueOf).orElse(null);
		//Return property form in a container 
        return Optional.of(new PropertyForm(type, numberOfBedrooms, numberOfBathrooms, quadrant));
    }
	
	//Get property type
    public Property.Type getPropertyType() {
        return propertyType;
    }

	//Set property type
    public void setPropertyType(final Property.Type propertyType) {
        this.propertyType = propertyType;
    }

	//Get the number of bedrooms
    public Integer getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

	//Set the number of bedrooms
    public void setNumberOfBedrooms(final Integer numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

	//Get the number of bathrooms
    public Integer getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

	//Set the number of bathrooms
    public void setNumberOfBathrooms(final Integer numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

	//Get the city quadrant
    public Property.CityQuadrant getCityQuadrant() {
        return cityQuadrant;
    }

	//Set the city quadrant
    public void setCityQuadrant(final Property.CityQuadrant cityQuadrant) {
        this.cityQuadrant = cityQuadrant;
    }

	//Override the equals() method
    @Override
    public boolean equals(final Object o) {
		//Make sure this property form exists
        if (this == o) {
            return true;
        }
		//Make sure the other property form exists
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
		//Return true/false depending on whether the properties of both property forms are equal
        final PropertyForm that = (PropertyForm) o;
        return propertyType == that.propertyType &&
                Objects.equals(numberOfBedrooms, that.numberOfBedrooms) &&
                Objects.equals(numberOfBathrooms, that.numberOfBathrooms) &&
                cityQuadrant == that.cityQuadrant;
    }
	
	//Override the hashCode() method
    @Override
    public int hashCode() {
		//Return each of the properties of the property form
        return Objects.hash(propertyType, numberOfBedrooms, numberOfBathrooms, cityQuadrant);
    }
}
