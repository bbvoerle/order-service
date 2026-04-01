
================================================================================
                          Order Service Application
================================================================================
================================================================================




========================================
Anleitung zum Starten der Anwendung
========================================

- Einrichtung der PostgreSQL-Datenbank:
  Anlegen von Benutzer und Datenbank:

   Über ein SQL-Client-Tool (z. B. psql, DBeaver, pgAdmin) folgende Befehle ausführen:

   CREATE USER ordersuser WITH PASSWORD 'orderspw';
   CREATE DATABASE ordersdb;
   ALTER DATABASE ordersdb OWNER TO ordersuser;

   Die Tabellen werden beim ersten Start der Anwendung automatisch
   durch Hibernate erzeugt.

- Anwendung starten über IntelliJ (Run/Debug) oder:
  mvn spring-boot:run

- Beispiel Order POST Request:
  curl -X POST http://localhost:8080/orders \
    -H "Content-Type: application/json" \
    -d '{"customerId":"12345","amount":99.99}'

- Beispiel GET Orders Request:
    in Browser: http://localhost:8080/orders


========================================
Architekturüberblick
========================================

Die Anwendung besteht aus folgenden Komponenten:

- REST Controller zur Entgegennahme von Aufträgen
- Service-Schicht für Geschäftslogik
- Persistenz via Spring Data JPA
- Order-Tabelle für eingehende Bestellungen
- MessageOut-Tabelle für asynchrone Verarbeitung
- Scheduler und JMS Producer zum Versenden von Nachrichten in die Queue
- JMS Listener zum Lesen aus der Queue und Weiterverarbeitung

Ablauf (Happy Path):
REST Controller
-> OrderService
-> OrderRepository&MessageOutRepository
-> MessageOutScheduler
-> OrderMessageProducer&MessageOutRepository
-> (order-created queue)
-> OrderMessageListener
-> OrderRepository


========================================
Wichtige technische Entscheidungen
========================================

- Verwendung einer klassischen Schichtenarchitektur (Controller, Service, Repository)
- Nutzung von DTOs zur Trennung von API und Persistenzmodell
- Order und MessageOut werden innerhalb einer einzigen Transaktion gespeichert, um die Atomizität zu gewährleisten
- Einsatz von JMS für asynchrone Kommunikation
- Verwendung eines Outbox-Patterns (MessageOut, MessageOutScheduler) zur zuverlässigen Nachrichtenverarbeitung, derzeit ohne automatische Retry-Logik
- Die Verarbeitung erfolgt nach dem Prinzip der eventual consistency:
  Der Auftrag wird synchron gespeichert, die Weiterverarbeitung erfolgt asynchron einen Scheduler (Cron Job) sowie Messaging über JMS.
- Separate Enums für den Orderstatus (CREATED, PROCESSED) sowie für den MessageOut-Status (NEW, SENT, FAILED)
- @RestControllerAdvice (GlobalExceptionHandler) zur Behandlung von Datenbankfehlern bei den REST-Calls.


========================================
Fehlerbehandlung
========================================

- Datenbankfehler werden über eine globale Exception-Behandlung (@ControllerAdvice) abgefangen
- Messaging-Fehler werden nicht im REST-Call, sondern im Scheduler behandelt
- Wenn das Schreiben in die Queue fehlschlägt, wird der Eintrag als FAILED markiert und nicht erneut automatisch versendet.
  Eine produktionsnahe Erweiterung wäre die Implementierung einer at-least-once Delivery durch Retry-Strategien.
- Idempotente Verarbeitung im OrderMessageListener (status == PROCESSED => keine Aktion) verhindert Probleme bei mehrfacher Zustellung


========================================
Annahmen
========================================

- Vereinfachte Verarbeitung innerhalb einer einzelnen Anwendung
- Keine Authentifizierung oder Autorisierung implementiert
- Fokus auf technische Struktur und nicht auf vollständige Fachlogik
- Keine automatische Retry-Logik bei Messaging-Fehlern; stattdessen wird der Status auf FAILED gesetzt


========================================
Mögliche Erweiterungen für den Produktionseinsatz
========================================

- Externer Message Broker statt eingebettetem ActiveMQ
- Implementierung von Retry-Strategien und Dead-Letter-Queues
- Einsatz von Flyway oder Liquibase für Datenbankmigrationen
- Monitoring und Metriken (z. B. Spring Actuator, Prometheus)
- Containerisierung mit Docker und Orchestrierung mit Kubernetes
- Trennung REST-Applikation, Messageproducer (job) und Queuelistener.
