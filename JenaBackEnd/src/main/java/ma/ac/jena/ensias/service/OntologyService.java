package ma.ac.jena.ensias.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.StmtIteratorImpl;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import lombok.extern.slf4j.Slf4j;
import ma.ac.jena.ensias.model.Triplet;
@SuppressWarnings("deprecation")
@Slf4j
@Service
public class OntologyService {
  
  
  @Value("${nameSpace}")
  private String nameSpace;
  
  @Value("${ontologie.name}")
  private String ontologieName;
  
  
  @Value("${path.file}")
  private String pathOntology;
  
  public void uplaodOntology(MultipartFile ontologie) {
    
    if(! new File(pathOntology).exists())
    {
        new File(pathOntology).mkdir();
    }
    
    String orgName = ontologie.getOriginalFilename();
    String filePath = pathOntology+"//" + orgName;
    File dest = new File(filePath);
    try {
      ontologie.transferTo(dest);
    } catch (IllegalStateException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  
  public List<Triplet> getInferencesOntology(String nameOntologie,Boolean isFullDeduction) {
    String filePath = pathOntology+"//" + nameOntologie;
    
    OntModel myOntology= ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
    FileInputStream in = null;
    try 
    {
       in=new FileInputStream(filePath);
    } 
    catch (FileNotFoundException ex) 
    {
       log.error(ex.getMessage());
    }
    myOntology.read(in,nameSpace);
    
    // definir la methde reasoner 
    Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
    // Create the inference model.
    InfModel infModel = ModelFactory.createInfModel(reasoner,myOntology);
    ExtendedIterator<Statement> stmts;
    if(!isFullDeduction) {
        // Supprimer les relation existantes
        stmts = infModel.listStatements().filterDrop(new Filter<Statement>() {
          @Override
          public boolean accept(Statement o) {
              return myOntology.contains( o );
            }
          });
    }
    else {
      stmts=infModel.listStatements();
    }
    
    // convertir la liste statemante à un autre model clean nommé deductions
    Model deductions = ModelFactory.createDefaultModel().add( new StmtIteratorImpl( stmts ));
    
    deductions.write( System.out, "TTL" );
    
    //
    List<Triplet> triplets = new ArrayList<Triplet>();
    
    StmtIterator  stmtIterator = deductions.listStatements();
    
    if (stmtIterator.hasNext()) {
      
      while (stmtIterator.hasNext()) {
        
        Statement statement = stmtIterator.nextStatement();
        Resource subject = statement.getSubject(); // get the subject
        Property predicate = statement.getPredicate(); // get the predicate
        RDFNode object = statement.getObject();
        System.out.print(subject.toString());
        System.out.print(" " + predicate.toString() + " ");
        if (object instanceof Resource) {
          System.out.print(object.toString());
        } else {
          // object is a literal
          System.out.print(" \"" + object.toString() + "\"");
        }
        
        Triplet triplet = new Triplet();
        triplet.setRessource(subject.toString());
        triplet.setPropriete(predicate.toString());
        triplet.setValeur(object.toString());
        triplets.add(triplet);
        
        System.out.println("\n .");
        System.out.println();
        
      }
  } else {
      System.out.println("Aucune déductions n'est générer");
  } 
    
    System.out.println("count = "+triplets.size());
    
    return triplets;
    
  }
  
  
  public List<Triplet> deductionsModel(Boolean isFullDeduction) {
    
    OntModel myOntology= ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
    FileInputStream in = null;
    try 
    {
       in=new FileInputStream(ontologieName);
    } 
    catch (FileNotFoundException ex) 
    {
       log.error(ex.getMessage());
    }
    myOntology.read(in,nameSpace);
    
    // definir la methde reasoner 
    Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
    // Create the inference model.
    InfModel infModel = ModelFactory.createInfModel(reasoner,myOntology);
    ExtendedIterator<Statement> stmts;
    if(!isFullDeduction) {
        // Supprimer les relation existantes
        stmts = infModel.listStatements().filterDrop(new Filter<Statement>() {
          @Override
          public boolean accept(Statement o) {
              return myOntology.contains( o );
            }
          });
    }
    else {
      stmts=infModel.listStatements();
    }
    
    // convertir la liste statemante à un autre model clean nommé deductions
    Model deductions = ModelFactory.createDefaultModel().add( new StmtIteratorImpl( stmts ));
    
    deductions.write( System.out, "TTL" );
    
    //
    List<Triplet> triplets = new ArrayList<Triplet>();
    
    StmtIterator  stmtIterator = deductions.listStatements();
    
    if (stmtIterator.hasNext()) {
      
      while (stmtIterator.hasNext()) {
        
        Statement statement = stmtIterator.nextStatement();
        Resource subject = statement.getSubject(); // get the subject
        Property predicate = statement.getPredicate(); // get the predicate
        RDFNode object = statement.getObject();
        System.out.print(subject.toString());
        System.out.print(" " + predicate.toString() + " ");
        if (object instanceof Resource) {
          System.out.print(object.toString());
        } else {
          // object is a literal
          System.out.print(" \"" + object.toString() + "\"");
        }
        
        Triplet triplet = new Triplet();
        triplet.setRessource(subject.toString());
        triplet.setPropriete(predicate.toString());
        triplet.setValeur(object.toString());
        triplets.add(triplet);
        
        System.out.println("\n .");
        System.out.println();
        
      }
  } else {
      System.out.println("Aucune déductions n'est générer");
  } 
    
    System.out.println("count = "+triplets.size());
    
    return triplets;
    
  }
  
  
  public List<Triplet> execSparqQL() {
    
    OntModel myOntology= ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
    FileInputStream in = null;
    try 
    {
       in=new FileInputStream(ontologieName);
    } 
    catch (FileNotFoundException ex) 
    {
       log.error(ex.getMessage());
    }
    myOntology.read(in,nameSpace);
    
    String queryString = "PREFIX nS:<https://dbpedia.org/ontology/EducationalInstitution#> "
        + "SELECT  * "
         + "WHERE { ?x nS:Knows ?y."
         + "FILTER (?y NOT IN (nS:SaadBELEFQIH)) }";
    
    Query query = QueryFactory.create(queryString) ;
    QueryExecution qexec = QueryExecutionFactory.create(queryString, myOntology) ;
    QueryExecution qexec1 = QueryExecutionFactory.create(queryString, myOntology) ;
    List<Triplet> triplets = new ArrayList<Triplet>();
    
    try {
      ResultSet results = qexec.execSelect() ;
      ResultSet results1 = qexec1.execSelect() ;
      
        while (results.hasNext()) { 
          QuerySolution soln = results.nextSolution() ;
          Resource x = soln.getResource("y") ; // Get a result variable by name. Resource
          Triplet triplet = new Triplet();
          triplet.setRessource(soln.getResource("x").toString());
          triplet.setPropriete("<https://dbpedia.org/ontology/EducationalInstitution#Knows");
          triplet.setValeur(soln.getResource("y").toString());
          triplets.add(triplet);
        }
        
        ResultSetFormatter.out(System.out, results1,query) ;
       
      
     } finally { qexec.close() ; }
    
    
    return triplets;
    
  }
  
  
  
  
  
  
  


}
