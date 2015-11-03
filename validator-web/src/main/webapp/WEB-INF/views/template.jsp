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

    <link rel="stylesheet" href="<c:url value="/webjars/bootstrap/3.3.4/css/bootstrap.min.css" />" />

    <script src="<c:url value="/webjars/jquery/2.1.4/jquery.min.js" />"></script>

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
<script type="text/javascript">
    var pkBaseURL = (("https:" == document.location.protocol) ? "https://<c:out value="${piwikSite}" />/" : "http://<c:out value="${piwikSite}" />/");
    document.write(unescape("%3Cscript src='" + pkBaseURL + "piwik.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
    try {
        var piwikTracker = Piwik.getTracker(pkBaseURL + "piwik.php", <c:out value="${piwikId}" />);
        piwikTracker.trackPageView();
        pageTracker._addOrganic("google.no","q");
        pageTracker._addOrganic("abcsok.no","q");
        pageTracker._addOrganic("verden.abcsok.no","q");
        piwikTracker.enableLinkTracking();
    } catch( err ) {}
</script>
<noscript><p><img src="https://<c:out value="${piwikSite}" />/piwik.php?idsite=<c:out value="${piwikId}" />" style="border:0" alt="" /></p></noscript>
</c:if>

</body>
</html>