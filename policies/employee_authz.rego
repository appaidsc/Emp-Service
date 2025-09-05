package emp_service.authz
import future.keywords.in

# Default: deny all
default allow = false

# --- Allow Rules ---

# Rule 1: Director has full access to everything.
allow if { "DIRECTOR" in input.user.roles }

# Rule 2: HR, Accounts Manager, and Accounts Viewer can read any data.
allow if {
    input.method == "GET"
    some role in {"HR", "ACCOUNTS_MANAGER", "ACCOUNTS_VIEWER"}
    role in input.user.roles
}

# --- Employee-Specific Rules ---

# Rule 3a: An Employee can view their own record.
allow if {
    "EMPLOYEE" in input.user.roles
    input.method == "GET"
    input.path[3] == input.user.employeeId
}

# Rule 3b: An Employee can update their own personal info.
allow if {
    "EMPLOYEE" in input.user.roles
    is_personal_info_update
    input.path[3] == input.user.employeeId
}

# --- HR-Specific Rules ---

# Rule 4a: HR can create new employees.
allow if {
    "HR" in input.user.roles
    input.method == "POST"
    is_employees
}

# Rule 4b: HR can create new departments.
allow if {
    "HR" in input.user.roles
    input.method == "POST"
    is_departments
}

# Rule 4c: HR can update an employee's personal info.
allow if {
    "HR" in input.user.roles
    is_personal_info_update
}

# Rule 4d: HR can assign an employee to a department.
allow if {
    "HR" in input.user.roles
    is_department_assignment
}

# --- Accounts-Specific Rules ---

# Rule 5: Accounts Manager can update salaries.
allow if {
    "ACCOUNTS_MANAGER" in input.user.roles
    is_salary_update
}


# --- Helpers ---
is_employees if { input.path[2] == "employees"; count(input.path) == 3 }
is_departments if { input.path[2] == "departments"; count(input.path) == 3 }
is_personal_info_update if { input.path[4] == "personal-info" }
is_salary_update if { input.path[4] == "salary" }
is_department_assignment if { input.path[4] == "department" }