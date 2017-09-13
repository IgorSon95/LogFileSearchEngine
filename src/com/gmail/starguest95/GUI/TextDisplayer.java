package com.gmail.starguest95.GUI;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by isv on 13.09.17.
 */
public class TextDisplayer extends DefaultHighlighter.DefaultHighlightPainter{
    private JTextArea textDisplayer = new JTextArea();

    private List<WordPositions> getWordPositionsList() {
        return wordPositionsList;
    }

    private List<WordPositions> wordPositionsList = new ArrayList<>();
    private Highlighter.HighlightPainter simpleWord =
            new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);
    private Highlighter.HighlightPainter focusedWord =
            new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);

    private JTextArea getTextDisplayer() {
        return textDisplayer;
    }

    private void setTextDisplayer(JTextArea textDisplayer) {
        this.textDisplayer = textDisplayer;
    }

    private Highlighter hlForDoc;


    public TextDisplayer(JTextArea ta, File file, String textForSearching) {
        super(Color.darkGray);
        setTextDisplayer(ta);
        //read sent file
        List<String> fileLines = new ArrayList();
        try {
            //read all lines of the file
            fileLines = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Some error with file reading");
            e.printStackTrace();
        }

        hlForDoc = getTextDisplayer().getHighlighter();

        //enter text into text area
        fileLines.stream().forEach(p -> getTextDisplayer().append(p));
        //highlight text
        highlightDoc(textForSearching);
        //add type event to text displayer
        getTextDisplayer().addKeyListener(new DisplayListener());

    }

    private void highlightDoc(String textForSearching){

        try {

            Document doc = getTextDisplayer().getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;

            //it dont matter is text is in upper case or lower case
            while ((pos = text.toUpperCase().indexOf(textForSearching.toUpperCase(), pos)) >= 0){
                highlightWord(pos, pos + textForSearching.length(), simpleWord);
                //add coordinates of words that can be focused
                getWordPositionsList().add(new WordPositions(pos, pos + textForSearching.length()));
                pos += textForSearching.length();
            }

            //sort word positions in ascending order
            getWordPositionsList().stream().sorted((o1, o2) -> o1.compareTo(o2)).collect(Collectors.toList());

        }catch (Exception e){

        }

    }

    private void highlightWord(int begin, int end, Highlighter.HighlightPainter color){

        //highlight a word in textarea
        try {
            hlForDoc.addHighlight(begin, end, color);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    //this class is supposed to store indexes of word that have to be highlighted
    private class WordPositions implements Comparable<WordPositions>{
        private int begin = 0;
        private int end = 0;

        public WordPositions(int begin, int end) {
            this.begin = begin;
            this.end = end;
        }

        public int getBegin() {
            return begin;
        }

        public void setBegin(int begin) {
            this.begin = begin;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        @Override
        public int compareTo(WordPositions o) {

            return this.getBegin() - o.getBegin();
        }
    }

    private class DisplayListener implements KeyListener {

        private int curIdx = -1;

        public int getCurIdx() {
            return curIdx;
        }

        public void setCurIdx(int curIdx) {
            this.curIdx = curIdx;
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            //switch key press event
            switch (e.getKeyCode()){
                //if right arrow button was pressed
                case KeyEvent.VK_RIGHT:
                    if ((getCurIdx() > -1) && (getCurIdx() < getWordPositionsList().size())){
                        //paint last word with simple color
                        changeHLColor(getCurIdx(), simpleWord);
                    }
                    if (getCurIdx() != getWordPositionsList().size() - 1) {
                        //highlight next word
                        setCurIdx(getCurIdx() + 1);
                        changeHLColor(getCurIdx(), focusedWord);
                    } else {
                        //if tail of the list reached then
                        setCurIdx(0);
                        changeHLColor(getCurIdx(), focusedWord);
                    }
                    break;
                //if left arrow button was pressed
                case KeyEvent.VK_LEFT:
                    if ((getCurIdx() > -1) && (getCurIdx() < getWordPositionsList().size())){
                        //paint last word with simple color
                        changeHLColor(getCurIdx(), simpleWord);
                    }
                    if (getCurIdx() > 0) {
                        //highlight previous word
                        setCurIdx(getCurIdx() - 1);
                        changeHLColor(getCurIdx(), focusedWord);
                    } else {
                        //if head of the list reached then
                        setCurIdx(getWordPositionsList().size() - 1);
                        changeHLColor(getCurIdx(), focusedWord);
                    }
                    break;
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    //highlight function coloring words in unpredictable order, so we need to find highlight in
    //ascending order
    private void changeHLColor(int idx, Highlighter.HighlightPainter newColor){

        Highlighter.Highlight hlObj = null;
        //hlObj = hlForDoc.getHighlights();

        for (int i = 0; i < hlForDoc.getHighlights().length; i++){
            //if we found needed highlight
            if (getWordPositionsList().get(idx).getBegin() ==
                    hlForDoc.getHighlights()[i].getStartOffset()){
                //copy ref of it
                hlObj = hlForDoc.getHighlights()[i];
            }
        }

        if (hlObj != null){
            //remove highlight
            hlForDoc.removeHighlight(hlObj);
            //change color of highlight
            try {
                hlForDoc.addHighlight(hlObj.getStartOffset(), hlObj.getEndOffset(), newColor);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

}
