BondBroker
==========

## Operations
### Build Docker Image
* gradlew jibDockerBuild

Note: Make sure that the port 8080 is being exposed from Docker.

### Run in console
* gradlew bootRun

## Postman collection
See postman folder.

## Notes
* You can use local gradle binary ("gradle") instead of bundled gradle wrapper ("gradlew")

## Known issues
* JavaDocs missing on many places
* Lot of tests is missing
* Spring security is not used
* Database is not optimised
* 