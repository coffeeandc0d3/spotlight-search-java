import javax.swing.event.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.AttributeSet.ColorAttribute;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import javax.swing.SwingUtilities;
import java.nio.channels.SelectableChannel;
import java.io.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Will prompt user with empty textbox 
// Will dynamically return temporary array of textfields for each returned dir
// On user selection of an index of these strings, this will be stored 
// ...as the search query.
// Program will execute xdg-open <~dir/FILE>

public class spotlightSearch extends JFrame 
{
  static String chosenQuery;

  JTextField[] results;

  static JPanel centerPanel;
  static Box vBox;

  public spotlightSearch() 
  {
    this.setSize(1000, 800);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
    this.setResizable(true);

    results = new JTextField[200];
    JTextField searchBox = new JTextField();
    searchBox.setText("");
    searchBox.setPreferredSize(new Dimension(500, 30));
    searchBox.setEditable(true);
    searchBox.setVisible(true);

    // Init vertical box layout scaling
    searchBox.addActionListener(new ActionListener() 
    {

      public void actionPerformed(ActionEvent e) 
      {
        // Execute fdfind with text in searchBox:
        try 
        {
          
          ProcessBuilder builder = new ProcessBuilder("fdfind", searchBox.getText());
          builder.inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE);
          
          Process p;
          p = builder.start();

          try (BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
               String line;
               int count = 0;
               while ((line = buf.readLine()) != null) 
               {
                 count++;
                 if (count == 200) {break;}
                 results[count] = new JTextField(line);
//               System.out.println("Found: " + line); 
              
                 vBox.add(results[count]);
                 pack();
            }
          } catch (Exception e1) {
            e1.printStackTrace();
          }

          p.waitFor();

        } catch (Exception e2) 
          {
            e2.printStackTrace();
          }
      }

    });

//  Otherwise if nothing is input, initializes all components prior to scaling
    vBox = Box.createVerticalBox();
    centerPanel = new JPanel();

    JPanel contentPanel = (JPanel) getContentPane();
    contentPanel.setLayout(new BorderLayout());
    contentPanel.add(searchBox, "South");
    contentPanel.add(centerPanel, "Center");
    centerPanel.add(vBox);
    pack();

//  Keep last 
    this.setVisible(true);
  }

  public static void main(String[] args) 
  {
      new spotlightSearch();
  } 

  public static void openDirectory(String selectedResult) 
  {
    try 
    {
      Process p = Runtime.getRuntime().exec(new String[] { "xdg-open", selectedResult });
    } catch (IOException e2) {
      System.out.println("Error executing command. Please try restarting. ");
      e2.printStackTrace();
    }
  }

}
