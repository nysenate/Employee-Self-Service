<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section>
  <div class="time-attendance-hero">
    <h2>Attendance History</h2>
  </div>

  <record-history link-to-entry-page></record-history>

  <div modal-container>
    <modal modal-id="record-details">
      <div record-detail-modal></div>
    </modal>
  </div>
</section>
