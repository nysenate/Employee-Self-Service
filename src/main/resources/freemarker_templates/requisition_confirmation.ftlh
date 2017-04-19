<!DOCTYPE html>
<html lang="en">
<body>
<p style="font-size: small;"><i>
*** This is an automated message.  Please do not reply to this email. ***
</i></p>
<br/>
<p>
Hi ${requisition.customer.firstName},
</p>
<p>
Your requisition request has been received by supply:
</p>

<div style="background-color: #efefef; padding: 10px; width: 500px; border-radius: 2px;">
<p>
<b>Id:</b> ${requisition.requisitionId}
<br/>
<b>Destination:</b> ${requisition.destination.locationDescription}
</p>

<p>
<table>
  <tr>
    <th>Item</td>
    <th>Quantity</td>
  </tr>
  <#list requisition.lineItems as lineItem>
  <tr>
    <td>${lineItem.item.description}</td>
    <td style="text-align: center; vertical-align: middle;">${lineItem.quantity}</td>
  </tr>
  </#list>
</table>
</p>
<br/>

<#if requisition.specialInstructions.isPresent()>
<p>
<b>Special Instructions:</b>
<br/>
${requisition.specialInstructions.get()}
</p>
</#if>
</div>
<br/>

<p>
Click <a href="${orderHistoryUrl}">here</a> to view the status of your requisition.
</p>

<p>
If necessary please contact Senate Supply with any questions. (518) 455-2361
</p>

<p>Thank you.</p>
</body>
</html>