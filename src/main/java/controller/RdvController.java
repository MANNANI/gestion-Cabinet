package controller;

import bean.Employe;
import bean.Medecin;
import bean.MedecinSecretaire;
import bean.Patient;
import bean.Rdv;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.RdvFacade;

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
import service.EmployeFacade;
import service.MargeBloquanteFacade;

@Named("rdvController")
@SessionScoped
public class RdvController implements Serializable {

    @EJB
    private service.RdvFacade ejbFacade;
    @EJB
    private MargeBloquanteFacade margeBloquanteFacade;
    @EJB
    private EmployeFacade employeFacade;
    private List<Rdv> items = null;
    private Rdv selected;

    public RdvController() {
    }

    public Rdv getSelected() {
        if (selected == null) {
            selected = new Rdv();
        }
            return selected;
    }

    public void setSelected(Rdv selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private RdvFacade getFacade() {
        return ejbFacade;
    }

    public Rdv prepareCreate() {
        selected = new Rdv();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RdvCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RdvUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RdvDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
public String nbrJourRestent(Rdv rdv) {
        if (rdv != null) {
            Date date = new Date();
            int res = rdv.getDateDebut().getDate() - date.getDate();
            return "" + res;
        }
        return "?";
    }

    public void editDateRdv() {
        FacesContext context = FacesContext.getCurrentInstance();
        MedecinSecretaire ms = (MedecinSecretaire) util.Session.getAttribut("UserEmploye");
        boolean isExiste = margeBloquanteFacade.testMarge(selected.getDateDebut(), ms.getMedecin());
        if (isExiste) {
            ejbFacade.edit(selected);
        } 
    }

    public List<Rdv> getItems() {
        Medecin medecin=new Medecin();
        Employe employe = (Employe) util.Session.getAttribut("UserEmploye");
        String type = employeFacade.findUserFonction(employe);
        if (type.equals("MedecinSecretaire")) {
            MedecinSecretaire ms = (MedecinSecretaire) util.Session.getAttribut("UserEmploye");
         medecin = ms.getMedecin();
        } else {
        medecin = (Medecin) util.Session.getAttribut("UserEmploye");
        }
       
             items = ejbFacade.RdvsByMedecin(medecin);
       
        if (items == null) {
            items = new ArrayList<>();
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

    public Rdv getRdv(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Rdv> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Rdv> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Rdv.class)
    public static class RdvControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RdvController controller = (RdvController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rdvController");
            return controller.getRdv(getKey(value));
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
            if (object instanceof Rdv) {
                Rdv o = (Rdv) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Rdv.class.getName()});
                return null;
            }
        }

    }

    public void acceptDemandeRdv(Rdv rdv) {
        rdv.setConfirmer(true);
        selected = rdv;
        update();
    }

    public void deleteDemandeRdv(Rdv rdv) {
        selected = rdv;
        destroy();
    }

    public void detailPatient(Rdv rdv) {
        selected = rdv;
    }

}
