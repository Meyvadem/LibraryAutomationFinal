/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package componentLib;

import com.mycompany.cpdeneme.Book;
import com.mycompany.cpdeneme.BookDatabase;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Merve
 */
public class LibRegisterBookPanel extends javax.swing.JFrame {

    DefaultTableModel bookTable = new DefaultTableModel();
    Connection con;

    public LibRegisterBookPanel() {
        initComponents();

        jTable1.setModel(bookTable);
        bookTable.setColumnIdentifiers(new String[]{"  ID", "  Title", "  Author", "  Category", "  Price", "  Is Available"});

        try {
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/Library Management", "App", "123");
        } catch (SQLException ex) {
            Logger.getLogger(LibRegisterBookPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        fillBookTable();
    }

    boolean isBookIdExist(int bookID) {
        String sql = "SELECT * FROM book WHERE book_id=?";
        try ( PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookID);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(LibRegisterBookPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    void fillBookTable() {
        String sql = "SELECT * FROM book";
        try ( Statement s = con.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                bookTable.addRow(new Object[]{rs.getInt("book_id"), rs.getString("title"), rs.getString("author_name"),
                    rs.getString("category"), rs.getInt("price"), rs.getBoolean("is_available")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(LibRegisterBookPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void saveData() {
        if (txtID.getText().isEmpty() || txtAuthor.getText().isEmpty() || txtTitle.getText().isEmpty()
                || txtPrice.getText().isEmpty() || txtCategory.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "There is an unfilled box or boxes.", "Missing Information", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isBookIdExist(Integer.parseInt(txtID.getText()))) {
            String sqlUpdate = "UPDATE book "
                    + "SET title=?, author_name=?, category=?, price=? "
                    + "WHERE book_id=?";
            try ( PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                psUpdate.setString(1, txtTitle.getText());
                psUpdate.setString(2, txtAuthor.getText());
                psUpdate.setString(3, txtCategory.getText());
                psUpdate.setInt(4, Integer.parseInt(txtPrice.getText()));
                psUpdate.setInt(5, Integer.parseInt(txtID.getText()));
                psUpdate.executeUpdate();

                String sqlBook = "SELECT * FROM book WHERE book_id=?";
                try ( PreparedStatement psBook = con.prepareStatement(sqlBook)) {
                    psBook.setInt(1, Integer.parseInt(txtID.getText()));
                    ResultSet rsBook = psBook.executeQuery();
                    while (rsBook.next()) {

                        int rowCount = bookTable.getRowCount();
                        for (int i = 0; i < rowCount; i++) {

                            if (bookTable.getValueAt(i, 0).toString().equals(txtID.getText())) {
                                bookTable.setValueAt(txtTitle.getText(), i, 1);
                                bookTable.setValueAt(txtAuthor.getText(), i, 2);
                                bookTable.setValueAt(txtCategory.getText(), i, 3);
                                bookTable.setValueAt(txtPrice.getText(), i, 4);
                                bookTable.setValueAt(rsBook.getBoolean("is_available"), i, 5);
                                break;
                            }
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(LibRegisterBookPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

                txtID.setText("");
                txtTitle.setText("");
                txtAuthor.setText("");
                txtCategory.setText("");
                txtPrice.setText("");

            } catch (SQLException ex) {
                Logger.getLogger(LibRegisterBookPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String sqlInsert = "INSERT INTO book"
                    + "(book_id, title,author_name,category,price,is_available)"
                    + "VALUES (?,?,?,?,?,?)";
            try ( PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, Integer.parseInt(txtID.getText()));
                psInsert.setString(2, txtTitle.getText());
                psInsert.setString(3, txtAuthor.getText());
                psInsert.setString(4, txtCategory.getText());
                psInsert.setInt(5, Integer.parseInt(txtPrice.getText()));
                psInsert.setBoolean(6, true);
                psInsert.executeUpdate();

                Vector newData = new Vector();
                newData.add(txtID.getText());
                newData.add(txtTitle.getText());
                newData.add(txtAuthor.getText());
                newData.add(txtCategory.getText());
                newData.add(txtPrice.getText());
                newData.add(true);
                bookTable.addRow(newData);

                txtID.setText("");
                txtTitle.setText("");
                txtAuthor.setText("");
                txtCategory.setText("");
                txtPrice.setText("");

            } catch (SQLException ex) {
                Logger.getLogger(LibRegisterBookPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void deleteData() {
        if (jTable1.getSelectedRow() == -1) {
            JOptionPane.showConfirmDialog(this, "Please Select A Costumer!", "Costumer Selectin is Null", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sqlBook = "SELECT * FROM book WHERE book_id=?";
        try ( PreparedStatement psBook = con.prepareStatement(sqlBook)) {
            psBook.setInt(1, Integer.parseInt(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString()));
            ResultSet rsBook = psBook.executeQuery();
            while (rsBook.next()) {
                
                String sqDelete = ("DELETE FROM book "
                        + "WHERE book_id=?");

                int currValue = Integer.parseInt(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString());
                try ( PreparedStatement psDelete = con.prepareStatement(sqDelete)) {
                    psDelete.setInt(1, currValue);
                    psDelete.executeUpdate();

                    String sql = ("DELETE FROM issue_book "
                            + "WHERE issue_book_id=?");
                    try ( PreparedStatement ps1 = con.prepareStatement(sql)) {
                        ps1.setInt(1, rsBook.getInt("book_id"));
                        ps1.executeUpdate();

                        bookTable.removeRow(jTable1.getSelectedRow());

                    } catch (SQLException ex) {
                        Logger.getLogger(LibRegisterBookPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(LibRegisterBookPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(LibRegisterBookPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnSave = new javax.swing.JButton();
        txtID = new javax.swing.JTextField();
        txtTitle = new javax.swing.JTextField();
        txtAuthor = new javax.swing.JTextField();
        txtCategory = new javax.swing.JTextField();
        txtPrice = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 250, 221));

        jPanel2.setBackground(new java.awt.Color(0, 0, 153));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 40)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Register Book");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(342, 342, 342)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(14, 14, 14))
        );

        jTable1.setBackground(new java.awt.Color(204, 255, 242));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        btnSave.setBackground(new java.awt.Color(204, 204, 204));
        btnSave.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        txtID.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        txtTitle.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        txtAuthor.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        txtCategory.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        txtPrice.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("ID :");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Title :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Author :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Category : ");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("Price :");

        btnDelete.setBackground(new java.awt.Color(204, 204, 204));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(196, 196, 196));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel6.setText("Book Details");

        jButton2.setBackground(new java.awt.Color(204, 204, 204));
        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton2.setText("Record Data to File");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(331, 331, 331)
                                .addComponent(jLabel6))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 722, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 85, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(txtID, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(txtTitle)
                            .addComponent(txtAuthor))
                        .addGap(62, 62, 62)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(txtPrice, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                        .addComponent(txtCategory, javax.swing.GroupLayout.Alignment.LEADING))
                                    .addComponent(jLabel7))
                                .addGap(81, 81, 81)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE))
                                    .addComponent(jButton2)))
                            .addComponent(jLabel5))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(txtTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel7)
                            .addComponent(btnDelete))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addGap(65, 65, 65))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 739, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveData();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteData();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        LibBookManagementPanel a = new LibBookManagementPanel();
        a.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        String sql = "SELECT * FROM book";
        ArrayList<String> names = new ArrayList<>();
        try ( Statement s = con.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                names.add(rs.getInt("book_id") + "   " + rs.getString("title") + "   " + rs.getString("author_name")
                        + "   " + rs.getString("category") + "   " + rs.getInt("price") + "   " + rs.getBoolean("is_available"));

                try ( BufferedWriter file = new BufferedWriter(new FileWriter("C:\\Files\\BookDetails.txt"))) {
                    file.write("Id - Title - Author Name - Category - Price - Is Available");
                    file.newLine();
                    file.write(" ");
                    file.newLine();

                    for (String name : names) {
                        file.write(name);
                        file.newLine();
                        file.flush();
                    }

                } catch (IOException ex) {
                    Logger.getLogger(LibRegisterBookPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(LibRegisterBookPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LibRegisterBookPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LibRegisterBookPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LibRegisterBookPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LibRegisterBookPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LibRegisterBookPanel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtAuthor;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtTitle;
    // End of variables declaration//GEN-END:variables
}
