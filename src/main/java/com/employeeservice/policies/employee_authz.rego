package emp_service.authz
import future.keywords.in

# Default: deny all
default allow = false

# --- Allow Rules ---

# Director: full access
allow { "DIRECTOR" in input.user.roles }

# Read access for HR, Accounts Manager, Accounts Viewer
allow {
    input.method == "GET"
    some role in {"HR", "ACCOUNTS_MANAGER", "ACCOUNTS_VIEWER"}
    role in input.user.roles
}

# Employee can view/update their own record
allow {
    "EMPLOYEE" in input.user.roles
    input.path[3] == input.user.employeeId
    input.method == "GET" or is_personal_info_update
}

# HR can manage employee data
allow {
    "HR" in input.user.roles
    (input.method == "POST" and is_employees) or
    is_personal_info_update or
    is_department_assignment
}

# Accounts Manager can update salaries
allow {
    "ACCOUNTS_MANAGER" in input.user.roles
    is_salary_update
}

# --- Helpers ---
is_employees           { input.path[2] == "employees"; count(input.path) == 3 }
is_personal_info_update{ input.path[4] == "personal-info" }
is_salary_update       { input.path[4] == "salary" }
is_department_assignment{ input.path[4] == "department" }
