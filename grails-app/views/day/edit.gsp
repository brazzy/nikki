

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit Day</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Day List</g:link></span>
        </div>
        <div class="body">
            <h1>Edit Day</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${dayInstance}">
            <div class="errors">
                <g:renderErrors bean="${dayInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${dayInstance?.id}" />
                <input type="hidden" name="version" value="${dayInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="date">Date:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:dayInstance,field:'date','errors')}">
                                    <g:datePicker name="date" value="${dayInstance?.date}" precision="minute" ></g:datePicker>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="directory">Directory:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:dayInstance,field:'directory','errors')}">
                                    <g:select optionKey="id" from="${Directory.list()}" name="directory.id" value="${dayInstance?.directory?.id}" ></g:select>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="images">Images:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:dayInstance,field:'images','errors')}">
                                    
<ul>
<g:each var="i" in="${dayInstance?.images?}">
    <li><g:link controller="image" action="show" id="${i.id}">${i?.encodeAsHTML()}</g:link></li>
</g:each>
</ul>
<g:link controller="image" params="['day.id':dayInstance?.id]" action="create">Add Image</g:link>

                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="waypoints">Waypoints:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:dayInstance,field:'waypoints','errors')}">
                                    
<ul>
<g:each var="w" in="${dayInstance?.waypoints?}">
    <li><g:link controller="waypoint" action="show" id="${w.id}">${w?.encodeAsHTML()}</g:link></li>
</g:each>
</ul>
<g:link controller="waypoint" params="['day.id':dayInstance?.id]" action="create">Add Waypoint</g:link>

                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
		            <span class="button"><g:actionSubmit class="list" value="Export" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
