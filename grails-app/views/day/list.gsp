

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Day List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
        </div>
        <div class="body">
            <h1>Day List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="date" title="Date" />
                        
                   	        <th>Directory</th>
                   	    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${dayInstanceList}" status="i" var="dayInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="edit" id="${dayInstance.id}">${fieldValue(bean:dayInstance, field:'id')}</g:link></td>
                        
                            <td>${fieldValue(bean:dayInstance, field:'date')}</td>
                        
                            <td>${fieldValue(bean:dayInstance, field:'directory')}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${dayInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
