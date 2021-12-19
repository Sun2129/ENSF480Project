package util;

import models.PropertyForm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

//Utility class for PropertyForm
public class PropertyFormUtil {
	
	//Filters a property form (provided by the user when they are searching for properties) for usage by the controllers
    public static String getPropertyFilterQuery(final PropertyForm propertyForm) {
		//Create a new StringBuilder which selects properties that are ACTIVE if executed in SQL
        final StringBuilder sb = new StringBuilder("Select * FROM property WHERE property_status = 'ACTIVE'");
        final Map<String, Object> columnMethodMap = new LinkedHashMap<>();
		
		//If the property type is not null...
        if (!Objects.isNull(propertyForm.getPropertyType())) {
			//Add it to the stringbuilder
            sb.append(String.format(" AND property_type = '%s'", propertyForm.getPropertyType()));
        }
		
		//If the city quadrant is not null...
        if (!Objects.isNull(propertyForm.getCityQuadrant())) {
			//Add it to the stringbuilder
            sb.append(String.format(" AND city_quadrant = '%s'", propertyForm.getCityQuadrant()));
        }
		
		//If the number of bathrooms is not null...
        if (!Objects.isNull(propertyForm.getNumberOfBathrooms())) {
			//Add it to the stringbuilder
            sb.append(String.format(" AND number_of_bathrooms = %d", propertyForm.getNumberOfBathrooms()));
        }
		
		//If the number of bedrooms is not null...
        if (!Objects.isNull(propertyForm.getNumberOfBedrooms())) {
			//Add it to the stringbuilder
            sb.append(String.format(" AND number_of_bedrooms = %d", propertyForm.getNumberOfBedrooms()));
        }
		
		//Convert the string builder to a string
        final String query = sb.toString();
        System.out.println("DEBUG: query = " + query);
		
		//Return the query to be performed in the database
        return query;
    }
}
