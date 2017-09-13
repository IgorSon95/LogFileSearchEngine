package com.gmail.starguest95.GUI;

import com.gmail.starguest95.FolderManager;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by isv on 12.09.17.
 */
public class MainForm extends JFrame{

    private JFrame MainFrame;
    private JButton OpenBtn;
    private JPanel MainPanel;
    private JTextArea FileExtensionTA;
    private JTextArea TextForSearchingTA;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("../");
    private JPanel DTPanel = new JPanel();
    private JTree directoryTree;
    private JTextArea TextDispayTA;
    private JFileChooser chooser;
    private DefaultTreeModel model;

    public MainForm(){

        //set default JTree parameter
        directoryTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("../")));

        setContentPane(MainPanel);//set some default parameters
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 600));
        pack();
        setVisible(true);
        OpenBtn.addActionListener(new OpenDirListener());

    }

    private void treeInit(File dir){
        //create root of our directory
        root = new DefaultMutableTreeNode(dir);
        //get list of current files with folders
        File[] folderEntries = dir.listFiles();
        //iterate through the list
        for (File entry : folderEntries){

            DefaultMutableTreeNode child = new DefaultMutableTreeNode(entry);
            root.add(child);
            //add child of the root
            addChildToRoot(child, entry);
        }

        directoryTree.setModel(new DefaultTreeModel(root));

        directoryTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                //last selected node
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) directoryTree.getLastSelectedPathComponent();
                //node as file
                File file = new File(node.toString());
                //if nothing is selected or directory
                if (node == null || file.isDirectory()){
                    //do nothing
                } else {
                    //show file on the right side
                    TextDispayTA.setText("");
                    TextDisplayer TD = new TextDisplayer(TextDispayTA, file, TextForSearchingTA.getText());
                }
            }
        });

    }

    private static void addChildToRoot(DefaultMutableTreeNode mroot, File froot){


        File[] fall = froot.listFiles();
        if (fall == null) return;
        for (int i = 0; i < fall.length; i++){
            File file = fall[i];
            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(file);
            //display only needed results
            if (FolderManager.getFilteredFilesList().stream().anyMatch(file::equals) ||
                    file.isDirectory()) {
                //add file as an object
                mroot.add(newChild);
            }
            //if file is directory then recursive call
            if (file.isDirectory()){
                addChildToRoot(newChild, file);
            }
        }

    }


    //open button listener
    private class OpenDirListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //create directory chooser
            chooser = new JFileChooser();
            //name a title
            chooser.setDialogTitle("Open directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //if user chose directory
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                //send directory to file manager
                FolderManager FM = new FolderManager(chooser.getSelectedFile(), FileExtensionTA.getText(),
                        TextForSearchingTA.getText());
                //tree inition call
                treeInit(chooser.getSelectedFile());
            } else {
                //if file wasnt select then

            }
        }
    }

    public static void infoBox(String infoMessage, String titleBar)//function that makes dialogue message
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

}
