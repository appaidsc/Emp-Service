#package emp_service.authz
#import future.keywords.in
#
## Default: deny all
#default allow = false
#
## --- Allow Rules ---
#
## Rule 1: Director has full access to everything.
#allow if {
#    "DIRECTOR" in input.user.roles
#}
#
## Rule 2: HR, Accounts Manager, and Accounts Viewer can read any data.
#allow if {
#    input.method == "GET"
#    some role in {"HR", "ACCOUNTS_MANAGER", "ACCOUNTS_VIEWER"}
#    role in input.user.roles
#}
#
## --- Employee-Specific Rules ---
#
## Rule 3a: An Employee can view their own record.
#allow if {
#    "EMPLOYEE" in input.user.roles
#    input.method == "GET"
#    count(input.path) >= 4
#    input.path[3] == input.user.employeeId
#}
#
## Rule 3b: An Employee can update their own personal info.
#allow if {
#    "EMPLOYEE" in input.user.roles
#    is_personal_info_update
#    count(input.path) >= 4
#    input.path[3] == input.user.employeeId
#}
#
## --- HR-Specific Rules ---
#
## Rule 4a: HR can create new employees.
#allow if {
#    "HR" in input.user.roles
#    input.method == "POST"
#    is_employees
#}
#
## Rule 4b: HR can create new departments.
#allow if {
#    "HR" in input.user.roles
#    input.method == "POST"
#    is_departments
#}
#
## Rule 4c: HR can update an employee's personal info.
#allow if {
#    "HR" in input.user.roles
#    is_personal_info_update
#}
#
## Rule 4d: HR can assign an employee to a department.
#allow if {
#    "HR" in input.user.roles
#    is_department_assignment
#}
#
## --- Accounts-Specific Rules ---
#
## Rule 5: Accounts Manager can update salaries.
#allow if {
#    "ACCOUNTS_MANAGER" in input.user.roles
#    is_salary_update
#}
#
## --- Helpers ---
## Fixed path indices to match actual request structure
## For /api/v1/departments: path = ["api", "v1", "departments"]
## For /api/v1/employees: path = ["api", "v1", "employees"]
#is_employees if {
#    count(input.path) >= 3
#    input.path[0] == "api"
#    input.path[1] == "v1"
#    input.path[2] == "employees"
#}
#
#is_departments if {
#    count(input.path) >= 3
#    input.path[0] == "api"
#    input.path[1] == "v1"
#    input.path[2] == "departments"
#}
#
#is_personal_info_update if {
#    count(input.path) >= 5
#    input.path[4] == "personal-info"
#}
#
#is_salary_update if {
#    count(input.path) >= 5
#    input.path[4] == "salary"
#}
#
#is_department_assignment if {
#    count(input.path) >= 5
#    input.path[4] == "department"
#}


package emp_service.authz

import rego.v1

# Default: deny all
default allow := false

# --- Allow Rules ---

# Rule 1: Director has full access to everything.
allow if {
    "DIRECTOR" in input.user.roles
}

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
    count(input.path) >= 4
    input.path[3] == input.user.employeeId
}

# Rule 3b: An Employee can update their own personal info.
allow if {
    "EMPLOYEE" in input.user.roles
    is_personal_info_update
    count(input.path) >= 4
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
# Helper functions to identify request types
is_employees if {
    count(input.path) >= 3
    input.path[0] == "api"
    input.path[1] == "v1"
    input.path[2] == "employees"
}

is_departments if {
    count(input.path) >= 3
    input.path[0] == "api"
    input.path[1] == "v1"
    input.path[2] == "departments"
}

is_personal_info_update if {
    count(input.path) >= 5
    input.path[4] == "personal-info"
}

is_salary_update if {
    count(input.path) >= 5
    input.path[4] == "salary"
}

is_department_assignment if {
    count(input.path) >= 5
    input.path[4] == "department"
}

# --- Debug helpers (remove in production) ---
debug_info := {
    "user_roles": input.user.roles,
    "method": input.method,
    "path": input.path,
    "path_count": count(input.path),
    "is_departments": is_departments,
    "is_employees": is_employees
}