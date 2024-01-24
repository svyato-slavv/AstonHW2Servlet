package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "city")
@Getter
@Setter
@NoArgsConstructor
public class City {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer cityId;
    @Column(name = "name")
    String cityName;
    @OneToMany(mappedBy = "city")
    List<Weather> weatherList = new ArrayList<>();


    public City(String cityName) {
        this.cityName = cityName;
    }
}
