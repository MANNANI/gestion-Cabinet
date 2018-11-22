package controller;

import bean.MargeBloquante;
import bean.MargeItem;
import bean.Medecin;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.MargeBloquanteFacade;

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
import org.primefaces.event.SelectEvent;
import service.MargeItemFacade;
import service.MedecinFacade;

@Named("margeBloquanteController")
@SessionScoped
public class MargeBloquanteController implements Serializable {

    @EJB
    private MedecinFacade medecinFacade;
    @EJB
    private MargeItemFacade margeItemFacade;
    @EJB
    private service.MargeBloquanteFacade ejbFacadeBl;
    private List<MargeBloquante> items = null;
    private MargeBloquante selected;
    private MargeItem margeItem;
    private List<MargeItem> margeItems = new ArrayList<>();
    private Date dateDebut = new Date();
    private Date dateFin = new Date();
    private Medecin medecine;

    public List<Medecin> comboMedecin() {
        System.out.println("ha les med " + medecinFacade.findAll());
        return medecinFacade.findAll();
    }

    public MargeBloquanteController() {
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public Medecin getMedecine() {
       medecine=(Medecin)util.Session.getAttribut("UserEmploye");
        return medecine;
    }

    public void setMedecine(Medecin medecine) {
        this.medecine = medecine;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public MargeItem getMargeItem() {
        if (margeItem == null) {
            margeItem = new MargeItem();
        }
        return margeItem;
    }

    public void setMargeItem(MargeItem margeItem) {
        this.margeItem = margeItem;
    }

    public MargeBloquante getSelected() {
        if (selected == null) {
            selected = new MargeBloquante();
        }
        return selected;
    }

    public List<MargeItem> getMargeItems() {
        if (margeItems == null) {
            margeItems = new ArrayList<>();
        }
        return margeItems;
    }

    public void setMargeItems(List<MargeItem> margeItems) {
        this.margeItems = margeItems;
    }

    public void setSelected(MargeBloquante selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MargeBloquanteFacade getFacade() {
        return ejbFacadeBl;
    }

    /*MargeItem*/
    public void createMargeItem() {
        margeItem.setId(margeItemFacade.generateId("MargeItem", "id"));
        margeItems.add(margeItem);
        margeItem = new MargeItem();
    }

    /*margueBloquante*/
    public MargeBloquante prepareCreate() {
        selected = new MargeBloquante();
        initializeEmbeddableKey();
        return selected;
    }

    public String columnTableMargeItem(MargeItem margeItem) {
        String type = "";
        if (margeItem.getAnnee() != 0) {
            type = "" + margeItem.getAnnee() + " annee ";
        }
        if (margeItem.getJour() != 0) {
            type = "" + margeItem.getJour() + " jour ";
        }
        if (margeItem.getMois() != 0) {
            type = "" + margeItem.getMois() + " mois ";
        }
        return type;
    }

    public void create() {
        selected=new MargeBloquante();
        selected.setDateFin(dateFin);
        selected.setDateDebut(dateDebut);
         medecine=(Medecin)util.Session.getAttribut("UserEmploye");
        System.out.println("haa le medecin"+medecine);
        ejbFacadeBl.creer(margeItems, selected,medecine);
        margeItems = new ArrayList<>();
        selected = new MargeBloquante();
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MargeBloquanteUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MargeBloquanteDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<MargeBloquante> getItems() {
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

    public MargeBloquante getMargeBloquante(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<MargeBloquante> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MargeBloquante> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter("margeBloquanteConverter")
    public static class MargeBloquanteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MargeBloquanteController controller = (MargeBloquanteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "margeBloquanteController");
            return controller.getMargeBloquante(getKey(value));
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
            if (object instanceof MargeBloquante) {
                MargeBloquante o = (MargeBloquante) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MargeBloquante.class.getName()});
                return null;
            }
        }

    }

}
