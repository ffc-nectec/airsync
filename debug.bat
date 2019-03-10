echo off
cls
java -Dfile.encoding=utf-8 -jar airsync.jar -v >> debug.log
java -Dfile.encoding=utf-8 -jar airsync.jar -runnow >> debug.log
