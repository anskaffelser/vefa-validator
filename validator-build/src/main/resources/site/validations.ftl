<#include "_header.ftl">

<h1>Test</h1>

<table class="table table-striped">
    <thead>
    <tr>
        <th>File</th>
        <th>Status</th>
    </tr>
    </thead>
    <tbody>
    <#list validations as v>
    <tr>
        <td><a href="test-${filenames[v.report.filename]}.html">${v.report.filename}</a></td>
        <td class="status-${v.report.flag}">${v.report.flag}</td>
    </tr>
    </#list>
    </tbody>
</table>

<#include "_footer.ftl">
