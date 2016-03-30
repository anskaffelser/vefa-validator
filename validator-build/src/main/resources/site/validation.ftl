<#include "_header.ftl">

<style>
    div.status { float: right; text-align: center; width: 80pt; padding: 0.7pt 0; }
    div.report > div.status { font-size: 20pt; width: 180pt; padding: 10pt 0; }
    div.section { border-top: 1px solid #ccc; margin: 5pt 0; padding: 5pt 0;}
    div.section > .title { font-size: 15pt; margin: 8pt 0 5pt; }
    div.section > div.status { font-size: 15pt; width: 125pt; padding: 5pt 0; }
    span.identifier { font-weight: bold; }
    div.assertion { border-top: 1px solid #eee; margin: 2pt 0; padding: 2pt 0; }
    div.assertion > div.location { margin-top: 5pt; }
    div.assertion > div.test { margin-top: 5pt; }
    ul.meta { margin: 0; padding: 0; }
    ul.meta li { list-style-type: none; display: inline; margin-right: 10pt; color: #999; }
</style>


<div class="report">

    <div class="status status-${validation.report.flag}">${validation.report.flag}</div>
    <h1 class="title">
        Test: ${validation.report.filename}
    </h1>

    <#if validation.report.description??>
        <p>${validation.report.description}</p>
    </#if>

    <ul class="meta">
        <#if validation.report.configuration??><li title="Configuration"><a href="configuration-${validation.report.configuration}.html"><span class="glyphicon glyphicon-cog"></span> ${validation.report.configuration}</a></li></#if>
    </ul>

    <#list validation.report.section as section>
        <div class="section">

            <div class="status status-${section.flag}"><span class="glyphicon"></span> ${section.flag}</div>
            <div class="title">${section.title}</div>

            <ul class="meta">
                <#if section.runtime??><li title="Runtime"><span class="glyphicon glyphicon-time"></span> ${section.runtime}</li></#if>
            </ul>

            <#list section.assertion as assertion>
                <div class="assertion">
                    <div class="status status-${assertion.flag}"><span class="glyphicon"></span> ${assertion.flag}</div>
                    <div class="title">
                        <span class="identifier">${assertion.identifier}</span>
                        ${assertion.text}
                    </div>
                </div>
            </#list>
        </div>
    </#list>

</div>

<#include "_footer.ftl">
