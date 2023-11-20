package semillero.ecosistema.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "provincias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @ManyToOne
    @JoinColumn(name = "pais_id")
    private CountryEntity pais;
}