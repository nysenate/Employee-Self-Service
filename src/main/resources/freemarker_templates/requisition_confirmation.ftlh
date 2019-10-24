<!DOCTYPE html>
<html lang="en">
<body>
<p style="font-size: small;">
<i>*** This is an automated message.  Please do not reply to this email. ***</i>
</p>
<br/>
<p>
Hi ${requisition.customer.firstName},
</p>
<p>
Your requisition request has been received by Senate Supply.
</p>

<#if requisition.deliveryMethod.name() == "PICKUP">
  <p>
    Please pickup your order from LC100 at your earliest convenience.
  </p>
</#if>

<div style="background-color: #efefef; padding: 10px; width: 500px; border-radius: 2px;">
<p>
<b>Id:</b> ${(requisition.requisitionId)?c}
<br/>
<b>Destination:</b> ${requisition.destination.locationDescription}
</p>

<p>
<table>
<thead>
  <tr>
  <th>Item</th>
  <th>Quantity</th>
  </tr>
</thead>
<tbody>
  <#list requisition.lineItems as lineItem>
  <tr>
  <td>${lineItem.item.description}</td>
  <td style="text-align: center; vertical-align: middle;">${lineItem.quantity}</td>
  </tr>
  </#list>
</tbody>
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
Click <a href="${domainUrl}/supply/history/order-history">here</a> to view the status of your requisition.
</p>

<p>
If you have any questions, please contact Senate Supply at 518-455-2361.
</p>

<p>
Thank you.
</p>
</body>
</html>
