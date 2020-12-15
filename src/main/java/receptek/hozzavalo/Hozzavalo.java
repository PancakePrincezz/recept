package receptek.hozzavalo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import receptek.foto.Foto;
import receptek.user.User;

import javax.persistence.*;
import java.time.Duration;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="recept_Hozzavalo")
public class Hozzavalo {
    @Id
    @GeneratedValue
    private int Id;

    @Column(nullable = false)
    private String Hozzavalo;

    @Column(nullable = false)
    private int ReceptId;

}
