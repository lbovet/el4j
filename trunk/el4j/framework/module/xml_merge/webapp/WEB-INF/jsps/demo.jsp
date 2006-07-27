<html>
<head><title>XmlMerge demo</title></head>
<body>
<form method=POST>

<table border=0>


<tr><td>

Source 1<br>
<textarea cols=50 rows=25 name="source1">
<%= request.getAttribute("source1") %>
</textarea>
</td>

<td><input type="submit" value="Merge"/></td>

<td>
Source 2<br>
<textarea cols=50 rows=25 name="source2">
<%= request.getAttribute("source2") %>
</textarea>
</td>
</tr>

<tr><td valign=top>
Configuration<br>
<textarea cols=50 rows=10 name="conf">
<%= request.getAttribute("conf") %>
</textarea>
<p>Actions
<ul>
<li>MERGE - Merges the element content
<li>REPLACE - Replaces or creates the element
<li>OVERRIDE - Replaces only if it already exists
<li>COMPLETE - Creates only if it does not exist
<li>DELETE - Deletes the element
<li>PRESERVE - Does nothing on the element
<li>INSERT - Inserts the element after existing <br>elements with the same name<br>(use with <tt>matcher.default=SKIP</tt>)
</ul>

</td>
<td></td>
<td valign=top>
Result<br>
<textarea cols=50 rows=30 name="result">
<%= request.getAttribute("result") %>
</textarea>


</td>

</tr></table>

</form>


</body>
</html>