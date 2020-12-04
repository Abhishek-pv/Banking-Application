# Simulating Load with Gatling for Testing

## Description
This directory holds the Gatling assets used to simulate load to test the services defined by this project.

Firstly, we perform a coverage test that injects a single user to touch all (but one) of the defined service. 
[insert img here]
Second, a scenario is simulated that touches a handful of the services for a duration of 30 minutes with 50 connections/users. This results in roughly 20 API calls / second.
[insert img here]

## Running the simulation
Both of the above tests are in the same script and only one can be run at a time. Navigate to the "./gatling-charts-highcharts-bundle-3.4.2/user-files/simulations/computerdatabase" directory and in the "BasicSimulation.scala" file follow the instructions to uncomment the line to code according to the test you want to simulate.

