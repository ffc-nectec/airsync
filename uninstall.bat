@echo on
echo Uninstall FFC AirSync...
@echo off
echo Delete launcher.version >> uninstall.log
DEL /S /Q "%FFC_HOME%\launcher.version" >> uninstall.log
RD /S /Q "%FFC_HOME%\data" >> uninstall.log
DEL /S /Q "%FFC_HOME%\jreVersion.txt" >> uninstall.log
DEL /S /Q "%FFC_HOME%\log.cfg" >> uninstall.log
DEL /S /Q "%userprofile%\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup\ffc-airsync.bat" >> uninstall.log
DEL /S /Q "%FFC_HOME%\airsync.jar" >> uninstall.log
RD /S /Q "%FFC_HOME%\jre" >> uninstall.log
DEL /S /Q "%FFC_HOME%\ffc-airsync.exe" >> uninstall.log
DEL /S /Q "%FFC_HOME%\uninstall.ffc" >> uninstall.log
DEL /S /Q "%FFC_HOME%\uninstall.bat" >> uninstall.log
setx FFC_HOME "" >> uninstall.log
REG delete HKCU\Environment /F /V FFC_HOME >> uninstall.log

echo FFC AirSync uninstall successful! >> uninstall.log

exit
