package receptek.user;

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
@Table(name="recept_User")
public class User {
    @Id
    @GeneratedValue
    private int UserId;

    @Column(nullable = false)
    private String UserName;

    @Column(nullable = false)
    private String PwHash;
}
