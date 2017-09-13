package com.gmail.starguest95;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by isv on 12.09.17.
 */
public class FolderManager {

    List<File> filesList = new ArrayList();
    private static List<File>  filteredFilesList = new ArrayList<>();
    List<String> acceptableExtensions = new ArrayList();

    public FolderManager(File currentFolder, String fileExt, String textToSearchFor) {

        filteredFilesList = new ArrayList();
        //read all extensions
        extensionParser(fileExt);
        //read all files in directory
        collectFiles(currentFolder);
        //search for needed files
        filesList.stream().forEach((p) -> filterFilesList(p));
        //fill filtered files list
        filesList.stream().forEach((p) -> searchFileForText(p, textToSearchFor));

    }

    private void collectFiles(File dir){
        //get list of current files with folders
        File[] folderEntries = dir.listFiles();
        //iterate through the list
        for (File entry : folderEntries){
            //if entry is a folder
            if (entry.isDirectory()){
                //recursive call
                collectFiles(entry);
            } else {
                //if this is file, then add it to the list
                filesList.add(entry);
            }
        }
    }

    private void filterFilesList(File curFile){

        if (curFile != null) {
            //if file extension is acceptable
            if (acceptableExtensions.stream().anyMatch(getExtension(curFile)::equals)) {
                //do nothing
            } else {
                //delete file if it not allowable
                filesList.remove(curFile);
            }
        }

    }

    private String getExtension(File curFile){
        if (curFile != null) {
            //get file name
            String fileName = curFile.getName();
            //if file name contains dot and it not first symbol
            if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
                //return file extension
                return fileName.substring(fileName.lastIndexOf(".") + 1);
            } else {
                return "";
            }
        }
        return null;

    }

    public static List<File> getFilteredFilesList() {
        return filteredFilesList;
    }

    private void searchFileForText(File curFile, String text){
        List<String> fileLines = new ArrayList();
        try {
            //read all lines of the file
            fileLines = Files.readAllLines(Paths.get(curFile.getAbsolutePath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Some error with file reading");
            e.printStackTrace();
        }
        //search for text in current file
        //if file contains searching text
        if (fileLines.stream().anyMatch((s) -> s.contains(text))){
            //add this file to filtered list
            filteredFilesList.add(curFile);
        } else {
            //if this file not contains text do nothing
        }

    }

    private void extensionParser(String fileExt){
        //delete all spaces
        fileExt = fileExt.replaceAll(" ", "");
        List<String> temp = new ArrayList<>();
        //this list will accumulate extensions as String[]
        temp.add(fileExt);
        //add all extensions to the list
        acceptableExtensions = Arrays.asList(temp.stream().flatMap((p)
                -> Arrays.asList(p.split(",")).stream().distinct()).toArray(String[]::new));
    }
}
