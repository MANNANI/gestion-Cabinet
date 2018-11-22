package controller;

import bean.Admin;
import bean.AdminSecretaire;
import bean.Configuration;
import bean.Employe;
import bean.Medecin;
import bean.MedecinSecretaire;
import bean.Residence;
import bean.Specialite;
import bean.UserConnexion;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.EmployeFacade;
import java.io.Serializable;
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
import service.AdminSecretaireFacade;
import service.DeviceFacade;
import service.MedecinFacade;
import service.UserConnexionFacade;

@Named("employeController")
@SessionScoped
public class EmployeController implements Serializable {

    private List<Employe> items;
    private Employe selected;
    private String login;
    private Medecin medecine;
    private Configuration configuration;
    private Residence residence;
    private Specialite specialite;
    private UserConnexion user;
    private String typeBloquage;
    private String etatMdp;
    private String fonction;
    @EJB
    private UserConnexionFacade userConnexionFacade;
    @EJB
    private service.EmployeFacade ejbFacade;
    @EJB
    private MedecinFacade medecinFacade;
    @EJB
    private AdminSecretaireFacade adminSecretaireFacade;
    @EJB
    private DeviceFacade deviceFacade;

    public EmployeController() {
    }

    public Employe getParam(Employe employe) {
        employe.setId(ejbFacade.generateId("Employe", "id"));
        employe.setCin(selected.getCin());
        employe.setNom(selected.getNom());
        employe.setDateNaissance(selected.getDateNaissance());
        employe.setEmail(selected.getEmail());
        employe.setPrenom(selected.getPrenom());
        employe.setTel(selected.getTel());
        return employe;
    }

    public void creerEmploye(Employe employe) {
        UserConnexion user = userConnexionFacade.creer(login);
        System.out.println("the User create is ----->" + user);
        deviceFacade.creerDevice(user, "Employe");
        employe = getParam(employe);
        user.setEmploye(employe);
        employe.setUserConnexion(user);
        ejbFacade.create(employe);
    }

//    public void createAdmin() {
//        Employe employe = new Admin();
//        creerEmploye(employe);
//        items.add(employe);
//    }
    public void createAdminSec() {
        AdminSecretaire adminSecretaire = new AdminSecretaire();
        Admin admin = (Admin) util.Session.getAttribut("UserEmploye");
        adminSecretaire.setAdmin(admin);
        creerEmploye(adminSecretaire);
        items.add(adminSecretaire);
    }

    public void createMedecinSec() {
        MedecinSecretaire medecinSecretaire = new MedecinSecretaire();
        Admin admin = (Admin) util.Session.getAttribut("UserEmploye");
        creerEmploye(medecinSecretaire);
        items.add(medecinSecretaire);
    }

    public void createMedecin() {
        Medecin medecin = new Medecin();
        Admin admin = (Admin) util.Session.getAttribut("UserEmploye");
        creerEmploye(medecin);
        items.add(medecin);
    }

    public String job() {
        return ejbFacade.findUserFonction(selected);
    }

    public void recherche() {
        System.out.println("typeBloquage --->" + typeBloquage);
        System.out.println("etatMdp --->" + etatMdp);
        System.out.println("fonction --->" + fonction);
        items = ejbFacade.recherche(typeBloquage, etatMdp, fonction);
        System.out.println("ha items resultaat------>" + items);
    }

    public void ListBloquer() {
        if (typeBloquage.equals("Bloquer")) {
            items = ejbFacade.UserBloquer();
        } else if (typeBloquage.equals("NonBloquer")) {
            items = ejbFacade.UserNotBloquer();
        }
        System.out.println(items);
    }

    public void bloquerDebloquer() {
        ejbFacade.bloquerDebloquer(selected);
    }

    public void ListEtat() {
        items = ejbFacade.listEtat(etatMdp);
        System.out.println(items);
    }

    public void prepareCreate() {
        initializeEmbeddableKey();
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("EmployeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("EmployeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("EmployeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (null != persistAction) {
                    switch (persistAction) {
                        case CREATE:
                            Employe employe;
                            /*choisir linstance*/
                            if (fonction.equals("Medecin")) {
                                employe = new Medecin();
                            } else if (fonction.equals("AdminSecretaire")) {
                                employe = new AdminSecretaire();
                            } else if (fonction.equals("MedecinSecretaire")) {
                                employe = new MedecinSecretaire();
                            }
//                            creerEmploye(employe);
                            break;
                        case UPDATE:
                            getFacade().edit(selected);
                            break;
                        case DELETE:
                            getFacade().deleteEmp(selected);
                            break;
                        default:
                            break;
                    }
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

    public Employe getEmploye(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Employe> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Employe> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter("employeControllerConverter")
    public static class EmployeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EmployeController controller = (EmployeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "employeController");
            return controller.getEmploye(getKey(value));
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
            if (object instanceof Employe) {
                Employe o = (Employe) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Employe.class.getName()});
                return null;
            }
        }

    }

    public Employe getSelected() {
        selected = (Employe) util.Session.getAttribut("UserEmploye");
        if (selected == null) {
            selected = new Employe();
        }
        System.out.println("haaa l'employe------>" + selected);
        return selected;
    }

    public void setSelected(Employe selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected = new Employe();
    }

    protected void initializeEmbeddableKey() {
        selected = new Employe();
    }

    private EmployeFacade getFacade() {
        return ejbFacade;
    }

    public UserConnexion getUser() {
        if (user == null) {
            user = new UserConnexion();
        }
        return user;
    }

    public void setUser(UserConnexion user) {
        this.user = user;
    }

    public Residence getResidence() {
        if (residence == null) {
            residence = new Residence();
        }
        return residence;
    }

    public void setResidence(Residence residence) {
        this.residence = residence;
    }

    public Specialite getSpecialite() {
        if (specialite == null) {
            specialite = new Specialite();
        }
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Medecin getMedecine() {
        if (medecine == null) {
            medecine = new Medecin();
        }
        return medecine;
    }

    public String getFonction() {
        if (fonction == null) {
            fonction = "";
        }
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public void setMedecine(Medecin medecine) {
        this.medecine = medecine;
    }

    public List<Employe> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public EmployeFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(EmployeFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEtatMdp() {
        if (etatMdp == null) {
            etatMdp = "";
        }
        return etatMdp;
    }

    public void setEtatMdp(String etatMdp) {
        this.etatMdp = etatMdp;
    }

    public String getTypeBloquage() {
        if (typeBloquage == null) {
            typeBloquage = "";
        }
        return typeBloquage;
    }

    public void setTypeBloquage(String typeBloquage) {
        this.typeBloquage = typeBloquage;
    }

//    
//      public void bloquer() {
//        if (selected.getUserConnexion() != null) {
//            selected.getUserConnexion().setBlocked(1);
//            userConnexionFacade.edit(selected.getUserConnexion());
//        } else {
//            /*afficher un message*/
//            System.out.println("po de user connexion ");
//        }
//    }
//
//    /*debloquer un user*/
//    public void debloquer() throws MessagingException {
//        if (selected.getUserConnexion() != null) {
//            selected.getUserConnexion().setBlocked(0);
//            selected.getUserConnexion().setPassword(util.HashageUtil.genererMdp());
//            selected.getUserConnexion().setMdpChanged(false);
//            userConnexionFacade.edit(selected.getUserConnexion());
//            util.EmailUtil.sendMail("projetcabinet28@gmail.com", "test123456/", util.EmailUtil.recupererMdp(selected.getUserConnexion().getPassword(), selected.getNom()), selected.getEmail(), "Changement de mot de passe");
//        } else {
//            /*afficher un message*/
//            System.out.println("po de user connexion");
//        }
//    }
}
