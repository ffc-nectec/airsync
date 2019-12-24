@echo on
echo Uninstall FFC
echo Delete launcher.version
DEL /S /Q "%FFC_HOME%\launcher.version"  >> "%FFC_HOME%\uninstall.log"

echo Delete data
RD /S /Q "%FFC_HOME%\data" >> "%FFC_HOME%\uninstall.log"

echo Delete jreVersion.txt
DEL /S /Q "%FFC_HOME%\jreVersion.txt" >> "%FFC_HOME%\uninstall.log"

echo Delete log.cfg
DEL /S /Q "%FFC_HOME%\log.cfg" >> "%FFC_HOME%\uninstall.log"

echo Delete FFC startup
DEL /S /Q "%userprofile%\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup\ffc-airsync.bat" >> "%FFC_HOME%\uninstall.log"

echo Delete airsync.jar
DEL /S /Q "%FFC_HOME%\airsync.jar" >> "%FFC_HOME%\uninstall.log"

echo Delete jre
RD /S /Q "%FFC_HOME%\jre" >> "%FFC_HOME%\uninstall.log"

echo Delete ffc-airsync.exe
DEL /S /Q "%FFC_HOME%\ffc-airsync.exe" >> "%FFC_HOME%\uninstall.log"

echo Delete uninstall.ffc
DEL /S /Q "%FFC_HOME%\uninstall.ffc" >> "%FFC_HOME%\uninstall.log"

echo Delete  uninstall.bat
DEL /S /Q "%FFC_HOME%\uninstall.bat" >> "%FFC_HOME%\uninstall.log"

echo Remove FFC_HOME
setx FFC_HOME ""
REG delete HKCU\Environment /F /V FFC_HOME >> "%FFC_HOME%\uninstall.log"

echo Success uninstall FFC Airsync

exit /B
