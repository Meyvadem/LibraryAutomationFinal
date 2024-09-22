/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package componentLib;

import com.mycompany.cpdeneme.Member;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Merve
 */
public class LibMemberManagementPanel extends javax.swing.JFrame {

    @Override
    public int checkImage(Image image, ImageObserver observer) {
        return super.checkImage(image, observer); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    Connection con;
    DefaultTableModel tm = new DefaultTableModel();

    /**
     * Creates new form LibMemberManagementPanel
     */
    public LibMemberManagementPanel() {
        initComponents();
        JTable.setModel(tm);
        tm.setColumnIdentifiers(new String[]{"  ID", "  First Name", "  Last Name", "  User Name", "  Password"});

        try {
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/Library Management", "App", "123");
        } catch (SQLException ex) {
            Logger.getLogger(LibMemberManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        fillMemberTable();

    }

    void fillMemberTable() {
        String sql = "SELECT * FROM member";
        try ( Statement s = con.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                tm.addRow(new Object[]{rs.getInt("member_id"), rs.getString("first_name"), rs.getString("last_name"),
                    rs.getString("username"), rs.getInt("password")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(LibMemberManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    boolean isMemberIdExist(int memberID) {
        String sql = "SELECT * FROM member WHERE member_id=?";
        try ( PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberID);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(LibMemberManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    void saveData() {
        if (txtID.getText().isEmpty() || txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() || txtUsername.getText().isEmpty()
                || txtPassword.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "There is an unfilled box or boxes.", "Missing Information", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isMemberIdExist(Integer.parseInt(txtID.getText()))) {
            String sqlUpdate = "UPDATE member "
                    + "SET first_name=?, last_name=?, username=?, password=? "
                    + "WHERE member_id=?";
            try ( PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                psUpdate.setString(1, txtFirstName.getText());
                psUpdate.setString(2, txtLastName.getText());
                psUpdate.setString(3, txtUsername.getText());
                psUpdate.setInt(4, Integer.parseInt(txtPassword.getText()));
                psUpdate.setInt(5, Integer.parseInt(txtID.getText()));
                psUpdate.executeUpdate();

                String sqlQuery = "SELECT * FROM member WHERE member_id=?";
                try ( PreparedStatement psQuery = con.prepareStatement(sqlQuery)) {
                    psQuery.setInt(1, Integer.parseInt(txtID.getText()));
                    ResultSet rsQuery = psQuery.executeQuery();
                    while (rsQuery.next()) {

                        int rowCount = tm.getRowCount();
                        for (int i = 0; i < rowCount; i++) {

                            if (tm.getValueAt(i, 0).toString().equals(txtID.getText())) {
                                tm.setValueAt(txtFirstName.getText(), i, 1);
                                tm.setValueAt(txtLastName.getText(), i, 2);
                                tm.setValueAt(txtUsername.getText(), i, 3);
                                tm.setValueAt(txtPassword.getText(), i, 4);

                                break;
                            }
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(LibMemberManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

                txtID.setText("");
                txtFirstName.setText("");
                txtLastName.setText("");
                txtUsername.setText("");
                txtPassword.setText("");

            } catch (SQLException ex) {
                Logger.getLogger(LibMemberManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String sqlInsert = "INSERT INTO member"
                    + "(member_id, first_name,last_name,username,password)"
                    + "VALUES (?,?,?,?,?)";
            try ( PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, Integer.parseInt(txtID.getText()));
                psInsert.setString(2, txtFirstName.getText());
                psInsert.setString(3, txtLastName.getText());
                psInsert.setString(4, txtUsername.getText());
                psInsert.setInt(5, Integer.parseInt(txtPassword.getText()));

                psInsert.executeUpdate();

                Vector newData = new Vector();
                newData.add(txtID.getText());
                newData.add(txtFirstName.getText());
                newData.add(txtLastName.getText());
                newData.add(txtUsername.getText());
                newData.add(txtPassword.getText());
                newData.add(true);
                tm.addRow(newData);

                txtID.setText("");
                txtFirstName.setText("");
                txtLastName.setText("");
                txtUsername.setText("");
                txtPassword.setText("");

            } catch (SQLException ex) {
                Logger.getLogger(LibMemberManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void deleteMemberData() {
        if (JTable.getSelectedRow() == -1) {
            JOptionPane.showConfirmDialog(this, "Please Select A Member!", "Member Selectin is Null", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int currValue = Integer.parseInt(JTable.getValueAt(JTable.getSelectedRow(), 0).toString());

        String sqlBook = "SELECT * FROM book WHERE member_id=?";
        try ( PreparedStatement psBook = con.prepareStatement(sqlBook)) {
            psBook.setInt(1, currValue);
            ResultSet rsBook = psBook.executeQuery();
            while (rsBook.next()) {

                String sqlUpdate = "UPDATE book "
                        + "SET member_id=?,is_available=? "
                        + "WHERE member_id=?";
                try ( PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                    ps.setNull(1, Types.INTEGER);
                    ps.setBoolean(2, true);
                    ps.setInt(3, currValue);
                    ps.executeUpdate();

                    String sqDelete = ("DELETE FROM member "
                            + "WHERE member_id=?");
                    try ( PreparedStatement psDelete = con.prepareStatement(sqDelete)) {
                        psDelete.setInt(1, currValue);
                        psDelete.executeUpdate();

                        String sql = ("DELETE FROM issue_book "
                                + "WHERE issue_book_id=?");
                        try ( PreparedStatement ps1 = con.prepareStatement(sql)) {
                            ps1.setInt(1, rsBook.getInt("book_id"));
                            ps1.executeUpdate();

                            tm.removeRow(JTable.getSelectedRow());

                        } catch (SQLException ex) {
                            Logger.getLogger(LibReturnBookPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(LibReturnBookPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(LibReturnBookPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(LibReturnBookPanel.class.getName()).log(Level.SEVERE, null, ex);
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
        btnBack = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtFirstName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtLastName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        btnAddMember = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 248, 232));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(253, 217, 135), 2));

        jPanel2.setBackground(new java.awt.Color(0, 153, 153));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 32)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 228, 168));
        jLabel1.setText("Costumer Management");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(245, 245, 245)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        btnBack.setBackground(new java.awt.Color(186, 186, 186));
        btnBack.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        JTable.setBackground(new java.awt.Color(239, 239, 255));
        JTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(JTable);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("ID : ");

        txtID.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("First Name : ");

        txtFirstName.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Last Name :");

        txtLastName.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("User Name : ");

        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Password : ");

        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        btnAddMember.setBackground(new java.awt.Color(204, 204, 204));
        btnAddMember.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        btnAddMember.setText("Save");
        btnAddMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMemberActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(204, 204, 204));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel8.setText("Member Detail");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 768, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3))
                                    .addGap(62, 62, 62)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 171, Short.MAX_VALUE)
                                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2))
                                    .addGap(62, 62, 62)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(btnAddMember, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addContainerGap(61, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(355, 355, 355)
                .addComponent(jLabel8)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddMember))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addGap(3, 3, 3)
                        .addComponent(btnDelete))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(12, 12, 12)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnBack)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        LibrarienHomePanel a = new LibrarienHomePanel();
        a.setVisible(true);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnAddMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMemberActionPerformed
        // TODO add your handling code here:
        saveData();
    }//GEN-LAST:event_btnAddMemberActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteMemberData();
    }//GEN-LAST:event_btnDeleteActionPerformed

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
            java.util.logging.Logger.getLogger(LibMemberManagementPanel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LibMemberManagementPanel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LibMemberManagementPanel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LibMemberManagementPanel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LibMemberManagementPanel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable JTable;
    private javax.swing.JButton btnAddMember;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDelete;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
