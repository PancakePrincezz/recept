package receptek.recept;

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
@Table(name="recept_Recept")
public class Recept {
    @Id
    @GeneratedValue
    private int ReceptId;

    @Column(nullable = false)
    private String Cim;

    @Column(nullable = false)
    private String Leiras;

    @Column(nullable = false)
    private String Hozzavalok;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="SzerzoId", referencedColumnName="UserId")
    private User SzerzoUser;

    public User getSzerzoUser() {
        return SzerzoUser;
    }

    public void setSzerzoUser (User SzerzoUser) {
        this.SzerzoUser = SzerzoUser;
    }


    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="FotoId", referencedColumnName="Id")
    private Foto Foto;

    public Foto getFoto() {
        return Foto;
    }

    public void setFoto (Foto foto) {
        this.Foto = foto;
    }
}
