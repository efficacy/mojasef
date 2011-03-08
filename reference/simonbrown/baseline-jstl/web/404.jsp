<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<jsp:useBean id="blogService" scope="request" class="domain.BlogService"/>
<c:set var="blog" value="${blogService.blog}" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

  <head>
    <title>${blog.name}</title>
    <link rel="stylesheet" href="screen.css" type="text/css" />
  </head>

  <body>
    <div id="container">
      <h1>${blog.name}</h1>
      <h2>${blog.description}</h2>

      <h3>Page not found</h3>
    </div>
  </body>

</html>