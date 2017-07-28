<!DOCTYPE html>
<html lang="en">
  <head>
      <title>Time Record Manager Exceptions</title>
  </head>
  <body>
    <p>
      ${exceptions?size} error<#if exceptions?size gt 1 >s</#if>
      occurred during the last time record manager run.
    </p>
    <p>
      (Reported at ${reportTime})
    </p>
    <br>
    <#setting datetime_format="MM/dd/yy HH:mm:ss.SSS">
    <#list exceptions as ex>
      <hr>
      <br>
      <h1>#${ex?counter}</h1>
      <table cellpadding="2.5">
        <tr>
          <th>Occurred</th>
          <td>${ex.timestamp}</td>
        </tr>
        <tr>
          <th>Emp. Id</th>
          <td>${ex.employee.employeeId}</td>
        </tr>
        <tr>
          <th>Emp. Name</th>
          <td>${ex.employee.fullName}</td>
        </tr>
        <tr>
          <th>Emp. Email</th>
          <td>
              <a href="mailto:${ex.employee.email}">${ex.employee.email}</a>
          </td>
        </tr>
      </table>
      <br>
      <pre>${ex.stackTrace}</pre>
      <br>
    </#list>
    </body>
</html>
