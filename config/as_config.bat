
echo off
rem ==============这里是可以改的变量=================
rem 目标语言
set TARGET=as
rem 源码的输出位置
set code_target_dir=..\src\com\randioo\configClass
rem 压缩包存放位置
set assets_zip_target_dir=..\..\RandiooTest\src\assets\gameRes\RDOwlFight
rem =================================================

set ZIP_SOURCE_DIR=.\out
set ZIP_OUT_DIR=.\out
set ZIP_FILE_PREFIX=tbl

set source_dir=.\out

rem 生成代码和数据
.\jre7\bin\java -Dfile.encoding=utf-8 -jar .\randioo-excel-0.0.1-SNAPSHOT.jar %TARGET%

if not exist out\*.tbl (
	echo no files product.
	) else (

rem 压缩tbl文件
7za a -tzip %ZIP_OUT_DIR%/config.cfg %ZIP_SOURCE_DIR%/*.%ZIP_FILE_PREFIX%

rem 移动源码文件和配置表数据包到指定文件夹
mkdir %code_target_dir%
mkdir %assets_zip_target_dir%

copy /y %source_dir%\*.%TARGET% %code_target_dir%
copy /y %source_dir%\*.cfg %assets_zip_target_dir%
)
pause