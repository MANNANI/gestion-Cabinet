/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Admin;
import bean.AdminSecretaire;
import bean.Employe;
import bean.UserConnexion;
import java.util.Date;
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
public class AdminSecretaireFacade extends AbstractFacade<AdminSecretaire> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @EJB
    private UserConnexionFacade userConnexionFacade;
    @EJB
    private AdminFacade adminFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AdminSecretaireFacade() {
        super(AdminSecretaire.class);
    }

    public int deleteListe(List<AdminSecretaire> listes) {
        for (AdminSecretaire liste : listes) {
            UserConnexion user = userConnexionFacade.find(liste.getUserConnexion().getLogin());
            liste.setUserConnexion(null);
            userConnexionFacade.remove(user);
            remove(liste);
            listes.remove(liste);
        }
        if (listes.isEmpty()) {
            return 1;
        } else {
            return -1;
        }

    }

    public void deleteListeByEmpl(Employe employe) {
        List<AdminSecretaire> sec = em.createQuery("select n FROM AdminSecretaire n WHERE n.id='" + employe.getId() + "'").getResultList();
        UserConnexion user = userConnexionFacade.find(sec.get(0).getUserConnexion().getLogin());
        sec.get(0).setUserConnexion(null);
        userConnexionFacade.remove(user);
        remove(sec.get(0));
    }

    public void attribuerSecAadmin(Employe admin, Employe sec) {
        AdminSecretaire secr = find(sec.getId());
        Admin ad = adminFacade.find(admin.getId());
        secr.setAdmin(ad);
        create(secr);
    }

    public List<AdminSecretaire> recherche(AdminSecretaire selected, Date datMin, Date dateMax, String etatB, String etatM) {
        System.out.println("fct recherche facade =>selected" + selected);
        System.out.println("fct recherche facade =>datMax" + dateMax);
        System.out.println("fct recherche facade =>etatM" + etatM);
        System.out.println("fct recherche facade =>etatB" + etatB);
        String requette = "SELECT a FROM AdminSecretaire a WHERE 1=1";
        if (!selected.getCin().equals("")) {
            requette += util.DateUtil.addConstraint("a", "cin", "=", selected.getCin());
        }
        if (!selected.getEmail().equals("")) {
            requette += util.DateUtil.addConstraint("a", "email", "=", selected.getEmail());
        }
        if (!selected.getNom().equals("")) {
            requette += util.DateUtil.addConstraint("a", "nom", "=", selected.getNom());
        }
        if (!selected.getPrenom().equals("")) {
            requette += util.DateUtil.addConstraint("a", "prenom", "=", selected.getPrenom());
        }
        if (!selected.getTel().equals("")) {
            requette += util.DateUtil.addConstraint("a", "tel", "=", selected.getTel());
        }
        if (datMin != null && dateMax != null) {
            requette += util.DateUtil.addConstraintMinMaxDate("a", "dateNaissance", datMin, dateMax);
        }
        System.out.println("la requette=>" + requette);
        List<AdminSecretaire> listes = em.createQuery(requette).getResultList();
        System.out.println("haa la liste des sec" + listes);
        if (listes!=null) {
            int i = listes.size();
            System.out.println("haa i"+i);
            AdminSecretaire liste=new AdminSecretaire();
           while(i>0){
               
               liste=listes.get(i-1);
               System.out.println("haa i"+i+"ha l'element coresp a i=>"+liste);
                if (liste.getUserConnexion() != null) {
                    if (etatB != null) {
                        if (etatB.equals("Bloquer")) {
                            if (liste.getUserConnexion().getBlocked() != 1) {
                                System.out.println("nonbl");
                                listes.remove(liste);
                            }
                        } else if (liste.getUserConnexion().getBlocked() != 0) {
                            System.out.println("bl");
                            listes.remove(liste);
                        }
                    }
                    if (etatM != null) {
                        if (etatM.equals("Changer")) {
                            if (!liste.getUserConnexion().isMdpChanged()) {
                                System.out.println("mdp non changer");
                                listes.remove(liste);
                            }
                        } else if (liste.getUserConnexion().isMdpChanged()) {
                            System.out.println("mdp changer");
                            listes.remove(liste);
                            
                        }
                        
                    }
                } else {
                    listes.remove(liste);
                }
                i--;
            }
        }
        System.out.println("ha la liste final =>"+listes);
        return listes;

    }

}
