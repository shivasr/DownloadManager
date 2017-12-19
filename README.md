# DownloadManager
Code can be found under https://github.com/shivasr/DownloadManager

Code Walkthrough video is found at https://www.youtube.com/watch?v=Y-cm3K0aTjU

Download Manager Demo: https://www.youtube.com/watch?v=jCrMlVaWszw

Instructions to build:

1. Get the source on the disk to location say, /Users/shiva/Downloads
2. Unzip the sources
3. From the base folder execute ./gradlew build (in linux) or gradlew build on windows. 
4. Once built use ./build/libs/download_manager_0.1.0.jar for execution.

Command to execute the utility:
java -jar download_manager-0.1.0.jar -f <Path to file containing URLs> -loc <Download Location>

