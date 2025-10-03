@echo off
setlocal enabledelayedexpansion

echo Replacing package and import statements...

for /r src %%f in (*.java) do (
    powershell -ExecutionPolicy Bypass -Command "(Get-Content '%%f') -replace 'package com\.skeletor', 'package com.archetype' -replace 'import com\.skeletor', 'import com.archetype' -replace 'com\.skeletor\.', 'com.archetype.' | Set-Content '%%f' -Encoding UTF8"
)

for /r src %%f in (*.groovy) do (
    powershell -ExecutionPolicy Bypass -Command "(Get-Content '%%f') -replace 'package com\.skeletor', 'package com.archetype' -replace 'import com\.skeletor', 'import com.archetype' -replace 'com\.skeletor\.', 'com.archetype.' | Set-Content '%%f' -Encoding UTF8"
)

echo Done!
