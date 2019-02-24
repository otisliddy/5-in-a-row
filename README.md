# 5 In A Row
A variation of the famous Connect Four game, intended to be played in 2 client web sessions, one for each player.

## Prerequisites
- Java 11
- Maven
- Google Chrome

## How to play
1. Start the application by one of the following methods. 
   * Package the application with ./mvnw clean package, then run java -jar target/5-in-a-row-0.0.1-SNAPSHOT.jar
   * Run ./mvnw spring-boot:run
   
   See class SystemProperties for possible program arguments. It is possible to change game parameters, for example:

1. In Google Chrome, open up two tabs or windows. In each, go to http://localhost:8080
1. In the first session, enter a name and press Start
1. In the second session, enter a name a press tab
1. It's the first player to move first. Enter a number and press Move.
1. Repeat the above step alternately for each player until game completion.
1. To play again at any time, open up two new sessions to http://localhost:8080, without hitting refresh on an existing session.
