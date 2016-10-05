<html>
    <head>
        <title>Validate a SWID Tag - Upload</title>
    </head>
    <body>
        <h1>Please select a SWID tag to validate</h1>
        <form method="post" action="validate.html" enctype="multipart/form-data">
            <input type="file" name="file"/>
            <select name="tag-type">
            	<option label="primary" value="primary">Primary</option>
            	<option label="patch" value="patch">Patch</option>
            	<option label="corpus" value="corpus">Corpus</option>
            	<option label="supplemental" value="supplemental">Supplemental</option>
            </select>
            <input type="submit" value="Validate"/>
        </form>
    </body>
</html>