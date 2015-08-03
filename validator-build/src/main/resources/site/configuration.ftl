<#include "_header.ftl">

<h1>Configuration: ${configuration.title}</h1>

<#if configuration.profileId?? || configuration.customizationId??>
    <h2>Declaration</h2>

    <dl class="dl-horizontal">
        <#if configuration.profileId??>
        <dt>ProfileId</dt>
        <dd>${configuration.profileId}</dd>
        </#if>

        <#if configuration.customizationId??>
        <dt>CustomizationId</dt>
        <dd>${configuration.customizationId}</dd>
        </#if>
    </dl>
</#if>

<#if (configuration.inherit?size > 0)>
<h2>Inherits</h2>

<ul>
    <#list configuration.inherit as f>
        <li>${f}</li>
    </#list>
</ul>
</#if>

<#if (configuration.file?size > 0)>
    <h2>Files</h2>

    <ul>
        <#list configuration.file as f>
            <li>${f.path}</li>
        </#list>
    </ul>
</#if>

<#if (vals?size > 0)>
    <h2>Tests</h2>

    <ul>
    <#list vals as v>
        <li><a href="test-${filenames[v.report.filename]}.html">${v.report.filename}</a> (${v.report.flag})</li>
    </#list>
    </ul>
</#if>

<#include "_footer.ftl">