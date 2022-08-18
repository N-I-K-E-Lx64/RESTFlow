# RESTFlow

Die folgenden Wiki-Links sind hilfreich für das Verständnis der Orchestration Engine:
- [Basic Example](https://github.com/N-I-K-E-Lx64/RESTFlow/wiki/Basic-Beispiel)

## Installation
### Benötigte Tools
- Git [Download](https://git-scm.com/downloads)
- Maven (mind. Version 3.6.1) [Download](https://maven.apache.org/download.cgi)
- Java 17
  SDK [Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

Da für die Durchführung des Beispiels einige REST Anfragen gesendet werden müssen, wird die Verwendung eines REST Clients wie [Postman](https://www.getpostman.com/) oder [Advanced REST Client](https://install.advancedrestclient.com/install) empfohlen.

1. Repository download: `git clone https://github.com/N-I-K-E-Lx64/RESTFlow.git`
2. Navigate to the project folder `cd .../RESTFlow`
3. Initialize the UI (git submodule) `git submodule init`
4. Update the UI `git submodule update`
5. Build the backend and the frontend `mvn spring-boot:run` *(this also starts the execution)*
