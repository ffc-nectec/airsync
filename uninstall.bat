@echo off
echo Uninstall FFC >> "%FFC_HOME%\uninstall.log"
echo Delete launcher.version >> uninstall.log
DEL /S /Q "%FFC_HOME%\launcher.version"

echo Delete data >> "%FFC_HOME%\uninstall.log"
RD /S /Q "%FFC_HOME%\data"

echo Delete jreVersion.txt >> "%FFC_HOME%\uninstall.log"
DEL /S /Q "%FFC_HOME%\jreVersion.txt"

echo Delete log.cfg >> "%FFC_HOME%\uninstall.log"
DEL /S /Q "%FFC_HOME%\log.cfg"

echo Delete FFC startup >> "%FFC_HOME%\uninstall.log"
DEL /S /Q "%userprofile%\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup\ffc-airsync.bat"

echo Delete airsync.jar >> "%FFC_HOME%\uninstall.log"
DEL /S /Q "%FFC_HOME%\airsync.jar"

echo Delete jre >> "%FFC_HOME%\uninstall.log"
RD /S /Q "%FFC_HOME%\jre"

echo Delete ffc-airsync.exe >> "%FFC_HOME%\uninstall.log"
DEL /S /Q "%FFC_HOME%\ffc-airsync.exe"

echo Delete uninstall.ffc >> "%FFC_HOME%\uninstall.log"
DEL /S /Q "%FFC_HOME%\uninstall.ffc"

echo Delete  uninstall.bat >> "%FFC_HOME%\uninstall.log"
DEL /S /Q "%FFC_HOME%\uninstall.bat"

echo Remove FFC_HOME >> "%FFC_HOME%\uninstall.log"
setx FFC_HOME ""
REG delete HKCU\Environment /F /V FFC_HOME

echo Success uninstall FFC Airsync

SLEEP 10
exit
