package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "uzer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long chatId;

}
