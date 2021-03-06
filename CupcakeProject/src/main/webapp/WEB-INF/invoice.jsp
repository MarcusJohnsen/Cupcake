<%-- 
    Document   : invoice
    Created on : 24-10-2019, 12:13:46
    Author     : Michael N. Korsgaard
--%>

<%@page import="logic.Admin"%>
<%@page import="logic.LineItem"%>
<%@page import="java.util.ArrayList"%>
<%@page import="logic.Invoice"%>
<%@page import="logic.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Invoices</title>
        <link rel="stylesheet" type="text/css" href="css/styleHeader.css">
        <style>
            body {
                background-image: url("decorations/linesBackground.png");
                background-repeat: repeat;
                background-attachment: fixed;
            }
        </style>
    </head>
    <body>
        <jsp:include page="/JSP-Parts/cupcake-Header.jsp"/>
        <%
            User user = (User) session.getAttribute("user");
            ArrayList<Invoice> invoiceList = null;
            int tableSize = 700;
            if (User.isUserCustomer(user)) {
                invoiceList = Invoice.createCustomerInvoicesFromDB(user.getEmail());

            } else if (User.isUserAdmin(user)) {
                Admin admin = (Admin) user;
                invoiceList = admin.getInvoices();  
                tableSize = 900;
            }
            String username = user.getUsername();
        %>

        <div class="circle" style="margin: 0 auto; width: 500px">
            <div class="circleFrame">
                <%if (User.isUserAdmin(user)) {%>
                <h1 align="center">All Invoices</h1>
                <%} else {%>
                <h1 align="center"><%=username%> Invoices</h1>
                <%}%>
            </div>
        </div>
        <br>
        <br>
        <table border = "1" align = "center" style="width:<%=tableSize%>px" bgcolor="fffef2">
            <thead>
                <tr bgcolor = "#87E187">
                    <th style="width:15%">Invoices</th>
                    <th style="width:55%">Details</th>
                    <th style="width:15%">Order Date</th>
                    <th style="width:15%">Total Price</th>
                        <%if (User.isUserAdmin(user)) {%>
                    <th style="width:15%">Customer</th>
                        <%}%>
                </tr>
            </thead>
            <tbody>
                <%
                    for (Invoice invoice : invoiceList) {
                        String id = Integer.toString(invoice.getInvoiceID());
                        String customer = invoice.getUser().getEmail();
                        String date = invoice.getDate();
                        String totalPrice = Double.toString(invoice.getTotalPrice());
                %>
                <tr>
                    <td align="center"><%=id%></td>
                    <td align="center">
                        <table style="width:100%" bgcolor="fffef2">
                            <tbody>
                                <%
                                    for (LineItem lineItem : invoice.getLineItems()) {
                                        String cupcakeDescription = lineItem.toString();
                                        int qty = lineItem.getAmount();
                                        double price = lineItem.getSubTotalPrice();
                                %>
                                <tr>
                                    <td align="right" style="width:20%"><%=qty%>x  </td>
                                    <td align="left" style="width:60%"><%=cupcakeDescription%></td>
                                    <td align="left" style="width:20%"><%=price%>,-</td>
                                </tr>
                                <%}%>
                            </tbody>
                        </table>
                    </td>
                    <td align="center"><%=date%></td>
                    <td align="center"><%=totalPrice%>,-</td>
                    <%if (User.isUserAdmin(user)) {%>
                    <td align="center"><%=customer%></td>
                    <%}%>
                </tr>
                <%}%>
            </tbody>
        </table>
        <br>
        <form action="FrontController" method="POST">
            <input type="hidden" name="command" value="goToJsp" />
            <input type="hidden" name="goToJsp" value="userPage" />
            <%if (User.isUserAdmin(user)) {%>
            <p align="center"> <input type="submit" value="Go back to control panel"/></p>
                <%} else {%>
            <p align="center"> <input type="submit" value="Go back to shop page"/></p>
                <%}%>
        </form>



    </body>
</html>
