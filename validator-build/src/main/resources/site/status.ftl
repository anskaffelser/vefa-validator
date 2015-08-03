<#include "_header.ftl">

<h1>Status: ${status}</h1>

<ul>
<#list vals as v>
    <li><a href="test-${filenames[v.report.filename]}.html">${v.report.filename}</a> (${v.report.flag})</li>
</#list>
</ul>

<#include "_footer.ftl">
