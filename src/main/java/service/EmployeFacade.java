/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.AdminSecretaire;
import bean.Employe;
import bean.UserConnexion;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author houda
 */
@Stateless
public class EmployeFacade extends AbstractFacade<Employe> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @EJB
    private AdminFacade adminFacade;
    @EJB
    private MedecinFacade medecinFacade;
    @EJB
    private MedecinSecretaireFacade medecinSecretaireFacade;
    @EJB
    private AdminSecretaireFacade adminSecretaireFacade;
    @EJB
    private PatientFacade patientFacade;
    @EJB
    private UserConnexionFacade userConnexionFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmployeFacade() {
        super(Employe.class);
    }

    public List<Employe> recherche(String type, String etat, String fonction) {
        List<Employe> listeEmp = new ArrayList();
        if (!etat.equals("")) {
            listeEmp.addAll(listEtat(etat));
        }
        if (!fonction.equals("")) {
            listeEmp.addAll(groupeFonction(fonction));
        }
        if (!type.equals("")) {
            if (type.equals("Bloquer")) {
                listeEmp.addAll(UserBloquer());
            } else if (type.equals("NonBloquer")) {
                listeEmp.addAll(UserNotBloquer());
            }
        }
        return listeEmp;
    }

    public List<Employe> listEtat(String etat) {
        if (etat.equals("Changer")) {
            return em.createQuery("SELECT emp FROM Employe emp WHERE emp.userConnexion.mdpChanged=1").getResultList();
        } else if (etat.equals("NonChanger")) {
            return em.createQuery("SELECT emp FROM Employe emp WHERE emp.userConnexion.mdpChanged=0").getResultList();
        } else {
            return new ArrayList();
        }
    }

    public List<Employe> UserBloquer() {
        return em.createQuery("SELECT emp FROM Employe emp WHERE  emp.userConnexion.blocked =1").getResultList();
    }

    public List<Employe> UserNotBloquer() {
        return em.createQuery("SELECT emp FROM Employe emp WHERE  emp.userConnexion.blocked =0").getResultList();
    }

    public int bloquerDebloquer(Employe employe) {
        return userConnexionFacade.bloquerDebloquer(employe.getUserConnexion());
    }

    public String findUserFonction(Employe employe) {
        String requete;
        String type = "";
        if (employe != null) {
            requete = "SELECT e.dtype FROM Employe e WHERE e.id=" + employe.getId();
            type = (String) getEntityManager().createNativeQuery(requete).getSingleResult();
        }
        return type;
    }

    public List<Employe> groupeFonction(String fonction) {
        List<Employe> emps = findAll();
        List<Employe> fonctions = new ArrayList<>();
        for (Employe e : emps) {
            if (findUserFonction(e).equals(fonction)) {
                fonctions.add(e);
            }
        }
        return fonctions;
    }

    public void deleteEmp(Employe employe) {
        String type = findUserFonction(employe);
        switch (type) {
            case "Admin":
                List<AdminSecretaire> sec = em.createQuery("select a from AdminSecretaire a WHERE a.admin.id='" + employe.getId() + "'").getResultList();
                int res = adminSecretaireFacade.deleteListe(sec);
                UserConnexion user = userConnexionFacade.find(employe.getUserConnexion().getLogin());
                employe.setUserConnexion(null);
                userConnexionFacade.remove(user);
                remove(employe);
                break;
            case "Medecin":
                medecinFacade.removeEmp(employe);
                break;
            case "MedecinSecretaire":
                medecinSecretaireFacade.deleteSecByEmpl(employe);
                break;
            case "AdminSecretaire":
                adminSecretaireFacade.deleteListeByEmpl(employe);
                break;
            default:
                break;
        }
    }

}
