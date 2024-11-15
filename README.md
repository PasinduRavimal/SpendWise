## Getting Started

Welcome to the JavaFX application development. Here is a guideline to help you get started to write JavaFX and Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies
- `dependencies`: the folder to maintain JavaFX sdk

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

**Do not change the folder structure.**

## How to Begin

1. Create a folder for the project in a desired location. *There should be no spaces in the folder path or the folder name*

> Example: D:/spendWise

2. Right click on the folder created and click "Open Git Bash here". *You should have git client installed for this step*

3. Enter the following commands in the given order.

```
git init
git remote add origin https://github.com/PasinduRavimal/RAD-Personal-Expenses-Management-System.git
git pull origin main
```

4. Right click on the folder and click "Open with Code".

5. Select "Yes, I trust the author".

6. Install the Java extension to the VS Code, if you haven't already.

7. From the VS Code explorer, go to "src" folder and double click on "App.java".

8. Go to "Run and Debug" section of the VS Code side menu (Hamburger menu).

9. Click the dropdown button next to the green arrow and select "App" option.

10. Click the green arrow button. If everything went well, you should get a welcome screen.

*We didn't use package managers. So you would need to add JavaFX and MariaDB database drivers to the class path and set VM arguments if you choose not to use VS Code. Furthermore, since this is an application made for a university group assignment, we didn't take any step to secure data by encryption or any other means. We do not hold any responsibilty for losses incurred due to the use of this application. See the license agreement for more information.*