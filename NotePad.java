package other;

import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;

/**
 * @author even_and_just
 * @function A simple version of note pad application. 
 */

public class NotePad extends JFrame implements ActionListener{
    
    JTextArea textArea = null;
    JMenuBar menuBar = null;
    
    JMenu menuFile = null;
    JMenuItem newFile = null;
    JMenuItem open = null;
    JMenuItem save = null;
    JMenuItem quit = null;
    
    JMenu menuEdit = null;
    JMenuItem copy = null;
    JMenuItem cut = null;
    JMenuItem paste = null;
    JMenuItem undo = null;
    JMenuItem redo = null;
    JMenuItem clearAll = null;
    JMenuItem findAndRep = null;
    
    JMenu menuView = null;
    JMenuItem background = null;
    JMenuItem font = null;
    
    Clipboard cb = null;
    UndoManager um = null;
    String curTextContent = "";
    JScrollPane jScrPane = null;
    JPopupMenu popMenu = null;
    
    JTextField textField1 = null;
    JTextField textField2 = null;
    
    int start = 0;
    int end = 0;

    public static void main(String args[]){
        NotePad np = new NotePad();
    }
    
    public NotePad(){
        init(200, 100);
    }
    
    public void init(int x, int y){
        textArea = new JTextArea();
        menuBar = new JMenuBar();
        cb = this.getToolkit().getSystemClipboard();
        um = new UndoManager();
        textArea.getDocument().addUndoableEditListener(um);
        
        popMenu = new JPopupMenu();
        constructPopMenu(popMenu);
        
        // Construct Menu for 'File'
        menuFile = constructMenu(menuFile, "File", 'F');
        
        newFile = constructMenuItem(newFile, "New (N)", 'N', "new");
        open = constructMenuItem(open, "Open (O)", 'O', "open");
        save = constructMenuItem(save, "Save (S)", 'S', "save");
        quit = constructMenuItem(quit, "Quit (Q)", 'Q', "quit");
        
        // Construct Menu for 'Edit'
        menuEdit = constructMenu(menuEdit, "Edit", 'E');
        
        copy = constructMenuItem(copy, "Copy (C)", 'C', "copy");
        cut = constructMenuItem(cut, "Cut (X)", 'X', "cut");
        paste = constructMenuItem(paste, "Paste (V)", 'V', "paste");
        undo = constructMenuItem(undo, "Undo (Z)", 'Z', "undo");
        redo = constructMenuItem(redo, "Redo", ' ', "redo");
        clearAll = constructMenuItem(clearAll, "Clear All", ' ', "clearAll");
        findAndRep = constructMenuItem(findAndRep, "Find And Replace (R)", 'R', "findRep");
        
        // Construct menu bar
        this.setJMenuBar(menuBar);
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        
        menuFile.add(newFile);
        menuFile.add(open);
        menuFile.add(save);
        menuFile.add(quit);
        
        menuEdit.add(copy);
        menuEdit.add(cut);
        menuEdit.add(paste);
        menuEdit.add(undo);
        menuEdit.add(redo);
        menuEdit.add(clearAll);
        menuEdit.add(findAndRep);
        
        textArea.add(popMenu);
        jScrPane = new JScrollPane(textArea);
        this.add(jScrPane);
        jScrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Set the text area/pad
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(x, y);
        this.setSize(800, 500);
        this.setVisible(true);
        this.setTitle("NotePad");
        
        // Register window listener
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                quit();
            }
        });
        
        // Register mouse listener
        this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                int mods = e.getModifiers();
                if((mods&InputEvent.BUTTON3_MASK)!=0){
                    popMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    
    public JMenu constructMenu(JMenu menu, String s, char c){
        menu = new JMenu(s);
        if(c!=' '){
            menu.setMnemonic(c);
        }
        return menu;
    }
    
    public JMenuItem constructMenuItem(JMenuItem menuItem, String s, char c, String cmd){
        menuItem = new JMenuItem(s);
        if(c!=' '){
            menuItem.setMnemonic(c);
        }
        menuItem.addActionListener(this);
        menuItem.setActionCommand(cmd);
        return menuItem;
    }
    
    // Construct popup menu
    public void constructPopMenu(JPopupMenu popMenu){
        JMenuItem copyPop = new JMenuItem("Copy (C)");
        JMenuItem cutPop = new JMenuItem("Cut (X)");
        JMenuItem pastePop = new JMenuItem("Paste (V)");
        JMenuItem clearAllPop = new JMenuItem("Clear All");
        
        popMenu.add(copyPop);
        popMenu.add(cutPop);
        popMenu.add(pastePop);
        popMenu.add(clearAllPop);
        
        copyPop.addActionListener(this);
        cutPop.addActionListener(this);
        pastePop.addActionListener(this);
        clearAllPop.addActionListener(this);
    }
    
    public void open(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Please Choose the File");
        // null--> use default settings
        fileChooser.showOpenDialog(null);
        fileChooser.setVisible(true);
        
        // Get the absolute path of the chosen file
        String fileName = fileChooser.getSelectedFile().getAbsolutePath();
        
        // Read in the file content using bufferedreader
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            
            StringBuilder sb = new StringBuilder();
            String sTmp = "";
            while((sTmp=br.readLine())!=null){
                sb.append(sTmp+"\r\n");
            }
            
            textArea.setText(sb.toString());
            curTextContent = this.textArea.getText();
            this.setTitle("NotePad - "+fileName);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public void save(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Please Select The Location To Save");
        fileChooser.showSaveDialog(null);
        fileChooser.setVisible(true);
        
        String fileName = fileChooser.getSelectedFile().getAbsolutePath();
        
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try{
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            
            pw.print(this.textArea.getText());
            curTextContent = this.textArea.getText();
            this.setTitle("NotePad - "+fileName);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                pw.close();
                bw.close();
                fw.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void quit(){
        if(!textArea.getText().equals(curTextContent)){
            int num = JOptionPane.showConfirmDialog(null, "Contents Modified! Save And Quit?", "Warning", 1);
            if(num==JOptionPane.OK_OPTION){
                save();
                System.exit(0);
            }
        }else{
            System.exit(0);
        }
    }
    
    public void copy(){
        String selectedText = textArea.getSelectedText();
        StringSelection editText = new StringSelection(selectedText);
        cb.setContents(editText, null);
    }

    public void cut(){
        String selectedText = textArea.getSelectedText();
        StringSelection editText = new StringSelection(selectedText);
        cb.setContents(editText, null);
        start = textArea.getSelectionStart();
        end = textArea.getSelectionEnd();
        textArea.replaceRange("", start, end);
        curTextContent = this.textArea.getText();
    }
    
    public void paste(){
        Transferable contents = cb.getContents(this);
        DataFlavor df = DataFlavor.stringFlavor;
        
        if(contents.isDataFlavorSupported(df)){
            try{
                String s = "";
                s = (String)contents.getTransferData(df);
                textArea.append(s);
                curTextContent = this.textArea.getText();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void findAndReplace(){
        JDialog find = new JDialog(this, "Find And Replace");
        find.setSize(300, 80);
        find.setLocation(300, 200);
        
        JLabel label1 = new JLabel("  Search For ");
        JLabel label2 = new JLabel("  Replace With ");
        textField1 = new JTextField(3);
        textField2 = new JTextField(3);
        JButton findButton = new JButton("Find/Next");
        JButton replaceButton = new JButton("Replace");
        
        JPanel jp = new JPanel(new GridLayout(2,3));
        
        jp.add(label1);
        jp.add(textField1);
        jp.add(findButton);
        jp.add(label2);
        jp.add(textField2);
        jp.add(replaceButton);
        
        find.add(jp);
        find.setVisible(true);
        
        findButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                find();
            }
        });
        
        replaceButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                find();
                String replaceWith = textField2.getText();
                textArea.select(start, end);
                textArea.replaceSelection(replaceWith);
                textArea.select(start, end);
            }
        });
        
    }
    
    public void find(){
        String textToFind = textField1.getText();
        String curWholeText = textArea.getText();
        
        start = curWholeText.indexOf(textToFind, end);
        end = start + textToFind.length();
        
        if(start==-1){
            JOptionPane.showMessageDialog(null, textToFind+" Not Found!", "NotePad", JOptionPane.WARNING_MESSAGE);
        }else{
            textArea.select(start, end);
        }
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        // TODO Auto-generated method stub
        
        // Handle different cases according to ActionCommands
        if(event.getActionCommand().equals("new")){
            textArea.setText("");
            init(200+(int)(Math.random()*10), 100+(int)(Math.random()*10));
        }else if(event.getActionCommand().equals("open")){
            open();
        }else if(event.getActionCommand().equals("save")){
            save();
        }else if(event.getActionCommand().equals("quit")){
            quit();
        }else if(event.getActionCommand().equals("copy")){
            copy();
        }else if(event.getActionCommand().equals("cut")){
            cut();
        }else if(event.getActionCommand().equals("paste")){
            paste();
        }else if(event.getActionCommand().equals("undo")){
            if(um.canUndo()){
                um.undo();
            }
        }else if(event.getActionCommand().equals("redo")){
            if(um.canRedo()){
                um.redo();
            }
        }else if(event.getActionCommand().equals("clearAll")){
            textArea.setText("");
        }else if(event.getActionCommand().equals("findRep")){
            findAndReplace();
        }
    }
}
