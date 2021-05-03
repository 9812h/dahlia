# dahlia

1. Run
```
mvn clean javafx:run
```
2. Build
```
mvn clean package
```
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
