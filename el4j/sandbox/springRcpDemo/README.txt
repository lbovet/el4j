# To run the Spring RCP v.0.3 Demo you have to do the following

# Create the database and keep the Network Server running
mvn db:prepareDB db:start

# Open a second console and start the demo
mvn exec:java
