$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
New-Item -ItemType Directory -Force -Path 'out' | Out-Null
$fuentes = Get-ChildItem -Recurse 'src/main/java' -Filter '*.java' | ForEach-Object FullName
javac --release 17 -encoding UTF-8 -d out $fuentes
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java '-Dfile.encoding=UTF-8' -cp out taberna.App
exit $LASTEXITCODE
