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

        // Caps total # of results at 200 
        results = new JTextField[200];
        JTextField searchBox = new JTextField();

        // Set a larger font for the search box
        searchBox.setFont(new Font("Arial", Font.PLAIN, 45)); 
        searchBox.setText("");
        searchBox.setPreferredSize(new Dimension(700, 80));
        searchBox.setEditable(true);

        // Initialize layout and components
        vBox = Box.createVerticalBox();
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        // Add vBox to a scrollable panel
        JScrollPane scrollPane = new JScrollPane(centerPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16); // Adjust scrolling speed
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel contentPanel = (JPanel) getContentPane();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(searchBox, BorderLayout.SOUTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(vBox, BorderLayout.NORTH); // Add vBox to the center panel

        pack();
        
        // Init vertical box layout scaling
        searchBox.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            { 
                // Execute fd with text in searchBox:
                try 
                {
                    ProcessBuilder builder = new ProcessBuilder("bin/fd", searchBox.getText(), "/");
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

                            // Create result JTextField with larger font
                            results[count] = new JTextField(line);
                            results[count].setFont(new Font("Arial", Font.PLAIN, 45)); // Font size 45
                            results[count].setEditable(false); // Make result text non-editable
                        
                            vBox.add(results[count]);
                            vBox.revalidate(); // Refresh layout of vBox
                            vBox.repaint();    // Repaint vBox

                            // Repack the entire window to adjust its size automatically
                            SwingUtilities.getWindowAncestor(vBox).pack();
                        }
                    } catch (Exception e1) 
                    {
                        System.out.println("[1] Sorry something went wrong. ");
                        e1.printStackTrace();
                    }

                    p.waitFor();

                } catch (Exception e2) 
                {
                    System.out.println("[2] Sorry something went wrong. ");
                    e2.printStackTrace();
                }
            }
        });
    } 

    // Provided user clicks a returned result, will open directory of their selection
    public static void openDirectory(String selectedResult) 
    {
        try 
        {
            Process p = Runtime.getRuntime().exec(new String[] { "xdg-open", selectedResult });
        } catch (IOException e2) 
        {
            System.out.println("Error executing command. Please try restarting. ");
            e2.printStackTrace();
        }
    }

    public static void main(String[] args) 
    {   
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new spotlightSearch().setVisible(true);
            }
        });
    } 
}
