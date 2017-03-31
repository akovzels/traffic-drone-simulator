# traffic-drone-simulator

## How to configure
You can configure drones speed and distance to tube stations. 
dron.cruising.speed
dron.station.distance.threshold
Parameters could be configured in src/main/resources/config/application.yml before build or via command line.

## How to build
mvn clean package

## How to run
java -jar target/drone-simulator-0.0.1-SNAPSHOT.jar [-Ddron.cruising.speed=10] [-Ddron.station.distance.threshold=350]
