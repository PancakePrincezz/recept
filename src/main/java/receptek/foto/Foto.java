package receptek.foto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Class representing the result of a game played by a specific player.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="recept_Foto")
public class Foto {
    @Id
    @GeneratedValue
    private int Id;

    @Column(nullable = false, length = 1024000)
    private String Data;
}
