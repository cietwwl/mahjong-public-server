
set SOURCE_DIR=doc
set TARGET_DIR=..\src\main\java\
mkdir %TARGET_DIR%
call order_error_proto.bat
call order_proto.bat
echo off
rem proto_path 是proto文件的import目录
echo on
protoc.exe --proto_path=%SOURCE_DIR% --java_out=%TARGET_DIR% ./%SOURCE_DIR%/*.proto