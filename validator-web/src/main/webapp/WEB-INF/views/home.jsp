<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form action="<c:url value="/" />" method="post" enctype="multipart/form-data" style="margin: 50pt 0;">
    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <div class="input-group input-group-lg">
                <input type="file" name="file" class="form-control">
                <span class="input-group-btn">
                    <button class="btn btn-primary" type="submit">Validate!</button>
                </span>
            </div>
        </div>
    </div>
</form>

<p style="padding: 0 20pt;">Uploaded files are stored on the server for a limited time. This interface is not intended for automatic validation or testing, and such use will result in blocking of IP(s).</p>

<div class="jumbotron" style="margin: 20pt 0;">
    <h2>Supported standards</h2>
    <div class="row">
        <c:forEach items="${packages}" var="p">
            <div class="col-lg-4 col-md-4" style="padding-top: 5pt;">
                <c:if test="${p.url != null}"><a href="<c:out value="${p.url}" />"><c:out value="${p.value}" /></a></c:if>
                <c:if test="${p.url == null}"><c:out value="${p.value}" /></c:if>
            </div>
        </c:forEach>
    </div>
</div>