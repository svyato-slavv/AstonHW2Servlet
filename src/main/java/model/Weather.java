package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "weather")
@Getter
@Setter
@NoArgsConstructor
public class Weather {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer weatherId;
    @Column(name = "date")
    LocalDate date;
    @Column(name = "max_temperature")
    Integer maxTemperature;
    @Column(name = "min_temperature")
    Integer minTemperature;
    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private City city;

    public Weather(LocalDate date, Integer maxTemperature, Integer minTemperature) {
        this.date = date;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
    }
}
