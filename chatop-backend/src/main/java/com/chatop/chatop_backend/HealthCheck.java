package com.chatop.chatop_backend;

/* 
 * This is a record class that represents the health check response.
 * The record class is a new feature in Java 14 that provides a compact syntax for declaring classes that are simple data carriers.
 * The record class is immutable and has a concise syntax for declaring its members.
 * The record class is a data carrier that is used to return the health check status and message.
 * The record class has two members: status and message.
 * The status member is of type ApplicationStatus, which is an enum that represents the application status.
 * The message member is of type String and represents the health check message.
 * The record class is used to return the health check response from the HealthCheckController.
 * The health check response contains the application status and message.
 * The health check response is returned as a JSON object with the status and message fields.
 * The health check response is used to verify that the application is running correctly.
 * The health check response is returned when the /healthcheck endpoint is called.
 */

 public record HealthCheck(ApplicationStatus status, String message) {
}
