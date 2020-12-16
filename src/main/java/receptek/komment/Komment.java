package receptek.komment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import receptek.foto.Foto;
import receptek.user.User;

import javax.persistence.*;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity

@Table(name="recept_Komment")
public class Komment {
    @Id
    @GeneratedValue
    private int Id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="HozzaszoloId", referencedColumnName="UserId")
    private User hozzaszoloUser;

    public User getHozzaszoloUser() {
        return hozzaszoloUser;
    }

    public void setHozzaszoloUser (User hozzaszoloUser) {
        this.hozzaszoloUser = hozzaszoloUser;
    }

    @Column(nullable = false)
    private int ReceptId;

    @Column(nullable = false, length = 20000)
    private String Komment;

    @Column(nullable = false)
    private Date Datum;
}
