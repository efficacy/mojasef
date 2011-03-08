<%@ page import="domain.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<%
  BlogService blogService = new BlogService();
  Blog blog = blogService.getBlog();
  request.setAttribute("blog", blog);

  BlogEntry blogEntry = blog.getBlogEntry(request.getParameter("id"));
  if (blogEntry == null) {
    response.sendError(HttpServletResponse.SC_NOT_FOUND);
    return;
  } else {
    request.setAttribute("blogEntry", blogEntry);
  }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

  <head>
    <title>${blogEntry.title} : ${blog.name}</title>
    <link rel="stylesheet" href="screen.css" type="text/css" />
  </head>

  <body>
    <div id="container">
      <h1>${blog.name}</h1>
      <h2>${blog.description}</h2>

      <div class="blogEntry">
        <h3>${blogEntry.title}</h3>

        ${blogEntry.body}

        <p>
        Posted on <fmt:formatDate value="${blogEntry.date}" timeZone="${blog.timeZone}" type="both" dateStyle="long" timeStyle="long" />
        </p>
      </div>
    </div>
  </body>

</html>