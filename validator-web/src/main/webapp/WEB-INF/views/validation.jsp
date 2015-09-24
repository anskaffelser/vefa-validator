<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style>
    div.status { float: right; text-align: center; width: 80pt; padding: 0.7pt 0; }
    div.status.status-SUCCESS { background-color: #dff0d8; color: #3c763d; }
    div.status.status-OK { background-color: #dff0d8; color: #3c763d; }
    div.status.status-EXPECTED { background-color: #d9edf7; color: #31708f; }
    div.status.status-WARNING { background-color: #fcf8e3; color: #8a6d3b; }
    div.status.status-ERROR { background-color: #f2dede; color: #a94442; }
    div.status.status-FATAL { background-color: #f2dede; color: #a94442; font-weight: bold; }
    div.report > div.status { font-size: 20pt; width: 180pt; padding: 10pt 0; }
    div.jumbotron { margin: 20pt 0; }
    div.report.details div.jumbotron { display: none; }
    div.section { border-top: 1px solid #ccc; margin: 5pt 0; padding: 5pt 0;}
    div.section.status-OK, div.section.status-EXPECTED { display: none; }
    div.report.details div.section { display: block; }
    div.section > .title { font-size: 15pt; margin: 8pt 0 5pt; }
    div.section > div.status { font-size: 15pt; width: 125pt; padding: 5pt 0; }
    span.identifier { font-weight: bold; }
    div.assertion { border-top: 1px solid #eee; margin: 2pt 0; padding: 2pt 0; display: none; }
    div.assertion:nth-child(2) { background-color: #eee; }
    div.section.details div.assertion { display: block; }
    div.assertion > div.location { display: none; margin-top: 5pt; }
    div.assertion > div.test { display: none; margin-top: 5pt; }
    div.assertion.details > div { display: block; }
    .collapsable { cursor: pointer; }
    ul.meta { margin: 0; padding: 0; }
    ul.meta li { list-style-type: none; display: inline; margin-right: 10pt; color: #999; }
    span.glyphicon-collapse-down { display: none; }
    .details > .title > span.glyphicon-collapse-down { display: inline; }
    .details > .title > span.glyphicon-expand { display: none; }
</style>
<script>
    jQuery(function($) {
        $('.collapsable').click(function(e) { $(e.target).parent().toggleClass('details'); });
    });
</script>

<div class="report">
    <div class="status status-<c:out value="${report.flag}" />"><span class="glyphicon <c:out value="${report.flag == 'OK' or report.flag == 'EXPECTED' ? 'glyphicon-ok' : 'glyphicon-remove'}" />"></span> <c:out value="${report.flag}" /></div>
    <h1 class="title collapsable">
        <span class="glyphicon glyphicon-expand"></span>
        <span class="glyphicon glyphicon-collapse-down"></span>
        <c:out value="${report.title}" />
    </h1>

    <c:if test="${report.description != null}"><p><c:out value="${report.description}" /></p></c:if>

    <c:set var="baseURL" value="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, pageContext.request.contextPath)}" />

    <ul class="meta">
        <li><a href="<c:url value="/v/${identifier}/source" />"><span class="glyphicon glyphicon-save"></span> Download source</a></li>
        <!-- <li><a href="#" id="copy" data-clipboard-text="<c:out value="${baseURL}" /><c:url value="/v/${identifier}" />"><span class="glyphicon glyphicon-copy"></span> Copy link</a></li> -->
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
        <div class="status status-<c:out value="${section.flag}" />"><span class="glyphicon <c:out value="${section.flag == 'OK' or section.flag == 'EXPECTED' ? 'glyphicon-ok' : 'glyphicon-remove'}" />"></span> <c:out value="${section.flag}" /></div>
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
            <div class="status status-<c:out value="${test.flag}" />"><span class="glyphicon <c:out value="${test.flag == 'OK' or test.flag == 'EXPECTED' ? 'glyphicon-ok' : 'glyphicon-remove'}" />"></span> <c:out value="${test.flag}" /></div>
            <div class="title<c:if test="${test.location != null or test.test != null}"> collapsable</c:if>">
                <c:if test="${test.location != null or test.test != null}">
                    <span class="glyphicon glyphicon-expand"></span>
                    <span class="glyphicon glyphicon-collapse-down"></span>
                </c:if>
                <c:if test="${test.location == null and test.test == null}">
                    <span class="glyphicon glyphicon-unchecked"></span>
                </c:if>
                <span class="identifier"><c:out value="${test.identifier}" /></span>
                <c:out value="${test.text}" />
            </div>
            <c:if test="${test.location != null}"><div class="location">
                <c:forEach var="e" items="${fn:split(test.location, '/')}">
                    <c:if test="${fn:startsWith(e, '@') == false}">
                        <c:set var="f" value="${fn:split(e, '\\\[')}" />
                        <c:set var="namespace" value="${f[1].split('\\\'')[1]}" />
                        <!-- ':' / 58 -->
                        <c:set var="parent" value="${fn:substring(namespace, namespace.lastIndexOf(58) + 1, fn:length(namespace))}" />
                        \ <span title="<c:out value="${namespace}" />"><c:out value="${fn:replace(fn:replace(fn:replace(parent, 'CommonBasicComponents-2', 'cbc'), 'CommonAggregateComponents-2', 'cac'), 'urn:oasis:names:specification:ubl:schema:xsd:', '')}" /></span>:<strong>${f[0].split(':')[1]}</strong>[${f[2]}
                    </c:if>
                    <c:if test="${fn:startsWith(e, '@') == true}">
                        \ <strong><c:out value="${e}" /></strong>
                    </c:if>
                </c:forEach>
            </div></c:if>
            <c:if test="${test.test != null}"><div class="test"><strong>Test:</strong> <c:out value="${test.test}" /></div></c:if>
        </div>
        </c:forEach>
    </div>
    </c:forEach>
</div>