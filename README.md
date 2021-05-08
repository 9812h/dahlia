# dahlia
0. Requirements
- JDK 16
- Maven

1. Run project
```
mvn clean javafx:run
```
2. Build executable `.jar` file
```
mvn clean package
```
This will generate an executable `.jar` file and place it into folder `shade`.

3. Notice
- Place Chrome driver into folder `drivers` (this folder is in the same folder with `.jar` file) and rename it to `chromedriver.exe` (for Windows).
- Logs are in folder `logs`.
- Running folder structure:
```
<any folder>
|
|__ <executable .jar file>
|
|__ drivers
|   |
|   |__ chromedriver.exe
|
|__ logs
    |
    |__ <log files>     
```
