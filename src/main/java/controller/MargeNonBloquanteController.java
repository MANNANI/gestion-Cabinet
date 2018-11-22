package controller;

import bean.Employe;
import bean.MargeNonBloquante;
import bean.Medecin;
import bean.Vacance;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.MargeNonBloquanteFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.poi.hssf.record.VCenterRecord;
import org.primefaces.model.DualListModel;
import service.MedecinFacade;
import service.VacanceFacade;

@Named("margeNonBloquanteController")
@SessionScoped
public class MargeNonBloquanteController implements Serializable {

    @EJB
    private MedecinFacade medecinFacade;
    @EJB
    private VacanceFacade vacanceFacade;
    @EJB
    private service.MargeNonBloquanteFacade ejbFacade;
    private List<MargeNonBloquante> items = null;
    private MargeNonBloquante selected;
    private List<Vacance> vacances = new ArrayList<>();
    private Employe med = new Medecin();
    private DualListModel<Vacance> vac;

    
    @PostConstruct
    public void init() {
       
       List<Vacance> themesSource =vacanceFacade.findAll();
        List<Vacance> themesTarget = new ArrayList<Vacance>();
         vac = new DualListModel<Vacance>(themesSource, themesTarget);
         
    }

    public DualListModel<Vacance> getVac() {
        return vac;
    }

    public void setVac(DualListModel<Vacance> vac) {
        this.vac = vac;
    }
    
    

    public MargeNonBloquanteController() {
    }

    public Employe getMed() {
        if (med == null) {
            med = new Medecin();
        }
        return med;
    }

    public void setMed(Employe med) {
        this.med = med;
    }

    public List<Vacance> getVacances() {
        return vacances;
    }

    public void setVacances(List<Vacance> vacances) {
        this.vacances = vacances;
    }

    public MargeNonBloquante getSelected() {
        return selected;
    }

    public void setSelected(MargeNonBloquante selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MargeNonBloquanteFacade getFacade() {
        return ejbFacade;
    }

    public MargeNonBloquante prepareCreate() {
        selected = new MargeNonBloquante();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
      med  =(Medecin) util.Session.getAttribut("UserEmploye");
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MargeNonBloquanteCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MargeNonBloquanteUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MargeNonBloquanteDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<MargeNonBloquante> getItems() {
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
                    getFacade().creer(vacances, med);

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

    public MargeNonBloquante getMargeNonBloquante(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<MargeNonBloquante> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MargeNonBloquante> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = MargeNonBloquante.class)
    public static class MargeNonBloquanteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MargeNonBloquanteController controller = (MargeNonBloquanteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "margeNonBloquanteController");
            return controller.getMargeNonBloquante(getKey(value));
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
            if (object instanceof MargeNonBloquante) {
                MargeNonBloquante o = (MargeNonBloquante) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MargeNonBloquante.class.getName()});
                return null;
            }
        }

    }

}
