<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="value" required="true" type="no.difi.xsd.vefa.validator._1.FlagType" %>

<div class="status status-<c:out value="${value}" />">
    <span class="glyphicon <c:out value="${value == 'OK' or value == 'EXPECTED' ? 'glyphicon-ok' : 'glyphicon-remove'}" />"></span>
    <c:out value="${value}" />
</div>