<!DOCTYPE html>
<html lang="en">
<head>
    <title>
        Action needed! Departments in ESS are missing a department head
    </title>
</head>
<body>
<p>
    <i>
        *** This is an automated message. Please do not reply to this email. ***
    </i>
</p>
<br/>


<p>
    The following departments are missing a department head in the ess.department postgresql table.
    Employees in these departments may not be able to perform certain actions (i.e. submit a travel request)
    in ESS until the department head data is updated.
</p>

<p>
    <#list departments as department>
        <li>${department.name}</li>
    </#list>
</p>

<br/>
<p>
    Thank you.
</p>
</body>
</html>
