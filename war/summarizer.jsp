<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
  <title>Summarizer</title>
  <link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'/>
  <link rel="stylesheet" type="text/css" href="stylesheets/style.css"/>
</head>
<body align="center">
  <div id="header">
    <h1>Light Weight Summarizer</h1>
  </div>
  <div id="main">
    <div id="left">
      <a href="http://krishperumal.com" target="_blank">Creator's Site</a><br/>
      <a href="http://bit.ly/krish_icon_2011" target="_blank">Publication</a><br/>
      <a href="http://krishperumal.com/contact.html" target="_blank">Contact</a>
    </div>
    <div id="right" style="text-align: center">
      <form action="summarizer" method="post">
	<textarea name="input_text" rows="12" cols="100">
	<%
	  if(request.getAttribute("input")!=null)
	    out.print(request.getAttribute("input"));
	%>
	</textarea><br/>
	Percentage Summary Size: <input align="center" name="percentage" type="text" size="2" maxlength="2" 
	<%
  	  if(request.getAttribute("percentage")!=null)
            out.print("value=\""+request.getAttribute("percentage")+"\"");
	%>
	/>
	<input align="center" type="submit" value="Summarize" />
      </form>
	(Optimum results at 50% summary size)
      <div id="summary" style="text-align: justify">
	<!--<textarea name="input_text" rows="12" cols="100" readonly="readonly" style="background-color: white">-->
	  <%
	    if(request.getAttribute("summary")!=null)
	      out.print(request.getAttribute("summary"));
	  %>
	<!--</textarea>-->
      </div>
    </div>
  </div>
  <div id="footer">
    &copy; 2012, Krish Perumal, All Rights Reserved
  </div>
</body>
</html>
