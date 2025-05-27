<!DOCTYPE html>
<html lang="en">
<head>
    <title>
        Travel Application for ${view.travelerFullName} on
        ${view.datesOfTravel} has been edited
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
    Hi ${recipient.firstName},
</p>
<p>
    The Travel Application for ${view.travelerFullName} on ${view.datesOfTravel}
    has been <strong>edited</strong>.
</p>
<p>
    <a href="${domainUrl}/travel/application/${view.appId}">Click here</a> to view the current status of the travel application.
</p>
<br/>
<p>
    Thank you.
</p>
</body>
</html>
