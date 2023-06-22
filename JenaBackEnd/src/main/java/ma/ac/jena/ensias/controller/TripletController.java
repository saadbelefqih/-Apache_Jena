package ma.ac.jena.ensias.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ma.ac.jena.ensias.service.OntologyService;

@RestController
@RequestMapping("/ontologyApi")
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600)
@RequiredArgsConstructor
public class TripletController {
  
  
  private final OntologyService ontologyService;
  
  @GetMapping("/getLocalOntology/")
  ResponseEntity<?> getLocalOntology(){
    return ResponseEntity.ok(ontologyService.deductionsModel(false));
  }
  
  
  @GetMapping("/getFullLocalOntology/")
  ResponseEntity<?> getFullLocalOntology(){
    return ResponseEntity.ok(ontologyService.deductionsModel(true));
  }
  
  
  @PostMapping("/uploadOntology/")
  ResponseEntity<?> uplaodImage(@RequestParam("imageFile") MultipartFile ontologie) throws IOException {
      System.out.println("Original Image Byte Size - " + ontologie.getBytes().length);
      ontologyService.uplaodOntology(ontologie);
      return ResponseEntity.ok(HttpStatus.OK);
  }
  
  
  @GetMapping("/{nameOntology}/getInferencesOntology")
  ResponseEntity<?> getInferencesOntology(@PathVariable("nameOntology") String nameOntology){
    return ResponseEntity.ok(ontologyService.getInferencesOntology(nameOntology,true));
  }
  
  @GetMapping("/getResultExecRqt1")
  ResponseEntity<?> getResultExecSPARQL(){
    return ResponseEntity.ok(ontologyService.execSparqQL());
  }


}
