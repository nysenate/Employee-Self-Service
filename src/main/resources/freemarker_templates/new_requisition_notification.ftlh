<!DOCTYPE html>
<html lang="en">
<body>
<p style="font-size: small;"><i>
*** This is an automated message.  Please do not reply to this email. ***
</i></p>
<br/>

<p>
The following requisition has been submitted:
</p>

<div style="background-color: #efefef; padding: 10px; width: 500px; border-radius: 2px;">
<p>
<b>Id:</b> ${requisition.requisitionId}
<br/>
<b>Customer:</b> ${requisition.customer.firstName} ${requisition.customer.lastName}
<br/>
<b>Destination:</b> ${requisition.destination.locId.code} - ${requisition.destination.locationDescription}
<br/>
<b>Ordered:</b> ${orderedDateTime}
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
Click <a href="${domainUrl}/supply/manage/fulfillment?requisitionId=${requisition.requisitionId}">here</a> to view/edit this order.
<br/>
Click <a href="${domainUrl}/supply/manage/fulfillment">here</a> to view all pending orders.
</p>
<br/>

<p>Thank you.</p>
</body>
</html>