<!DOCTYPE html>
<html lang="en">
<#setting date_format="MM/dd/yy">
<head>
    <title>
        Disapproved Travel Application for ${app.getTraveler().getFullName()} on
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
    has been <strong>disapproved</strong> by ${disapprover.getFullName()}.
</p>

<p>
    The reason given for disapproval is:
</p>
<p>
    <strong>${reason}</strong>
</p>
<p>
    If possible, you may make changes to your application and re-submit it.
</p>
<br/>
<p>
    Thank you.
</p>
</body>
</html>
