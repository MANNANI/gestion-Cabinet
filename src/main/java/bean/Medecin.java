/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author houda
 */
@Entity
public class Medecin extends Employe implements Serializable {

    @ManyToOne
    private Specialite specialite;
    @OneToMany(mappedBy = "medecin")
    private List<MedecinSecretaire> medecinSecretaires;
    @OneToMany(mappedBy = "medecin")
    private List<Rdv> rdvs;
    @ManyToOne
    private Residence residence;
    @OneToOne
    private Configuration configuration;
    @OneToMany(mappedBy = "medecin")
    private List<Marge> marges;
    @OneToMany(mappedBy = "medecin")
    private List<NoteService> noteServices;

    public Medecin(Specialite specialite, Residence residence, Configuration configuration) {
        this.specialite = specialite;
        this.residence = residence;
        this.configuration = configuration;
    }

    
    public Medecin() {
       super();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.specialite);
        hash = 79 * hash + Objects.hashCode(this.residence);
        hash = 79 * hash + Objects.hashCode(this.configuration);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Medecin other = (Medecin) obj;
        if (!Objects.equals(this.specialite, other.specialite)) {
            return false;
        }
        if (!Objects.equals(this.residence, other.residence)) {
            return false;
        }
        if (!Objects.equals(this.configuration, other.configuration)) {
            return false;
        }
        return true;
    }

    
    @Override
    public String toString() {
        return "Med" +super.toString()+ "specialite=" + specialite;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public List<MedecinSecretaire> getMedecinSecretaires() {
        return medecinSecretaires;
    }

    public void setMedecinSecretaires(List<MedecinSecretaire> medecinSecretaires) {
        this.medecinSecretaires = medecinSecretaires;
    }

    public List<Rdv> getRdvs() {
        return rdvs;
    }

    public void setRdvs(List<Rdv> rdvs) {
        this.rdvs = rdvs;
    }

    public Residence getResidence() {
        return residence;
    }

    public void setResidence(Residence residence) {
        this.residence = residence;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<Marge> getMarges() {
        return marges;
    }

    public void setMarges(List<Marge> marges) {
        this.marges = marges;
    }

    public List<NoteService> getNoteServices() {
        return noteServices;
    }

    public void setNoteServices(List<NoteService> noteServices) {
        this.noteServices = noteServices;
    }

}
