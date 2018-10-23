<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	
	<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Pojo to Json Serialization using Jersey with Jackson for Java REST Services</title>
        
        <script src="<%=request.getContextPath() %>/js/jquery-1.11.2.min.js"></script>
        <script>
            var ctxPath = "<%=request.getContextPath() %>";
            $(function(){                
                $("#postPerson, #postMessage").on("click", function(){
                    $.ajax({
                        url: $(this).attr("id") === "postMessage" ? ctxPath+"/service/message/post" : ctxPath+"/service/person/post",
                        type: "POST",
                        data: '{"firstName":"Michael", "lastName":"Jordan"}',
                        contentType: "application/json",
                        cache: false,
                        dataType: "json"
                    });
                });                
            });
        </script>
                
   	</head>

	<body>
	   <h1>Pojo to Json Serialization using Jersey with Jackson for Java REST Services</h1>
	   <ul>
	       <li><a href="<%=request.getContextPath() %>/service/message"><%=request.getContextPath() %>/service/message</a></li>
	       <li><a href="<%=request.getContextPath() %>/service/message/ping"><%=request.getContextPath() %>/service/message/ping</a></li>
	       <li><a href="<%=request.getContextPath() %>/service/person/get"><%=request.getContextPath() %>/service/person/get</a></li>
           <li><button id="postPerson">Post Person</button></li>
           <li><button id="postMessage">Post Message</button></li>
	   </ul>
           
    </body>
    
</html>