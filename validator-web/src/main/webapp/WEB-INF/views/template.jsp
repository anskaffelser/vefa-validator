<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<!DOCTYPE html>
<html>
<head>

    <title>VEFA Validator</title>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" href="<c:url value="/webjars/bootstrap/3.3.6/css/bootstrap.min.css" />" />
    <link rel="stylesheet" href="<c:url value="/style.css" />" />

    <script src="<c:url value="/webjars/jquery/2.2.4/jquery.min.js" />"></script>

</head>
<body<c:if test="${ngController != null}"> ng-controller="<c:out value="${ngController}" />"</c:if>>

<nav class="navbar navbar-default">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="<c:url value="/" />">VEFA Validator</a>
        </div>
    </div>
</nav>

<div class="container">

    <t:insertAttribute name="main" />

</div>

<c:if test="${piwikSite != null}">
<script async src="https://www.googletagmanager.com/gtag/js?id=UA-12685001-24"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());

  gtag('config', 'UA-12685001-24');
</script>
</c:if>

</body>
</html>