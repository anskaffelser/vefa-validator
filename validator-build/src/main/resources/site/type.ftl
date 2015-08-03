<#include "_header.ftl">

<h1>Type: ${type}</h1>

<ul class="nav nav-tabs" role="tablist">
    <li role="presentation" class="active"><a href="#status" aria-controls="status" role="tab" data-toggle="tab">Status</a></li>
    <li role="presentation"><a href="#rules" aria-controls="rules" role="tab" data-toggle="tab">Rules</a></li>
    <li role="presentation"><a href="#tests" aria-controls="tests" role="tab" data-toggle="tab">Tests</a></li>
    <li role="presentation"><a href="#configuration" aria-controls="configuration" role="tab" data-toggle="tab">Configuration</a></li>
</ul>

<!-- Tab panes -->
<div class="tab-content">
    <div role="tabpanel" class="tab-pane active" id="status">...</div>
    <div role="tabpanel" class="tab-pane" id="rules">

        <table class="table table-striped">
            <thead>
            <tr>
                <th>Rule</th>
                <th>Success</th>
                <th>Expected</th>
                <th>Unexpected</th>
            </tr>
            </thead>
            <tbody>
            <#list rules as r>
            <tr>
                <td>${r.name}</td>
                <td>${r.success}</td>
                <td>${r.expected}</td>
                <td>${r.unexpected}</td>
            </tr>
            </#list>
            </tbody>
        </table>

    </div>
    <div role="tabpanel" class="tab-pane" id="tests">

        <table class="table table-striped">
            <thead>
            <tr>
                <th>File</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody>
        <#list vals as v>
            <tr>
                <td><a href="test-${filenames[v.report.filename]}.html">${v.report.filename}</a></td>
                <td class="status-${v.report.flag}">${v.report.flag}</td>
            </tr>
        </#list>
            </tbody>
        </table>

    </div>
    <div role="tabpanel" class="tab-pane" id="configuration">

        <table class="table table-striped">
            <thead>
            <tr>
                <th>Identifier</th>
                <th>Configuration</th>
            </tr>
            </thead>
            <tbody>
            <#list confs as c>
            <tr>
                <td>${c.identifier}</td>
                <td><a href="configuration-${c.identifier}.html">${c.title}</a></td>
            </tr>
            </#list>
            </tbody>
        </table>

    </div>
</div>

<#include "_footer.ftl">