<table cellpadding="5">
  <tr>
    <th>Reported By</td>
    <td>${employee.fullName}</td>
  </tr>
  <tr>
    <th>Email</td>
    <td>
      <a href="mailto:${employee.email}">${employee.email}</a>
    </td>
  </tr>
  <tr>
    <th>Phone</td>
    <td>${employee.workPhone}</td>
  </tr>
  <tr>
    <th>Report Time</td>
    <td>${timestamp}</td>
  </tr>
  <tr>
    <th>URL</th>
    <td><a href="${errorReport.url}">${errorReport.url}</a></td>
  </tr>
</table>

<h3>Details</h3>
<pre style="background-color: #fafafa; border: 1px solid #e2e2e2; padding: 0.7em;">${details}</pre>