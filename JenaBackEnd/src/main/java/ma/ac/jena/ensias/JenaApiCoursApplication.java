package ma.ac.jena.ensias;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.StmtIteratorImpl;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.Filter;
import org.apache.jena.vocabulary.XSD;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class JenaApiCoursApplication {

  public static void main(String[] args) {
    SpringApplication.run(JenaApiCoursApplication.class, args);
    
    
    
    /*
     *  n Etape 1: Déterminer le domaine et le périmètre de l’ontologie ;
        n Etape 2: Enumérer les termes importants dans l’ontologie ;
        n Etape 3: Définir les classes et la hiérarchie des classes ;
        n Etape 4: Définir les relations entre les classes ;
     */
    
    //declaration  Ontologie
    OntModel myOntology= ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
    
    String namespace = "https://dbpedia.org/ontology/EducationalInstitution#";
    
    myOntology.createOntology (namespace);
    
  //Création de la classe  

    
    OntClass c_Etablisemnt = myOntology.createClass(namespace + "Highschool");
    
    OntClass c_Personne = myOntology.createClass(namespace + "Personne");
    c_Etablisemnt.addSubClass(c_Personne);
    
    OntClass c_Etudiant = myOntology.createClass(namespace + "Etudiant");
    c_Personne.addSubClass(c_Etudiant);
    
    OntClass c_chercheur = myOntology.createClass (namespace + "Chercheur");
    c_Personne.addSubClass(c_chercheur);
    
    OntClass c_enseignant = myOntology.createClass (namespace + "Enseignant");
    c_Personne.addSubClass(c_enseignant);
    
    //Création de la classe union
    RDFList listeClasses = myOntology.createList (new RDFNode[]{(RDFNode) c_chercheur, (RDFNode)c_enseignant});
    IntersectionClass c_enseignantChercheur = myOntology.createIntersectionClass (namespace + "EnseignantChercheur", listeClasses);
    
    
    OntClass c_Formation = myOntology.createClass(namespace + "Formation");
    c_Etablisemnt.addSubClass(c_Formation);
    
    
    /*
     * 
        n Etape 5: Définir les propriétés et son hiérarchie ;
        n Etape 6: Définir les domaines et les rang des propriétés ;
        n Etape 7: Définir les relations entre les propriétés ;
     */
    
    
  //Création de la propriété de données 
    
    // ObjectProperty
    ObjectProperty p_OffreDe = myOntology.createObjectProperty (namespace + "OffreDe");
    p_OffreDe.addDomain(c_Etablisemnt);
    p_OffreDe.addRange(c_Formation);
    
    ObjectProperty p_FormationDe = myOntology.createObjectProperty (namespace + "FormationDe");
    p_FormationDe.addDomain(c_Formation);
    p_FormationDe.addRange(c_Etablisemnt);
    p_FormationDe.addInverseOf(p_OffreDe);
    
    ObjectProperty p_SecomposeDe = myOntology.createObjectProperty (namespace + "SecomposeDe");
    p_SecomposeDe.addDomain(c_Etablisemnt);
    p_SecomposeDe.addRange(c_Personne);
    
    ObjectProperty p_AppartenirA = myOntology.createObjectProperty (namespace + "AppartenirA");
    p_AppartenirA.addDomain(c_Personne);
    p_AppartenirA.addRange(c_Etablisemnt);
    p_AppartenirA.addInverseOf(p_SecomposeDe);
    
    ObjectProperty p_InscrireA = myOntology.createObjectProperty (namespace + "InscrireA");
    p_InscrireA.addDomain(c_Etudiant);
    p_InscrireA.addRange(c_Formation);
    
    ObjectProperty p_AuProfit = myOntology.createObjectProperty (namespace + "AuProfit");
    p_AuProfit.addDomain(c_Formation);
    p_AuProfit.addRange(c_Etudiant);
    p_AuProfit.addInverseOf(p_InscrireA);
    
    ObjectProperty p_Enseigner = myOntology.createObjectProperty (namespace + "Enseigne");
    p_Enseigner.addDomain(c_enseignant);
    p_Enseigner.addRange(c_Etudiant);
    

    ObjectProperty p_Knows = myOntology.createObjectProperty (namespace + "Knows");
    p_Knows.addSubProperty(p_Enseigner);
    p_Knows.convertToSymmetricProperty();
    p_Knows.addDomain(c_enseignant);
    p_Knows.addRange(c_Etudiant);
    
    
    // DatatypeProperty
    
    
    DatatypeProperty intitule = myOntology.createDatatypeProperty (namespace + "Intitule");
    intitule.setDomain (c_Etablisemnt);
    intitule.setRange (XSD.xstring);
    
    DatatypeProperty hasName = myOntology.createDatatypeProperty (namespace + "HasName");
    hasName.setDomain (c_Formation);
    hasName.setRange (XSD.xstring);
    
    DatatypeProperty aPourCNE = myOntology.createDatatypeProperty (namespace + "CNE");
    aPourCNE.convertToFunctionalProperty();
    aPourCNE.setDomain (c_Etudiant);
    aPourCNE.setRange (XSD.xstring);
    
    DatatypeProperty aPourCIN = myOntology.createDatatypeProperty (namespace + "CIN");
    aPourCIN.convertToInverseFunctionalProperty();
    aPourCIN.setDomain (c_Personne);
    aPourCIN.setRange (XSD.xstring);
    
    DatatypeProperty aPourNom = myOntology.createDatatypeProperty (namespace + "Nom");
    aPourNom.setDomain (c_Personne);
    aPourNom.setRange (XSD.xstring);
    
    DatatypeProperty aPourPrenom = myOntology.createDatatypeProperty (namespace + "Prenom");
    aPourPrenom.setDomain (c_Personne);
    aPourPrenom.setRange (XSD.xstring);
    
    DatatypeProperty aPourDateNaissance = myOntology.createDatatypeProperty (namespace + "DateNaissance");
    aPourDateNaissance.convertToFunctionalProperty();
    aPourDateNaissance.setDomain (c_Personne);
    aPourDateNaissance.setRange (XSD.xstring);
    
    DatatypeProperty aPourCnss = myOntology.createDatatypeProperty (namespace + "Cnss");
    aPourCnss.convertToFunctionalProperty();
    aPourCnss.setDomain (c_enseignant);
    aPourCnss.setRange (XSD.xstring);
    
    DatatypeProperty apourIntituleFormation = myOntology.createDatatypeProperty (namespace + "APourIntituleFormation");
    apourIntituleFormation.convertToFunctionalProperty();
    apourIntituleFormation.setDomain (c_enseignant);
    apourIntituleFormation.setRange (XSD.xstring);
    
    
    /*
     * 
        n Etape 8: Définir les instances des classes.
     */
    myOntology.setNsPrefix( "HS", namespace );
    
    Individual i_ensias = c_Etablisemnt.createIndividual(namespace+"ENSIAS");
    i_ensias.addProperty(hasName, namespace+"École Nationale Supérieure d'Informatique et d'Analyse des Systèmes");
    
    
    Individual  i_ahmedZELLOU =c_enseignantChercheur.createIndividual(namespace+"AhmedZELLOU");
    i_ahmedZELLOU.addProperty(aPourNom, "ZELLOU");
    i_ahmedZELLOU.addProperty(aPourPrenom, "Ahmed");
    i_ahmedZELLOU.addProperty(aPourCnss, "AS01B4750");
    i_ahmedZELLOU.addProperty(aPourCIN, "A14785");
    i_ahmedZELLOU.addProperty(aPourDateNaissance, "1980-08-18");
    i_ahmedZELLOU.addProperty(p_AppartenirA, i_ensias);
    
    
    Individual i_saadBelefqih = c_Etudiant.createIndividual(namespace+"SaadBELEFQIH");
    i_saadBelefqih.addProperty(aPourNom, "BELEFQIH");
    i_saadBelefqih.addProperty(aPourPrenom, "Saad");
    i_saadBelefqih.addProperty(aPourCIN, "A478510");
    i_saadBelefqih.addProperty(aPourDateNaissance, "1994-05-30");
    i_saadBelefqih.addProperty(aPourCNE, "1210523811");
    i_saadBelefqih.addProperty(p_Knows, i_ahmedZELLOU);
    i_saadBelefqih.addProperty(p_AppartenirA, i_ensias);
    
    Individual i_nourImaneElghazi = c_Etudiant.createIndividual(namespace+"NourImaneElghazi");
    i_nourImaneElghazi.addProperty(aPourNom, "EL GHAZI");
    i_nourImaneElghazi.addProperty(aPourPrenom, "Nour Imane");
    i_nourImaneElghazi.addProperty(aPourCIN, "A4401");
    i_nourImaneElghazi.addProperty(aPourDateNaissance, "1994-05-30");
    i_nourImaneElghazi.addProperty(aPourCNE, "1710523808");
    i_nourImaneElghazi.addProperty(p_Knows, i_ahmedZELLOU);
    i_nourImaneElghazi.addProperty(p_AppartenirA, i_ensias);
    

    
    Individual i_miola = c_Formation.createIndividual(namespace+"MIOLA");
    i_miola.addProperty(hasName, "Master Internet des Objets : Logiciel et Analytique (MIOLA)");
    i_miola.addProperty(p_FormationDe, i_ensias);
    
    
    i_saadBelefqih.addProperty(p_InscrireA, i_miola);
    i_nourImaneElghazi.addProperty(p_InscrireA, i_miola);
    
    
    
    Individual i_M3S = c_Formation.createIndividual(namespace+"M3S");
    i_M3S.addProperty(hasName, "Master de Recherche en Sécurité Systèmes et Services (M3S)");
    i_M3S.addProperty(p_FormationDe, i_ensias);
    
    
    
    RDFDataMgr.write(System.out, myOntology, Lang.NTRIPLES);
    
    
   
    
    FileOutputStream fichierSortie = null;

    try {
            fichierSortie = new FileOutputStream (new File ("ontologie.owl"));
    }
    catch (FileNotFoundException ex) {
            
    }

    myOntology.write (fichierSortie);
   
   

  }

}
