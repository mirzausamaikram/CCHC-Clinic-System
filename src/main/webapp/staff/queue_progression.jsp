<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.cchc.model.QueueEntryBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    // simple session check
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    if (!"STAFF".equals(session.getAttribute("loginRole"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<QueueEntryBean> queueList = (List<QueueEntryBean>) request.getAttribute("queueList");
    String msg = (String) request.getAttribute("msg");
    Integer clinicId = (Integer) request.getAttribute("clinicId");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Queue Progression</title>
    </head>
    <body>
        <h2>Queue Progression</h2>

        <% if (msg != null) { %>
        <p><b><%= msg %></b></p>
        <% } %>

        <!-- simple queue management - call next button -->
        <form method="post" action="<%= request.getContextPath() %>/staff/queue-progression">
            <input type="hidden" name="action" value="callNext" />
            <input type="submit" value="Call Next" />
        </form>

        <br/>

        <!-- simple queue management - today queue table -->
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>Token No</th>
                <th>Status</th>
                <th>Priority</th>
                <th>Action</th>
            </tr>

            <% if (queueList != null && queueList.size() > 0) {
                for (int i = 0; i < queueList.size(); i++) {
                    QueueEntryBean q = queueList.get(i);
            %>
            <tr>
                <td><%= q.getTokenNo() %></td>
                <td><%= q.getQueueStatus() %></td>
                <td><%= q.getPriorityLevel() %></td>
                <td>
                    <% if (!"DONE".equals(q.getQueueStatus()) && !"MISSED".equals(q.getQueueStatus()) && !"CANCELLED".equals(q.getQueueStatus())) { %>

                    <form method="post" action="<%= request.getContextPath() %>/staff/queue-progression" style="display:inline">
                        <input type="hidden" name="action" value="skip" />
                        <input type="hidden" name="queueId" value="<%= q.getQueueId() %>" />
                        <input type="submit" value="Skip" />
                    </form>

                    <form method="post" action="<%= request.getContextPath() %>/staff/queue-progression" style="display:inline">
                        <input type="hidden" name="action" value="markServed" />
                        <input type="hidden" name="queueId" value="<%= q.getQueueId() %>" />
                        <input type="submit" value="Mark Done" />
                    </form>

                    <% } else { %>
                    -
                    <% } %>
                </td>
            </tr>
            <%  }
               } else { %>
            <tr>
                <td colspan="4">No entries in queue today</td>
            </tr>
            <% } %>
        </table>

        <p>
            <a href="<%= request.getContextPath() %>/staff/dashboard.jsp">Back to Dashboard</a> |
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </p>
    </body>
</html>
