<!DOCTYPE html>
<html lang="en">
<#setting date_format="MM/dd/yy">
<head>
    <title>
        Approved Travel Application for ${app.getTraveler().getFullName()} on
        ${app.activeAmendment().route().startDate()}
        <#if !app.activeAmendment().route().startDate().equals(app.activeAmendment().route().endDate())>
            - ${app.activeAmendment().route().endDate()}
        </#if>
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
    Hi ${toEmployee.firstName},
</p>
<p>
    The Travel Application for ${app.getTraveler().getFullName()} on
    ${app.activeAmendment().route().startDate()}
    <#if !app.activeAmendment().route().startDate().equals(app.activeAmendment().route().endDate())>
        - ${app.activeAmendment().route().endDate()}
    </#if>
    has been <strong>approved</strong>.
</p>
<br/>
<p>
    Thank you.
</p>
</body>
</html>
