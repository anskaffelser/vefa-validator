<#include "_header.ftl">

<h1>Configuration</h1>

<ul>
<#list configurations.configuration as c>
<li><a href="configuration-${c.identifier}.html">${c.title}</a></li>
</#list>
</ul>

<#include "_footer.ftl">