@echo off
echo Fixing UTF-8 BOM encoding issues...

powershell -ExecutionPolicy Bypass -Command "$files = Get-ChildItem -Path 'src' -Include *.java,*.groovy -Recurse; foreach ($file in $files) { $content = Get-Content $file.FullName -Raw; if ($content) { $Utf8NoBomEncoding = New-Object System.Text.UTF8Encoding $False; [System.IO.File]::WriteAllLines($file.FullName, $content, $Utf8NoBomEncoding) } }"

echo Done!
