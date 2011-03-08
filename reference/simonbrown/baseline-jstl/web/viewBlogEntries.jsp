<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

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

      <c:forEach var="blogEntry" items="${blog.blogEntries}">
        <div class="blogEntry">
          <h3>${blogEntry.title}</h3>

          <c:choose>
            <c:when test="${not empty blogEntry.excerpt}">
              ${blogEntry.excerpt}
              <p>
              <a href="viewBlogEntry.jsp?id=${blogEntry.id}">Read more</a>
              </p>
            </c:when>
            <c:otherwise>
              ${blogEntry.body}
            </c:otherwise>
          </c:choose>

          <p>
          Posted on <fmt:formatDate value="${blogEntry.date}" timeZone="${blog.timeZone}" type="both" dateStyle="long" timeStyle="long" />
          </p>
        </div>
      </c:forEach>
    </div>
  </body>

</html>