rd /s/q ..\..\proj.android\assets\src
rd /s/q ..\..\proj.android\assets\res
mkdir ..\..\proj.android\assets\res
xcopy /s ..\res ..\..\proj.android\assets\res
python cocos2d.py jscompile -s ../ -d ..\..\proj.android\assets
pause