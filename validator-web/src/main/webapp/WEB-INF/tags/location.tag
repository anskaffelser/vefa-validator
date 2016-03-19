<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="value" required="true" type="java.lang.String" %>

<c:forEach var="e" items="${fn:split(value, '/')}">
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