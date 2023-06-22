package ma.ac.jena.ensias.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Triplet {
  
  private String ressource;
  private String propriete;
  private String valeur;

}
