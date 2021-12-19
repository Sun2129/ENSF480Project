# ENSF480Project

An online property management system with three types of users: Renters, Landlords, and Managers. 

Renters: Renters are split into two types of renters: registered renters and non-registered renters. Renters that are not registered are able to enter their search criteria and view properties that are posted by landlords that match their criteria. Subsequently, renters are able to e-mail landlords if they would like more information on the property or if they would like to rent the property. Registered renters have all of the functionality of non-registered renters, but are also given notifications everytime they login on properties that were posted since their last login that match their saved criteria.

Landlords: A landlord is able to register a new property and then pay to have it posted online for a certain period of time. They are also able to change the status of their properties from active to rented, suspended, or cancelled.

Managers: A manager is able to view all of the registered renters, landlords, and properties in the system. At the same time, they are also able to change the status of properties, change the fee that landlords have to pay to have their property posted, and change the amount of time that a property can be posted for after a landlord has paid for the property to be posted. Lastly, a manager is able to request a summary based off of a period provided by the manager. The summary returns the number of houses listed in the period, the number of houses rented in the last period, the total number of active listings, and a list of the properties rented in the period and their information.

This system was implemented in Java, it uses Java GUI, and uses a MySQL database to store information. The files in the system are managed, compiled, and executed using Maven. Lastly, it uses common design patterns, such as the MVC pattern and the Singleton pattern, to allow for the system to be easily changeable, scalable, and maintainable in the future.

To use the system, first import the SQL file to have the appropriate database for this project. Secondly, insert the appropriate database username and password, where the SQL file was installed, into the DatabaseConfig.java file. Thirdly, with Maven installed, run the command mvn package to install the appropriate dependencies and compile the program. Lastly, run the command mvn exec:java to run the program. 
