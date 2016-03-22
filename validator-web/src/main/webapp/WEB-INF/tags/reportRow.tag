<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="v" tagdir="/WEB-INF/tags" %>

<%@ attribute name="value" required="true" type="no.difi.xsd.vefa.validator._1.Report" %>

<div class="report-row">
    <v:flag value="${value.flag}"/>
    <div class="title" data-uuid="<c:out value="${value.uuid}"/>">
        <span class="glyphicon glyphicon-expand"></span>
        <span class="glyphicon glyphicon-collapse-down"></span>
        <c:out value="${value.title}"/>
    </div>

    <ul class="meta">
        <c:if test="${value.runtime != null}"><li title="Total runtime"><span class="glyphicon glyphicon-time"></span> <c:out value="${value.runtime}" /></li></c:if>
        <c:if test="${views[value.uuid]}"><li><a href="<c:url value="/v/${identifier}/view/${value.uuid}" />"><span class="glyphicon glyphicon-paperclip"></span> See document</a></li></c:if>
        <c:if test="${value.filename != null}"><li title="Filename"><span class="glyphicon glyphicon-file"></span> <c:out value="${value.filename}" /></li></c:if>
        <c:if test="${value.configuration != null}"><li title="Configuration"><span class="glyphicon glyphicon-cog"></span> <c:out value="${value.configuration}" /></li></c:if>
        <c:if test="${value.build != null}"><li title="Build identifier"><span class="glyphicon glyphicon-tag"></span> <c:out value="${value.build}" /></li></c:if>
    </ul>

    <c:if test="${value.report != null}">
        <c:forEach items="${value.report}" var="r">
            <v:reportRow value="${r}"/>
        </c:forEach>
    </c:if>
</div>
