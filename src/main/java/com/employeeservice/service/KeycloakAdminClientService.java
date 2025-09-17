package com.employeeservice.service;


import com.employeeservice.entity.Employee;
import com.employeeservice.entity.Department;
import jakarta.validation.Valid;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakAdminClientService {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;


    public void createKeycloakUser(Employee employee) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType("client_credentials")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(employee.getEmail());
        user.setFirstName(employee.getFirstName());
        user.setLastName(employee.getLastName());
        user.setEmail(employee.getEmail());

        // Map the database employee ID to a custom attribute in Keycloak
        user.setAttributes(Map.of("employeeId", List.of(employee.getId().toString())));


        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("admin123");
        user.setCredentials(Collections.singletonList(credential));

        // Create the user and check the response
        Response response = keycloak.realm(realm).users().create(user);

        int statusCode = response.getStatus();
        if (statusCode != 201) {
            String responseBody = response.readEntity(String.class);
            throw new RuntimeException("Failed to create user in Keycloak. Status: " + statusCode + ", Body: " + responseBody);
        }

        // Find the user we just created
        List<UserRepresentation> users = keycloak.realm(realm).users().search(employee.getEmail());
        if (users.isEmpty()) {
            throw new RuntimeException("Could not find newly created user in Keycloak: " + employee.getEmail());
        }
        UserRepresentation createdUser = users.get(0); // This is the correct variable

        // Find the "EMPLOYEE" role
        RoleRepresentation employeeRole = keycloak.realm(realm).roles().get("EMPLOYEE").toRepresentation();
        if (employeeRole == null) {
            throw new RuntimeException("Could not find 'EMPLOYEE' role in Keycloak realm.");
        }

        // Finally, assign the role to the user using the correct variable
        keycloak.realm(realm).users().get(createdUser.getId()).roles().realmLevel().add(List.of(employeeRole));
    }


    public void deleteKeycloakUser(String email) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType("client_credentials")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();

        List<UserRepresentation> users = keycloak.realm(realm).users().search(email);

        if (users.isEmpty()) {
            System.out.println("Could not find user in Keycloak: " + email + "Nothing to delete");
            return;
        }

        UserRepresentation user = users.get(0);
        keycloak.realm(realm).users().delete(user.getId());

    }

}