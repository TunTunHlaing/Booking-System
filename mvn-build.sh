mvn clean install
mvn clean install -f ./booking-core-domain/pom.xml
mvn clean install -f ./booking-common/pom.xml

mvn clean install -f ./booking-auth/pom.xml
mvn clean install -f ./booking-package/pom.xml

mvn clean install -f ./booking-user/pom.xml
mvn clean install -f ./booking-application/pom.xml