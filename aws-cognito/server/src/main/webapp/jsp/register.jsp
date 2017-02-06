<!--
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
-->

<%@page import="main.java.com.amazonaws.cognito.devauthsample.Utilities"%>
<%@page import="main.java.com.amazonaws.cognito.devauthsample.Configuration"%>
<%@ page session="true" %>

<script type="text/javascript">
	function validateForm() {
		document.getElementById("userNameErrorMessage").innerHTML=null;
		document.getElementById("passwordErrorMessage").innerHTML=null;
		var uName = document.forms["register"]["username"].value;
		var validUserNameRegex = /^[0-9a-zA-Z@._]{3,128}$/;
	    if (!validUserNameRegex.test(uName)) {
	        document.getElementById("userNameErrorMessage").innerHTML = "Choose a user name between 3 to 128 characters. Allowed characters are letters, digits, '@', '_' and '.'";
	        return false;
	    }
	    
	    var validPasswordRegex = /.{6,12}/;
	    var pwd = document.forms["register"]["password"].value;
	    if (!validPasswordRegex.test(pwd)) {
	        document.getElementById("passwordErrorMessage").innerHTML = "Choose a password between 6 to 128 characters.";
	        return false;
	    }
	    return true;
	}
</script>

<html>
	<head>
		<title>Amazon Cognito Developer Authentication Sample - Register</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/styles.css" type="text/css" media="screen" charset="utf-8">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/styles-mobile.css" type="text/css" media="screen" charset="utf-8">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/styles-tablet.css" type="text/css" media="screen" title="no title" charset="utf-8">
	</head>
	<body class="register">

		<div id="header">
			<h1>Amazon Cognito Developer Authentication Sample</h1>
		</div>

		<div id="body">
			<fieldset>
				<legend>Register</legend>
				<form id="register" name="register" onsubmit="return validateForm()" action="/registeruser" method="POST">

					<table class="information-box">
						<tbody>
							<tr>
								<td class="th">
									<p><strong>Username:</strong></p>
								</td>
								<td>
									<p><input type="text" name="username" value="" placeholder="username" 
										title="Choose a user name between 3 to 128 characters. Allowed characters are letters, digits, '@', '_' and '.'" required></p>
								</td>
								<td>
								<span id="userNameErrorMessage" style="color:red"></span>
								</td>
							</tr>
							<tr>
								<td class="th">
									<p><strong>Password:</strong></p>
								</td>
								<td>
									<p><input type="password" name="password" value="" placeholder="password" title="Choose a password between 6 to 128 characters." required></p>
								</td>
								<td>
								<span id="passwordErrorMessage" style="color:red"></span>
								</td>
							</tr>
							<tr>
								<td class="th">&nbsp;</td>
								<td>
									<p><input type="submit" class="button" id="upload_button" value="Register"></p>
								</td>
							</tr>

						</tbody>
					</table>

				</form>
			</fieldset>
		</div>
		
		<%
		int port = request.getServerPort();
		if(443 != port) {
			if(response instanceof HttpServletResponse) {
		%>
		<p class="warning">Warning: You are not running SSL.</p>
		<%
			}	
		}
		%>
		
		<div id="footer">
			<p class="footnote"><%=Configuration.APP_NAME %> - Cognito Developer Authentication Sample</p>
		</div>

	</body>
</html>
