/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package componentLib;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Merve
 */
@Entity
@Table(name = "LIBRARIEN")
@NamedQueries({
    @NamedQuery(name = "Librarien.findAll", query = "SELECT l FROM Librarien l"),
    @NamedQuery(name = "Librarien.findByLibrarienId", query = "SELECT l FROM Librarien l WHERE l.librarienId = :librarienId"),
    @NamedQuery(name = "Librarien.findByFirstName", query = "SELECT l FROM Librarien l WHERE l.firstName = :firstName"),
    @NamedQuery(name = "Librarien.findByLastName", query = "SELECT l FROM Librarien l WHERE l.lastName = :lastName"),
    @NamedQuery(name = "Librarien.findByUsername", query = "SELECT l FROM Librarien l WHERE l.username = :username"),
    @NamedQuery(name = "Librarien.findByPassword", query = "SELECT l FROM Librarien l WHERE l.password = :password")})
public class Librarien implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "LIBRARIEN_ID")
    private Integer librarienId;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Basic(optional = false)
    @Column(name = "USERNAME")
    private String username;
    @Basic(optional = false)
    @Column(name = "PASSWORD")
    private int password;

    public Librarien() {
    }

    public Librarien(Integer librarienId) {
        this.librarienId = librarienId;
    }

    public Librarien(Integer librarienId, String username, int password) {
        this.librarienId = librarienId;
        this.username = username;
        this.password = password;
    }

    public Integer getLibrarienId() {
        return librarienId;
    }

    public void setLibrarienId(Integer librarienId) {
        this.librarienId = librarienId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (librarienId != null ? librarienId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Librarien)) {
            return false;
        }
        Librarien other = (Librarien) object;
        if ((this.librarienId == null && other.librarienId != null) || (this.librarienId != null && !this.librarienId.equals(other.librarienId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "componentLib.Librarien[ librarienId=" + librarienId + " ]";
    }
    
}
