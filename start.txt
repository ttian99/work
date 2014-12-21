@echo 
rem @echo off 
rem node js_build_apk.js
echo 
start ..\\star\\tools\\jscompile_res_to_android.bat
rem npm install xml-digester
node js_build_apk.js
pause