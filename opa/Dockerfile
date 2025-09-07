# Use the standard OPA image - this includes shell utilities
FROM openpolicyagent/opa:latest

# Copy your policy file directly into the policies directory
COPY ./opa/employee_authz.rego /policies/

# Expose port 8181
EXPOSE 8181