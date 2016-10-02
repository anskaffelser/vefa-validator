<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="v" tagdir="/WEB-INF/tags" %>

<script>
jQuery(function($) {
    $('.collapsable').click(function(e) { $(e.target).parent().toggleClass('details'); });
});
</script>

<div class="report">
    <v:flag value="${report.flag}" />
    <h1 class="title collapsable">
        <span class="glyphicon glyphicon-expand"></span>
        <span class="glyphicon glyphicon-collapse-down"></span>
        <c:out value="${report.title}" />
    </h1>

    <c:if test="${report.description != null}"><p><c:out value="${report.description}" /></p></c:if>

    <c:set var="baseURL" value="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, pageContext.request.contextPath)}" />

    <ul class="meta">
        <li><a href="<c:url value="/v/${identifier}/source" />"><span class="glyphicon glyphicon-save"></span> Download source</a></li>
        <c:if test="${report.runtime != null}"><li title="Total runtime"><span class="glyphicon glyphicon-time"></span> <c:out value="${report.runtime}" /></li></c:if>
        <c:if test="${report.configuration != null}"><li title="Configuration"><span class="glyphicon glyphicon-cog"></span> <c:out value="${report.configuration}" /></li></c:if>
        <c:if test="${report.build != null}"><li title="Build identifier"><span class="glyphicon glyphicon-tag"></span> <c:out value="${report.build}" /></li></c:if>
    </ul>

    <c:if test="${report.flag == 'OK'}">
        <div class="jumbotron">
            <h1>Congratulations</h1>
            <p>This validation didn't return any warnings or errors.</p>
            <p>Please keep in mind this is a technical validation. Valid documents must also be semantically correct.</p>
            <c:if test="${viewExists}">
            <p><a class="btn btn-primary btn-lg" href="<c:url value="/v/${identifier}/view" />" role="button">See document</a></p>
            </c:if>
        </div>
    </c:if>

    <c:forEach items="${report.section}" var="section">
    <div class="section status-<c:out value="${section.flag}" /><c:if test="${section.flag != 'OK' and section.flag != 'EXPECTED'}"> details</c:if>">
        <v:flag value="${section.flag}" />
        <div class="title<c:if test="${section.assertion.size() > 0}"> collapsable</c:if>">
            <c:if test="${section.assertion.size() > 0}">
                <span class="glyphicon glyphicon-expand"></span>
                <span class="glyphicon glyphicon-collapse-down"></span>
            </c:if>
            <c:if test="${section.assertion.size() == 0}">
                <span class="glyphicon glyphicon-unchecked"></span>
            </c:if>
            <c:out value="${section.title}" />
        </div>

        <ul class="meta">
            <c:if test="${section.runtime != null}"><li title="Runtime"><span class="glyphicon glyphicon-time"></span> <c:out value="${section.runtime}" /></li></c:if>
            <c:if test="${section.configuration != null}"><li title="Configuration"><span class="glyphicon glyphicon-cog"></span> <c:out value="${section.configuration}" /></li></c:if>
            <c:if test="${section.build != null}"><li title="Build identifier"><span class="glyphicon glyphicon-tag"></span> <c:out value="${section.build}" /></li></c:if>
        </ul>

        <c:forEach items="${section.assertion}" var="test">
        <div class="assertion">
            <v:flag value="${test.flag}" />
            <div class="title<c:if test="${test.location != null or test.test != null}"> collapsable</c:if>">
                <c:if test="${test.location != null or test.test != null}">
                    <span class="glyphicon glyphicon-expand"></span>
                    <span class="glyphicon glyphicon-collapse-down"></span>
                </c:if>
                <c:if test="${test.location == null and test.test == null}">
                    <span class="glyphicon glyphicon-unchecked"></span>
                </c:if>
                <c:if test="${test.infoUrl != null}">
                    <a href="${test.infoUrl}"><span class="identifier"><c:out value="${test.identifier}" /></span></a>
                </c:if>
                <c:if test="${test.infoUrl == null}">
                    <span class="identifier"><c:out value="${test.identifier}" /></span>
                </c:if>

                <c:out value="${test.textFriendly}" />
            </div>
            <c:if test="${test.location != null}"><div class="location"><v:location value="${test.location}" /></div></c:if>
            <c:if test="${test.test != null}"><div class="test"><strong>Test:</strong> <c:out value="${test.test}" /></div></c:if>
        </div>
        </c:forEach>
    </div>
    </c:forEach>
</div>