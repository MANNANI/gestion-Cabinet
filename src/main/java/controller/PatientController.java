package controller;

import bean.Medecin;
import bean.Patient;
import bean.Rdv;
import bean.UserConnexion;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.PatientFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.context.RequestContext;
import service.EmployeFacade;

@Named("patientController")
@SessionScoped
public class PatientController implements Serializable {

    private List<Patient> items = null;
    private Patient selected;
    private Medecin medecin;
    private List<Medecin> medecins;
    //les declaration
    private String anciennePassword;
    private String nouveauPassword;
    private String confirmationDeNouveauPassword;
    private List<Rdv> rdvs;
    private String nomRech;
    private String prenomRech;
    private String etat;

    @EJB
    private service.PatientFacade ejbFacade;
    @EJB
    private service.UserConnexionFacade connexionFacade;
    @EJB
    private service.RdvFacade rdvFacade;

    public PatientController() {
    }

    public Patient getSelected() {
        selected = (Patient) util.Session.getAttribut("UserPatient");
        if (selected == null) {
            selected = new Patient();
        }
        return selected;
    }

    public void listEtat() {
        items = ejbFacade.listEtat(etat);
    }

    public void setSelected(Patient selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PatientFacade getFacade() {
        return ejbFacade;
    }

    public void rechercheMedecin() {
        medecins.clear();
//        System.out.println("ha le nom " + nomRech);
//        selected = new Patient();
//        selected.setNom(nomRech);
//        selected.setPrenom(prenomRech);
//        Patient patient = ejbFacade.recherchePat(selected);
//        util.Session.setAttribut("PatientRech", patient);
//        System.out.println("ha spec" + medecin.getSpecialite());
        medecins = getFacade().rechercheMedecinPerReSp(medecin.getSpecialite(), medecin.getResidence());
    }

    public Patient prepareCreate() {
        selected = new Patient();
        initializeEmbeddableKey();
        RequestContext.getCurrentInstance().execute("PF('PatientCreateDialog').show()");
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PatientCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PatientUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("PatientDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Patient> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public String getNomRech() {
        return nomRech;
    }

    public void setNomRech(String nomRech) {
        this.nomRech = nomRech;
    }

    public String getPrenomRech() {
        return prenomRech;
    }

    public void setPrenomRech(String prenomRech) {
        this.prenomRech = prenomRech;
    }

    public Patient getPatient(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Patient> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Patient> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public Medecin getMedecin() {
        if (medecin == null) {
            medecin = new Medecin();
        }
        return medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public List<Medecin> getMedecins() {
        if (medecins == null) {
            medecins = new ArrayList<>();
        }
        return medecins;
    }

    public void setMedecins(List<Medecin> medecins) {
        this.medecins = medecins;
    }

    public String getAnciennePassword() {
        return anciennePassword;
    }

    public void setAnciennePassword(String anciennePassword) {
        this.anciennePassword = anciennePassword;
    }

    public String getNouveauPassword() {
        return nouveauPassword;
    }

    public void setNouveauPassword(String nouveauPassword) {
        this.nouveauPassword = nouveauPassword;
    }

    public String getConfirmationDeNouveauPassword() {
        return confirmationDeNouveauPassword;
    }

    public void setConfirmationDeNouveauPassword(String confirmationDeNouveauPassword) {
        this.confirmationDeNouveauPassword = confirmationDeNouveauPassword;
    }

    public List<Rdv> getRdvs() {
        rdvs = rdvFacade.rdvParPatient(getSelected());
        if (rdvs == null) {
            rdvs = new ArrayList();
        }
        return rdvs;
    }

    public void changePasseWordHashage() {
        if (selected != null) {
            if (selected.getUserConnexion() != null) {
                String str1 = util.HashageUtil.sha256(anciennePassword);
                String str2 = util.HashageUtil.sha256(nouveauPassword);
                String str3 = util.HashageUtil.sha256(confirmationDeNouveauPassword);
                if (selected.getUserConnexion().getPassword().equals(str1) && str2.equals(str3)) {
                    UserConnexion user = selected.getUserConnexion();
                    user.setPassword(str3);
                    user.setMdpChanged(true);
                    System.out.println(" ha le user.is changed" + user.isMdpChanged());
                    connexionFacade.edit(user);
                    System.out.println("it works");
                } else {
                    System.out.println("error");
                }
            }
        }
    }

    public void setRdvs(List<Rdv> rdvs) {
        this.rdvs = rdvs;
    }

    public String nbrJourRestent(Rdv rdv) {
        if (rdv != null) {
            Date date = new Date();
            int res = rdv.getDateDebut().getDate() - date.getDate();
            return "" + res;
        }
        return "?";
    }

    public String patientRdv(Medecin medecin) {
        if (medecin != null) {
            util.Session.delete("MedecinPatient");
            util.Session.setAttribut("MedecinPatient", medecin);
        }
        return "AgendaPatient?faces-redirect=true";

    }

    @FacesConverter(forClass = Patient.class)
    public static class PatientControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PatientController controller = (PatientController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "patientController");
            return controller.getPatient(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Patient) {
                Patient o = (Patient) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Patient.class.getName()});
                return null;
            }
        }

    }

    public void detailMedecin(Medecin m) {
        medecin = new Medecin();
        medecin = m;
    }

    public void deletRdv(Rdv r) {
        System.out.println("i ma insside deletRdv ");
        if (r != null) {
            rdvFacade.remove(r);
        }
    }
}
