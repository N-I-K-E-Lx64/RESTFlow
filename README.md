# RESTFlow

## Installation
### Benötigte Tools
- Git [Download](https://git-scm.com/downloads)
- Maven (mind. Version 3.6.1) [Download](https://maven.apache.org/download.cgi)
- Java 12 SDK [Download](https://www.oracle.com/technetwork/java/javase/downloads/jdk12-downloads-5295953.html)

Da für die Durchführung des Beispiels einige REST Anfragen gesendet werden müssen, wird die Verwendung eines REST Clients wie [Postman](https://www.getpostman.com/) oder [Advanced REST Client](https://install.advancedrestclient.com/install) empfohlen.

1. Download des Repositories: `git clone https://github.com/N-I-K-E-Lx64/RESTFlow.git`
2. Mithilfe der Kommandozeile in den Projekt Ordner navigieren `cd .../RESTFlow`
3. Projekt compilieren: `mvn package` (Alle benötigten Dependencies werden automatisch von Maven heruntergeladen)

## Beispiel
1. Die Orchestration Engine und der zugehörige Wevserver können durch den Befehl `java -jar target/RESTFlow-0.0.1.jar` gestartet werden
2. In den Ordner des Market Webservices navigieren: `cd .../RESTFlow/examples/basic/webservices/MockWebservice-Market`
3. Webservice compilieren: `mvn package`
4. Market Webservice starten: `java -jar target/market-0.0.1-SNAPSHOT.jar`
5. In den Ordner des Order Webservices navigieren: `cd .../RESTFlow/examples/basic/webservices/MockWebservice-Order`
6. Webservice compilieren: `mvn package`
7. Order-Webservice starten: `java -jar target/order-0.0.1-SNAPSHOT.jar`

Die für die Durchführung des Beispiels benötigten Dateien, wie das Workflow Modell und die beiden RAML Dokumente, befinden sich innerhalb des `/examples/basic/files` Ordners.
