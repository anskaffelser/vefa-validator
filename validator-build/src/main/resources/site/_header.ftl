<!DOCTYPE html>
<html>
<head>

    <title>VEFA Validator Builder</title>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

    <script src="https://code.jquery.com/jquery-1.11.3.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <style>
        .status-SUCCESS { background-color: #dff0d8; color: #3c763d; }
        .status-OK { background-color: #dff0d8; color: #3c763d; }
        .status-EXPECTED { background-color: #d9edf7; color: #31708f; }
        .status-WARNING { background-color: #fcf8e3; color: #8a6d3b; }
        .status-ERROR { background-color: #f2dede; color: #a94442; }
        .status-FATAL { background-color: #f2dede; color: #a94442; font-weight: bold; }
    </style>

</head>
<body>

<nav class="navbar navbar-default">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="index.html">VEFA Validator Builder</a>
        </div>
        <ul class="nav navbar-nav">
            <#list types as t>
            <li<#if (type?? && type == t)> class="active"</#if>><a href="type-${t}.html">${t}</a></li>
            </#list>
        </ul>
    </div>
</nav>

<div class="container">