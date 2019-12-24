@echo on
echo Uninstall FFC
echo Delete launcher.version
DEL /S /Q "%FFC_HOME%\launcher.version"

echo Delete data
RD /S /Q "%FFC_HOME%\data"

echo Delete jreVersion.txt
DEL /S /Q "%FFC_HOME%\jreVersion.txt"

echo Delete log.cfg
DEL /S /Q "%FFC_HOME%\log.cfg"

echo Delete FFC startup
DEL /S /Q "%userprofile%\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup\ffc-airsync.bat"

echo Delete airsync.jar
DEL /S /Q "%FFC_HOME%\airsync.jar"

echo Delete jre
RD /S /Q "%FFC_HOME%\jre"

echo Delete ffc-airsync.exe
DEL /S /Q "%FFC_HOME%\ffc-airsync.exe"

echo Delete uninstall.ffc
DEL /S /Q "%FFC_HOME%\uninstall.ffc"

echo Delete  uninstall.bat
DEL /S /Q "%FFC_HOME%\uninstall.bat"

echo Remove FFC_HOME
setx FFC_HOME ""
REG delete HKCU\Environment /F /V FFC_HOME

echo Success uninstall FFC Airsync

exit
