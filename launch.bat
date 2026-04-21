@echo off
"%~dp0jre\bin\java.exe" --module-path "%~dp0javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml -Djava.library.path="%~dp0javafx-sdk\bin" -jar "%~dp0VehicleIdentificationSystem-1.0-SNAPSHOT.jar"
pause
