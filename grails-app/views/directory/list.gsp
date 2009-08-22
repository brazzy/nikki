

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Directory List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New Directory</g:link></span>
        </div>
        <div class="body">
            <h1>Directory List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="path" title="Path" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${directoryInstanceList}" status="i" var="directoryInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${directoryInstance.id}">${fieldValue(bean:directoryInstance, field:'id')}</g:link></td>
                        
                            <td>${fieldValue(bean:directoryInstance, field:'path')}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${directoryInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
